package fr.milekat.MCPG_Cite.core.classes;

import fr.milekat.MCPG_Core.MainCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class ProfileManager {
    /**
     * Get a player by his discord id
     */
    public static Profile getProfile(UUID uuid) throws SQLException {
        Connection connection = MainCore.getSql();
        PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_player` WHERE `uuid` = ?;");
        q.setString(1, uuid.toString());
        q.execute();
        q.getResultSet().next();
        Profile profile = getProfileFromSQL(q);
        q.close();
        return profile;
    }

    /**
     * Set Profile from SQL ResultSet
     */
    public static Profile getProfileFromSQL(PreparedStatement q) throws SQLException {
        return new Profile(q.getResultSet().getString("username"),
                q.getResultSet().getString("uuid")!=null ?
                        UUID.fromString(q.getResultSet().getString("uuid")) : null,
                q.getResultSet().getInt("team_id"),
                q.getResultSet().getTimestamp("muted")==null ? null :
                        new Date(q.getResultSet().getTimestamp("muted").getTime()));
    }
}
