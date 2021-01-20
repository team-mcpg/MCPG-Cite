package fr.milekat.MCPG_Cite.npc.object;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class NpcDao extends BasicDAO<NpcProperties, String> {

    public NpcDao(Class<NpcProperties> entityClass, Datastore ds) {
        super(entityClass, ds);
    }
}
