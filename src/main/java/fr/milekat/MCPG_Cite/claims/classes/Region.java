package fr.milekat.MCPG_Cite.claims.classes;

import fr.milekat.MCPG_Cite.claims.events.MarketEvent;
import fr.milekat.MCPG_Cite.core.classes.Team;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;

public class Region {
    private final int id;
    private final String name;
    private Sign sign;
    private int price;
    private Team team;
    private ArrayList<Location> blocks;

    public Region(int id, String name, Sign sign, int price, Team team, ArrayList<Location> blocks) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.team = team;
        this.blocks = blocks;
        if (sign != null) setSign(sign);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
        if (sign != null) {
            updateSign();
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        if (sign != null) {
            updateSign();
        }
    }

    public ArrayList<Location> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Location> blocks) {
        this.blocks = blocks;
    }

    private void updateSign() {
        sign.setLine(0, MarketEvent.PREFIX);
        sign.setLine(1, name);
        if (team != null) {
            sign.setLine(2, team.getName());
            sign.setLine(3, MarketEvent.SELL);
        } else {
            sign.setLine(2, "§6Prix§c: §2" + price);
            sign.setLine(3, MarketEvent.BUY);
        }
        sign.update();
    }
}
