package fr.milekat.MCPG_Cite.frozen;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FrozenCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("tools")) {
            Player player = (Player) sender;
            if (ClaimManager.BUILDER.contains(player) && player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getInventory().setItem(1, new ItemBuilder(Material.IRON_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(2, new ItemBuilder(Material.GOLDEN_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(3, new ItemBuilder(Material.DIAMOND_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(5, new ItemBuilder(Material.IRON_PICKAXE).addLore("Frozen").build());
                player.getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_PICKAXE).addLore("Frozen").build());
                player.getInventory().setItem(7, new ItemBuilder(Material.DIAMOND_PICKAXE).addLore("Frozen").build());
            } else sender.sendMessage(MainCite.PREFIX + "§cVous devez être en /builder + créatif !");
        } else sendHelp(sender, label);
        return true;
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("§6/" + lbl + " help:§r Obtenir de l'aide sur la commande.");
        sender.sendMessage("§6/" + lbl + " tools:§r Add all tools in your inventory.");
    }
}
