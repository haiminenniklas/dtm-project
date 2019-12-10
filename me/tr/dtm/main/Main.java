package me.tr.dtm.main;

import me.tr.dtm.main.commands.GameCommands;
import me.tr.dtm.main.database.PlayerData;
import me.tr.dtm.main.database.SQL;
import me.tr.dtm.main.game.Game;
import me.tr.dtm.main.game.Map;
import me.tr.dtm.main.game.Team;
import me.tr.dtm.main.game.object.Monument;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private static Permission perms = null;
    public static Permission getVaultPermissions() {
        return perms;
    }

    private static Chat chat;
    public static Chat getVaultChat() {
        return chat;
    }

    private static boolean usesVault = false;
    public static boolean usesVault() {
        return usesVault;
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
        pm.registerEvents(new Game(), this);

        getCommand("dtm").setExecutor(this);
        getCommand("join").setExecutor(new GameCommands());
        getCommand("spec").setExecutor(new GameCommands());
        getCommand("leave").setExecutor(new GameCommands());

        if(!setupPermissions()) {
            DTM.warn("§eAs because Vault wasn't found, this plugin's features are limited");
            usesVault = false;
        } else {
            usesVault = true;
            setupChat();
        }

        Map.loadMaps((maps) -> {
            if(maps.size() < 1) {
                DTM.warn("No maps found, maybe you should add some?");
            } else {

                DTM.log("Loaded " + maps.size() + " maps from the Database!");
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(" ");
        System.out.println(" Plugin enabled! (Time lasted " + (end - start) + "ms)");
        System.out.println(" ");
        System.out.println("--------------------------------------");


        // Automatic data-saving for players
        if(config.getBoolean("auto-saving.enabled")) {

            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {

                int saved = 0;

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(PlayerData.savePlayer(player.getUniqueId())) {
                        saved += 1;
                    }
                    Messages.send(player, "data-saved");
                }

                DTM.log("Saved the data of " + saved + " players!");

            }, 20 * 60, (long) getConfig().getInt("auto-saving.interval") * 20);

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
                sender.sendMessage(" §amaps §7map management");
                sender.sendMessage("§7§m------------------------");

                return true;

            }

            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {

                sender.sendMessage("§a[DTM] §eReloading the configuration file...");
                reloadConfig();
                sender.sendMessage("§a[DTM] §eDone!");

            } else if(args[0].equalsIgnoreCase("database") || args[0].equalsIgnoreCase("db")) {

                if(args.length < 2) {
                    sender.sendMessage("§7§m------------------------");
                    sender.sendMessage(" §e/dtm database §7...");
                    sender.sendMessage(" ");
                    sender.sendMessage(" §ashow <player> §7show player's data from cache");
                    sender.sendMessage(" §aset <key> <value> <player> §7modify data for player");
                    sender.sendMessage(" §aadd <key> <value> <player> §7add for a value for player");
                    sender.sendMessage("§7§m------------------------");
                } else if(args.length == 3) {

                    if(args[1].equalsIgnoreCase("show")) {

                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        if(!PlayerData.isLoaded(target.getUniqueId())) {
                            if(!PlayerData.loadPlayer(target.getUniqueId())) {
                                PlayerData.loadNull(target.getUniqueId(), false);
                            }
                        }

                        UUID uuid = target.getUniqueId();

                        sender.sendMessage("§7§m------------------------");
                        sender.sendMessage(" §7Data of §a" + target.getName() + "§7:");
                        sender.sendMessage(" ");
                        sender.sendMessage(" §7Points: §e" + DTM.getPoints(uuid));
                        sender.sendMessage(" §7Kills: §e" + DTM.getKills(uuid));
                        sender.sendMessage(" §7Deaths: §e" + DTM.getDeaths(uuid));
                        sender.sendMessage(" §7Matches won: §e" + DTM.getWonMatches(uuid));
                        sender.sendMessage(" §7Matches lost: §e" + DTM.getLostMatches(uuid));
                        sender.sendMessage(" ");
                        sender.sendMessage(" §7UUID: §e" + uuid);
                        sender.sendMessage("§7§m------------------------");

                    }

                } else {

                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);
                    if(!PlayerData.isLoaded(target.getUniqueId())) {
                        if(!PlayerData.loadPlayer(target.getUniqueId())) {
                            PlayerData.loadNull(target.getUniqueId(), false);
                        }
                    }

                    UUID uuid = target.getUniqueId();

                    String key = args[2].toLowerCase();
                    int value;
                    try {
                        value = Integer.parseInt(args[3]);
                    } catch(NumberFormatException e) {
                        sender.sendMessage("§a[DTM] §cPlease, use only numbers as value!");
                        return true;
                    }

                    if(args[1].equalsIgnoreCase("set")) {

                        if(PlayerData.set(uuid, key, value)) {
                            sender.sendMessage("§a[DTM] §eData §a" + key + " §eset to §a" + value + " §efor player §a" + target.getName());
                        } else {
                            sender.sendMessage("§a[DTM] §eCould not set data §a" + key + " §eto §a" + value + " §efor player §a" + target.getName());
                        }


                    } else if(args[1].equalsIgnoreCase("add")) {

                        if(PlayerData.add(uuid, key, value)) {
                            sender.sendMessage("§a[DTM] §eAdded §a" + value + " §eto the data §a" + key + " §efor the player §a" + target.getName());
                        } else {
                            sender.sendMessage("§a[DTM] §eCould not add §a" + value + " §efor the data §a" + key + " §efor the player §a" + target.getName());
                        }

                    }

                }

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

                    // TODO: Make this
                    Messages.send(sender, "feature-not-available");

                } else if(args[1].equalsIgnoreCase("tps")) {
                    sender.sendMessage("§a[DTM] §7Average TPS: §a" + DTM.getAverageTPS());

                } else if(args[1].equalsIgnoreCase("running")) {

                    //TODO: Make this
                    Messages.send(sender, "feature-not-available");

                } else if(args[1].equalsIgnoreCase("loadData")) {
                    if(sender instanceof Player) {
                        sender.sendMessage("§a[DTM] §eLoading your data, see console for any errors...");
                        DTM.loadPlayer((Player)sender, () -> {
                            sender.sendMessage("§a[DTM] §eData loaded!");
                        });
                    } else {
                        Messages.send(sender, "command-for-players");
                    }
                }

            } else if(args[0].equalsIgnoreCase("map") || args[0].equalsIgnoreCase("maps")) {

                if(args.length < 2) {
                    sender.sendMessage("§7§m------------------------");
                    sender.sendMessage(" §e/dtm maps §7...");
                    sender.sendMessage(" ");
                    sender.sendMessage(" §a...show §7Show all the maps");
                    sender.sendMessage(" §a...create <name> §7Create new map");
                    sender.sendMessage(" §a...setMonument <RED|BLUE> <mapName> §7Set the monument block to the block you're looking at");
                    sender.sendMessage(" §a...setSpawn <RED|BLUE> <mapName> §7Set the spawnpoint of a team to your location");
                    sender.sendMessage(" §a...save <mapName> §7save a map!");
                    sender.sendMessage(" §a...delete <mapName> §7delete a map");
                    sender.sendMessage("§7§m------------------------");
                    return true;
                }

                if(args[1].equalsIgnoreCase("show")) {

                    sender.sendMessage("§7§m------------------------");
                    sender.sendMessage(" §7List of DTM maps (§a" + Map.getLoadedMaps().size() + "§7):");
                    for(java.util.Map.Entry<String, Map> e : Map.getLoadedMaps().entrySet()) {
                        sender.sendMessage(" §7- §e" + e.getKey() + " (World: " + e.getValue().getWorld().getName() + ")");
                    }
                    sender.sendMessage("§7§m------------------------");

                }

                if(args.length == 3) {

                    String mapName = args[2];

                    if(args[1].equalsIgnoreCase("create")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;

                            if(Map.getLoadedMaps().containsKey(mapName)) {
                                sender.sendMessage("§a[DTM] §cThat map already exists! Try a different name!");
                                return true;
                            }

                            Map map = new Map(player.getWorld(), new HashMap<>(), new HashMap<>());
                            map.setAsSetupped(false);
                            sender.sendMessage("§a[DTM] §eCreated the map §a" + mapName + " §e!");
                            sender.sendMessage("§a[DTM] §eNow you just need to set the Monument and Spawn locations! For help, check §a/dtm map§e!");
                            Map.getLoadedMaps().put(mapName, map);


                        } else {
                            Messages.send(sender, "command-for-players");
                        }

                    } else if(args[1].equalsIgnoreCase("save")) {

                        Map map = Map.getLoadedMaps().get(mapName);

                        if(map == null) {
                            sender.sendMessage("§a[DTM] §cNo map was found by the name '" + mapName + "'...");
                            return true;
                        }

                        if(!map.canPublish()) {
                            sender.sendMessage("§a[DTM] §cYou need to fully setup the map if you want to save it!");
                            return true;
                        }

                        sender.sendMessage("§a[DTM] §eSaving map §a" + mapName + "§e...");
                        Map.saveMap(mapName, map, (success) -> {
                            if(success) {
                                sender.sendMessage("§a[DTM] §eSuccessfully saved the map §e" + mapName + "§e!");
                            } else {
                                sender.sendMessage("§a[DTM] §cCould not save the map §e" + mapName + "§c...");
                            }
                        });

                    } else if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {


                        Map map = Map.getLoadedMaps().get(mapName);

                        if(map == null) {
                            sender.sendMessage("§a[DTM] §cNo map was found by the name '" + mapName + "'...");
                            return true;
                        }

                        sender.sendMessage("§a[DTM] §eSaving map §a" + mapName + "§e...");
                        Map.deleteMap(mapName, (success) -> {
                            if(success) {
                                sender.sendMessage("§a[DTM] §eSuccessfully deleted the map §e" + mapName + "§e!");
                            } else {
                                sender.sendMessage("§a[DTM] §cCould not delete the map §e" + mapName + "§c...");
                            }
                        });
                    }

                } else if(args.length >= 4) {

                    if(sender instanceof Player) {

                        Player player = (Player) sender;

                        String teamStr = args[2].toUpperCase();

                        if(!teamStr.equalsIgnoreCase("red") && !teamStr.equalsIgnoreCase("blue")) {
                            sender.sendMessage("§a[DTM] §cThe only available teams are RED or BLUE!");
                            return true;
                        }

                        Team team = Team.valueOf(teamStr);
                        String mapName = args[3];
                        Map map = Map.getLoadedMaps().get(mapName);

                        if(map == null) {
                            sender.sendMessage("§a[DTM] §cNo map was found by the name '" + mapName + "'...");
                            return true;
                        }

                        if(args[1].equalsIgnoreCase("setSpawn")) {

                            Location loc = player.getLocation();
                            if(!map.getSpawnpoints().containsKey(team)){
                                map.getSpawnpoints().put(team, loc);
                            } else {
                                map.getSpawnpoints().replace(team, loc);
                            }
                            sender.sendMessage("§a[DTM] §eSet the spawnpoint for team " + team.name() + " at your location! Now just save your changes with §a/dtm map save " + mapName + "§e!");

                        } else if(args[1].equalsIgnoreCase("setMonument")) {

                            Block block = player.getTargetBlockExact(15);
                            if(block == null) {
                                sender.sendMessage("§a[DTM] §cNo block in sight...");
                                return true;
                            }

                            Location loc = block.getLocation();
                            Monument mon = new Monument(team, loc);
                            if(!map.getMonuments().containsKey(team)) {
                                map.getMonuments().put(team, mon);
                            } else {
                                map.getMonuments().replace(team, mon);
                            }

                            sender.sendMessage("§a[DTM] §eSet the monument for team " + team.name() + " at (X: " + loc.getBlockX() + "Y: " + loc.getY() + " Z: " + loc.getZ() +")! Now just save your changes with §a/dtm map save " + mapName + "§e!");

                        }

                        if(map.canPublish()) {
                            map.setAsSetupped(true);
                        }


                    } else {
                        Messages.send(sender, "command-for-players");
                    }



                }

                else {
                    String mapName = args[1];

                    Map map = Map.getLoadedMaps().get(mapName);
                    if(map == null) {
                        sender.sendMessage("§a[DTM] §cNo map was found by the name '" + mapName + "'...");
                        return true;
                    }

                    Location blueSpawn = map.getSpawnpoints().get(Team.BLUE);
                    Location redSpawn = map.getSpawnpoints().get(Team.RED);

                    Location blueMon = map.getMonuments().get(Team.BLUE).getLocation();
                    Location redMon = map.getMonuments().get(Team.RED).getLocation();

                    sender.sendMessage("§7§m------------------------");
                    sender.sendMessage(" §7Map §e" + mapName + "§7:");
                    sender.sendMessage(" ");
                    sender.sendMessage(" §7World: §e" + map.getWorld().getName());
                    sender.sendMessage(" §7Spawnpoints: ");
                    sender.sendMessage("   §9BLUE§7: " + blueSpawn.getX() + ", " + blueSpawn.getY() + ", " + blueSpawn.getZ());
                    sender.sendMessage("   §cRED§7: " + redSpawn.getX() + ", " + redSpawn.getY() + ", " + redSpawn.getZ());
                    sender.sendMessage(" §7Monuments: ");
                    sender.sendMessage("   §9BLUE§7: " + blueMon.getX() + ", " + blueMon.getY() + ", " + blueMon.getZ());
                    sender.sendMessage("   §cRED§7: " + redMon.getX() + ", " + redMon.getY() + ", " + redMon.getZ());
                    sender.sendMessage("§7§m------------------------");


                }

            }


        }

        return true;
    }

    private boolean setupPermissions() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

}
