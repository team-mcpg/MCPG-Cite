package fr.milekat.MCPG_Cite.frozen;

import org.bukkit.plugin.java.JavaPlugin;

public class FrozenManager {
    public FrozenManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new FrozenEvents(), plugin);
        plugin.getCommand("frozen").setExecutor(new FrozenCmd());
    }
}
