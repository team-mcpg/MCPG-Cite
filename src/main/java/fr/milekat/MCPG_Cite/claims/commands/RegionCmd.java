package fr.milekat.MCPG_Cite.claims.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RegionCmd implements TabExecutor {
    private final Material CLAIMING = Material.BEDROCK;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true; /* Commande désactivée pour la console */
        if (sender.hasPermission("modo.claim.command.claim")) {
            if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {
                try {
                    Region region = addRegion((Player) sender, args[1]);
                    if (args.length == 3) { /* If player add a price [<price>] */
                        try {
                            updatePrice((Player) sender, region, Integer.parseInt(args[2]));
                        } catch (NumberFormatException exception) {
                            sender.sendMessage(MainCite.PREFIX + "§cPrix invalide, /rg update prix <prix>.");
                        }
                    }
                } catch (SQLException throwables) {
                    Bukkit.getLogger().warning("Erreur dans la création d'une nouvelle région.");
                    sender.sendMessage(MainCite.PREFIX + "§cErreur dans la création d'une nouvelle région.");
                    throwables.printStackTrace();
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("update")) {
                if (args.length >= 3 && !ClaimManager.REGIONS.containsKey(args[2])) {
                    sender.sendMessage(MainCite.PREFIX + "§cRégion non reconnue.");
                } else if (args.length == 4 && args[1].equalsIgnoreCase("prix")) {
                    try {
                        updatePrice((Player) sender, ClaimManager.REGIONS.get(args[2]), Integer.parseInt(args[3]));
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(MainCite.PREFIX + "§cMerci de mettre un prix valide.");
                    }
                } else if (args.length == 3 && args[1].equalsIgnoreCase("claim")) {
                    updateClaim((Player) sender, ClaimManager.REGIONS.get(args[2]));
                } else if (args.length == 3 && args[1].equalsIgnoreCase("sign")) {
                    updateSign((Player) sender, ClaimManager.REGIONS.get(args[2]));
                }
            } else if (args.length==2 && args[0].equalsIgnoreCase("tool")) {
                if (ClaimManager.REGIONS.containsKey(args[1])) {
                    ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
                    if (meta != null) meta.setDisplayName(args[1]);
                    ((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
                } else {
                    sender.sendMessage(MainCite.PREFIX + "§cRégion non reconnue.");
                }
            } else sendHelp(sender, label);
            return true;
        }
        sender.sendMessage(MainCite.PREFIX + "§cCommande pour les modos.");
        return true;
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender, String lbl){
        sender.sendMessage("§6/" + lbl + " help:§r Obtenir de l'aide sur la commande.");
        sender.sendMessage("§6/" + lbl + " add <nom_region> [<prix>]:§r Ajoute une région à la liste.");
        sender.sendMessage("§6/" + lbl + " update prix <nom_region> <prix>:§r défini le prix de la région.");
        sender.sendMessage("§6/" + lbl + " update claim <nom_region>:§r claim la zone (Avoir une selection WE).");
        sender.sendMessage("§6/" + lbl + " update sign <nom_region>:§r défini le panneau (Regarder le panneau).");
        sender.sendMessage("§6/" + lbl + " tool <nom_region>:§r défini l'outil (Avoir une blaze rod en main).");
    }

    /**
     *      Ajout d'une région dans la base SQL
     */
    private Region addRegion(Player player, String name) throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("INSERT INTO `mcpg_region`(`rg_name`) VALUES (?) RETURNING `rg_id`;");
        q.setString(1, name);
        q.execute();
        q.getResultSet().last();
        Region region = new Region(q.getResultSet().getInt("rg_id"),
                name, null, 0, null, new ArrayList<>());
        ClaimManager.REGIONS.put(name, region);
        q.close();
        player.sendMessage(MainCite.PREFIX + "§6Région §b" + name + "§6 créée.");
        return region;
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void updatePrice(Player player, Region region, Integer price) {
        try {
            updateSQLRegion(region,"price",null,price);
            region.setPrice(price);
            player.sendMessage(MainCite.PREFIX + "§6La région §b" + region.getName() +
                    " §6coûte désormais §2" + price + "Émeraudes§c.");
        } catch (SQLException throwables) {
            player.sendMessage(MainCite.PREFIX + "§6Erreur SQL.");
            Bukkit.getLogger().warning(MainCite.PREFIX + "Erreur de l'update du prix de: " + region.getName());
            throwables.printStackTrace();
        }
    }

    /**
     *      Définition / Redéfinition de blocks de la claim
     */
    private void updateClaim(Player player, Region region) {
        try {
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (worldEdit == null) {
                player.sendMessage(MainCite.PREFIX + "§cErreur WorlEdit");
                return;
            }
            com.sk89q.worldedit.regions.Region selection;
            try {
                selection = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
            } catch (IncompleteRegionException ignore) {
                player.sendMessage(MainCite.PREFIX + "§cMerci de faire une selection WE.");
                return;
            }
            CuboidRegion cuboid = new CuboidRegion(selection.getMaximumPoint(),selection.getMinimumPoint());
            Location pos1 = new Location(player.getWorld(),cuboid.getPos1().getX(),cuboid.getPos1().getY(),cuboid.getPos1().getZ());
            Location pos2 = new Location(player.getWorld(),cuboid.getPos2().getX(),cuboid.getPos2().getY(),cuboid.getPos2().getZ());
            StringBuilder pos = new StringBuilder();
            HashMap<Location, String> blocks = new HashMap<>();
            try {
                for (Block block : getBlocks(pos1,pos2,player.getWorld())) {
                    pos.append(";");
                    pos.append(McTools.getString(block.getLocation()));
                    blocks.put(block.getLocation(), region.getName());
                    block.setType(Material.AIR);
                }
                updateSQLRegion(region, "blocks", pos.substring(1), 0);
                ClaimManager.REGIONS_BLOCKS.putAll(blocks);
                region.setBlocks(new ArrayList<>(blocks.keySet()));
                player.sendMessage(MainCite.PREFIX + "§6Claim mise à jour pour la région §b" + region.getName() + "§c.");
            } catch (SQLException throwables) {
                player.sendMessage(MainCite.PREFIX + "§cErreur SQL.");
                Bukkit.getLogger().warning(MainCite.PREFIX + "Erreur de l'update du claim de: " + region.getName());
                throwables.printStackTrace();
            } catch (StringIndexOutOfBoundsException ignore) {
                player.sendMessage(MainCite.PREFIX + "§cAucun " + CLAIMING.toString() + ", opération annulée.");
            }
        } catch (ClassCastException  exception) {
            player.sendMessage(MainCite.PREFIX + "§cErreur WorlEdit");
            exception.printStackTrace();
        }
    }

    /**
     *      Défini l'emplacement du sign de la région
     */
    private void updateSign(Player player, Region region) {
        Block block = player.getTargetBlockExact(5);
        if (block != null && block.getState() instanceof Sign) {
            try {
                updateSQLRegion(region, "sign", McTools.getString(block.getLocation()), 0);
                region.setSign((Sign) block.getState());
                player.sendMessage(MainCite.PREFIX + "§6Panneau mis à jour pour la région §b" + region.getName() + "§c.");
            } catch (SQLException exception) {
                player.sendMessage(MainCite.PREFIX + "§6Erreur SQL.");
                Bukkit.getLogger().warning(MainCite.PREFIX + "Erreur update Sign de: " + region.getName());
                exception.printStackTrace();
            }
        } else player.sendMessage(MainCite.PREFIX + "§cMerci de regarder un panneau vierge.");
    }

    /**
     *      Update d'un paramètre dans le SQL, si String null, intValue sera utilisé
     */
    private void updateSQLRegion(Region region, String column, String stringValue, Integer intValue) throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("UPDATE `mcpg_region` SET `rg_" + column + "` = ? WHERE `rg_id` = ?;");
        q.setObject(1, stringValue == null ? intValue : stringValue);
        q.setInt(2, region.getId());
        q.execute();
        q.close();
    }

    /**
     *      Snippet from https://bukkit.org/threads/get-blocks-between-two-locations.262499/
     *      Get all blocks between 2 pos ! (Only if type=CLAIMING)
     */
    private List<Block> getBlocks(Location loc1, Location loc2, World w){
        //First of all, we create the list:
        List<Block> blocks = new ArrayList<>();
        //Next we will name each coordinate
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();
        //Then we create the following integers
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;
        //Now we need to make sure xMin is always lower then xMax
        if(x1 > x2){ //If x1 is a higher number then x2
            xMin = x2;
            xMax = x1;
        }else{
            xMin = x1;
            xMax = x2;
        }
        //Same with Y
        if(y1 > y2){
            yMin = y2;
            yMax = y1;
        }else{
            yMin = y1;
            yMax = y2;
        }
        //And Z
        if(z1 > z2){
            zMin = z2;
            zMax = z1;
        }else{
            zMin = z1;
            zMax = z2;
        }
        //Now it's time for the loop
        for(x = xMin; x <= xMax; x ++){
            for(y = yMin; y <= yMax; y ++){
                for(z = zMin; z <= zMax; z ++){
                    Block b = new Location(w, x, y, z).getBlock();
                    if (b.getType().equals(CLAIMING)) {
                        blocks.add(b);
                    }
                }
            }
        }
        //And last but not least, we return with the list
        return blocks;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arg1 = new ArrayList<>(Arrays.asList("add", "update", "tool", "help"));
        if (args[0].length() < 1) {
            return arg1;
        } else if (args.length <= 1) {
            for (String cmd : arg1) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    return new ArrayList<>(Collections.singletonList(cmd));
                }
            }
        } else {
            if (args[0].equalsIgnoreCase("update")) {
                if (args.length <= 2) {
                    return McTools.getTabArgs(args[1], new ArrayList<>(Arrays.asList("prix", "claim", "sign")));
                } else if (args.length <= 3 &&
                        (args[1].equalsIgnoreCase("prix") ||
                                args[1].equalsIgnoreCase("claim") ||
                                args[1].equalsIgnoreCase("sign"))) {
                    return McTools.getTabArgs(args[2], new ArrayList<>(ClaimManager.REGIONS.keySet()));
                }
            } else if (args[0].equalsIgnoreCase("tool")) {
                return McTools.getTabArgs(args[1], new ArrayList<>(ClaimManager.REGIONS.keySet()));
            }
        }
        return null;
    }
}
