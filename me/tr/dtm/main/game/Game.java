package me.tr.dtm.main.game;

import org.bukkit.entity.Player;

import java.util.List;

public class Game {

    private List<Player> players;
    private Map map;
    private boolean running;

    public Game(List<Player> players, Map map) {
        this.map = map;
        this.players = players;
        this.running = false;
    }

    public void init() {

    }

    private void start() {

        this.running = true;

    }

    public void end(Team winner) {

        this.running = false;

    }



}
