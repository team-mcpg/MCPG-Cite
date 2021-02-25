package fr.milekat.MCPG_Cite.utils;

import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class Base64Item {
    /**
     * Get an Base64 string of an ItemStack
     */
    public static String Serialize(ItemStack item) {
        try {
            return new StreamSerializer().serializeItemStack(item);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Get an ItemStack from Base64 string
     */
    public static ItemStack Deserialize(String data) {
        try {
            return new StreamSerializer().deserializeItemStack(data);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
