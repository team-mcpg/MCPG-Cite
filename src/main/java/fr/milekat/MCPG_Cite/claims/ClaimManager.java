package fr.milekat.MCPG_Cite.claims;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Cite.claims.commands.BuildMode;
import fr.milekat.MCPG_Cite.claims.commands.RegionCmd;
import fr.milekat.MCPG_Cite.claims.events.ClaimEditor;
import fr.milekat.MCPG_Cite.claims.events.MarketEvent;
import fr.milekat.MCPG_Cite.claims.events.WorldProtect;
import fr.milekat.MCPG_Cite.core.classes.TeamManager;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ClaimManager {
    public static ArrayList<Player> BUILDER = new ArrayList<>();
    public static LinkedHashMap<String, Region> REGIONS = new LinkedHashMap<>();
    public static HashMap<Location, String> REGIONS_BLOCKS = new HashMap<>();

    public ClaimManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new WorldProtect(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MarketEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ClaimEditor(), plugin);
        plugin.getCommand("builder").setExecutor(new BuildMode());
        plugin.getCommand("region").setExecutor(new RegionCmd());
        plugin.getCommand("region").setTabCompleter(new RegionCmd());
        try {
            loadRegions();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Load region error, disable plugin.");
            throwables.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Load regions from SQL
     */
    private void loadRegions() throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_region`;");
        q.execute();
        REGIONS.clear();
        REGIONS_BLOCKS.clear();
        while (q.getResultSet().next()) {
            Region region = new Region(q.getResultSet().getInt("rg_id"),
                    q.getResultSet().getString("rg_name"), null, q.getResultSet().getInt("rg_price"),
                    null, new ArrayList<>());
            if (q.getResultSet().getInt("team_id") != 0) {
                region.setTeam(TeamManager.getTeam(q.getResultSet().getInt("team_id")));
            }
            try {
                if (q.getResultSet().getString("rg_sign") != null) {
                    region.setSign((Sign) getBlock(q.getResultSet().getString("rg_sign")).getState());
                }
            } catch (ClassCastException ignore) {
                Bukkit.getLogger().warning("Sign error: " + q.getResultSet().getString("rg_sign"));
            }
            if (q.getResultSet().getString("rg_blocks") != null) {
                for (String loc : q.getResultSet().getString("rg_blocks").split(";")) {
                    region.getBlocks().add(McTools.getLocation("world", loc));
                    REGIONS_BLOCKS.put(McTools.getLocation("world", loc), region.getName());
                }
            }
            REGIONS.put(region.getName(), region);
        }
        q.close();
    }
    private Block getBlock(String location) { return McTools.getLocation("world", location).getBlock(); }

    /**
     *      Récupère le nom de la région à la pos (BlockPos)
     * @param location du block !
     * @return nom de la région (cite par défaut)
     */
    public static Region getRegion(Location location) {
        return REGIONS.getOrDefault(REGIONS_BLOCKS.get(location.getBlock().getLocation()),null);
    }

    /**
     *      Check si le joueur n'est pas dans une habitation qu'il ne possède pas
     * @param location pos du block
     * @param player joueur
     * @return autorisé true/false
     */
    public static boolean canTeleportHere(Location location, Player player) {
        if (BUILDER.contains(player)) return true;
        Region region = getRegion(location);
        if (region == null || region.getName().equalsIgnoreCase("throwable")) return false;
        return canBuild(location, player);
    }

    /**
     *      Check si le joueur peut intéragir avec le block
     * @param location pos du block
     * @param player joueur
     * @return autorisé true/false
     */
    public static boolean canInteract(Location location, Player player){
        if (BUILDER.contains(player)) return true;
        Region region = getRegion(location);
        if (region == null) return false;
        if (region.getName().equalsIgnoreCase("interact-ok")) return true;
        return canBuild(location, player);
    }

    /**
     *      Check si le joueur peut consuitre à la pos du block (==Il fait parti de l'équipe qui poscède la zone)
     * @param location pos du block
     * @param player joueur
     * @return autorisé true/false
     */
    public static boolean canBuild(Location location, Player player) {
        if (BUILDER.contains(player)) return true;
        Region region = getRegion(location);
        if (region == null || region.getTeam() == null) return false;
        for (OfflinePlayer members : region.getTeam().getMembers()) {
            if (members.getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }
}
