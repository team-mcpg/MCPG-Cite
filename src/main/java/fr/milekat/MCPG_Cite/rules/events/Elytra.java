package fr.milekat.MCPG_Cite.rules.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Elytra implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerUseFireWorkBoostElytra(PlayerInteractEvent event) {
        if (!event.getPlayer().isGliding()) return;
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.FIREWORK_ROCKET) &&
                !event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.FIREWORK_ROCKET)) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        event.setCancelled(true);
        denyMsg(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerUseArrowBoostElytra(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player player = (Player) event.getEntity();
            if (!player.isGliding()) return;
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player && arrow.getShooter() == player) {
                event.setCancelled(true);
                arrow.remove();
                denyMsg(player);
            }
        }
    }

    private void denyMsg(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§cDésolé, cette mécanique désactivée ici."));
    }
}
