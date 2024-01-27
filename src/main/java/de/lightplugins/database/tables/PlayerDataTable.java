package de.lightplugins.database.tables;

import de.lightplugins.master.Ashura;
import de.lightplugins.util.TableStatements;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PlayerDataTable {

    private final String tableName = "playerdata";

    public void createTable() {

        String tableStatement;
        TableStatements tableStatements = new TableStatements(Ashura.getInstance);

        // islandID = playerUUID + stage as number
        // example: 5265sd-das4f55e_1

        tableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "islandID TEXT,"
                + "kills INT"
                + ")";

        tableStatements.createTableStatement(tableStatement);
    }
}
