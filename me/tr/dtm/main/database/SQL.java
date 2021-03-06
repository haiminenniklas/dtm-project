package me.tr.dtm.main.database;

import me.tr.dtm.main.DTM;
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
                        DTM.log("Could not create SQLite Database file (#createNewFile())");
                    }
                } catch (IOException e) {
                    System.out.println("Could not create Database file");
                    e.printStackTrace();
                }

            }

            try {
                Class.forName("org.sqlite.JDBC");
                SQL.conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                DTM.log("Opened database successfully!");

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
                DTM.log("Opened database successfully");

                queries();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void queries(){

        String[] queries = new String[] {

                "CREATE TABLE IF NOT EXISTS `dtm_maps` (`name` VARCHAR(32), `red_spawnpoint` TEXT, `blue_spawnpoint` TEXT, `red_monument` LONGTEXT, `blue_monument` LONGTEXT, `world` TEXT,  PRIMARY KEY(`name`));",
                "CREATE TABLE IF NOT EXISTS `dtm_players` (`uuid` VARCHAR(64), `points` int(11), `kills` int(11), `deaths` int(11), `matches_won` int(11), `matches_lost` int(11), PRIMARY KEY(`uuid`));"

        };

        for(String query : queries) {
            try {
                if(!update(query)) {
                    DTM.log("Could not execute query (" + query + ")");
                }
            } catch(SQLException ex){
                ex.printStackTrace();
                DTM.log("Could not setup the database");
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
        if(DTM.getConfig().getBoolean("log-queries"))
            DTM.log("Executing database query '" + sql + "'...");
        return s.executeQuery(sql);
    }

    public static boolean update(String sql) throws SQLException {

        if(!SQL.hasConnection()) {
            SQL.setup();
        }
        int result = getConnection().createStatement().executeUpdate(sql);
        if(DTM.getConfig().getBoolean("log-queries"))
            DTM.log("Executing database query '" + sql +  "'...");
        return result > 0 ? true : false ;
    }

}
