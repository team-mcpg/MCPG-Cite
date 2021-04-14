package fr.milekat.MCPG_Cite.claims.events;

import fr.milekat.MCPG_Cite.utils.McTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MageTower implements Listener {
    private final World WORLD = Bukkit.getWorld("world");
    @EventHandler
    public void onTowerUpStair(PlayerMoveEvent event) {
        if (event.getTo()==null) return;
        Location pLoc = event.getTo();
        if (event.getPlayer().isSneaking()) return;
        if (!(event.getTo().getY() > event.getFrom().getY())) return;
        Location max = new Location(WORLD, 105, 84, 7);
        Location min = new Location(WORLD, 103, 81, 5);
        if (McTools.inArea(pLoc, max, min)) {
            pLoc.setY(pLoc.getY() + 32.5);
            event.getPlayer().teleport(pLoc);
        }
    }

    @EventHandler
    public void onTowerDownStair(PlayerMoveEvent event) {
        if (event.getTo()==null) return;
        Location pLoc = event.getTo();
        if (event.getPlayer().isSneaking()) return;
        if (!(event.getTo().getY() < event.getFrom().getY())) return;
        Location max = new Location(WORLD, 103, 115, 7);
        Location min = new Location(WORLD, 100, 112, 4);
        if (McTools.inArea(pLoc, max, min)) {
            pLoc.setY(pLoc.getY() - 32.5);
            event.getPlayer().teleport(pLoc);
        }
    }
}
