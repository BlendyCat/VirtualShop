package com.blendycat.vshop;

import com.blendycat.vshop.command.CommandSell;
import com.blendycat.vshop.sql.QueryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by EvanMerz on 8/18/17.
 */
public class ShopPlugin extends JavaPlugin {
    private Connection connection;
    private QueryManager qm;
    public static Economy eco = null;

    @Override
    public void onEnable(){
        if (!setupEconomy() ) {
            getLogger().info("disabled due to vault not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setUpConfig(getConfig());
        connection = getConnection(getConfig());
        qm = new QueryManager(connection);
        qm.createTables();
        getCommand("sell").setExecutor(new CommandSell(this));
    }

    @Override
    public void onDisable(){

    }

    private void setUpConfig(FileConfiguration config){
        config.addDefault("db_host", null);
        config.addDefault("db_port", null);
        config.addDefault("db_name", null);
        config.addDefault("db_user", null);
        config.addDefault("db_password", null);
        config.options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
    }

    /**
     * Connects to database and returns connection
     * @param config plugin configuration
     * @return connection to database
     */
    private Connection getConnection(FileConfiguration config){
        Connection conn = null;
        Properties connectionProperties = new Properties();
        //
        if(!config.isSet("db_host") ||
                !config.isSet("db_port") ||
                !config.isSet("db_username") ||
                !config.isSet("db_password") ||
                !config.isSet("db_name")) {
            getLogger().info("Database login information " +
                    "not configured correctly! Please make sure " +
                    "all values are filled out completely!");
            getPluginLoader().disablePlugin(this);
        }
        //put username and password in connectionProperties
        connectionProperties.put("user", config.getString("db_username"));
        connectionProperties.put("password", config.getString("db_password"));

        String connectionURL = "jdbc:mysql://"+config.getString("db_host")+
                ":"+config.getString("db_port")+"/"+config.getString("db_name");
        //connect to the database
        try{
            conn = DriverManager.getConnection(connectionURL, connectionProperties);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        //returns the connection
        return conn;
    }

    public QueryManager getQueryManager(){
        return qm;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }


}
