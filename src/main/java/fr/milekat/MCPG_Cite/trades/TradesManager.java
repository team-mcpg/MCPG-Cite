package fr.milekat.MCPG_Cite.trades;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.Base64Item;
import fr.milekat.MCPG_Core.MainCore;
import masecla.villager.classes.VillagerTrade;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradesManager {
    public final HashMap<Integer, Boolean> TRADERS = new HashMap<>();
    public final BiMap<Integer, List<VillagerTrade>> TRADES = HashBiMap.create();
    public boolean CAN_TRADE;

    /**
     *      Init of Trades features
     */
    public TradesManager(JavaPlugin plugin) {
        CAN_TRADE = false;
        plugin.getCommand("shop").setExecutor(new ShopCmd(this));
        plugin.getCommand("shop").setTabCompleter(new ShopCmd(this));
        plugin.getServer().getPluginManager().registerEvents(new ShopEvents(this), plugin);
        try {
            loadTrades();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Unable to load Trades from SQL.");
            throwables.printStackTrace();
        }
        CAN_TRADE = true;
    }

    /**
     *      Load all trades from MariaDB
     */
    public void loadTrades() throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_trades_npc`;");
        q.execute();
        TRADES.clear();
        TRADERS.clear();
        while (q.getResultSet().next()) {
            TRADERS.put(q.getResultSet().getInt("npc_id"), q.getResultSet().getBoolean("enable"));
            loadTrades(q.getResultSet().getInt("npc_id"));
        }
        q.close();
    }

    /**
     *      Load all trades of an npc from MariaDB
     */
    public void loadTrades(int npc_id) throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_trades` WHERE `npc` = ? " +
                "AND `phase` <= (SELECT MAX(`phase_step`) FROM `mcpg_phases`) ORDER BY `pos`;");
        q.setInt(1, npc_id);
        q.execute();
        this.TRADES.remove(npc_id);
        while (q.getResultSet().next()) {
            VillagerTrade villagerTrade = new VillagerTrade(
                    Base64Item.Deserialize(q.getResultSet().getString("itemOne")),
                    q.getResultSet().getString("itemTwo")==null ? null :
                            Base64Item.Deserialize(q.getResultSet().getString("itemTwo")),
                    Base64Item.Deserialize(q.getResultSet().getString("result")),
                    9999);
            List<VillagerTrade> trades = TRADES.getOrDefault(npc_id, new ArrayList<>());
            trades.add(villagerTrade);
            this.TRADES.put(npc_id, trades);
        }
        q.close();
    }
}
