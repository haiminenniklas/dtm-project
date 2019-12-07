package me.tr.dtm.main;

import me.tr.dtm.main.other.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        String message = Main.getInstance().getConfig().getString(msg_name);

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

        message = message.replaceAll("%player%", player.getName());
        message = message.replaceAll("%ping%", String.valueOf(Util.getPing(player)));

        // TODO: Continue this...

        return message;

    }

}
