package fr.milekat.MCPG_Cite.bank;

import fr.milekat.MCPG_Cite.bank.events.Atm;
import org.bukkit.plugin.Plugin;

public class BankManager {
    public BankManager(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Atm(), plugin);
    }
}
