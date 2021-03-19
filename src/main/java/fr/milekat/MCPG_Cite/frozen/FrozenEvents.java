package fr.milekat.MCPG_Cite.frozen;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.core.events.BankUpdate;
import fr.milekat.MCPG_Cite.utils.McTools;
import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FrozenEvents implements Listener {
    @EventHandler (priority = EventPriority.LOW)
    public void onFrozenSetup(PlayerInteractEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.hasItemMeta()) {
            ItemMeta meta = tool.getItemMeta();
            if (meta!=null && meta.getLore()!=null && meta.getLore().contains("Frozen")) {
                event.setCancelled(true);
                if (event.getClickedBlock()==null) return;
                int phase = 1;
                Material material = null;
                switch (tool.getType()) {
                    case IRON_SHOVEL:
                    {
                        material = event.getClickedBlock().getType();
                        break;
                    }
                    case GOLDEN_SHOVEL:
                    {
                        phase = 2;
                        material = event.getClickedBlock().getType();
                        break;
                    }
                    case DIAMOND_SHOVEL:
                    {
                        phase = 3;
                        material = event.getClickedBlock().getType();
                        break;
                    }
                    case IRON_PICKAXE:
                    {
                        material = Material.AIR;
                        break;
                    }
                    case GOLDEN_PICKAXE:
                    {
                        phase = 2;
                        material = Material.AIR;
                        break;
                    }
                    case DIAMOND_PICKAXE:
                    {
                        phase = 3;
                        material = Material.AIR;
                        break;
                    }
                }
                if (material==null) return;
                try {
                    Connection connection = MainCore.getSql();
                    PreparedStatement q = connection.prepareStatement(
                            "INSERT INTO `mcpg_phases_blocks`(`phase`, `location`, `material`) VALUES (?,?,?);");
                    q.setInt(1, phase);
                    q.setString(2, McTools.getFullString(event.getClickedBlock().getLocation()));
                    q.setString(3, material.name());
                    q.execute();
                    q.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void depositListener(BankUpdate event) {
        try {
            Connection connection = MainCore.getSql();
            PreparedStatement q = connection.prepareStatement("SELECT " +
                    "(SELECT MAX(`phase_step`) FROM `mcpg_phases` WHERE `value` - (SELECT SUM(`money`) FROM `mcpg_team`) < 0)" +
                    " > (SELECT MAX(phase_step) FROM `mcpg_phases` WHERE `phase_date` IS NOT NULL) AS new_phase;");
            q.execute();
            if (q.getResultSet().next() && q.getResultSet().getBoolean("new_phase")) {
                q = connection.prepareStatement("SELECT MAX(phase_step) as phase FROM `mcpg_phases` WHERE `phase_date` IS NULL;" +
                        "UPDATE `mcpg_phases` SET `phase_date`=NOW() WHERE `phase_step` = " +
                        "(SELECT MAX(phase_step) FROM `mcpg_phases` WHERE `phase_date` IS NULL);");
                q.execute();
                if (q.getResultSet().next()) newFrozenPhase(q.getResultSet().getInt("phase"));
            }
            q.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Process changes from the new Frozen Phase !
     */
    private void newFrozenPhase(int phase) throws SQLException {
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
