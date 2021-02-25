package fr.milekat.MCPG_Cite.trades;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.Base64Item;
import fr.milekat.MCPG_Core.MainCore;
import masecla.villager.classes.VillagerTrade;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
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
    public final BiMap<NPC, List<VillagerTrade>> TRADES = HashBiMap.create();
    public boolean CANTRADE;

    /**
     *      Init of Trades features
     */
    public TradesManager(JavaPlugin plugin) {
        plugin.getCommand("shop").setExecutor(new CmdShop(this));
        plugin.getCommand("shop").setTabCompleter(new CmdShop(this));
        plugin.getServer().getPluginManager().registerEvents(new Events(this), plugin);
        try {
            loadTrades();
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning(MainCite.PREFIX + "Unable to load Trades from SQL.");
            throwables.printStackTrace();
        }
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
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_trades` WHERE `npc` = ? ORDER BY `pos`;");
        q.setInt(1, npc_id);
        q.execute();
        NPC npc = CitizensAPI.getNPCRegistry().getById(npc_id);
        if (npc==null) return;
        this.TRADES.remove(npc);
        while (q.getResultSet().next()) {
            VillagerTrade villagerTrade = new VillagerTrade(
                    Base64Item.Deserialize(q.getResultSet().getString("itemOne")),
                    q.getResultSet().getString("itemTwo")==null ? null :
                            Base64Item.Deserialize(q.getResultSet().getString("itemTwo")),
                    Base64Item.Deserialize(q.getResultSet().getString("result")),
                    9999);
            List<VillagerTrade> trades = TRADES.getOrDefault(npc, new ArrayList<>());
            trades.add(villagerTrade);
            this.TRADES.put(npc, trades);
        }
        q.close();
    }

    /**
     *      Save / Update all trades from a NPC in MariaDB
     */
    public void saveTrade(NPC npc) throws SQLException {
        if (TRADES.containsKey(npc)) {
            ArrayList<VillagerTrade> trades = new ArrayList<>(TRADES.get(npc));
            StringBuilder queryBuilder = new StringBuilder();
            int loop = 1;
            for (VillagerTrade trade : trades) {
                queryBuilder.append("INSERT INTO `mcpg_trades`(`npc`, `pos`, ")
                        .append(trade.getItemTwo()!=null ? "`itemOne`, " : "")
                        .append("`itemTwo`, `result`) VALUES (")
                        .append(npc.getId()).append(loop)
                        .append(Base64Item.Serialize(trade.getItemOne()))
                        .append(trade.getItemTwo()!=null ? Base64Item.Serialize(trade.getItemTwo()) : null)
                        .append(Base64Item.Serialize(trade.getResult()))
                        .append(") ON DUPLICATE KEY UPDATE")
                        .append(" `itemOne` = '")
                        .append(Base64Item.Serialize(trade.getItemOne())).append("'")
                        .append(", `result` = '")
                        .append(Base64Item.Serialize(trade.getResult())).append("'")
                        .append(", `itemTwo` = '")
                        .append(trade.getItemTwo()!=null ? Base64Item.Serialize(trade.getItemTwo()) : null)
                        .append("';");
                loop++;
            }
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement(queryBuilder.toString());
            q.execute();
            q.close();
        }
    }

    /**
     *      Save all trades into MariaDB !
     */
    public void saveTrades() throws SQLException {
        for (NPC npc : TRADES.keySet()) {
            saveTrade(npc);
        }
    }
}
