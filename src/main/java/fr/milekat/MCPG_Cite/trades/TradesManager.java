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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradesManager {
    public final BiMap<NPC, List<VillagerTrade>> tradesMap = HashBiMap.create();
    public boolean allowTrades;

    /**
     *      Init of Trades features
     */
    public TradesManager(JavaPlugin plugin) {
        plugin.getCommand("shop").setExecutor(new CmdShop(this));
        plugin.getCommand("shop").setTabCompleter(new CmdShop(this));
        plugin.getServer().getPluginManager().registerEvents(new Events(this), plugin);
        try {
            loadTrades();
        } catch (SQLException | IOException throwables) {
            Bukkit.getLogger().warning(MainCite.prefix + "Unable to load Trades from SQL.");
            throwables.printStackTrace();
        }
    }

    /**
     *      Load all trades from MariaDB
     */
    private void loadTrades() throws SQLException, IOException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_trades` ORDER BY `npc`, `pos`;");
        q.execute();
        while (q.getResultSet().next()) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(q.getResultSet().getInt("npc"));
            VillagerTrade villagerTrade = new VillagerTrade(
                    Base64Item.Deserialize(q.getResultSet().getString("itemOne")),
                    q.getResultSet().getString("itemTwo")==null ? null :
                            Base64Item.Deserialize(q.getResultSet().getString("itemTwo")),
                    Base64Item.Deserialize(q.getResultSet().getString("result")),
                    9999);
            List<VillagerTrade> trades = tradesMap.getOrDefault(npc, new ArrayList<>());
            trades.add(villagerTrade);
            this.tradesMap.put(npc, trades);
        }
        q.close();
    }

    /**
     *      Save / Update all trades from a NPC in MariaDB
     */
    public void saveTrade(NPC npc) throws SQLException {
        if (tradesMap.containsKey(npc)) {
            ArrayList<VillagerTrade> trades = new ArrayList<>(tradesMap.get(npc));
            StringBuilder query = new StringBuilder();
            int loop = 1;
            for (VillagerTrade trade : trades) {
                query.append("INSERT INTO `mcpg_trades`(`npc`, `pos`, ")
                        .append(trade.getItemTwo()!=null ? "`itemOne`, " : "")
                        .append("`itemTwo`, `result`) VALUES (")
                        .append(npc.getId()).append(loop)
                        .append(Base64Item.Serialize(trade.getItemOne()))
                        .append(trade.getItemTwo()!=null ? Base64Item.Serialize(trade.getItemTwo()) : "")
                        .append(Base64Item.Serialize(trade.getResult()))
                        .append(") ON DUPLICATE KEY UPDATE")
                        .append(" `itemOne` = '").append(Base64Item.Serialize(trade.getItemOne())).append("'")
                        .append(", `result` = '").append(Base64Item.Serialize(trade.getResult())).append("'");
                if (trade.getItemTwo()!=null) {
                    query.append(", `itemTwo` = '").append(Base64Item.Serialize(trade.getItemTwo())).append("'");
                }
                query.append(";");
                loop++;
            }
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement(query.toString());
            q.execute();
            q.close();
        }
    }

    /**
     *      Save all trades into MariaDB !
     */
    public void saveTrades() {
        try {
            for (NPC npc : tradesMap.keySet()) {
                saveTrade(npc);
            }
        } catch (SQLException throwables) {
            Bukkit.getLogger().warning("Unable to save all NPC");
            throwables.printStackTrace();
        }
    }

    /**
     *      Allow/Disallow players from interact with Villagers
     */
    public void canTrades(boolean trade) {
        this.allowTrades = trade;
    }
}
