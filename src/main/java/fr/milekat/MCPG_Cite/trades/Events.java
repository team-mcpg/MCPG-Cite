package fr.milekat.MCPG_Cite.trades;

import fr.milekat.MCPG_Cite.utils.Base64Item;
import fr.milekat.MCPG_Core.MainCore;
import masecla.villager.classes.VillagerInventory;
import masecla.villager.classes.VillagerTrade;
import masecla.villager.events.VillagerTradeCompleteEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {
    private final TradesManager tradesManager;
    private final String GUIEDITPREFIX = "§c[EDIT] ";
    public Events(TradesManager tradesManager) {
        this.tradesManager = tradesManager;
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        if (event.getClicker().isSneaking() && event.getClicker().hasPermission("cite.shop.edit")) {
            openTradesEdit(event.getNPC(), event.getClicker());
        } else openTradesVillager(event.getNPC(), event.getClicker());
    }

    @EventHandler
    public void onNpcClick(NPCLeftClickEvent event) { openTradesVillager(event.getNPC(), event.getClicker()); }

    /**
     *      Open Trade Gui for player
     */
    private void openTradesVillager(NPC npc, Player player) {
        if (tradesManager.tradesMap.containsKey(npc)) {
            if (tradesManager.allowTrades) {
                List<VillagerTrade> trades = new ArrayList<>(tradesManager.tradesMap.get(npc));
                VillagerInventory tradeGui = new VillagerInventory(trades, player);
                tradeGui.setName(npc.getName());
                tradeGui.open();
            } else {
                player.sendMessage("§cDésolé, les échanges sont suspendus.");
            }
        }
    }

    /**
     *      Editing trades GUI
     */
    private void openTradesEdit(NPC npc, Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, GUIEDITPREFIX + npc.getId());
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement(
                    "SELECT `pos`, `itemOne`, `itemTwo`, `result` FROM `mcpg_trades` WHERE `npc` = ?;");
            q.setInt(1, npc.getId());
            q.execute();
            while (q.getResultSet().next()) {
                inv.setItem(q.getResultSet().getInt("pos"),
                        Base64Item.Deserialize(q.getResultSet().getString("itemOne")));
                if (q.getResultSet().getString("itemTwo")!=null) {
                    inv.setItem(q.getResultSet().getInt("pos") + 9,
                            Base64Item.Deserialize(q.getResultSet().getString("itemTwo")));
                }
                inv.setItem(q.getResultSet().getInt("pos") + 18,
                        Base64Item.Deserialize(q.getResultSet().getString("result")));
            }
            q.close();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        player.openInventory(inv);
    }
    
    @EventHandler
    public void saveTradesEdit(InventoryCloseEvent event) {
        if (!event.getView().getTitle().startsWith(GUIEDITPREFIX)) return;
        int tradesSize = event.getInventory().firstEmpty();
        if (tradesSize>8) tradesSize = 8;
        for (int loop = 0; loop <= tradesSize; loop++) {
            if (event.getInventory().getContents()[loop+18]!=null) {
                try {
                    Connection connection = MainCore.getSql();
                    PreparedStatement q = connection.prepareStatement(
                            "INSERT INTO `mcpg_trades`(`npc`, `pos`, `itemOne`, `itemTwo`, `result`) VALUES (?,?,?,?,?);");
                    q.setInt(1, Integer.parseInt(event.getView().getTitle().replaceAll(GUIEDITPREFIX, "")));
                    q.setInt(2, loop);
                    q.setString(3, Base64Item.Serialize(event.getInventory().getContents()[loop]));
                    q.setString(4, event.getInventory().getContents()[loop+9] != null ?
                            Base64Item.Serialize(event.getInventory().getContents()[loop+9]) : null);
                    q.setString(5, Base64Item.Serialize(event.getInventory().getContents()[loop+18]));
                    q.execute();
                    q.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTrade(VillagerTradeCompleteEvent event) {
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("INSERT INTO `mcpg_trades_logs`(" +
                    "`log_date`, `uuid`, `itemOne`, `qtOne`, `itemTwo`, `qtTwo`, `itemResult`, `qtResult`) " +
                    "VALUES (?,?,?,?,?,?,?,?);");
            q.setDate(1, new Date(new java.util.Date().getTime()));
            q.setString(2, event.getPlayer().getUniqueId().toString());
            q.setString(3, event.getTrade().getItemOne().getType().toString());
            q.setInt(4, event.getTrade().getItemOne().getAmount());
            q.setString(5, event.getTrade().getItemTwo().getType().toString());
            q.setInt(6, event.getTrade().getItemTwo().getAmount());
            q.setString(7, event.getTrade().getResult().getType().toString());
            q.setInt(8, event.getTrade().getResult().getAmount());
            q.execute();
            q.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
