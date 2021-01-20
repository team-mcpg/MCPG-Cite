package fr.milekat.MCPG_Cite.trades;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdShop implements TabExecutor {
    private TradesManager tradesManager;
    public CmdShop(TradesManager tradesManager) {
        this.tradesManager = tradesManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sendHelp(sender, label);
        return true;
    }

    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("/" + lbl + " help : Obtenir de l'aide sur la commande.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if ("help".startsWith(args[0].toLowerCase())) {
                return new ArrayList<>(Collections.singletonList("help"));
            }
        }
        return null;
    }
}
