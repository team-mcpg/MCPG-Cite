package fr.milekat.MCPG_Cite.core.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FrozenTools implements Listener {
    @EventHandler (ignoreCancelled = true)
    public void onFrozenToolsUse(PlayerInteractEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.hasItemMeta()) {
            ItemMeta meta = tool.getItemMeta();
            if (meta!=null && meta.getLore()!=null && meta.getLore().contains("Frozen")) {
                event.setCancelled(true);
            }
        }
    }
}
