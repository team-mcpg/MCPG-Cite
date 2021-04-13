package fr.milekat.MCPG_Cite.frozen;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrozenCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length > 0 && args[0].equalsIgnoreCase("tools")) {
            if (ClaimManager.BUILDER.contains(player) && player.getGameMode().equals(GameMode.CREATIVE)) {
                player.getInventory().setItem(1, new ItemBuilder(Material.IRON_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(2, new ItemBuilder(Material.GOLDEN_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(3, new ItemBuilder(Material.DIAMOND_SHOVEL).addLore("Frozen").build());
                player.getInventory().setItem(5, new ItemBuilder(Material.IRON_PICKAXE).addLore("Frozen").build());
                player.getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_PICKAXE).addLore("Frozen").build());
                player.getInventory().setItem(7, new ItemBuilder(Material.DIAMOND_PICKAXE).addLore("Frozen").build());
            } else sender.sendMessage(MainCite.PREFIX + "§cVous devez être en /builder + créatif !");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (player.getTargetBlockExact(6) == null) {
                sender.sendMessage(MainCite.PREFIX + "§cAucun block trouvé, rapprochez vous.");
                return true;
            }
            try {
                Connection connection = MainCore.getSql();
                PreparedStatement q = connection.prepareStatement(
                        "DELETE FROM `mcpg_phases_blocks` WHERE `phase` = ? AND `location` = ?;");
                q.setInt(1, Integer.parseInt(args[1]));
                q.setString(2, McTools.getFullString(player.getTargetBlockExact(6).getLocation()));
                q.execute();
                q.close();
                player.sendMessage("§aReset de " + McTools.getFullString(player.getTargetBlockExact(6).getLocation()) + ".");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (NumberFormatException throwable) {
                sendHelp(sender, label);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("skip")) {
            try {
                Connection connection = MainCore.getSql();
                PreparedStatement q = connection.prepareStatement(
                        "SELECT MIN(phase_step) as phase FROM `mcpg_phases` WHERE `phase_date` IS NULL;" +
                        "UPDATE `mcpg_phases` SET `phase_date`=NOW() WHERE `phase_step` = " +
                        "(SELECT MIN(phase_step) FROM `mcpg_phases` WHERE `phase_date` IS NULL);");
                q.execute();
                if (q.getResultSet().next()) FrozenUtils.newFrozenPhase(q.getResultSet().getInt("phase"));
                sender.sendMessage(MainCite.PREFIX + "§6New phase: §b" + q.getResultSet().getInt("phase"));
                q.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            try {
                Connection connection = MainCore.getSql();
                PreparedStatement q = connection.prepareStatement(
                        "UPDATE `mcpg_phases` SET `phase_date`= NULL WHERE `phase_step` >= ?;");
                q.setInt(1, Integer.parseInt(args[1]));
                q.execute();
                sender.sendMessage(MainCite.PREFIX + "§Phase set to §b" + Integer.parseInt(args[1]) + "§r with no update.");
                q.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (NumberFormatException ignore) {
                sender.sendMessage(MainCite.PREFIX + "§cThe phase must be a digit between 0-9.");
            }
        } else sendHelp(sender, label);
        return true;
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("§6/" + lbl + " help:§r Obtenir de l'aide sur la commande.");
        sender.sendMessage("§6/" + lbl + " skip:§r Skip the current phase, without emeralds count.");
        sender.sendMessage("§6/" + lbl + " set <phase>:§r Redefine the phase, without emeralds count.");
        sender.sendMessage("§6/" + lbl + " tools:§r Add all tools in your inventory.");
        sender.sendMessage("§6/" + lbl + " reset <phase>:§r Reset phase action from the targeted block.");
    }
}
