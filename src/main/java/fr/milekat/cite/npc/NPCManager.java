package fr.milekat.cite.npc;

import fr.milekat.MCPG_Core.MainCore;
import fr.milekat.cite.MainCite;
import fr.milekat.cite.npc.commands.CmdNPC;
import fr.milekat.cite.npc.object.NpcDao;
import fr.milekat.cite.npc.object.NpcProperties;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.Objects;

public class NPCManager {
    public static ArrayList<NpcProperties> npcs;
    private final NpcDao npcdao;

    public NPCManager(JavaPlugin plugin) {
        //  Register NPC class into MongoDB
        MainCore.getMongoDB().getMorphia().map(NpcProperties.class);
        Datastore datastore = MainCore.getMongoDB().getMorphia().createDatastore(MainCore.getMongoDB().getMongoClient(), "dbName");
        datastore.ensureIndexes();
        npcdao = new NpcDao(NpcProperties.class, datastore);
        //  Load npcs ArrayList
        resetNPCs();
        //  Load /npc command
        Objects.requireNonNull(plugin.getCommand("npc")).setExecutor(new CmdNPC());
    }

    /**
     * Load all NPC
     */
    public void resetNPCs() {
        npcs.clear();
        npcs.addAll(getNPC());
        if (npcs.size() < 1) {
            Bukkit.getLogger().warning(MainCite.Prefix + "No NPC where loaded.");
            return;
        }
        setNPCsetup(npcs);
    }

    /**
     * Load / reload a specific NPC from MariaDB
     */
    public NpcProperties getNPC(Integer id) {
        return npcdao.findOne("id", id);
    }

    /**
     * Load all NPC from Maria storage
     * @return NPC loaded count
     */
    public ArrayList<NpcProperties> getNPC() {
        return new ArrayList<>(npcdao.find().asList());
    }

    /**
     *      Save a NPC object into MariaDB
     */
    public void saveNPC(NpcProperties npc) {
        npcdao.save(npc);
    }

    /**
     *      Save all NPC
     */
    public void saveNPC(ArrayList<NpcProperties> npcs) {
        for (NpcProperties npc: npcs) {
            saveNPC(npc);
        }
    }

    /**
     * Init the NPC (Position, Name, etc..)
     */
    public static void setNPCsetup(NpcProperties npc) {
        npc.setNpc(MainCore.getNPCLib().createNPC(npc.getNames()));
        npc.getNpc().setLocation(npc.getLocation());
        MineSkinFetcher.fetchSkinFromIdAsync(npc.getSkinId(), skin -> npc.getNpc().setSkin(skin));
        npc.getNpc().create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (npc.isVisible()) {
                npc.getNpc().show(player);
            } else {
                npc.getNpc().hide(player);
            }
        }
    }

    /**
     * Init all things of the NPC (Position, Name, etc..)
     */
    public static void setNPCsetup(ArrayList<NpcProperties> npcs) {
        for (NpcProperties npc : npcs) {
            setNPCsetup(npc);
        }
    }
}
