package fr.milekat.MCPG_Cite.claims.utils;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.claims.classes.Region;
import fr.milekat.MCPG_Cite.claims.events.MarketEvent;
import fr.milekat.MCPG_Cite.core.classes.Team;
import fr.milekat.MCPG_Core.MainCore;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class RegionMarket extends FastInv {
    private final Player player;
    private final Team team;
    private final Region region;
    private final Sign sign;

    public RegionMarket(boolean sell, Player player, Team team, Region region, Sign sign) {
        super(InventoryType.DISPENSER, sell ? "§aAcheter cette habitation ?" : "§cVendre cette habitation ?");
        this.player = player;
        this.team = team;
        this.region = region;
        this.sign = sign;
        if (sell) {
            setItems(getBorders(), new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).build());
            setItem(4, new ItemBuilder(Material.EMERALD).addLore("Acheter", "§a" + region.getPrice()).build(),
                    inventoryClickEvent -> buyRegion());
        } else {
            setItems(getBorders(), new ItemBuilder(Material.RED_STAINED_GLASS_PANE).build());
            setItem(4, new ItemBuilder(Material.EMERALD).addLore("Vendre", "§a" +
                    Math.round((region.getPrice() * MarketEvent.FEE/100))).build(), inventoryClickEvent -> sellRegion());
        }
        open(player);
    }

    /**
     *      Application de l'achat de la région
     */
    private void buyRegion() {
        region.setTeam(team);
        team.setMoney(team.getMoney()-region.getPrice());
        sign.setLine(2,team.getName());
        sign.setLine(3, MarketEvent.SOLD);
        sign.update();
        updateTransaction();
        player.sendMessage(MainCite.PREFIX + "§6Vous venez d'acheter la région §b" + region.getName() + "§6.");
        Bukkit.getLogger().info(MainCite.PREFIX + "L'équipe " + team.getName() + " achète la région " + region.getName());
    }

    /**
     *      Application de la vente de la région, revente à RESELLPERCENT% du prix de base
     */
    private void sellRegion() {
        region.setTeam(null);
        team.setMoney(team.getMoney()+Math.round((region.getPrice() * MarketEvent.FEE/100)));
        sign.setLine(2,"§6Prix§c: §2" + region.getPrice());
        sign.setLine(3, MarketEvent.SELL);
        sign.update();
        updateTransaction();
        player.sendMessage(MainCite.PREFIX + "§6Vous venez de vendre la région §b" + region.getName() + "§6.");
        Bukkit.getLogger().info(MainCite.PREFIX + "L'équipe " + team.getName() + " vends la région " + region.getName());
    }

    /**
     *      Mise à jour de l'argent de l'équipe & de l'équipe titulaire de la région
     */
    private void updateTransaction() {
        Connection connection = MainCore.getSql();
        try {
            PreparedStatement q = connection.prepareStatement("UPDATE `mcpg_regions` SET `team_id` = ? WHERE `rg_id` = ?;" +
                    "UPDATE `mcpg_team` SET `money` = ? WHERE `team_id` = ?;");
            if (region.getTeam()==null) {
                q.setNull(1, Types.NULL);
            } else {
                q.setInt(1, region.getTeam().getId());
            }
            q.setInt(2, region.getId());
            q.setInt(3, team.getMoney());
            q.setInt(4, team.getId());
            q.execute();
            q.close();
        } catch (SQLException throwables) {
            player.sendMessage(MainCite.PREFIX + "§cErreur internet, contact le staff.");
            Bukkit.getLogger().warning("Erreur d'update SQL suite à transaction.");
            Bukkit.getLogger().warning("Data[region:" +region.getId()+",team:"+team.getId()+",money:"+team.getMoney()+"].");
            throwables.printStackTrace();
        }
    }
}
