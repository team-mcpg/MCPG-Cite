package fr.milekat.MCPG_Cite.claims.classes;

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
    private final ArrayList<Location> blocks;

    public Region(int id, String name, Sign sign, int price, Team team, ArrayList<Location> blocks) {
        this.id = id;
        this.name = name;
        this.sign = sign;
        this.price = price;
        this.team = team;
        this.blocks = blocks;
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
    }

    public ArrayList<Location> getBlocks() {
        return blocks;
    }
}
