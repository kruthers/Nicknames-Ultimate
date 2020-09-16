/*
 * Nickname Ultimate - A comprehensive  nickname plugin for spigot
 * Copyright (C) 2020 kruthers
 *
 * This Program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kruthers.nicknames.utils.storage;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class MySQL {
    private static final NicknamesUltimate plugin = JavaPlugin.getPlugin(NicknamesUltimate.class);
    private static final Logger LOGGER = plugin.getLogger();

    private static final String nicknamesTable = "nicks_ultimate_nickname_storage";

    private static Connection connection;
    private static boolean connected = false;

    private static Collection<String> nicknames = new ArrayList<>();

    public static boolean init() {
        //connect to database
        try {
            String host = plugin.getConfig().getString("storage.database.host");
            int port = plugin.getConfig().getInt("storage.database.port");
            String username = plugin.getConfig().getString("storage.database.username");
            String password = plugin.getConfig().getString("storage.database.password");
            String database = plugin.getConfig().getString("storage.database.database");

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("mysql://"+host+":"+port+"/"+database,username,password);
            connected=true;
        } catch (ClassNotFoundException err) {
            LOGGER.severe("No valid mysql driver installed, make sure to installed one before continuing: "+err.getMessage());
        } catch (SQLException err) {
            LOGGER.severe("Failed to start plugin during database connection phase with "+err.getMessage());
            return false;
        }

        //check if tables exist
        try {
            String createStatement = "CREATE TABLE IF NOT EXISTS ? (uuid TINYTEXT(36) UNIQUE NOT NULL, username VARCHAR(16) NOT NULL UNIQUE, nickname TEXT, PRIMARY KEY (uuid))";

            PreparedStatement statement = connection.prepareStatement(createStatement);
            statement.setString(0, nicknamesTable);

            statement.execute();


        } catch (SQLException err){
            LOGGER.warning("Failed to start plugin during table creation phase with "+err.getMessage());
            return false;

        }

        //load any stored nicknames into memory
        nicknames=getNicknamesFromDB();

        return true;
    }

    public static void end() throws SQLException{
        if (connected){
            connection.close();
        }
    }


    //nickname management
    public static boolean checkNick(String check, UUID exclude) throws SQLException{
        String query = "SELECT uuid FROM ? WHERE nickname=?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(0, nicknamesTable);
            statement.setString(1, check);
            ResultSet results = statement.executeQuery();
            connection.close();

            while (results.next()){
                if (!results.getString("uuid").equals(exclude.toString())){
                    return true;
                }
            }

        } catch (SQLException err) {
            throw new Error(err);
        }

        return false;
    }

    public static boolean checkUsername(String check, UUID exclude) throws SQLException{
        String query = "SELECT uuid FROM ? WHERE username = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(0, nicknamesTable);
            statement.setString(1, check);
            ResultSet results = statement.executeQuery();
            connection.close();

            if (results.next()){
                if (!results.getString("uuid").equals(exclude.toString())){
                    return true;
                }
            }

        } catch (SQLException err) {
            throw new Error(err);
        }

        return false;
    }

    public static boolean update_nick(String nick, Player player) throws SQLException{
        String insertStatement = "INSERT INTO ? (uuid,username,nickname) VALUES (?,?,?) ON DUPLICATE KEY UPDATE nickname = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            statement.setString(0, nicknamesTable);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, nick);
            statement.setString(4, nick);
            statement.executeUpdate();

            connection.close();

        } catch (SQLException err){
            throw new  Error(err);
        }

        nicknames = getNicknamesFromDB();

        return true;
    }

    public static boolean insertUser(Player player){
        String insertStatement = "INSERT INTO ? (uuid,username) VALUES (?,?) ON DUPLICATE KEY UPDATE username = ?";


        try {
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            statement.setString(0, nicknamesTable);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setString(3, player.getName());

            statement.executeUpdate();

            connection.close();

        } catch (SQLException err){
            LOGGER.warning("Failed to insert a user with the error: "+err.getMessage());
            return false;
        }

        return true;

    }

    public static List<OfflinePlayer> getPlayers(String nick){
        String queryStatement = "SELECT uuid,nick FROM ? WHERE nickname IS NOT NULL";

        try {
            List<OfflinePlayer> players = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement(queryStatement);
            statement.setString(0, nicknamesTable);

            ResultSet results = statement.executeQuery();
            statement.close();

            while (results.next()){
                String check = results.getString("nickname");
                UUID uuid = UUID.fromString(results.getString("uuid"));

                check = Utils.removeAllFormatting(check);

                if (check.equalsIgnoreCase(nick)){
                    players.add(Bukkit.getOfflinePlayer(uuid));
                }
            }

            return players;

        } catch (SQLException err) {
            LOGGER.warning("Failed to select user: "+err.getMessage());
            return new ArrayList<>();
        }
    }

    public static String[] getPlayerData(UUID uuid){
        String queryStatement = "SELECT * FROM ? WHERE uuid = ?";
        try {
            String[] playerData = new String[2];

            PreparedStatement statement = connection.prepareStatement(queryStatement);
            statement.setString(0, nicknamesTable);
            statement.setString(1,uuid.toString());

            ResultSet results = statement.executeQuery();

            statement.close();

            while (results.next()){
                playerData[0]=results.getString("username");
                playerData[1]=results.getString("nickname");
            }

            if (playerData[0]==null){
                return null;
            } else {
                return playerData;
            }


        } catch (SQLException err) {
            LOGGER.warning("Failed to select user: "+err.getMessage());
            return null;
        }
    }

    private static Collection<String> getNicknamesFromDB(){
        String queryStatement = "SELECT nickname FROM ?";

        try {
            List<String> nicknames = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement(queryStatement);
            statement.setString(0, nicknamesTable);

            ResultSet results = statement.executeQuery();

            statement.close();

            while (results.next()){
                nicknames.add(results.getString("nickname"));
            }

            if (nicknames.size()>0){
                return new ArrayList<>();
            } else {
                return nicknames;
            }


        } catch (SQLException err) {
            LOGGER.warning("Failed to get nickname list: "+err.getMessage());
            return new ArrayList<>();
        }
    }


    //variable returns
    public static Collection<String> getNicknames() {
        return nicknames;
    }

}
