package me.tr.dtm.main.game;

import me.tr.dtm.main.DTM;
import me.tr.dtm.main.database.SQL;
import me.tr.dtm.main.game.object.Monument;
import me.tr.dtm.main.other.callback.TypedCallback;
import me.tr.dtm.main.other.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {

    private static HashMap<String, Map> loadedMaps = new HashMap<>();

    private World world;
    private HashMap<Team, Location> spawns;
    private HashMap<Team, Monument> monuments;
    private boolean setupped;

    public Map(World world, HashMap<Team, Location> spawns, HashMap<Team, Monument> monuments) {

        this.world = world;
        this.spawns = spawns;
        this.monuments = monuments;
        this.setupped = true;

    }

    public boolean isSetupped() {
        return this.setupped;
    }

    public void setAsSetupped(boolean val) {
        this.setupped = val;
    }

    public World getWorld() {
        return world;
    }

    public HashMap<Team, Location> getSpawnpoints() {
        return spawns;
    }

    public HashMap<Team, Monument> getMonuments() {
        return monuments;
    }

    public boolean canPublish() {
        return (spawns.size() == 2 && monuments.size() == 2);
    }

    private static boolean exists(String name) {

        try {
            ResultSet result = SQL.query("SELECT * FROM `dtm_maps` WHERE `name` = '" + name + "';");
            return result.next();
        } catch(SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static HashMap<String, Map> getLoadedMaps() {
        return loadedMaps;
    }

    public static void saveMap(String name, Map map, TypedCallback<Boolean> cb) {

        if(!map.setupped) cb.execute(false);

        DTM.async(() -> {

            if(Map.exists(name)) {

                try {
                    boolean result = SQL.update("UPDATE `dtm_maps` SET " +
                            "`red_spawnpoint` = '" + (Util.locationToText(map.getSpawnpoints().get(Team.RED))) + "', " +
                            "`blue_spawnpoint`= '" + (Util.locationToText(map.getSpawnpoints().get(Team.BLUE))) + "', " +
                            "`red_monument` = '" + (Util.locationToText(map.getMonuments().get(Team.RED).getLocation())) +  "', " +
                            "`blue_monument` = '" + (Util.locationToText(map.getMonuments().get(Team.BLUE).getLocation())) + "', " +
                            "`world` = '" + (map.getWorld().getName()) + "' " +
                            "WHERE `name` = '" + name + "';");
                    cb.execute(result);
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    cb.execute(false);
                }

            } else {

                try {

                    boolean result = SQL.update("INSERT INTO `dtm_maps` VALUES(" +
                            "'" + name + "'," +
                            "'" + (Util.locationToText(map.getSpawnpoints().get(Team.RED))) + "'," +
                            "'" + (Util.locationToText(map.getSpawnpoints().get(Team.BLUE))) + "'," +
                            "'" + (Util.locationToText(map.getMonuments().get(Team.RED).getLocation())) + "'," +
                            "'" + (Util.locationToText(map.getMonuments().get(Team.BLUE).getLocation())) + "'," +
                            "'" + (map.getWorld().getName()) + "');");

                    cb.execute(result);
                } catch(SQLException ex) {
                    ex.printStackTrace();
                    cb.execute(false);
                }

            }

        });

    }

    public static void loadMap(String name, TypedCallback<Map> cb) {

        DTM.async(() -> {

            try {

                ResultSet result = SQL.query("SELECT * FROM `dtm_maps` WHERE `name` = '" + name + "';");
                if(result.next()) {
                    Map map = mapFromResult(result);
                    cb.execute(map);
                    addMap(name, map);
                } else {
                    cb.execute(null);
                }

            } catch(SQLException ex) {
                ex.printStackTrace();
                cb.execute(null);
            }

        });

    }

    public static void addMap(String name, Map map) {
        if(!loadedMaps.containsKey(name)) {
            loadedMaps.put(name, map);
        } else {
            loadedMaps.replace(name, map);
        }
    }

    public static void loadMaps(TypedCallback<List<Map>> cb) {

        List<Map> maps = new ArrayList<>();

        // Clear the current map cache, as because it will be replaced
        getLoadedMaps().clear();

        DTM.async(() -> {

            try {

                ResultSet result = SQL.query("SELECT * FROM `dtm_maps`;");

                while(result.next()) {
                    Map map = mapFromResult(result);
                    maps.add(map);
                    addMap(result.getString("name"), map);
                }

            } catch(SQLException ex) {
                ex.printStackTrace();
                cb.execute(maps);
            }

        });

        cb.execute(maps);

    }

    public static void deleteMap(String name, TypedCallback<Boolean> cb) {

        if(!getLoadedMaps().containsKey(name)) {
            cb.execute(false);
        }

        DTM.async(() -> {

            try {

                boolean result = SQL.update("DELETE FROM `dtm_maps` WHERE `name` = '" + name + "';");
                if(result) {
                    getLoadedMaps().remove(name);
                }
                cb.execute(result);

            } catch(SQLException ex) {
                ex.printStackTrace();
                cb.execute(false);
            }

        });

    }

    private static Map mapFromResult(ResultSet result) throws SQLException {
        HashMap<Team, Location> spawnpoints = new HashMap<>();
        HashMap<Team, Monument> monuments = new HashMap<>();

        spawnpoints.put(Team.RED, Util.textToLocation(result.getString("red_spawnpoint")));
        spawnpoints.put(Team.BLUE, Util.textToLocation(result.getString("blue_spawnpoint")));

        monuments.put(Team.RED, new Monument(Team.RED, Util.textToLocation("red_monument")));
        monuments.put(Team.BLUE, new Monument(Team.BLUE, Util.textToLocation("blue_monument")));

        return new Map(Bukkit.getWorld(result.getString("world")), spawnpoints, monuments);

    }

}
