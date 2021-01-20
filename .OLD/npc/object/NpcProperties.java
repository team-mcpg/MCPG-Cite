package fr.milekat.MCPG_Cite.npc.object;

import fr.milekat.MCPG_Cite.utils.object.MongoLoc;
import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.mongodb.morphia.annotations.*;

import java.util.List;

@Entity(value = "npcs", noClassnameStored = true)
public class NpcProperties {
    @Id
    private Integer id;
    @Indexed
    private List<String> names;
    private Integer skinId;
    @Embedded
    private MongoLoc mongoLoc;
    @Indexed
    private boolean visible;
    @Transient
    private NPC npc;

    /**
     *      Method for MongoDB load
     */
    public NpcProperties() {}

    public NpcProperties(int id, List<String> names, Integer skinId, Location location, boolean visible) {
        this.id = id;
        this.names = names;
        this.skinId = skinId;
        this.mongoLoc = new MongoLoc(location);
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
        return mongoLoc.getLocation();
    }

    public void setLocation(Location location) {
        this.mongoLoc = new MongoLoc(location);
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
