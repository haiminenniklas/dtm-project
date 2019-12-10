package me.tr.dtm.main.game;

import me.tr.dtm.main.DTM;
import me.tr.dtm.main.Messages;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game implements Listener {

    private static final List<Player> players = new ArrayList<>();
    private static final List<Player> spectators = new ArrayList<>();
    private static final HashMap<Team, Player> teams = new HashMap<>();

    private static boolean running = false;
    private static boolean paused = false;

    private static List<Block> placedBlocks = new ArrayList<>();

    public static void join(Player player) {

        if(running) {
            Messages.send(player, "game-running-error");
            return;
        }

        if(!players.contains(player)) {
            players.add(player);
        }

        player.setGameMode(GameMode.SPECTATOR);

        if(players.size() >= DTM.getConfig().getInt("games.min-players-to-start")) {
            startCountdown();
        }

    }

    public static boolean hasJoined(Player player) {
        return players.contains(player);
    }

    public static void spectate(Player player) {

        if(!DTM.getConfig().getBoolean("spectating.enabled")) {
            Messages.send(player, "feature-not-available");
            return;
        }

        if(hasJoined(player)) {
            leave(player);
        }

        player.setGameMode(GameMode.SPECTATOR);


    }

    public static void leave(Player player) {

    }

    private static void startCountdown() {

    }

    public static void start() {



    }

    public static void resume() {

    }

    public static void pause() {

    }

    public static void end(Team winner) {

    }

}
