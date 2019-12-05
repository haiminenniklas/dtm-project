package me.tr.dtm.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class DTM {

    public static void log(String msg) {
        Bukkit.getLogger().log(Level.INFO, msg);
    }

    public static void err(String msg) {
        Bukkit.getLogger().log(Level.WARNING, msg);
    }

    public static void logColored(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void warn(String message) {
        Bukkit.getLogger().log(Level.WARNING, message);
    }

    public static void task(Runnable task) {
        Bukkit.getScheduler().runTask(Main.getInstance(), task);
    }

    public static void async(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), task);
    }

    public static void every(int seconds, Runnable task, boolean async) {
        if(async) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), task, 20, (long) getAverageTPS() * seconds);
        } else {
            Bukkit.getScheduler().runTaskTimer(Main.getInstance(), task, 20, (long) getAverageTPS() * seconds);
        }
    }

    public static void after(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task, 20 * seconds);
    }

    public static void after(int seconds, Runnable task, boolean async) {
        if(async) {
            DTM.after(seconds, task);
        } else {
            DTM.afterAsync(seconds, task);
        }
    }

    public static void afterAsync(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), task, (long) getAverageTPS() * seconds);
    }

    public static void every(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), task, 20, (long) getAverageTPS() * seconds);
    }

    public static void everyAsync(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), task, 20, (long) getAverageTPS() * seconds);
    }

    public static double getAverageTPS() {
        double average;
        try {
            Class<?> craftServer = Class.forName ( "org.bukkit.craftbukkit." + getServerVersion() + ".CraftServer" );
            Method getServer = craftServer.getMethod ( "getServer" );
            Object nmsServer = getServer.invoke ( Bukkit.getServer ( ) );
            double[] recentTps = (double[]) nmsServer.getClass().getField("recentTps").get(nmsServer);
            average = (recentTps[0] + recentTps[1] + recentTps[2]) / 3;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0d;
        }
        return average;
    }

    public static String getServerVersion ( ) {
        return Bukkit.getServer ( ).getClass ( ).getPackage ( ).getName ( ).substring ( 23 );
    }

}
