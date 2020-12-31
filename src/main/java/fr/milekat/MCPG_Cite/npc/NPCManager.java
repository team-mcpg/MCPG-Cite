package fr.milekat.MCPG_Cite.npc;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.npc.commands.CmdNPC;
import fr.milekat.MCPG_Cite.npc.events.PlayerJoin;
import fr.milekat.MCPG_Cite.npc.object.NpcDao;
import fr.milekat.MCPG_Cite.npc.object.NpcProperties;
import fr.milekat.MCPG_Core.MainCore;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;

public class NPCManager {
    public static ArrayList<NpcProperties> npcs = new ArrayList<>();
    private final NpcDao npcdao;

    public NPCManager(JavaPlugin plugin) {
        //  Register NPC class into MongoDB
        Morphia morphia = new Morphia();
        morphia.map(NpcProperties.class);
        Datastore datastore = morphia.createDatastore(MainCore.getMongoDB().getMongoClient(), "minecraft");
        datastore.ensureIndexes();
        npcdao = new NpcDao(NpcProperties.class, datastore);
        //  Load npcs ArrayList
        loadNPCs();
        //  Register /npc command
        plugin.getCommand("npc").setExecutor(new CmdNPC(this));
        //  Show NPC on login
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoin(), plugin);
    }

    /**
     *      Load all NPC
     */
    public void loadNPCs() {

        npcs.addAll(getNPC());
        if (npcs.size() < 1) {
            Bukkit.getLogger().warning(MainCite.prefix + "No NPC where loaded.");
            return;
        }
        setNPCsetup(npcs);
    }

    /**
     *      Remove all NPC from the server
     */
    public void destroyNPCs() {
        for (NpcProperties npc : npcs) {
            if (npc.getNpc().isCreated()) {
                npc.getNpc().destroy();
            }
        }
        npcs.clear();
    }

    /**
     *      Load / reload a specific NPC from MongoDB
     */
    public NpcProperties getNPC(Integer id) {
        return npcdao.findOne("id", id);
    }

    /**
     *      Load all NPC from MongoDB storage
     *      @return NPC loaded count
     */
    public ArrayList<NpcProperties> getNPC() {
        return new ArrayList<>(npcdao.find().asList());
    }

    /**
     *      Save a NPC object into MongoDB
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
     *      Init the NPC (Position, Name, etc..)
     */
    public void setNPCsetup(NpcProperties npc) {
        if (npc.getNpc()!=null && npc.getNpc().isCreated()) {
            npc.getNpc().destroy();
        }
        npc.setNpc(MainCore.getNPCLib().createNPC(npc.getNames()));
        npc.getNpc().setLocation(npc.getLocation());
        MineSkinFetcher.fetchSkinFromIdAsync(npc.getSkinId(), skin -> npc.getNpc().setSkin(skin));
        npc.getNpc().create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (npc.isVisible() && !npc.getNpc().isShown(player)) {
                npc.getNpc().show(player);
            } else if (npc.getNpc().isShown(player)) {
                npc.getNpc().hide(player);
            }
        }
    }

    /**
     *      Init all things of the NPC (Position, Name, etc..)
     */
    public void setNPCsetup(ArrayList<NpcProperties> npcs) {
        for (NpcProperties npc : npcs) {
            setNPCsetup(npc);
        }
    }
}
