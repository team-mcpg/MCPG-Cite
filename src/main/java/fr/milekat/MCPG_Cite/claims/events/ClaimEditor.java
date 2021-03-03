package fr.milekat.MCPG_Cite.claims.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClaimEditor implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock()==null || event.getHand()!=null && event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (player.hasPermission("modo.claim.event.checkclaim") &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            toolClaim(player, event, block);
        }
    }
    
    private void toolClaim(Player player, PlayerInteractEvent event, Block block) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) &&
                player.hasPermission("modo.claim.event.checkclaim") &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            Region region;
            if (player.isSneaking()) {
                region = ClaimManager.getRegion(block.getRelative(event.getBlockFace()).getLocation());
            } else {
                region = ClaimManager.getRegion(block.getLocation());
            }
            if (region==null) {
                player.sendMessage(MainCite.PREFIX + "§6Le block n'est pas claim");
            } else {
                player.sendMessage(MainCite.PREFIX + "§6Région du block: §b" + region.getName());
            }
            event.setCancelled(true);
        } else if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                player.hasPermission("modo.claim.event.checkclaim")) &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) &&
                player.isSneaking()) {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            if (meta!=null && ClaimManager.regions.containsKey(meta.getDisplayName())) {
                Region region = ClaimManager.regions.get(meta.getDisplayName());
                if (ClaimManager.regionsBlocks.getOrDefault(block.getLocation(),"cite")
                        .equalsIgnoreCase("cite")) {
                    addToClaim(region, block, player);
                } else {
                    removeToClaim(block.getLocation(), player);
                }
            }
        }
    }

    /**
     * Add a block to region !
     */
    private void addToClaim(Region region, Block block, Player player) {
        Connection connection = MainCore.getSql();
        try {
            Location loc = block.getLocation();
            PreparedStatement q = connection.prepareStatement("UPDATE `mcpg_regions` SET" +
                    " `rg_locs`=CONCAT(IFNULL(CONCAT(`rg_locs`,';'),''),?) WHERE `rg_id`=?;");
            q.setString(1, loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
            q.setInt(2, region.getId());
            q.execute();
            q.close();
            ClaimManager.regionsBlocks.put(block.getLocation(), region.getName());
            player.sendMessage(MainCite.PREFIX + "§6Block ajouté à §b" + region.getName());
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Erreur dans l'ajout d'un block à une région.");
            throwables.printStackTrace();
            player.sendMessage(MainCite.PREFIX + "§cErreur dans l'ajout du block.");
        }
    }

    /**
     * Remove a block from region
     */
    private void removeToClaim(Location loc, Player player) {
        Connection connection = MainCore.getSql();
        try {
            Region region = ClaimManager.regions.get(ClaimManager.regionsBlocks.get(loc));
            PreparedStatement q = connection.prepareStatement("SELECT `rg_locs` FROM `mcpg_regions` WHERE `rg_id` = ?;");
            q.setInt(1, region.getId());
            q.execute();
            q.getResultSet().last();
            ArrayList<String> locs =
                    new ArrayList<>(Arrays.asList(q.getResultSet().getString("rg_locs").split(";")));
            locs.remove(loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
            String posSQL;
            if (locs.size()>0) {
                StringBuilder pos = new StringBuilder();
                for (String loop : locs) {
                    pos.append(loop);
                    pos.append(";");
                }
                posSQL = pos.substring(0, pos.length() - 1);
            } else {
                posSQL = null;
            }
            q.close();
            q = connection.prepareStatement("UPDATE `mcpg_regions` SET `rg_locs` = ? WHERE `rg_id` = ?;");
            q.setString(1, posSQL);
            q.setInt(2, region.getId());
            q.execute();
            q.close();
            ClaimManager.regionsBlocks.remove(loc);
            player.sendMessage(MainCite.PREFIX + "§6Block retiré de la claim.");
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Erreur dans le remove d'un block à une région.");
            throwables.printStackTrace();
            player.sendMessage(MainCite.PREFIX + "§cErreur dans le remove du block.");
        }
    }
}
