package me.tr.dtm.main.game;

import me.tr.dtm.main.game.object.Monument;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class Map {

    private World world;
    private HashMap<Team, Location> spawns;
    private HashMap<Team, Monument> monuments;

    public Map(String name) {
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
}
