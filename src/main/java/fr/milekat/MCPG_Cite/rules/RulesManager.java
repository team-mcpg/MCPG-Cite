package fr.milekat.MCPG_Cite.rules;

import fr.milekat.MCPG_Cite.rules.events.Elytra;
import org.bukkit.plugin.Plugin;

public class RulesManager {
    public RulesManager(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Elytra(), plugin);
    }
}
