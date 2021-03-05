package fr.milekat.MCPG_Cite.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class McTools {
    public static String getArgs(Integer skipedargs, String... args) {
        StringBuilder sb = new StringBuilder();
        int loop = 1;
        for (String string : args) {
            if (loop <= skipedargs) continue;
            sb.append(string);
            sb.append(" ");
            loop++;
        }
        return sb.toString();
    }

    /**
     * Get true/false from Bukkit command arg
     */
    public static ArrayList<String> getBool(String arg) {
        if ("true".startsWith(arg.toLowerCase(Locale.ROOT))) {
            return new ArrayList<>(Collections.singletonList("true"));
        } else if ("false".startsWith(arg.toLowerCase(Locale.ROOT))) {
            return new ArrayList<>(Collections.singletonList("false"));
        } else return null;
    }

    /**
     * Format list of args for Mc tab,
     */
    public static ArrayList<String> getTabArgs(String arg, List<String> MyStrings) {
        ArrayList<String> MySortStrings = new ArrayList<>();
        for(String loop : MyStrings) {
            if(loop.toLowerCase().startsWith(arg.toLowerCase()))
            {
                MySortStrings.add(loop);
            }
        }
        return MySortStrings;
    }

    /**
     * @return Location as string with format "world:x:y:z"
     */
    public static String getFullString(Location l) {
        if (l == null || l.getWorld() == null) {
            return null;
        }
        return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * @return Location as string with format "x:y:z"
     */
    public static String getString(Location l) {
        if (l == null) {
            return null;
        }
        return l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is empty.
     * @param sLoc - serialized location in format "world:x:y:z"
     */
    public static Location getFullLocation(String sLoc) {
        if (sLoc == null || sLoc.trim().equals("") || sLoc.trim().equalsIgnoreCase("null")) {
            return null;
        }
        String[] loc = sLoc.split(":");
        if (loc.length == 4) return new Location(Bukkit.getServer().getWorld(loc[0]),
                    Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
        return null;
    }

    /**
     * Converts a serialized location to a Location. Returns null if string is empty.
     * @param loc - serialized location in format "x:y:z"
     */
    public static Location getLocation(String world, String loc) { return getFullLocation(world + ":" + loc); }

    /**
     * Get how many the player has this material
     */
    public static int getAmount(Player player, Material material) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int has = 0;
        for (ItemStack item : items)
        {
            if ((item != null) && (item.getType() == material) && (item.getAmount() > 0))
            {
                has += item.getAmount();
            }
        }
        return has;
    }

    /**
     * Check if the inventory can store all items stacks
     * @param size inventory size (Chest = 27, Player = 36, etc..)
     * @param count how much items you want add (How much stacks of ItemStack)
     */
    public static boolean canStore(Inventory baseInv, int size, ItemStack items, int count) {
        Inventory inv = Bukkit.createInventory(null, size, "canStore");
        inv.setContents(baseInv.getStorageContents());
        for (int i=0; i<count; i++){
            final Map<Integer, ItemStack> map = inv.addItem(items); // Attempt to add in inventory
            if (!map.isEmpty()) return false; // If not empty, it means the player's inventory is full.
        }
        return true;
    }
}
