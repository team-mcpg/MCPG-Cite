package fr.milekat.MCPG_Cite.trades;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Core.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdShop implements TabExecutor {
    private final TradesManager tradesManager;
    public CmdShop(TradesManager tradesManager) {
        this.tradesManager = tradesManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        try {
            if (args.length == 2 && args[0].equalsIgnoreCase("lock")) {
                tradesManager.allowTrades = Boolean.parseBoolean(args[1]);
                sender.sendMessage(MainCite.prefix + "Trades définis sur " + tradesManager.allowTrades);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                tradesManager.loadTrades();
                sender.sendMessage(MainCite.prefix + "Trades reloaded !");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("saveAll")) {
                tradesManager.saveTrades();
                sender.sendMessage(MainCite.prefix + "Trades saved !");
            } else sendHelp(sender, label);
        } catch (SQLException | IOException throwables) {
            sender.sendMessage(MainCite.prefix + "§cSQL error, check console.");
            throwables.printStackTrace();
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("/" + lbl + " help : Obtenir de l'aide sur la commande.");
        sender.sendMessage("/" + lbl + " lock <true/false> : Vérrouille les échanges.");
        sender.sendMessage("/" + lbl + " reload : Recharge tous les échanges du SQL.");
        sender.sendMessage("/" + lbl + " saveAll : Save tous les échanges sur le SQL.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> arg1 = new ArrayList<>(Arrays.asList("help", "lock", "saveAll"));
        if (args.length == 0) {
            return arg1;
        } else if (args.length == 1) {
            for (String cmd : arg1) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    return new ArrayList<>(Collections.singletonList(cmd));
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lock")) {
                return CmdUtils.getBool(args[1]);
            }
        }
        return null;
    }
}
