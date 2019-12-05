package me.tr.dtm.main;

import me.tr.dtm.main.database.SQL;
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

        SQL.setup();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(), this);

        long end = System.currentTimeMillis();
        System.out.println(" ");
        System.out.println(" Plugin enabled! (Time lasted " + (end - start) + "ms)");
        System.out.println(" ");
        System.out.println("--------------------------------------");



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
        System.out.println(" ");
        System.out.println(" Plugin disabled! Good bye!");
        System.out.println(" ");
        System.out.println("--------------------------------------");
    }
}
