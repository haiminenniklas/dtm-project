package me.tr.dtm.main.database;

import me.tr.dtm.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQL {

    public static Connection conn = null;

    public static void setup() {

        FileConfiguration config = Main.getInstance().getConfig();
        if(!config.getBoolean("mysql.enabled")) {
            File dataFolder = new File(Main.getInstance().getDataFolder().getAbsolutePath() + File.separator + "database.db");
            if (!dataFolder.exists()) {
                try {
                    if(!dataFolder.createNewFile()){
                        System.out.println("Could not create Database file (#createNewFile())");
                    }
                } catch (IOException e) {
                    System.out.println("Could not create Database file");
                    e.printStackTrace();
                }

            }

            try {
                Class.forName("org.sqlite.JDBC");
                SQL.conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                System.out.println("Opened database successfully");

                queries();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            String user = config.getString("mysql.user");
            String password = config.getString("mysql.password");
            String address = config.getString("mysql.address");

            try {
                Class.forName("com.mysql.jdbc.Driver");
                SQL.conn = DriverManager.getConnection("jdbc:mysql://" + address+ ":3306/autiomc", user, password);
                System.out.println("Opened database successfully");

                queries();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void queries(){

        String[] queries = new String[] {

                "CREATE TABLE IF NOT EXISTS `players` (`uuid` VARCHAR(120), player_name TEXT, money int(11), rank TEXT, joined TEXT, crystals int(11) , PRIMARY KEY(`uuid`));",
                "CREATE TABLE IF NOT EXISTS `homes` (`uuid` VARCHAR(120), first_home TEXT, second_home TEXT, third_home TEXT, PRIMARY KEY(`uuid`));",
                "CREATE TABLE IF NOT EXISTS `mined_ores` (`uuid` VARCHAR(120), diamond int(11), gold int(11), iron int(11), coal int(11), total int(11), PRIMARY KEY (`uuid`));",
                "CREATE TABLE IF NOT EXISTS `levels` (`uuid` VARCHAR(120), level int(11), xp int(11), total_xp int(11), PRIMARY KEY (`player_name`));",
                "CREATE TABLE IF NOT EXISTS `player_aliases` (`player_name` VARCHAR(32), `addresses` LONGTEXT, PRIMARY KEY(`uuid`));",
                "CREATE TABLE IF NOT EXISTS `settings` (`uuid` VARCHAR(120), scoreboard TEXT, privacy TEXT, chat TEXT, `treefall` TEXT, PRIMARY KEY(`uuid`));",
                "CREATE TABLE IF NOT EXISTS `mail` (`uuid` VARCHAR(120), `last_mail` BIGINT(11), `streak` int(11), `tickets` int(11), PRIMARY KEY(`uuid`));",
                "CREATE TABLE IF NOT EXISTS `warps` (`name` VARCHAR(32), `display_name` TEXT, `loc_x` int(11), `loc_y` int(11), `loc_z` int(11), `loc_pitch` float, `loc_yaw` float, `world` TEXT, `description` LONGTEXT, PRIMARY KEY(`name`));"
        };

        for(String query : queries) {
            try {
                if(!update(query)) {
                    System.out.println("Could not execute query (" + query + ")");
                }
            } catch(SQLException ex){
                ex.printStackTrace();
                System.out.println("Could not setup the database");
            }
        }

    }

    public static Connection getConnection() {
        return SQL.conn;
    }

    public static boolean hasConnection() throws SQLException {

        return SQL.conn != null && !SQL.conn.isClosed();
    }

    public static ResultSet query(String sql) throws SQLException {

        if(!SQL.hasConnection()) {
            SQL.setup();
        }
        Statement s = getConnection().createStatement();
        System.out.println("Executing database query '" + sql +  "'...");
        return s.executeQuery(sql);
    }

    public static boolean update(String sql) throws SQLException {

        if(!SQL.hasConnection()) {
            SQL.setup();
        }
        int result = getConnection().createStatement().executeUpdate(sql);
        System.out.println("Executing database query '" + sql +  "'...");
        return result > 0 ? true : false ;
    }

}
