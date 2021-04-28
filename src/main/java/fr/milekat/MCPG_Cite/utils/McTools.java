package fr.milekat.MCPG_Cite.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class McTools {
    public static String getArgs(Integer skipped_args, String... args) {
        StringBuilder sb = new StringBuilder();
        int loop = 1;
        for (String string : args) {
            if (loop <= skipped_args) continue;
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
     * Check if loc is between 2 locations
     */
    public static boolean inArea(Location loc, Location max, Location min) {
        //  X
        if (loc.getX() > max.getX() || loc.getX() < min.getX()) return false;
        //  Y
        if (loc.getY() > max.getY() || loc.getY() < min.getY()) return false;
        //  Z
        return !(loc.getZ() > max.getZ() || loc.getZ() < min.getZ());
    }

    /**
     * Get how many the player has this item stack
     */
    public static int getAmount(Player player, ItemStack item) {
        int has = 0;
        for (ItemStack loopItem : player.getInventory().getContents()) {
            if ((loopItem != null) && (loopItem.isSimilar(item)) && (loopItem.getAmount() > 0)) {
                has += loopItem.getAmount();
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

    /**
     * Send player to server
     */
    public static void sendPlayerToServer(Plugin plugin, Player player, String server) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
