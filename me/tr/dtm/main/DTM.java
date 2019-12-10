package me.tr.dtm.main;

import com.sun.scenario.Settings;
import me.tr.dtm.main.database.PlayerData;
import me.tr.dtm.main.other.callback.Callback;
import me.tr.dtm.main.other.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;
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
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), task, (long) 20 * seconds);
    }

    public static void every(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), task, 20, (long) 20 * seconds);
    }

    public static void everyAsync(int seconds, Runnable task) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), task, 20, (long) 20 * seconds);
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
        return Util.round(average, 2);
    }

    private static String getServerVersion ( ) {
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

    public static void teleportToSpawn(Player player) {

        Util.heal(player);
        Util.removePotionEffects(player);
        Util.clearInventory(player);

        player.teleport(getSpawn());
        Messages.send(player, "teleport-to-spawn");

    }

    public static FileConfiguration getConfig() {
        return Main.getInstance().getConfig();
    }

    public static void savePlayer(Player player) {
        DTM.async(() -> PlayerData.savePlayer(player.getUniqueId()));
    }

    public static void loadPlayer(Player player) {
        DTM.async(() -> PlayerData.loadPlayer(player.getUniqueId()));
    }

    public static void loadPlayer(Player player, Callback cb) {

        DTM.async(() -> {
            PlayerData.loadPlayer(player.getUniqueId());
            cb.execute();
        });
    }

    public static void savePlayer(Player player, Callback cb) {

        DTM.async(() -> {
            PlayerData.savePlayer(player.getUniqueId());
            cb.execute();
        });
    }

    public static Server getServer() {
        return Bukkit.getServer();
    }

    public static Main getPlugin() {
        return Main.getInstance();
    }

    public static boolean usesVault() {
        return Main.usesVault();
    }

    public static int getKills(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }
       return (int) PlayerData.getValue(uuid, "kills");
    }

    public static int getDeaths(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }
        return (int) PlayerData.getValue(uuid, "deaths");
    }

    public static int getPoints(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }
        return (int) PlayerData.getValue(uuid, "points");
    }

    public static int getWonMatches(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }
        return (int) PlayerData.getValue(uuid, "matches_won");
    }

    public static int getLostMatches(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }
        return (int) PlayerData.getValue(uuid, "matches_lost");
    }

    public static double getKDRatio(UUID uuid) {
        if(!PlayerData.isLoaded(uuid)) {
            PlayerData.loadNull(uuid, false);
        }

        if(getDeaths(uuid) < 1) return getKills(uuid);
        if(getKills(uuid) < 1) return 0.0;

        double kd = ((double) getKills(uuid) /(double)  getDeaths(uuid));

        return Util.round(kd, 2);

    }


}
