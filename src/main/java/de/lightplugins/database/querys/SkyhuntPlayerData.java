package de.lightplugins.database.querys;


/*
 * ----------------------------------------------------------------------------
 *  This software and its source code, including text, graphics, and images,
 *  are the sole property of lightPlugins ("Author").
 *
 *  You are granted a non-exclusive, non-transferable, revocable license
 *  to use, copy, modify, and distribute this software, provided that you
 *  include this copyright notice in all copies.
 *
 *  Unauthorized reproduction or distribution of this software, or any portion
 *  of it, may result in severe civil and criminal penalties, and will be
 *  prosecuted to the maximum extent possible under the law.
 * ----------------------------------------------------------------------------
 */

import de.lightplugins.master.Ashura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * This software is developed and maintained by lightPlugins.
 * For inquiries, please contact @discord: .light4coding.
 *
 * @version 5.0
 * @since 2021-07-19
 */

public class SkyhuntPlayerData {


    public Ashura plugin;
    private final String tableName = "playerdata";
    public SkyhuntPlayerData(Ashura plugin) {
        this.plugin = plugin;
    }
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //  ----------------------------------------------------------------  \\


    public CompletableFuture<Boolean> createNewStageForPlayer(String islandID, int kills) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareNewStageForPlayer(islandID, kills, connection)) {

                if(!hasAlreadyStage(islandID).get()) {
                    ps.execute();
                    logInfo("Successfully created new island with ID " + islandID);
                    return true;
                }
                logInfo("Player already has an Island with ID " + islandID);
                return false;

            } catch (SQLException | InterruptedException | ExecutionException e) {
                logError("An error occurred while creating a new island with ID " + islandID, e);
                return false;
            }
        });
    }

    private PreparedStatement prepareNewStageForPlayer(
            String islandID,
            int kills,
            Connection connection) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableName + " (islandID, kills) VALUES (?, ?)");

        ps.setString(1, islandID);
        ps.setInt(2, kills);
        return ps;
    }

    //  ----------------------------------------------------------------  \\

    public CompletableFuture<Boolean> hasAlreadyStage(String islandID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareHasAlreadyStage(islandID, connection)) {

                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                logError("An error occurred while searching for island with ID " + islandID, e);
                return false;
            }
        });
    }

    private PreparedStatement prepareHasAlreadyStage(String islandID, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + tableName + " WHERE islandID =?");
        ps.setString(1, islandID);
        return ps;
    }

    //  ----------------------------------------------------------------  \\

    public CompletableFuture<Boolean> deleteIsland(String islandID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareDeleteIsland(islandID, connection)) {

                ps.executeUpdate();
                logInfo("Successfully deleted the Island with ID " + islandID);
                return true;
            } catch (SQLException e) {
                logError("An error occurred while deleting the Island with ID " + islandID, e);
                return false;
            }
        });
    }

    private PreparedStatement prepareDeleteIsland(String islandID, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableName + " WHERE islandID = ?");
        ps.setString(1, islandID);
        return ps;
    }

    //  ----------------------------------------------------------------  \\

    public CompletableFuture<Boolean> updateKills(String islandID, int kills) {
        return CompletableFuture.supplyAsync(() -> {

            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareUpdateKills(islandID, kills, connection)) {

                if(hasAlreadyStage(islandID).get()) {
                    ps.execute();
                    logInfo("Successfully updated kills on island ID " + islandID);
                    return true;
                }
                logInfo("Player has no Island with ID " + islandID);
                return false;

            } catch (SQLException | InterruptedException | ExecutionException e) {
                logError("An error occurred while updating kills on an Island with ID " + islandID, e);
                return false;
            }
        });
    }

    private PreparedStatement prepareUpdateKills(String islandID, int kills, Connection connection) throws SQLException {

        PreparedStatement ps = connection.prepareStatement("UPDATE " + tableName + " SET kills = ? WHERE islandID = ?");
        ps.setInt(1, kills);
        ps.setString(2, islandID);
        return ps;
    }

    //  ----------------------------------------------------------------  \\

    public CompletableFuture<Integer> getKills(String islandID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = plugin.ds.getConnection();
                 PreparedStatement ps = prepareGetKills(islandID, connection)) {

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("kills");
                } else {
                    // Wenn es keinen Datensatz gibt, handle dies entsprechend
                    return 0;
                }
            } catch (SQLException e) {
                logError("An error occurred while searching for kills with Island ID " + islandID, e);
                return 0;
            }
        });
    }


    private PreparedStatement prepareGetKills(String islandID, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE islandID = ?");
        ps.setString(1, islandID);
        return ps;
    }

    //  ----------------------------------------------------------------  \\


    private void logError(String message, Throwable e) {
        logger.error(message, e);
    }

    private void logInfo(String message) {
        if(Ashura.settings.getConfig().getBoolean("settings.debug")) {
            logger.info(message);
        }
    }
}
