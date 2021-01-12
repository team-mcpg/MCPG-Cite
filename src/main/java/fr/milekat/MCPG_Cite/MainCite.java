package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.npc.NPCManager;
import fr.milekat.MCPG_Cite.npc.events.PlayerJoin;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    public static String Prefix = "[MCPG-Cite] ";
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        npcManager = new NPCManager(this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
    }

    @Override
    public void onDisable() {
        npcManager.saveNPC(NPCManager.npcs);
    }
}
