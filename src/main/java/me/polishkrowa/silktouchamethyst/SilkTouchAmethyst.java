package me.polishkrowa.silktouchamethyst;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class SilkTouchAmethyst extends JavaPlugin implements Listener {

    private static final HashMap<Player, List<Location>> brokenBlocks = new HashMap<>();
    public static boolean usePermission = true;
    public static String warnMessage = "";


    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        usePermission = getConfig().getBoolean("usePermission");
        warnMessage = getConfig().getString("breakWarning");
    }

    @Override
    public void onDisable() { }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.BUDDING_AMETHYST))
            return;
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR))
            return;

        List<Location> list;
        if (!brokenBlocks.containsKey(event.getPlayer())) {
            list = new ArrayList<>();
        } else {
            list = brokenBlocks.get(event.getPlayer());
        }

        if (!event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH) || (usePermission && !event.getPlayer().hasPermission("silktouchamethyst.use"))) {
            if (list.contains(event.getBlock().getLocation())) {
                list.remove(event.getBlock().getLocation());
                brokenBlocks.put(event.getPlayer(), list);
                return;
            }


            list.add(event.getBlock().getLocation());
            brokenBlocks.put(event.getPlayer(), list);
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', warnMessage));

            return;
        }

        event.setExpToDrop(0);
        event.setDropItems(false);
        Location loc = event.getBlock().getLocation();
        World world = event.getBlock().getWorld();

        world.dropItemNaturally(loc, new ItemStack(Material.BUDDING_AMETHYST));
        list.remove(event.getBlock().getLocation());
        brokenBlocks.put(event.getPlayer(), list);
    }

    @EventHandler
    public static void onPlayerLeave(PlayerQuitEvent event) {
        brokenBlocks.remove(event.getPlayer());
    }
}
