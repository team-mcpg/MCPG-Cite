package fr.milekat.MCPG_Cite.core.classes;

import java.util.Date;
import java.util.UUID;

public class Profile {
    private final String name;
    private final UUID uuid;
    private final int team;
    private final Date muted;

    public Profile(String name, UUID uuid, int team, Date muted) {
        this.name = name;
        this.uuid = uuid;
        this.team = team;
        this.muted = muted;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getTeam() {
        return team;
    }

    public Date getMuted() {
        return muted;
    }

    public boolean isMute() {
        return this.muted != null;
    }
}
