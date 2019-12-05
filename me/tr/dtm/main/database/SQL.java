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

            String user = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            String address = config.getString("mysql.address");
            String port = String.valueOf(config.getInt("mysql.port"));
            String database = config.getString("mysql.database");

            try {
                Class.forName("com.mysql.jdbc.Driver");
                SQL.conn = DriverManager.getConnection("jdbc:mysql://" + address+ ":" + port + "/" + database, user, password);
                System.out.println("Opened database successfully");

                queries();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void queries(){

        String[] queries = new String[] {

                "CREATE TABLE IF NOT EXISTS `dtm_maps` (`name` VARCHAR(32), `title` TEXT, `red_spawnpoint` TEXT, `blue_spawnpoint` TEXT, `monuments` LONGTEXT, PRIMARY KEY(`name`));"

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
