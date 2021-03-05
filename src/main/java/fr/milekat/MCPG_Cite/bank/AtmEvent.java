package fr.milekat.MCPG_Cite.bank;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AtmEvent implements Listener {
    private final int BANKER = 1;

    @EventHandler
    public void onOpenAtm(NPCLeftClickEvent event) {
        if (event.getNPC().getId() == BANKER) openAtm(event.getClicker());
    }

    @EventHandler
    public void onOpenAtm(NPCRightClickEvent event) {
        if (event.getNPC().getId() == BANKER) openAtm(event.getClicker());
    }

    private void openAtm(Player player) { new AtmGui().open(player); }
}
