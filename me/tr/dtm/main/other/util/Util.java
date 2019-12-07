package me.tr.dtm.main.other.util;

import me.tr.dtm.main.Main;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            int ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
            return ping;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getToday() {

        String pattern = "MM/DD/YYYY";

        if(Main.getInstance().getConfig().getString("date-format") != null) {
            pattern = Main.getInstance().getConfig().getString("date-format");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
