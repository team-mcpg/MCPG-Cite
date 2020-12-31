package fr.milekat.cite.npc.object;

import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;

@Entity(value = "npcs", noClassnameStored = true)
public class NpcProperties {
    @Id
    private final Integer id;
    @Indexed
    private List<String> names;
    private Integer skinId;
    private Location location;
    private boolean visible;
    @Transient
    private NPC npc;

    public NpcProperties(int id, List<String> names, Integer skinId, Location location, boolean visible) {
        this.id = id;
        this.names = names;
        this.skinId = skinId;
        this.location = location;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Integer getSkinId() {
        return skinId;
    }

    public void setSkinId(Integer skinId) {
        this.skinId = skinId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }
}
