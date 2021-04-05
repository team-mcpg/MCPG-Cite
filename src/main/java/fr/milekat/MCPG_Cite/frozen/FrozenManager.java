package fr.milekat.MCPG_Cite.frozen;

import fr.milekat.MCPG_Cite.core.events.FrozenTools;
import org.bukkit.plugin.java.JavaPlugin;

public class FrozenManager {
    public FrozenManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new FrozenEvents(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FrozenTools(), plugin);
        plugin.getCommand("frozen").setExecutor(new FrozenCmd());
    }
}
