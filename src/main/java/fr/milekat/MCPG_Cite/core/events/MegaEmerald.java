package fr.milekat.MCPG_Cite.core.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParseException;

public class MegaEmerald implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onMegaEmeraldPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.EMERALD_BLOCK) &&
                event.getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onMegaEmeraldCraft(InventoryClickEvent event) {
        if ((event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING) ||
                event.getView().getTopInventory().getType().equals(InventoryType.WORKBENCH)) &&
                event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory()) &&
                event.getCursor() != null && event.getCursor().getType().equals(Material.EMERALD_BLOCK) &&
                event.getCursor().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMegaCompact(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getWhoClicked().getInventory()) &&
                event.getCursor() != null && event.getCursor().getType().equals(Material.EMERALD_BLOCK) &&
                event.getCursor().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            ItemMeta metaCursor = event.getCursor().getItemMeta();
            try {
                int emeralds = 0;
                if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.EMERALD_BLOCK) &&
                        event.getCurrentItem().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
                    ItemMeta metaCurrent = event.getCurrentItem().getItemMeta();
                    if (metaCurrent!=null && metaCurrent.getLore()!=null && metaCurrent.getLore().size()>=2) {
                        emeralds = MainCite.df.parse(metaCurrent.getLore().get(1)).intValue() *
                                event.getCurrentItem().getAmount();
                    }
                }
                if (metaCursor!=null && metaCursor.getLore()!=null && metaCursor.getLore().size()>=2) {
                    event.setCancelled(true);
                    emeralds = MainCite.df.parse(metaCursor.getLore().get(1)).intValue() * event.getCursor().getAmount() +
                            emeralds;
                    event.getWhoClicked().setItemOnCursor(null);
                    event.setCurrentItem(generateMegaEmerald(String.valueOf(emeralds)));
                }
            } catch (ParseException ignore) {}

        }
    }

    /**
     * Generate a new Item Stack with emeralds value
     */
     public static ItemStack generateMegaEmerald(String emeralds) {
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .name("§rMéga Émeraude").addLore("§r§6Valeur en banque§c:")
                .addLore(MainCite.df.format(Integer.parseInt(emeralds)))
                .enchant(Enchantment.LOOT_BONUS_BLOCKS, 10).flags(ItemFlag.HIDE_ENCHANTS).build();
    }
}
