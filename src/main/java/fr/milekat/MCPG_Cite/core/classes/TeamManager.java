package fr.milekat.MCPG_Cite.core.classes;

import fr.milekat.MCPG_Core.MainCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class TeamManager {

    /**
     * Retrieve team of player
     */
    public static Team getTeam(int id) throws SQLException, IllegalArgumentException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT t.`team_id`, t.`name`, t.`money`, r.`rg_name` " +
                "FROM `mcpg_team` AS t LEFT JOIN `mcpg_region` AS r ON t.`team_id` = r.`team_id` WHERE t.`team_id` = ?;");
        q.setInt(1, id);
        return setTeam(connection, q);
    }

    /**
     * Retrieve team of player
     */
    public static Team getTeam(OfflinePlayer player) throws SQLException, IllegalArgumentException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT t.`team_id`, t.`name`, t.`money`, r.`rg_name` " +
                "FROM `mcpg_team` AS t LEFT JOIN `mcpg_region` AS r ON t.`team_id` = r.`team_id` WHERE t.`team_id` = " +
                "(SELECT `team_id` FROM `mcpg_player` WHERE `uuid` = '" + player.getUniqueId() + "');");
        return setTeam(connection, q);
    }

    /**
     * Set team from SQL team select
     */
    private static Team setTeam(Connection connection, PreparedStatement q) throws SQLException {
        q.execute();
        q.getResultSet().next();
        Team team = new Team(q.getResultSet().getInt("team_id"), q.getResultSet().getString("name"),
                q.getResultSet().getInt("money"), new ArrayList<>(), q.getResultSet().getString("rg_name"));
        q.close();
        q = connection.prepareStatement("SELECT `uuid` FROM `mcpg_player` WHERE `team_id` = ?;");
        q.setInt(1, team.getId());
        q.execute();
        while (q.getResultSet().next()) {
            team.addMembers(Bukkit.getServer().getOfflinePlayer(UUID.fromString(q.getResultSet().getString("uuid"))));
        }
        q.close();
        return team;
    }

    public static void setMoney(Player player, int money) throws SQLException {
        PreparedStatement q = MainCore.getSql().prepareStatement(
                "UPDATE `mcpg_team` SET `money` = `money` + ? WHERE `team_id` = " +
                "(SELECT `team_id` FROM `mcpg_player` WHERE `uuid` = '" + player.getUniqueId() + "');" +
                "INSERT INTO `mcpg_transactions`(`uuid`, `amount`) VALUES (?,?);");
        q.setInt(1, money);
        q.setString(2, player.getUniqueId().toString());
        q.setInt(3, money);
        q.execute();
        q.close();
    }
}
