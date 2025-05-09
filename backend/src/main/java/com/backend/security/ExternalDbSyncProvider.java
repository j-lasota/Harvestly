package com.backend.security;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class ExternalDbSyncProvider implements EventListenerProvider {
    private final String _realmId = "your realm uuid";
    private final String _dbUrl = "jdbc:postgresql://0.0.0.0:5432/<db name>";
    private final String _dbUser = "postgres";
    private final String _dbPassword = "your password";
    private final String _dbSql = "INSERT INTO user_ (user_id, first_name, last_name, email, username) VALUES (?,?,?,?,?)";

    public ExternalDbSyncProvider() {};

    @Override
    public void onEvent(Event event) {
        // print statement for debug, remove after testing
        System.out.println("EVENT DETECTED!");

        // just use once to get the uuid of your realm.
        System.out.println(event.getRealmId());

        /*  It is important to check the realm id first to make sure we aren't
            adding other realms' users to our database. To support the same
            behavior for other realms, do the same thing and get their realm id
            and then add your custom logic under a new if statement for that
            realm id.
        */
        if (event.getRealmId().equals(_realmId) && event.getType() == EventType.REGISTER) {
            // print statement for debug, remove after testing
            System.out.println("EVENT REGISTER IN REALM DETECTED!");

            try (Connection conn = DbConnect();
                 var pstmt = conn.prepareStatement(_dbSql)) {
                pstmt.setObject(1, UUID.fromString(event.getUserId()));
                pstmt.setString(2, event.getDetails().get("first_name"));
                pstmt.setString(3, event.getDetails().get("last_name"));
                pstmt.setString(4, event.getDetails().get("email"));
                pstmt.setString(5, event.getDetails().get("username"));

                int affectedRows = pstmt.executeUpdate();
                // print statement for debug, remove after testing
                System.out.println("Inserted " + affectedRows + " row(s)");
            }
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {}

    @Override
    public void close() {}

    private Connection DbConnect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(_dbUrl, _dbUser, _dbPassword);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
