package fr.milekat.MCPG_Cite.claims.commands;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildMode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (ClaimManager.BUILDER.contains((Player) sender)) {
            ClaimManager.BUILDER.remove((Player) sender);
            sender.sendMessage(MainCite.PREFIX + "§6Mode build §cDésactivé§6.");
        } else {
            ClaimManager.BUILDER.add((Player) sender);
            sender.sendMessage(MainCite.PREFIX + "§6Mode build §2Activé§6.");
        }
        return true;
    }
}
