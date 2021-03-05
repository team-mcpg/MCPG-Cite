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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {
    private final TradesManager MANAGER;
    private final String GUI_EDIT_PREFIX = "§c[EDIT] ";

    public Events(TradesManager manager) { this.MANAGER = manager; }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        if (!MANAGER.TRADERS.containsKey(event.getNPC().getId())) return;
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
        if (MANAGER.TRADES.containsKey(npc.getId()) && MANAGER.TRADERS.containsKey(npc.getId())) {
            if (MANAGER.CAN_TRADE && MANAGER.TRADERS.get(npc.getId())) {
                List<VillagerTrade> trades = new ArrayList<>(MANAGER.TRADES.get(npc.getId()));
                VillagerInventory tradeGui = new VillagerInventory(trades, player);
                tradeGui.setName(npc.getName());
                tradeGui.open();
            } else {
                player.sendMessage("§cDésolé, je suis fermé pour le moment.");
            }
        }
    }

    /**
     *      Editing trades GUI
     */
    private void openTradesEdit(NPC npc, Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, GUI_EDIT_PREFIX + npc.getId());
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        player.openInventory(inv);
    }
    
    @EventHandler
    public void saveTradesEdit(InventoryCloseEvent event) {
        try {
            if (!event.getView().getTitle().startsWith(GUI_EDIT_PREFIX)) return;
            int tradesSize = event.getInventory().firstEmpty();
            if (tradesSize>8) tradesSize = 8;
            Connection connection = MainCore.getSql();
            for (int loop = 0; loop <= tradesSize; loop++) {
                if (event.getInventory().getContents()[loop+18]!=null) {
                    PreparedStatement q = connection.prepareStatement(
                            "INSERT INTO `mcpg_trades`(`npc`, `pos`, `itemOne`, `itemTwo`, `result`) " +
                                "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE `itemOne` = ?, `itemTwo` = ?, `result` = ?;");
                    q.setInt(1, Integer.parseInt(event.getView().getTitle().replace(GUI_EDIT_PREFIX, "")));
                    q.setInt(2, loop);
                    q.setString(3, Base64Item.Serialize(event.getInventory().getContents()[loop]));
                    q.setString(4, event.getInventory().getContents()[loop + 9] != null ?
                            Base64Item.Serialize(event.getInventory().getContents()[loop + 9]) : null);
                    q.setString(5, Base64Item.Serialize(event.getInventory().getContents()[loop + 18]));
                    q.setString(6, Base64Item.Serialize(event.getInventory().getContents()[loop]));
                    q.setString(7, event.getInventory().getContents()[loop + 9] != null ?
                            Base64Item.Serialize(event.getInventory().getContents()[loop + 9]) : null);
                    q.setString(8, Base64Item.Serialize(event.getInventory().getContents()[loop + 18]));
                    q.execute();
                    q.close();
                }
            }
            MANAGER.loadTrades(Integer.parseInt(event.getView().getTitle().replace(GUI_EDIT_PREFIX, "")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerTrade(VillagerTradeCompleteEvent event) {
        for (int i=0; i < event.getCount(); i++) {
            try {
                Connection connection = MainCore.getSql();
                PreparedStatement q = connection.prepareStatement("INSERT INTO `mcpg_trades_logs`" +
                        "(`uuid`, `itemOne`, `qtOne`, `itemTwo`, `qtTwo`, `itemResult`, `qtResult`) VALUES (?,?,?,?,?,?,?);");
                q.setString(1, event.getPlayer().getUniqueId().toString());
                q.setString(2, event.getTrade().getItemOne().getType().toString());
                q.setInt(3, event.getTrade().getItemOne().getAmount());
                if (event.getTrade().getItemTwo() != null) {
                    q.setString(4, event.getTrade().getItemTwo().getType().toString());
                    q.setInt(5, event.getTrade().getItemTwo().getAmount());
                } else {
                    q.setNull(4, Types.NULL);
                    q.setNull(5, Types.NULL);
                }
                q.setString(6, event.getTrade().getResult().getType().toString());
                q.setInt(7, event.getTrade().getResult().getAmount());
                q.execute();
                q.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
