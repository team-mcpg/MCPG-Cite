package fr.milekat.MCPG_Cite.claims.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SendEvent implements Listener {
    private final Plugin MAIN;
    private final HashMap<UUID, Long> COOL_DOWN = new HashMap<>();
    public SendEvent(Plugin plugin) {
        this.MAIN = plugin;
    }

    @EventHandler
    public void onLinkClick(NPCRightClickEvent event) {
        if (!(event.getNPC().getId() == 40)) return;
        if (COOL_DOWN.containsKey(event.getClicker().getUniqueId())) {
            if ((System.currentTimeMillis() - COOL_DOWN.get(event.getClicker().getUniqueId())) < 2000L) {
                return;
            }
        }
        COOL_DOWN.remove(event.getClicker().getUniqueId());
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("SELECT `value` FROM `mcpg_config` WHERE `name` = ?;");
            q.setString(1, "EVENT");
            q.execute();
            q.getResultSet().next();
            if (!q.getResultSet().getBoolean("value")) {
                event.getClicker().sendMessage(MainCite.PREFIX + "§cPas d'event en cours, reviens plus tard !");
                COOL_DOWN.put(event.getClicker().getUniqueId(), System.currentTimeMillis());
                return;
            }
            McTools.sendPlayerToServer(MAIN, event.getClicker(), "event");
        } catch (SQLException | IOException throwable) {
            event.getClicker().sendMessage(MainCite.PREFIX + "§cErreur interne, contact le staff");
            throwable.printStackTrace();
        }
    }
}
