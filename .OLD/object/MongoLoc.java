package fr.milekat.MCPG_Cite.utils.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;

@Embedded
public class MongoLoc {
    @Indexed
    private String world;
    private Double x;
    private Double y;
    private Double z;
    private Float yaw;
    private Float pitch;

    /**
     *      For MongoDB load
     */
    public MongoLoc() { }

    /**
     *      Serialized location for MongoDB save
     */
    public MongoLoc(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
    }
}
