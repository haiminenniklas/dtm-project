package me.tr.dtm.main.game;

import me.tr.dtm.main.DTM;
import me.tr.dtm.main.Messages;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game implements Listener {

    private static final List<Player> players = new ArrayList<>();
    private static final List<Player> spectators = new ArrayList<>();
    private static final HashMap<Player, Team> teams = new HashMap<>();

    private static boolean running = false;
    private static boolean paused = false;
    private static boolean countingDown = false;

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

        if(DTM.getConfig().getBoolean("game-description-message.enabled")) {
            Messages.multipleLines(player, "game-description-message.message");
        }

        if(canStart()) {
            startCountdown();
        }

    }

    public static boolean canStart() {
        return players.size() >= DTM.getConfig().getInt("games.min-players-to-start");
    }

    public static boolean hasJoined(Player player) {
        return players.contains(player);
    }

    public static void spectate(Player player) {

        if(!DTM.getConfig().getBoolean("spectating.enabled")) {
            //Add this to the /spec command
            //Messages.send(player, "feature-not-available");
            return;
        }

        if(hasJoined(player)) {
            leave(player);
        }

        player.setGameMode(GameMode.SPECTATOR);


    }

    public static void leave(Player player) {

        if(countingDown) {

        }

    }

    private static void startCountdown() {

        Game.countingDown = true;

        DTM.every(1, () -> {

            int toStart = 20;
            if(!canStart()) {
                for(Player player : players) {
                    Messages.sendOther(player, "game-countdown.countdown-stopped");
                    countingDown = false;
                }

            } else {
                for(Player player : players) {
                    String msg = DTM.getConfig().getString("game-countdown.message");
                    msg = Messages.serverPlaceholders(msg);
                    msg = Messages.playerPlaceholders(player, msg);
                    msg = msg.replaceAll("%seconds_left%", String.valueOf(toStart));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                toStart -= 1;

                if(toStart < 1) {
                    start();
                }

            }


        });

    }

    public static void start() {

        countingDown = false;
        running = true;
        paused = false;

        for(Player player : players) {
            String msg = DTM.getConfig().getString("game-countdown.game-starting");
            msg = Messages.serverPlaceholders(msg);
            msg = Messages.playerPlaceholders(player, msg);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }

        // Sort players into teams
        sortTeams();

    }

    private static void sortTeams() {

        for(Player player : players) {

            boolean joinRed = new Random().nextBoolean();
            if(joinRed) {

                teams.put(player, Team.RED);

                if(getPlayers(Team.RED).size() + 1 > getPlayers(Team.BLUE).size()) {
                    teams.put(player, Team.BLUE);
                }

            } else {
                teams.put(player, Team.BLUE);


                if(getPlayers(Team.BLUE).size() + 1 > getPlayers(Team.RED).size()) {
                    teams.put(player, Team.RED);
                }
            }

        }
    }

    public static List<Player> getPlayers(Team team) {

        List<Player> teamPlayers = new ArrayList<>();

        for(Player player : players) {

            if(teams.get(player) == team) {
                teamPlayers.add(player);
            }

        }

        return teamPlayers;
    }

    public static void resume() {

    }

    public static void pause() {

    }

    public static void end(Team winner) {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        Player player = e.getPlayer();

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();

    }



}
