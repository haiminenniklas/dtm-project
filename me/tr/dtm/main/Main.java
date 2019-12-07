package me.tr.dtm.main;

import me.tr.dtm.main.database.PlayerData;
import me.tr.dtm.main.database.SQL;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Main.instance = this;

        System.out.println("-------- Destroy The Monument --------");
        System.out.println(" ");
        System.out.println(" Plugin made by: (c) Niklas Haiminen, Smath Game Development");
        System.out.println(" Version: " + getDescription().getVersion());
        System.out.println(" Wiki: http://smath.fi/games/dtm");
        System.out.println(" Discord: http://smath.fi/games/discord");
        System.out.println(" ");
        System.out.println(" Enabling plugin...");
        long start = System.currentTimeMillis();
        System.out.println(" ");
        System.out.println(" If any errors occur, it will be shown!");
        System.out.println(" Make sure to share the errors in our Discord community!");
        System.out.println(" ");

        // Initialization code...

        saveDefaultConfig();

        FileConfiguration config = getConfig();

        SQL.setup();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(), this);

        getCommand("dtm").setExecutor(this);



        long end = System.currentTimeMillis();
        System.out.println(" ");
        System.out.println(" Plugin enabled! (Time lasted " + (end - start) + "ms)");
        System.out.println(" ");
        System.out.println("--------------------------------------");


        // Automatic data-saving for players
        if(config.getBoolean("auto-saving.enabled")) {

            DTM.everyAsync(config.getInt("auto-saving.interval"), () -> {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData.savePlayer(player.getUniqueId());
                    Messages.send(player, "data-saved");
                }
            });

        }

    }

    @Override
    public void onDisable() {
        System.out.println("-------- Destroy The Monument --------");
        System.out.println(" ");
        System.out.println(" Plugin made by: (c) Niklas Haiminen, Smath Game Development");
        System.out.println(" Version: " + getDescription().getVersion());
        System.out.println(" Wiki: http://smath.fi/games/dtm");
        System.out.println(" Discord: http://smath.fi/games/discord");
        System.out.println(" ");
        System.out.println(" Disabling plugin...");
        System.out.println(" Plugin disabled! Good bye!");
        System.out.println(" ");
        System.out.println("--------------------------------------");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(label.equalsIgnoreCase("dtm")) {

            if(!sender.hasPermission("dtm.admin")) {
                Messages.send(sender, "no-permission");
                return true;
            }

            if(args.length < 1 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {

                sender.sendMessage("§7§m------------------------");
                sender.sendMessage(" §e/dtm §7...");
                sender.sendMessage(" ");
                sender.sendMessage(" §areload §7reload the config file");
                sender.sendMessage(" §adatabase §7database utility");
                sender.sendMessage(" §asetspawn §7set the server spawn");
                sender.sendMessage(" §adebug §7debugging");
                sender.sendMessage("§7§m------------------------");

                return true;

            }

            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {

                sender.sendMessage("§a[DTM] §eReloading the configuration file...");
                reloadConfig();
                sender.sendMessage("§a[DTM] §eDone!");

            } else if(args[0].equalsIgnoreCase("database") || args[0].equalsIgnoreCase("db")) {

                sender.sendMessage("§7§m------------------------");
                sender.sendMessage(" §e/dtm database §7...");
                sender.sendMessage(" ");
                sender.sendMessage(" §a...");
                sender.sendMessage("§7§m------------------------");

            } else if(args[0].equalsIgnoreCase("setspawn")) {

                if(sender instanceof Player) {
                    DTM.setSpawn(((Player)sender).getLocation());
                    sender.sendMessage("§a[DTM] §eSpawn set on your location!");
                } else {
                    Messages.send(sender, "command-for-players");
                }

            } else if(args[0].equalsIgnoreCase("debug")) {

                if(args.length < 2) {
                    sender.sendMessage("§7§m------------------------");
                    sender.sendMessage(" §e/dtm debug §7...");
                    sender.sendMessage(" ");
                    sender.sendMessage(" §arun §7fix found problems");
                    sender.sendMessage(" §atps §7server's tps measured by DTM");
                    sender.sendMessage(" §arunning §7amount of games running");
                    sender.sendMessage(" §aloadData §7load your data from the Database");
                    sender.sendMessage("§7§m------------------------");
                    return true;
                }

                if(args[1].equalsIgnoreCase("run")) {

                } else if(args[1].equalsIgnoreCase("tps")) {
                    sender.sendMessage("§a[DTM] §7Average TPS: §a" + DTM.getAverageTPS());

                } else if(args[1].equalsIgnoreCase("running")) {

                } else if(args[1].equalsIgnoreCase("loadData")) {

                }

            }


        }

        return true;
    }
}
