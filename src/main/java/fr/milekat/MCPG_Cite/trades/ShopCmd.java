package fr.milekat.MCPG_Cite.trades;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShopCmd implements TabExecutor {
    private final TradesManager manager;

    public ShopCmd(TradesManager manager) { this.manager = manager; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        try {
            if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
                addShop(sender, Integer.parseInt(args[1]));
            } else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
                updateShop(sender, Integer.parseInt(args[1]), true);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
                updateShop(sender, Integer.parseInt(args[1]), false);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("lock")) {
                manager.CAN_TRADE = Boolean.parseBoolean(args[1]);
                sender.sendMessage(MainCite.PREFIX + "Trades définis sur " + manager.CAN_TRADE);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                manager.loadTrades();
                sender.sendMessage(MainCite.PREFIX + "Trades updated !");
            } else sendHelp(sender, label);
        } catch (SQLException throwables) {
            sender.sendMessage(MainCite.PREFIX + "§cSQL error, check console.");
            sender.sendMessage(throwables.getMessage());
            throwables.printStackTrace();
        } catch (NumberFormatException throwables) {
            sender.sendMessage(MainCite.PREFIX + "§cErreur de format.");
            sendHelp(sender, label);
        }
        return true;
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage("§6/" + lbl + " help:§r Obtenir de l'aide sur la commande.");
        sender.sendMessage("§6/" + lbl + " add <NPC id>:§r Ajoute le NPC à la liste des shops.");
        sender.sendMessage("§6/" + lbl + " enable <NPC id>:§r Active les échanges du NPC.");
        sender.sendMessage("§6/" + lbl + " disable <NPC id>:§r Désactive les échanges du NPC.");
        sender.sendMessage("§6/" + lbl + " lock <true/false>:§r Vérrouille les échanges.");
        sender.sendMessage("§6/" + lbl + " reload:§r Recharge tous les échanges du SQL.");
    }

    /**
     * Add a NPC to SQL register
     */
    private void addShop(CommandSender sender, Integer npc) {
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("INSERT INTO `mcpg_trades_npc`(`npc_id`) VALUES (?);");
            q.setInt(1, npc);
            q.execute();
            q.close();
            manager.TRADERS.put(npc, false);
            sender.sendMessage(MainCite.PREFIX + "§aShop ajouté §e#" + npc + "§a.");
        } catch (SQLException throwables) {
            sender.sendMessage(MainCite.PREFIX + "§cErreur SQL, le NPC existe déjà ? §4" + throwables.getMessage());
        }
    }

    /**
     * Enable / Disable the shop
     */
    private void updateShop(CommandSender sender, Integer npc, Boolean enable) {
        try {
            if (!manager.TRADERS.containsKey(npc)) {
                sender.sendMessage(MainCite.PREFIX + "§cLe NPC n'existe pas ?");
                return;
            }
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("UPDATE `mcpg_trades_npc` SET `enable`=? WHERE `npc_id` = ?;");
            q.setBoolean(1, enable);
            q.setInt(2, npc);
            q.execute();
            q.close();
            manager.TRADERS.put(npc, enable);
            sender.sendMessage(MainCite.PREFIX + "§aShop §e#" + npc + " " + (enable ? "§2Activé" : "§cDésactivé") + "§a.");
        } catch (Exception throwables) {
            sender.sendMessage(MainCite.PREFIX + "§cErreur SQL. §4" + throwables.getMessage());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> arg1 = new ArrayList<>(Arrays.asList("help", "add", "enable", "disable", "lock", "reload"));
        if (args[0].length() < 1) {
            return arg1;
        } else if (args.length <= 1) {
            for (String cmd : arg1) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    return new ArrayList<>(Collections.singletonList(cmd));
                }
            }
        } else if (args.length <= 2) {
            if (args[0].equalsIgnoreCase("lock")) {
                return McTools.getBool(args[1]);
            } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
                return manager.TRADERS.keySet().stream().map(Object::toString).collect(Collectors.toList());
            }
        }
        return null;
    }
}
