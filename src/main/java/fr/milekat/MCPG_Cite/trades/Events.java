package fr.milekat.MCPG_Cite.trades;

import masecla.villager.classes.VillagerInventory;
import masecla.villager.classes.VillagerTrade;
import masecla.villager.events.VillagerInventoryModifyEvent;
import masecla.villager.events.VillagerTradeCompleteEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {
    private final TradesManager tradesManager;
    public Events(TradesManager tradesManager) {
        this.tradesManager = tradesManager;
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) { openTrade(event.getNPC(), event.getClicker()); }

    @EventHandler
    public void onNpcClick(NPCLeftClickEvent event) { openTrade(event.getNPC(), event.getClicker()); }

    /**
     *      Open Trade Gui for player
     */
    private void openTrade(NPC npc, Player player) {
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

    @EventHandler
    public void onPlayerTrade(VillagerTradeCompleteEvent event) {

    }
}
