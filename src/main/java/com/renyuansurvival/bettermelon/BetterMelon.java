package com.renyuansurvival.bettermelon;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;


public final class BetterMelon extends JavaPlugin implements Listener {

    private boolean Residence = true;
    private String Prefix;
    private ItemStack silkAxe;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Prefix = getConfig().getString("message.prefix","&f[&6服务器&f] ");
        Bukkit.getPluginManager().registerEvents(this,this);
        requireNonNull(getCommand("bettermelon")).setExecutor(this);
        requireNonNull(getCommand("bettermelon")).setTabCompleter(this);
        try{
            ResidenceApi.getResidenceManager();
        }catch (NoClassDefFoundError error){
            Residence = false;
        }
        silkAxe = new ItemStack(Material.STONE_AXE);
        silkAxe.addEnchantment(Enchantment.SILK_TOUCH, 1);

    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        if (!event.isCancelled()) {
            Block block = event.getClickedBlock();
            ItemStack item = event.getItem();
            Player player = event.getPlayer();
            if (event.getHand().equals(EquipmentSlot.HAND) && block != null && item == null && !(Residence && ResidenceApi.getResidenceManager().getByLoc(block.getLocation()) != null && !ResidenceApi.getResidenceManager().getByLoc(block.getLocation()).getPermissions().playerHas(event.getPlayer(), Flags.destroy,true))) {
                Material blockType = block.getType();
                if (((getConfig().getBoolean("type.melon",true) && blockType.equals(Material.MELON)) || (getConfig().getBoolean("type.pumpkin",true) && blockType.equals(Material.PUMPKIN))) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if(getConfig().getBoolean("settings.no-drop",false)){
                        Collection<ItemStack> dropsItem;
                        if(getConfig().getBoolean("settings.silk-touch",false)) {
                            dropsItem = block.getDrops(silkAxe);
                        }else{
                            dropsItem = block.getDrops();
                        }
                        List<ItemStack> dropList = new ArrayList<>(dropsItem);
                        block.setType(Material.AIR);
                        player.getInventory().addItem(dropList.get(0));
                    }else{
                        if (getConfig().getBoolean("settings.silk-touch", false)) {
                            block.breakNaturally(silkAxe);
                        } else {
                            block.breakNaturally();
                        }
                    }
                    if (getConfig().getBoolean("message.enable",false)) {
                        String blockName;
                        if (blockType.equals(Material.MELON)) {
                            blockName = getConfig().getString("message.melon", "西瓜");
                        } else {
                            blockName = getConfig().getString("message.pumpkin", "南瓜");
                        }
                        if (getConfig().getBoolean("message.enable", false)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Prefix + getConfig().getString("message.message", "你采集了一个") + blockName));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
            if (sender.hasPermission("bettermelon.reload")) {
                reloadConfig();
                Prefix = getConfig().getString("message.prefix", "&f[&6服务器&f] ");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Prefix + getConfig().getString("message.reload", "插件已重载")));
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Prefix + getConfig().getString("message.no-permission", "你没有权限")));
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Prefix + getConfig().getString("message.command", "/bettermelon reload - 重载插件")));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            List<String> list = new ArrayList<>();
            list.add("reload");
            return list;
        }
        return null;
    }
}
