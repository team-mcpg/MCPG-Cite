package fr.milekat.MCPG_Cite.claims;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Cite.claims.commands.BuildMode;
import fr.milekat.MCPG_Cite.claims.events.ClaimEditor;
import fr.milekat.MCPG_Cite.claims.events.MarketEvent;
import fr.milekat.MCPG_Cite.claims.events.WorldProtect;
import fr.milekat.MCPG_Cite.core.TeamManager;
import fr.milekat.MCPG_Cite.utils.LocationParser;
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
    public static ArrayList<Player> builder = new ArrayList<>();
    public static LinkedHashMap<String, Region> regions = new LinkedHashMap<>();
    public static HashMap<Location, String> regionsBlocks = new HashMap<>();

    public ClaimManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new WorldProtect(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ClaimEditor(), plugin);
        plugin.getCommand("builder").setExecutor(new BuildMode());
        plugin.getCommand("builder").setTabCompleter(new BuildMode());
        try {
            loadRegions();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Load region error, disable plugin.");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Load regions from SQL
     */
    private void loadRegions() throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("");
        q.execute();
        regions.clear();
        regionsBlocks.clear();
        while (q.getResultSet().next()) {
            Region region = new Region(q.getResultSet().getInt("region_id"),
                    q.getResultSet().getString("region_name"), null, q.getResultSet().getInt("price"),
                    null, new ArrayList<>());
            if (q.getResultSet().getInt("team_id") != 0) {
                region.setTeam(TeamManager.getTeam(q.getResultSet().getInt("team_id")));
            }
            if (q.getResultSet().getString("rg_sign") != null) {
                region.setSign((Sign) getBlock(q.getResultSet().getString("rg_sign")).getState());
                updateSign(region);
            }
            if (q.getResultSet().getString("rg_locs") != null) {
                for (String loc : q.getResultSet().getString("rg_locs").split(";")) {
                    region.getBlocks().add(LocationParser.getLocation("world", loc));
                    regionsBlocks.put(LocationParser.getLocation("world", loc), region.getName());
                }
            }
            regions.put(region.getName(), region);
        }
    }
    private Block getBlock(String location) {
        return LocationParser.getLocation("world", location).getBlock();
    }

    /**
     *      Mise à jour du contenu des panneaux
     */
    private void updateSign(Region region) {
        Sign sign = region.getSign();
        sign.setLine(0, MarketEvent.PREFIX);
        sign.setLine(1, region.getName());
        if (region.getTeam() != null) {
            sign.setLine(2, region.getTeam().getName());
            sign.setLine(3, MarketEvent.SOLD);
        } else {
            sign.setLine(2, "§6Prix§c: §2" + region.getPrice());
            sign.setLine(3, MarketEvent.SELL);
        }
        sign.update();
    }

    /**
     *      Récupère le nom de la région à la pos (BlockPos)
     * @param location du block !
     * @return nom de la région (cite par défaut)
     */
    public static Region getRegion(Location location) {
        return regions.getOrDefault(regionsBlocks.get(location.getBlock().getLocation()),null);
    }

    /**
     *      Check si le joueur n'est pas dans une habitation qu'il ne possède pas
     * @param location pos du block
     * @param player joueur
     * @return autorisé true/false
     */
    public static boolean canTeleportHere(Location location, Player player) {
        if (builder.contains(player)) return true;
        Region region = getRegion(location);
        if (region == null || region.getName().equalsIgnoreCase("interact-ok")) return false;
        return canBuild(location, player);
    }

    /**
     *      Check si le joueur peut intéragir avec le block
     * @param location pos du block
     * @param player joueur
     * @return autorisé true/false
     */
    public static boolean canInteract(Location location, Player player){
        if (builder.contains(player)) return true;
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
        if (builder.contains(player)) return true;
        Region region = getRegion(location);
        if (region == null || region.getTeam() == null) return false;
        for (OfflinePlayer members : region.getTeam().getMembers()) {
            if (members.getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }
}
