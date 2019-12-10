package me.tr.dtm.main.other.util;

import me.tr.dtm.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String locationToText(Location loc) {

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        World world = loc.getWorld();

        return world.getName() + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;

    }

    public static Location textToLocation(String text) {

        String[] values = text.split(";");

        double x = Double.parseDouble(values[1]);
        double y = Double.parseDouble(values[2]);
        double z = Double.parseDouble(values[3]);

        float yaw = Float.parseFloat(values[4]);
        float pitch = Float.parseFloat(values[5]);

        World world = Bukkit.getWorld(values[0]);

        return new Location(world, x, y, z, yaw, pitch);

    }

    public static void heal(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        removePotionEffects(player);
    }
    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
    }

    public static void removePotionEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }


}
