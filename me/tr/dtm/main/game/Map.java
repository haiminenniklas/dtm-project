package me.tr.dtm.main.game;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class Map {

    private World world;
    private HashMap<Team, Location> spawns;

    public Map(String name) {
    }

    public World getWorld() {
        return world;
    }

    public HashMap<Team, Location> getSpawnpoints() {
        return spawns;
    }
}
