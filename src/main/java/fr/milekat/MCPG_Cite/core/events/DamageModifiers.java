package fr.milekat.MCPG_Cite.core.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 *      From https://github.com/NikV2/CombatPlus/blob/master/src/main/java/me/nik/combatplus/utils/SetAttackSpeed.java
 *      From https://github.com/NikV2/CombatPlus/blob/master/src/main/java/me/nik/combatplus/listeners/DamageModifiers.java
 *      Damage copy from BedRock Edition
 */
public class DamageModifiers implements Listener {
    /**
     * Set attack speed from Minecraft Java 1.8
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        AttributeInstance pAttributes = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (pAttributes!=null) pAttributes.setBaseValue(24);
        event.getPlayer().saveData();
    }

    /**
     * This Listener Changes the Damage Dealt to All Entities to the Old Values
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        ItemStack tool = player.getInventory().getItemInMainHand();
        String weapon = tool.getType().name();
        switch (weapon) {
            //  Swords
            case "NETHERITE_SWORD":
            case "GOLDEN_SWORD":
            case "GOLD_SWORD":
            case "DIAMOND_SWORD":
            case "IRON_SWORD":
            case "STONE_SWORD":
            case "WOODEN_SWORD":
            case "GOLDEN_HOE":
            case "GOLD_HOE":
            case "WOODEN_HOE":
                damageConverter(event, tool, 1);
                break;
            //  Pickaxes
            case "NETHERITE_PICKAXE":
            case "DIAMOND_PICKAXE":
            case "GOLDEN_PICKAXE":
            case "GOLD_PICKAXE":
            case "IRON_PICKAXE":
            case "STONE_PICKAXE":
            case "WOODEN_PICKAXE":
                damageConverter(event, tool, 0);
                break;
            //  Axes
            case "NETHERITE_AXE":
            case "DIAMOND_AXE":
                damageConverter(event, tool, -2);
                break;
            case "GOLDEN_AXE":
            case "GOLD_AXE":
            case "IRON_AXE":
            case "WOODEN_AXE":
                damageConverter(event, tool, -3);
                break;
            case "STONE_AXE":
                damageConverter(event, tool, -4);
                break;
            //  Hoes (Again, the tool!)
            case "NETHERITE_HOE":
                damageConverter(event, tool, 5);
                break;
            case "DIAMOND_HOE":
                damageConverter(event, tool, 4);
                break;
            case "IRON_HOE":
                damageConverter(event, tool, 3);
                break;
            case "STONE_HOE":
                damageConverter(event, tool, 2);
                break;
            //  Shovels
            case "NETHERITE_SHOVEL":
            case "DIAMOND_SHOVEL":
            case "DIAMOND_SPADE":
            case "GOLDEN_SHOVEL":
            case "GOLDEN_SPADE":
            case "GOLD_SHOVEL":
            case "GOLD_SPADE":
            case "IRON_SHOVEL":
            case "IRON_SPADE":
            case "STONE_SHOVEL":
            case "STONE_SPADE":
            case "WOODEN_SHOVEL":
            case "WOODEN_SPADE":
                damageConverter(event, tool, -0.5);
                break;
        }
    }

    /**
     * Change Damage event with the tool modifier and Sharpness with 1.8 damages
     */
    private void damageConverter(EntityDamageByEntityEvent event, ItemStack item, double modifier) {
        double newDmg = event.getDamage() + modifier;
        if (item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            double sharpLvl = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            double oldSharpDmg = sharpLvl >= 1 ? sharpLvl * 1.25 : 0; //1.8
            double newSharpDmg = sharpLvl >= 1 ? 1 + (sharpLvl - 1) * 0.5 : 0; //1.9+
            newDmg = newDmg + oldSharpDmg - newSharpDmg;
        }
        event.setDamage(newDmg);
    }
}
