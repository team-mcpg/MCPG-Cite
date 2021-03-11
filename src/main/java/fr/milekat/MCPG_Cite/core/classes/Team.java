package fr.milekat.MCPG_Cite.core.classes;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

public class Team {
    private final int id;
    private final String name;
    private int money;
    private final ArrayList<OfflinePlayer> members;
    private final String region;

    public Team(int id, String name, int money, ArrayList<OfflinePlayer> members, String region) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.members = members;
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<OfflinePlayer> getMembers() {
        return members;
    }

    public int getSize() {
        return this.members.size();
    }

    public void addMembers(OfflinePlayer member) {
        this.members.add(member);
    }

    public String getRegion() {
        return region;
    }
}
