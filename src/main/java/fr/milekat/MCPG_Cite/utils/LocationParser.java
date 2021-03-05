package fr.milekat.MCPG_Cite.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationParser {
    /**
     * Converts a location to a simple string representation
     * If location is null, returns empty string
     * @param l la loc à convertir
     * @return Loc en String
     */
    public static String getFullString(Location l) {
        if (l == null || l.getWorld() == null) {
            return null;
        }
        return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is empty
     * @param s - serialized location in format "world:x:y:z"
     * @return Location
     */
    public static Location getFullLocation(String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return null;
        }
        String[] loc = s.split(":");
        if (loc.length == 4) {
            return new Location(Bukkit.getServer().getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
        }
        return null;
    }

    public static String getString(Location l) {
        if (l == null) {
            return null;
        }
        return l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is empty
     * @param world - the world name
     * @param loc - serialized location in format "x:y:z"
     * @return Location
     */
    public static Location getLocation(String world, String loc) {
        return getFullLocation(world + ":" + loc);
    }
}
