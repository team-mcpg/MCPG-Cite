package fr.milekat.MCPG_Cite.claims.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.utils.McTools;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldProtect implements Listener {
    private void denyMsg(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§cDésolé, vous ne pouvez pas effectuer cette action ici."));
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceWater(PlayerBucketEmptyEvent event) {
        if (!ClaimManager.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onWaterTake(PlayerBucketFillEvent event) {
        if (!ClaimManager.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!ClaimManager.canBuild(event.getBlockPlaced().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!ClaimManager.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta!=null && itemMeta.isUnbreakable()) {
            event.setCancelled(true);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§cDésolé, le Hammer ne fonctionne pas ici."));
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock()==null || event.getHand()!=null && event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (player.hasPermission("modo.claim.event.checkclaim") &&
                player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) return;
        if (!event.getClickedBlock().getType().isInteractable()) return;
        if (block.getType().equals(Material.ENDER_CHEST)) return;
        if (block.getType().equals(Material.CRAFTING_TABLE)) return;
        if (block.getBlockData() instanceof Stairs) return;
        if (block.getState() instanceof Sign) return;
        if (!ClaimManager.canInteract(block.getLocation(), player)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (event.getHand() == EquipmentSlot.HAND) {
                denyMsg(player);
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if ((event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) && event.getTo()!=null) {
            if (!ClaimManager.canTeleportHere(event.getPlayer().getLocation(), event.getPlayer()) ||
                    !ClaimManager.canTeleportHere(event.getTo(), event.getPlayer())) {
                event.setCancelled(true);
                denyMsg(event.getPlayer());
            }
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY)) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (!ClaimManager.canInteract(event.getPlayer().getLocation().getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event){
        if (!event.getPlayer().getOpenInventory().getType().equals(InventoryType.MERCHANT)) {
            if (!ClaimManager.BUILDER.contains(event.getPlayer())) {
                event.setCancelled(true);
                denyMsg(event.getPlayer());
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemFrameDestroy(HangingBreakEvent event){
        if (event.getCause().equals(HangingBreakEvent.RemoveCause.OBSTRUCTION)) {
            event.setCancelled(true);
            event.getEntity().getLocation().getBlock().setType(Material.AIR);
            for (Player loopPlayer : Bukkit.getOnlinePlayers()){
                if (ClaimManager.BUILDER.contains(loopPlayer)) {
                    loopPlayer.sendMessage(MainCite.PREFIX + "§cAttention ! Il ne faut pas poser de block sur une item frame (Pos:" +
                            McTools.getFullString(event.getEntity().getLocation()) + ")");
                }
            }
        }
        else if (event.getCause().equals(HangingBreakEvent.RemoveCause.PHYSICS)) {
            event.setCancelled(true);
            for (Player loopPlayer : Bukkit.getOnlinePlayers()){
                if (ClaimManager.BUILDER.contains(loopPlayer)) {
                    loopPlayer.sendMessage(MainCite.PREFIX + "§cAttention ! Item frame volante : "
                            + McTools.getFullString(event.getEntity().getLocation()));
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemFrameBy(HangingBreakByEntityEvent event){
        if (!(event.getRemover() instanceof Player)) {
            event.setCancelled(true);
        } else if (!ClaimManager.BUILDER.contains((Player) event.getRemover())) {
            event.setCancelled(true);
            denyMsg((Player) event.getRemover());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemFromItemFrameBy(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof ItemFrame) {
            if (!(event.getDamager() instanceof Player)) {
                event.setCancelled(true);
            } else if (!ClaimManager.BUILDER.contains((Player) event.getDamager())) {
                event.setCancelled(true);
                denyMsg((Player) event.getDamager());
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onItemTurnItemFrameBy(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof ItemFrame) {
            if (!ClaimManager.BUILDER.contains(event.getPlayer())) {
                event.setCancelled(true);
                denyMsg(event.getPlayer());
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event){
        if (event.getPlayer()==null) {
            event.setCancelled(true);
        } else if (!ClaimManager.BUILDER.contains(event.getPlayer())) {
            event.setCancelled(true);
            denyMsg(event.getPlayer());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        } else if (!ClaimManager.BUILDER.contains((Player) event.getEntity())) {
            event.setCancelled(true);
            denyMsg((Player) event.getEntity());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || ClaimManager.BUILDER.contains((Player) event.getDamager())) return;
        if (!ClaimManager.canBuild(event.getEntity().getLocation(), (Player) event.getDamager())) {
            event.setCancelled(true);
            denyMsg((Player) event.getDamager());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPrimeExplode(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onSaturation(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (ClaimManager.getRegion(block.getLocation())==null ||
                    ClaimManager.getRegion(block.getLocation()).getName().equalsIgnoreCase("interact-ok")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (ClaimManager.getRegion(block.getLocation())==null ||
                    ClaimManager.getRegion(block.getLocation()).getName().equalsIgnoreCase("interact-ok")) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
