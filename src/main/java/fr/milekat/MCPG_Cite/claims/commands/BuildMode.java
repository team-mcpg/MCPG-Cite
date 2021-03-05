package fr.milekat.MCPG_Cite.claims.commands;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuildMode implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (Boolean.parseBoolean(args[0])) {
                ClaimManager.BUILDER.add((Player) sender);
                sender.sendMessage(MainCite.PREFIX + "§6Mode build §2Activé§6.");
            } else {
                ClaimManager.BUILDER.remove((Player) sender);
                sender.sendMessage(MainCite.PREFIX + "§6Mode build §cDésactivé§6.");
            }
        } catch (Exception ignore) {
            sendHelp(sender, label);
        }
        return true;
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("§6/" + lbl + " help:§r Obtenir de l'aide sur la commande.");
        sender.sendMessage("§6/" + lbl + " <true/false>:§r Active / Désactive le mode builder.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arg1 = new ArrayList<>(Arrays.asList("true", "false", "help"));
        if (args[0].length() < 1) {
            return arg1;
        } else if (args.length <= 1) {
            for (String cmd : arg1) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    return new ArrayList<>(Collections.singletonList(cmd));
                }
            }
        }
        return null;
    }
}
