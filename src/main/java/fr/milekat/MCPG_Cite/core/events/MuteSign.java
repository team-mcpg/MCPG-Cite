package fr.milekat.MCPG_Cite.core.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.core.classes.Profile;
import fr.milekat.MCPG_Cite.core.classes.ProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.SQLException;

public class MuteSign implements Listener {
    @EventHandler
    public void onSignEditMuted(SignChangeEvent event) {
        try {
            Profile profile = ProfileManager.getProfile(event.getPlayer().getUniqueId());
            if (profile.isMute()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(MainCite.PREFIX + "§cVous êtes mute.");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
