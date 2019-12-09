package me.tr.dtm.main;

import me.tr.dtm.main.other.util.Util;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Messages {

    public static void send(CommandSender sender, String msg_name) {
        String message = Main.getInstance().getConfig().getString("messages." + msg_name);

        if(message == null) {
            throw new NullPointerException("Could not find message with identifier '" + msg_name + "'");
        }

        if(Main.getInstance().getConfig().getBoolean("msg-prefix-enabled")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("msg-prefix") + " " + message));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static void send(Player player, String msg_name) {
        String message = Main.getInstance().getConfig().getString("messages." + msg_name);

        if(message == null) {
            throw new NullPointerException("Could not find message with identifier '" + msg_name + "'");
        }

        message = serverPlaceholders(message);
        message = playerPlaceholders(player, message);

        if(Main.getInstance().getConfig().getBoolean("msg-prefix-enabled")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("msg-prefix") + " " + message));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }

    public static String serverPlaceholders(String message) {
        message = message.replaceAll("%players_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        message = message.replaceAll("%server_version%", Bukkit.getVersion());
        message = message.replaceAll("%max_online%", String.valueOf(Bukkit.getMaxPlayers()));
        message = message.replaceAll("%dtm_version%", Main.getInstance().getDescription().getVersion());
        message = message.replaceAll("%average_tps%", String.valueOf(DTM.getAverageTPS()));
        message = message.replaceAll("%date%", Util.getToday());

        return message;
    }

    public static String playerPlaceholders(Player player, String message) {

        UUID uuid = player.getUniqueId();

        message = message.replaceAll("%player%", player.getName());
        message = message.replaceAll("%ping%", String.valueOf(Util.getPing(player)));
        message = message.replaceAll("%wins%", String.valueOf(DTM.getWonMatches(uuid)));
        message = message.replaceAll("%losses%", String.valueOf(DTM.getLostMatches(uuid)));
        message = message.replaceAll("%points%", String.valueOf(DTM.getPoints(uuid)));
        message = message.replaceAll("%kills%", String.valueOf(DTM.getKills(uuid)));
        message = message.replaceAll("%deaths%", String.valueOf(DTM.getDeaths(uuid)));
        message = message.replaceAll("%kd_ratio%", String.valueOf(DTM.getKDRatio(uuid)));

        if(DTM.usesVault()) {
            Permission perm = Main.getVaultPermissions();
            Chat chat = Main.getVaultChat();

            message = message.replaceAll("%primary_group%", perm.getPrimaryGroup(player));
            message = message.replaceAll("%prefix%", chat.getPlayerPrefix(player));
            message = message.replaceAll("%suffix%", chat.getPlayerSuffix(player));
        }


        // TODO: Continue this...

        return message;

    }

    public static void giveScoreboard(Player player) {

        FileConfiguration config = DTM.getConfig();

        if(!config.getBoolean("scoreboard.enabled"))
            return;

        // Clear the scoreboard
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        String title = config.getString("scoreboard.title");
        title = Messages.playerPlaceholders(player, title);
        title = Messages.serverPlaceholders(title);

        Objective obj = board.registerNewObjective("dash", "dummy",
                ChatColor.translateAlternateColorCodes('&', title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> rawLines = config.getStringList("scoreboard.scoreboard");
        List<String> lines = new ArrayList<>();

        // Replace the placeholders and chat colors
        for(String s : rawLines) {
            s = Messages.playerPlaceholders(player, s);
            s = Messages.serverPlaceholders(s);
            s = ChatColor.translateAlternateColorCodes('&', s);
            lines.add(s);
        }

        int size = lines.size();

        // Check size
        if(size > 15) {
            DTM.err("Scoreboard cannot handle more than 15 lines!");
            return;
        }

        // Go through the lines top to bottom
        for(int i =0 ; i < size; i++) {

            Team line = board.registerNewTeam("line-"  + i);
            String text = lines.get(i);
            int pos = (size - 1) - i;

            line.addEntry("§" + i + "§r§" + i);

            if(text.length() >= 64) {

                line.setPrefix(text.substring(0, 64));
                line.setSuffix(text.substring(65));

            } else if(text.length() < 64) {

                line.setPrefix(text);

            } else {
                DTM.err("Texts for DTM scoreboard can be only 1 - 32 characters in length!");
                return;
            }

            obj.getScore("§" + i + "§r§" + i).setScore(pos);

        }

        DTM.everyAsync(4, () -> {

            // Go through the lines top to bottom, and update them
            for(int i =0 ; i < size; i++) {

                Team line = board.getTeam("line-"  + i);
                String text = lines.get(i);
                int pos = (size - 1) - i;

                // Clear the lines and add them again to update the values
                lines.clear();
                for(String s : rawLines) {
                    s = Messages.playerPlaceholders(player, s);
                    s = Messages.serverPlaceholders(s);
                    s = ChatColor.translateAlternateColorCodes('&', s);
                    lines.add(s);
                }


                if(text.length() >= 64) {

                    line.setPrefix(text.substring(0, 64));
                    line.setSuffix(text.substring(65));

                } else if(text.length() < 64) {

                    line.setPrefix(text);

                } else {
                    DTM.err("Texts for DTM scoreboard can be only 1 - 32 characters in length!");
                    return;
                }

                obj.getScore("§" + i + "§r§" + i).setScore(pos);

            }

            player.setScoreboard(board);
        });

    }

}
