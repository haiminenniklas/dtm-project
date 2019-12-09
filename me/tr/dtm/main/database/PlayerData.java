package me.tr.dtm.main.database;

import me.tr.dtm.main.DTM;
import me.tr.dtm.main.other.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    static HashMap<UUID, HashMap<String, Object>> player_data = new HashMap<>();

    public static void loadNull(UUID uuid, boolean save) {
        HashMap<String, Object> empty = new HashMap<>();

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        // Basic User Data
        empty.put("points", 0);
        empty.put("kills", 0);
        empty.put("deaths", 0);
        empty.put("matches_won", 0);
        empty.put("matches_lost", 0);
        empty.put("save", save);

        player_data.put(uuid, empty);

    }

    public static boolean loadPlayer(UUID uuid){

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        HashMap<String, Object> data = new HashMap<>();

        try {
            ResultSet result = SQL.query("SELECT * FROM `dtm_players` WHERE `uuid` = '" + uuid.toString() + "';");
            if(result.next()) {

                // User's Basic Data

                data.put("points", result.getInt("points"));
                data.put("kills", result.getInt("kills"));
                data.put("deaths", result.getInt("deaths"));
                data.put("matches_won", result.getInt("matches_won"));
                data.put("matches_lost", result.getInt("matches_lost"));
                data.put("save", true);


                player_data.put(uuid, data);

                DTM.log("Loaded player " + uuid + " (" + player.getName() + ") from Database");
                return true;

            } else {
                loadNull(uuid, true);
            }


        } catch(SQLException ex) {
            ex.printStackTrace();
            loadNull(uuid, false);
        }

        return false;

    }

    public static HashMap<String, Object> getData(UUID uuid) {
        if(!isLoaded(uuid)) {
            loadNull(uuid, false);
        }
        return player_data.get(uuid);
    }

    public static boolean savePlayer(UUID uuid) {

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if(!isLoaded(uuid)) {
            loadNull(uuid, true);
        }

        HashMap<String, Object> data = player_data.get(uuid);
        if(!(boolean) data.get("save")) {
            return false;
        }

        String[] updateQueries = new String[] {
                "UPDATE `dtm_players` SET `points` = " + data.get("points") + ", `kills` = " + data.get("kills") + ", `deaths` = " + data.get("deaths") + ", `matches_won` = " + data.get("matches_won") +
                        ", `matches_lost` = " + data.get("matches_lost") + " WHERE `uuid` = '" + uuid + "';",
        };

        String[] saveQueries = new String[] {
                "INSERT INTO `dtm_players` VALUES('" + uuid + "', " + data.get("points") + ", " + data.get("kills") + ", " + data.get("deaths") +  ", " + data.get("matches_won")
                        + ", " + data.get("matches_lost") + ");",
        };

        try {

            int successful = 0;

            for(int i = 0; i < updateQueries.length; i++) {

                String update = updateQueries[i];
                DTM.log("Executing Database update query: " + update);
                if(!SQL.update(update)) {
                    DTM.log("Could not execute update query " + update + " trying to execute the equivalent save query: " + saveQueries[i]);
                    if(SQL.update(saveQueries[i])) {
                        successful += 1;
                    } else {
                        System.err.println("Could not save or update the player " + uuid + " (" + player.getName() + ").. Maybe you should check it out?");
                    }

                } else {
                    successful += 1;
                }

            }

            if(successful >= 1) {
                DTM.log("Updated or Saved " + successful + "/" + updateQueries.length + " tables for " + uuid +  " (" + player.getName() + ")!");
                return true;
            }

        } catch(SQLException ex) {
            ex.printStackTrace();
            return false;

        }
        return false;

    }

    public static boolean isLoaded(UUID uuid) {
        return player_data.containsKey(uuid);
    }

    public static Object getValue(UUID uuid, String key) {

        if(!isLoaded(uuid)) {
            loadNull(uuid, false);
        }

        HashMap<String, Object> data = player_data.get(uuid);
        if(!data.containsKey(key)) {
            return null;
        }

        return data.get(key);

    }

    public static boolean set(UUID uuid, String key, Object value) {
        if(!isLoaded(uuid)) {
            loadNull(uuid, false);
        }

        HashMap<String, Object> data = player_data.get(uuid);
        if(!data.containsKey(key)) {
            return false;
        }

        data.put(key, value);
        player_data.put(uuid, data);
        return true;

    }

    public static boolean add(UUID uuid, String key, int value) {
        if(!isLoaded(uuid)) {
            loadNull(uuid, false);
        }

        HashMap<String, Object> data = player_data.get(uuid);
        if(!data.containsKey(key)) {
            return false;
        }

        try {
            int obj = (int) data.get(key);
            int newVal = obj += value;
            data.put(key, newVal);
            player_data.put(uuid, data);
            return true;
        } catch(NumberFormatException ex) {
            ex.printStackTrace();
        }

        return false;

    }

}
