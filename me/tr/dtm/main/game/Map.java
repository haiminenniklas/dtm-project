package me.tr.dtm.main.game;

import me.tr.dtm.main.game.object.Monument;
import me.tr.dtm.main.other.callback.TypedCallback;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {

    private static HashMap<String, Map> loadedMaps = new HashMap<>();

    private World world;
    private HashMap<Team, Location> spawns;
    private HashMap<Team, Monument> monuments;

    public Map(World world, HashMap<Team, Location> spawns, HashMap<Team, Monument> monuments) {

        this.world = world;
        this.spawns = spawns;
        this.monuments = monuments;

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

    public static void saveMap(String name, Map map, TypedCallback<Boolean> cb) {

        

    }

    public static void loadMap(String name, TypedCallback<Map> cb) {



    }

    public static void loadMaps(TypedCallback<List<Map>> cb) {

    }

}
