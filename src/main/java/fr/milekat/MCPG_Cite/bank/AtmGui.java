package fr.milekat.MCPG_Cite.bank;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.core.classes.TeamManager;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.text.ParseException;
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
        emeralds.put(11, 4);
        emeralds.put(12, 8);
        emeralds.put(19, 16);
        emeralds.put(20, 32);
        emeralds.put(21, 64);
        for (Map.Entry<Integer, Integer> loop : emeralds.entrySet()) {
            setItem(loop.getKey(), new ItemBuilder(Material.EMERALD)
                    .name("§2Déposer " + loop.getValue()).amount(loop.getValue()).build(), e -> {
                if (e.getWhoClicked().getInventory().containsAtLeast(emerald, loop.getValue())) {
                    emeraldsProcess((Player) e.getWhoClicked(), loop.getValue(),
                            new ItemBuilder(Material.EMERALD).amount(loop.getValue()).build(), false);
                } else {
                    e.getWhoClicked().sendMessage(MainCite.PREFIX + "§bVous n'avez pas assez d'émeraude.");
                }
            });
        }
        //  Blocks
        HashMap<Integer, Integer> emeraldBlocks = new HashMap<>();
        emeraldBlocks.put(14, 1);
        emeraldBlocks.put(15, 4);
        emeraldBlocks.put(16, 8);
        emeraldBlocks.put(23, 16);
        emeraldBlocks.put(24, 32);
        emeraldBlocks.put(25, 64);
        for (Map.Entry<Integer, Integer> loop : emeraldBlocks.entrySet()) {
            setItem(loop.getKey(), new ItemBuilder(Material.EMERALD_BLOCK)
                    .name("§2Déposer " + loop.getValue()).amount(loop.getValue()).build(), e -> {
                if (e.getWhoClicked().getInventory().containsAtLeast(emeraldBlock, loop.getValue())) {
                    emeraldsProcess((Player) e.getWhoClicked(), loop.getValue() * 9,
                            new ItemBuilder(Material.EMERALD_BLOCK).amount(loop.getValue()).build(), false);
                } else {
                    e.getWhoClicked().sendMessage(MainCite.PREFIX + "§bVous n'avez pas assez d'émeraude.");
                }
            });
        }
        //  All emeralds
        setItem(31, new ItemBuilder(Material.HOPPER_MINECART).name(ChatColor.DARK_GREEN + "Tout déposer").build(), e -> {
            int e_amount = McTools.getAmount((Player) e.getWhoClicked(), new ItemStack(Material.EMERALD));
            int b_amount = McTools.getAmount((Player) e.getWhoClicked(), new ItemStack(Material.EMERALD_BLOCK));
            if (e_amount > 0) {
                emeraldsProcess((Player) e.getWhoClicked(), e_amount, new ItemStack(Material.EMERALD), true);
            }
            if (b_amount > 0) {
                emeraldsProcess((Player) e.getWhoClicked(), b_amount * 9, new ItemStack(Material.EMERALD_BLOCK), true);
            }
            if (e_amount == 0 && b_amount==0 && !e.getWhoClicked().getInventory().contains(Material.EMERALD_BLOCK)) {
                e.getWhoClicked().sendMessage(MainCite.PREFIX + "§cAucune émeraude trouvée.");
            } else {
                emeraldsProcess((Player) e.getWhoClicked());
            }
        });
        setItem(35, new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "Sortir du compte").build(),
                e -> e.getWhoClicked().closeInventory());
    }

    /**
     * Process deposit ! (Remove emeralds from player inventory and add them into SQL)
     */
    private void emeraldsProcess(Player player, int emeralds, ItemStack item, boolean full) {
        try {
            TeamManager.addMoney(player, emeralds);
            if (full) {
                for (ItemStack loopItem : player.getInventory().getContents()) {
                    if (loopItem!=null && item.isSimilar(loopItem)) player.getInventory().removeItem(loopItem);
                }
            } else {
                player.getInventory().removeItem(item);
            }
            player.sendMessage(MainCite.PREFIX + "§6Tu as déposé §b" + MainCite.df.format(emeralds) + " §bémeraudes§c.");
        } catch (SQLException throwable) {
            player.sendMessage(MainCite.PREFIX + "§cError, contact un admin.");
            throwable.printStackTrace();
        }
    }

    /**
     * Mega Emeralds check and process
     */
    private void emeraldsProcess(Player player) {
        for (ItemStack loopItem : player.getInventory().getContents()) {
            if (loopItem==null) continue;
            ItemMeta meta = loopItem.getItemMeta();
            if (loopItem.getType().equals(Material.EMERALD_BLOCK) &&
                    loopItem.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS) &&
                    meta!=null && meta.getLore()!=null && meta.getLore().size()>=2) {
                try {
                    emeraldsProcess(player, MainCite.df.parse(meta.getLore().get(1)).intValue() *
                            loopItem.getAmount(), loopItem, true);
                } catch (ParseException ignore) {
                    player.sendMessage(MainCite.PREFIX + "§cItem corrompu, contact le staff pour remplacement.");
                }
            }
        }
    }

    @Override
    protected void onClick(InventoryClickEvent event) { event.setCancelled(true); }
}
