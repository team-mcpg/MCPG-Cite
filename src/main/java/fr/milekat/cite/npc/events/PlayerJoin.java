package fr.milekat.cite.npc.events;

import fr.milekat.cite.npc.NPCManager;
import fr.milekat.cite.npc.object.NpcProperties;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        for (NpcProperties npc : NPCManager.npcs){
            npc.getNpc().show(event.getPlayer());
        }
    }
}
