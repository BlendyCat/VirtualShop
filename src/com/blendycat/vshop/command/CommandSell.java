package com.blendycat.vshop.command;

import com.blendycat.vshop.ShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by EvanMerz on 8/18/17.
 */
public class CommandSell implements CommandExecutor {

    private ShopPlugin main;

    public CommandSell(ShopPlugin main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length >= 1){

                if(isDouble(args[0])){
                    if(!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                        ItemStack item = player.getInventory().getItemInMainHand();
                        main.getQueryManager().insertItem(player, item.getType(),
                                item.getDurability(), Double.parseDouble(args[0]),
                                item.getAmount());
                        player.sendMessage(ChatColor.AQUA+"Item put on shop!");
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
                                new ItemStack(Material.AIR));
                    }else{
                        player.sendMessage(ChatColor.RED+"You must be holding what you want to sell!");
                    }

                }else{
                    player.sendMessage(ChatColor.RED+"Invalid Price!");
                }

            }else{
                //incorrect argument length
                player.sendMessage(ChatColor.RED + "Not enough arguments! \n" +
                        "Correct Syntax: /sell <price> [amount]");
            }

        }else{
            //sender is not player
            sender.sendMessage("Error: This command is only for players!");
        }
        return true;
    }

    private boolean isDouble(String d){
        try{
            double r = Double.parseDouble(d);
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    private boolean isInt(String i){
        try{
            double r = Integer.parseInt(i);
            return true;
        }catch(Exception ex){
            return false;
        }
    }
}
