package fr.milekat.MCPG_Cite.core;

import fr.milekat.MCPG_Cite.core.commands.MegaEmeraldCmd;
import fr.milekat.MCPG_Cite.core.events.*;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreManager {
        public CoreManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new DamageModifiers(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MegaEmerald(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new JoinMessage(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MuteSign(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FrozenTools(), plugin);
        plugin.getCommand("mega").setExecutor(new MegaEmeraldCmd());
    }
}
