package com.renyuansurvival.bettermelon;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;


public final class BetterMelon extends JavaPlugin implements Listener {

    private boolean Residence = true;
    private final List<String> list = Collections.singletonList("reload");
    private static String Prefix;
    private static BetterMelon Plugin;
    private static String Melon;
    private static String Pumpkin;

    @Override
    public void onEnable() {
        Plugin = this;
        if (Bukkit.getPluginManager().getPlugin("Residence") == null) Residence = false;
        saveDefaultConfig();
        refreshConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        requireNonNull(getCommand("bettermelon")).setExecutor(this);
        requireNonNull(getCommand("bettermelon")).setTabCompleter(this);
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (!event.isCancelled() &&
                !(Residence && ResidenceApi.getResidenceManager().getByLoc(block.getLocation()) != null && !ResidenceApi.getResidenceManager().getByLoc(block.getLocation()).getPermissions().playerHas(player,Flags.destroy,true)) &&
                event.getItem() == null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null && hand != null && hand.equals(EquipmentSlot.HAND) &&
                ((getConfig().getBoolean("type.melon",true) && block.getType().equals(Material.MELON)) || (getConfig().getBoolean("type.pumpkin",true) && block.getType().equals(Material.PUMPKIN)))) {
            Material blockType = block.getType();
            if(getConfig().getBoolean("settings.no-drop",false)){
                block.setType(Material.AIR);
                if(getConfig().getBoolean("settings.silk-touch",false)){
                    player.getInventory().addItem(new ItemStack(blockType));
                }else{
                    for (ItemStack drops : block.getDrops()) {
                        player.getInventory().addItem(drops);
                    }
                }
            }else{
                if(getConfig().getBoolean("settings.silk-touch",false)) {
                    block.setType(Material.AIR);
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(blockType));
                }else{
                    block.breakNaturally();
                }
            }
            if (getConfig().getBoolean("message.enable",false)) {
                String blockName = blockType.equals(Material.MELON) ? Melon : Pumpkin;
                sendMessage(player,getConfig().getString("message.message", "??????????????????") + blockName);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("bettermelon.reload")){
            sendMessage(sender, getConfig().getString("message.no-permission","???????????????"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            refreshConfig();
            sendMessage(sender, getConfig().getString("message.reload","???????????????"));
            return true;
        }

        sendMessage(sender, getConfig().getString("message.command", "/bettermelon reload - ????????????"));
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? list : null ;
    }

    public static BetterMelon getPlugin() {
        return Plugin;
    }

    public static void refreshConfig(){
        FileConfiguration config = getPlugin().getConfig();
        Prefix = config.getString("message.prefix","&f[&6?????????&f] ");
        Melon = config.getString("message.melon", "??????");
        Pumpkin = config.getString("message.pumpkin", "??????");
    }

    public static void sendMessage(CommandSender player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Prefix + message));
    }
}
