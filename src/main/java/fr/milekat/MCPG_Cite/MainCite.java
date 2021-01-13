package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.npc.NPCManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    public static String prefix = "[MCPG-Cite] ";
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        npcManager = new NPCManager(this);
    }

    @Override
    public void onDisable() {
        npcManager.destroyNPCs();
    }
}
