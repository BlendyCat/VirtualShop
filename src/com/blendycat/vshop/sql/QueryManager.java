package com.blendycat.vshop.sql;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;

/**
 * Created by EvanMerz on 8/18/17.
 */
public class QueryManager {
    private Connection conn;

    public QueryManager(Connection conn){
        this.conn = conn;
    }


    public void createTables(){
        try{
            CallableStatement stmt = conn.prepareCall(
                    "CREATE TABLE IF NOT EXISTS `blendy_shop` (" +
                            "`ID` INT NOT NULL AUTO_INCREMENT," +
                            "`UUID`, VARCHAR(255) NOT NULL," +
                            "`Material` VARCHAR(255) NOT NULL," +
                            "`Meta` INT NOT NULL DEFAULT '0'," +
                            "`Price` DECIMAL(6,2) NOT NULL," +
                            "`Amount` INT NOT NULL," +
                            "PRIMARY KEY(ID));");
            stmt.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Inserts new row if not exists and adds to existing results
     * @param seller seller of item
     * @param material material of item
     * @param meta meta of item
     * @param price price of item
     * @param amount amount of item being sold
     */
    public void insertItem(Player seller, Material material, int meta, double price, int amount){
        try{
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT ID, Amount FROM blendy_shop WHERE Material = ? AND " +
                    "Meta = ? AND UUID = ?;");
            stmt.setString(0, material.toString());
            stmt.setInt(1, meta);
            stmt.setString(2, seller.getUniqueId().toString());
            stmt.execute();
            ResultSet result = stmt.getResultSet();
            result.last();
            seller.sendMessage(ChatColor.AQUA + "There are " + result.getRow() + " rows!");
            if(result.getRow() >= 1) {
                //adds amount to previous row
                int id = result.getInt(0);
                int oldAmount = result.getInt(1);
                stmt = conn.prepareStatement(
                        "UPDATE blendy_shop" +
                                "SET Amount = ? WHERE " +
                                "Material = ? AND" +
                                "Meta = ? AND" +
                                "UUID = ?;");
                stmt.setInt(0, oldAmount+amount);
                stmt.setString(1, material.toString());
                stmt.setInt(2, meta);
                stmt.setString(3, seller.getUniqueId().toString());
            }else {
                //adds as new row to table
                stmt = conn.prepareStatement(
                        "INSERT INTO blendy_shop(`UUID`, `Material`, `Meta`, `Price`, `Amount`) " +
                                "VALUES(?, ?, ?, ?, ?);");
                stmt.setString(0, seller.getUniqueId().toString());
                stmt.setString(1, material.toString());
                stmt.setInt(2, meta);
                stmt.setDouble(3, price);
                stmt.setInt(4, amount);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
