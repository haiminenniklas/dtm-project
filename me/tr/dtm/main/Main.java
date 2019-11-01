package me.tr.dtm.main;

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
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Events(), this);

    }

    @Override
    public void onDisable() {

    }
}
