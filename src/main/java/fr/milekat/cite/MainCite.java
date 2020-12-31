package fr.milekat.cite;

import fr.milekat.cite.npc.NPCManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    public static String Prefix = "[MCPG-Cite] ";
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        npcManager = new NPCManager(this);
    }

    @Override
    public void onDisable() {
        npcManager.saveNPC(NPCManager.npcs);
    }
}
