package fr.milekat.MCPG_Cite.bank;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AtmGui extends FastInv {
    private final ItemStack emerald = new ItemStack(Material.EMERALD);
    private final ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK);

    public AtmGui() {
        super(36, "§2[Banque] Effectuer un dépot");
        setItems(IntStream.range(0, 35).toArray(), new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§2Dépot").build());
        //  Emeralds
        HashMap<Integer, Integer> emeralds = new HashMap<>();
        emeralds.put(10, 1);
        emeralds.put(11, 5);
        emeralds.put(12, 10);
        emeralds.put(19, 16);
        emeralds.put(20, 32);
        emeralds.put(21, 64);
        for (Map.Entry<Integer, Integer> loop : emeralds.entrySet()) {
            setItem(loop.getKey(), new ItemBuilder(Material.EMERALD)
                    .name("§2Déposer " + loop.getValue()).amount(loop.getValue()).build(), e -> {
                if (e.getWhoClicked().getInventory().containsAtLeast(emerald, loop.getValue())) {
                    process((Player) e.getWhoClicked(), loop.getValue(), Material.EMERALD);
                } else {
                    e.getWhoClicked().sendMessage(MainCite.PREFIX + "§bVous n'avez pas assez d'émeraude.");
                }
            });
        }
        //  Blocks
        HashMap<Integer, Integer> emeraldBlocks = new HashMap<>();
        emeraldBlocks.put(14, 1);
        emeraldBlocks.put(15, 5);
        emeraldBlocks.put(16, 10);
        emeraldBlocks.put(23, 16);
        emeraldBlocks.put(24, 32);
        emeraldBlocks.put(25, 64);
        for (Map.Entry<Integer, Integer> loop : emeraldBlocks.entrySet()) {
            setItem(loop.getKey(), new ItemBuilder(Material.EMERALD_BLOCK)
                    .name("§2Déposer " + loop.getValue()).amount(loop.getValue()).build(), e -> {
                if (e.getWhoClicked().getInventory().containsAtLeast(emeraldBlock, loop.getValue())) {
                    process((Player) e.getWhoClicked(), loop.getValue(), Material.EMERALD_BLOCK);
                } else {
                    e.getWhoClicked().sendMessage(MainCite.PREFIX + "§bVous n'avez pas assez d'émeraude.");
                }
            });
        }
        //  All emeralds
        setItem(31, new ItemBuilder(Material.HOPPER_MINECART).name(ChatColor.DARK_GREEN + "Tout déposer").build(), e -> {
            int e_amount = McTools.getAmount((Player) e.getWhoClicked(),Material.EMERALD);
            int b_amount = McTools.getAmount((Player) e.getWhoClicked(),Material.EMERALD_BLOCK);
            if (e_amount > 0) {
                process((Player) e.getWhoClicked(), e_amount, Material.EMERALD);
            }
            if (b_amount > 0) {
                process((Player) e.getWhoClicked(), b_amount, Material.EMERALD_BLOCK);
            }
            if (e_amount == 0 && b_amount == 0) {
                e.getWhoClicked().sendMessage(MainCite.PREFIX + "§cAucune émeraude trouvée.");
            }
        });
        setItem(35, new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "Sortir du compte").build(),
                e -> e.getWhoClicked().closeInventory());
    }

    /**
     * Process deposit ! (Remove emeralds from player inventory and add them into SQL)
     */
    private void process(Player player, int itemCount, Material material) {
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("UPDATE `mcpg_team` SET `money` = `money` + ? WHERE `team_id` = " +
                    "(SELECT `team_id` FROM `mcpg_player` WHERE `uuid` = '" + player.getUniqueId() + "');" +
                    "INSERT INTO `mcpg_transactions`(`uuid`, `amount`) VALUES (?,?);");
            final int emeraldsCount = material.equals(Material.EMERALD) ? itemCount : itemCount * 9;
            q.setInt(1, emeraldsCount);
            q.setString(2, player.getUniqueId().toString());
            q.setInt(3, emeraldsCount);
            q.execute();
            q.close();
            player.getInventory().removeItem(new ItemBuilder(material).amount(itemCount).build());
            player.sendMessage(MainCite.PREFIX + "§6Tu as déposé §b" + emeraldsCount + " §bémeraudes§c.");
        } catch (SQLException throwables) {
            player.sendMessage(MainCite.PREFIX + "§cError, contact un admin.");
            throwables.printStackTrace();
        }
    }

    @Override
    protected void onClick(InventoryClickEvent event) { event.setCancelled(true); }
}
