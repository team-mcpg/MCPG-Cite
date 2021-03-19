package fr.milekat.MCPG_Cite.core;

import fr.milekat.MCPG_Cite.core.commands.MegaEmeraldCmd;
import fr.milekat.MCPG_Cite.core.events.FrozenTools;
import fr.milekat.MCPG_Cite.core.events.JoinMessage;
import fr.milekat.MCPG_Cite.core.events.MegaEmerald;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreManager {
    public CoreManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new MegaEmerald(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new JoinMessage(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FrozenTools(), plugin);
        plugin.getCommand("mega").setExecutor(new MegaEmeraldCmd());
    }
}
