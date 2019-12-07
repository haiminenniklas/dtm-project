package me.tr.dtm.main;

import com.sun.scenario.Settings;
import me.tr.dtm.main.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class DTM {

    public static boolean isLoggingEnabled() {
        return Main.getInstance().getConfig().getBoolean("logging");
    }

    public static void log(String msg) {

        if(!isLoggingEnabled()) return;

        Bukkit.getLogger().log(Level.INFO, msg);
    }

    public static void err(String msg) {
        if(!isLoggingEnabled()) return;
        Bukkit.getLogger().log(Level.WARNING, msg);
    }

    public static void logColored(String msg) {

        if(!isLoggingEnabled()) return;
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void warn(String message) {
        if(!isLoggingEnabled()) return;
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
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static void setSpawn(Location loc) {

        FileConfiguration config = Main.getInstance().getConfig();
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", String.valueOf(loc.getYaw()));
        config.set("spawn.pitch", String.valueOf(loc.getPitch()));
        config.set("spawn.world", loc.getWorld().getName());

        Main.getInstance().saveConfig();
    }

    public static Location getSpawn() {

        FileConfiguration config = Main.getInstance().getConfig();

        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");

        float yaw = Float.parseFloat(config.getString("spawn.yaw"));
        float pitch = Float.parseFloat(config.getString("spawn.pitch"));

        World world = Bukkit.getWorld(config.getString("spawn.world"));

        return new Location(world, x, y, z, yaw, pitch);

    }
    public static FileConfiguration getConfig() {
        return Main.getInstance().getConfig();
    }

    public static void savePlayer(Player player) {
        DTM.async(() -> PlayerData.savePlayer(player.getUniqueId()));
    }

    public static void loadPlayer(Player player) {
        DTM.async(() -> PlayerData.savePlayer(player.getUniqueId()));
    }


    public static Server getServer() {
        return Bukkit.getServer();
    }

    public static Main getPlugin() {
        return Main.getInstance();
    }



}
