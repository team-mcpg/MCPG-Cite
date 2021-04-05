package fr.milekat.MCPG_Cite.claims.events;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Cite.claims.utils.RegionMarket;
import fr.milekat.MCPG_Cite.core.classes.Team;
import fr.milekat.MCPG_Cite.core.classes.TeamManager;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;

public class MarketEvent implements Listener {
    public static final String PREFIX = "§7[§bLa Cité Givrée§7]";
    public static final String BUY = "§b*§aà vendre§b*";
    public static final String SELL = "§b*§cVendu§b*";
    public static final float FEE = 80;

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock()==null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!sign.getLine(0).equalsIgnoreCase(PREFIX)) return;
        event.setCancelled(true);
        try {
            Team team = TeamManager.getTeam(event.getPlayer());
            Region region = ClaimManager.REGIONS.get(sign.getLine(1));
            if (sign.getLine(3).equalsIgnoreCase(BUY)) {
                if (team.getRegion()!=null) {
                    event.getPlayer().sendMessage(MainCite.PREFIX + "§cVous avez déjà une habitation !");
                    return;
                }
                if (team.getMoney() >= region.getPrice()) {
                    if (event.getPlayer().isSneaking()) {
                        new RegionMarket(true, event.getPlayer(), team, region);
                    } else {
                        event.getPlayer().sendMessage(MainCite.PREFIX + "§6Sneak pour acheter cette habitation !");
                    }
                } else {
                    event.getPlayer().sendMessage(MainCite.PREFIX + "§cVotre équipe n'a pas assez d'argent.");
                }
            } else if (sign.getLine(3).equalsIgnoreCase(SELL)) {
                if (team.getId() == region.getTeam().getId()) {
                    if (event.getPlayer().isSneaking()) {
                        new RegionMarket(false, event.getPlayer(), team, region);
                    } else {
                        event.getPlayer().sendMessage(MainCite.PREFIX + "§6Sneak pour vendre cette habitation !");
                    }
                } else {
                    event.getPlayer().sendMessage(MainCite.PREFIX + "§cVotre équipe n'est pas propriétaire de la région.");
                }
            }
        } catch (SQLException throwable) {
            event.getPlayer().sendMessage(MainCite.PREFIX + "§cErreur internet, contact le staff.");
            throwable.printStackTrace();
        }
    }
}
