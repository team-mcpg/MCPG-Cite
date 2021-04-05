package fr.milekat.MCPG_Cite.frozen;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrozenUtils {
    /**
     * Process changes from the new Frozen Phase !
     */
    public static void newFrozenPhase(int phase) throws SQLException {
        Bukkit.getLogger().info(MainCite.PREFIX + "Nouvelle phase de dégèle, phase: " + phase);
        /* Step 1 set all blocks */
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement(
                "SELECT `location`, `material` FROM `mcpg_phases_blocks` WHERE `phase` = ?;");
        q.setInt(1, phase);
        q.execute();
        while (q.getResultSet().next()) {
            Material material = Material.getMaterial(q.getResultSet().getString("material"));
            if (material==null) material = Material.AIR;
            McTools.getFullLocation(q.getResultSet().getString("location")).getBlock().setType(material);
        }
        /* Update NPC trades */
        MainCite.getTrades().loadTrades();
    }
}
