package de.lightplugins.itemdrop;

import de.lightplugins.master.Ashura;
import de.lightplugins.util.ItemGlow;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class ItemDrop implements Listener {

    private Map<UUID, BukkitRunnable> itemTimers = new HashMap<>();

    @EventHandler
    public void onWheatBreak(BlockBreakEvent e) {

        Block block = e.getBlock();
        World world = e.getPlayer().getWorld();

        if(!world.getName().equalsIgnoreCase("domarsk_10")) {
            return;
        }

        Bukkit.getLogger().log(Level.WARNING, "TEST " + block.getBlockData().getAsString());

        if(block.getType().equals(Material.WHEAT)) {
            Ageable ageable = (Ageable) block.getBlockData();
            if(ageable.getAge() == 7) {
                block.getDrops().clear();
                block.getDrops().add(new ItemStack(Material.WHEAT, 1));
            }
        }

    }

    @EventHandler
    public void onBlockBreakEvent(ItemSpawnEvent e) {

        Item item = e.getEntity();

        UUID itemUUID = item.getUniqueId();
        ItemStack itemStack = item.getItemStack();

        if(itemStack.getItemMeta() == null) {
            return;
        }

        if(!itemTimers.containsKey(itemUUID)) {
            startTimer(item);
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent e) {
        Item item = e.getEntity();
        UUID itemUUID = item.getUniqueId();

        itemTimers.remove(itemUUID);
        startTimer(item);

    }

    private void startTimer(Item item) {

        UUID itemUUID = item.getUniqueId();

        BukkitRunnable timerTask = new BukkitRunnable() {

            int timerTicks = 61;

            @Override
            public void run() {

                timerTicks--;

                int stackAmount = item.getItemStack().getAmount();

                if(timerTicks <= 0) {

                    Location location = item.getLocation();

                    if(location.getWorld() == null) {
                        item.remove();
                        cancel();
                        itemTimers.remove(itemUUID);
                        return;
                    }

                    location.getWorld().spawnParticle(Particle.LAVA, location, 20);
                    item.remove();
                    cancel();
                    itemTimers.remove(itemUUID);
                    return;
                }
                String itemName = "";

                item.setCustomName(null);
                item.setCustomNameVisible(false);

                if(item.getItemStack().getItemMeta() == null) {
                    item.setCustomName(item.getName());
                }

                itemName = Ashura.colorTranslation.hexTranslation(
                        "&7[#dc143d" + timerTicks + "&7] &f" + stackAmount + " &7x &f" + item.getName());

                if(item.getItemStack().getType().equals(Material.AIR)) {
                    cancel();
                    itemTimers.remove(itemUUID);
                    return;
                }
                if(item.getItemStack().getItemMeta().hasDisplayName()) {
                    itemName = Ashura.colorTranslation.hexTranslation(
                            "&7[#dc143d" + timerTicks + "&7] &f" + stackAmount + " &7x " + item.getItemStack().getItemMeta().getDisplayName());

                }

                item.setCustomName(itemName);
                item.setCustomNameVisible(true);
                item.setGlowing(true);
                ItemGlow.setGlowColor(ChatColor.GRAY, item);

            }
        };

        itemTimers.put(itemUUID, timerTask);
        timerTask.runTaskTimer(Ashura.getInstance, 0, 20);
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {

        Item item = e.getItemDrop();
        UUID itemUUID = item.getUniqueId();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getItemMeta() == null) {
            return;
        }

        if(!itemTimers.containsKey(itemUUID)) {

            startTimer(item);

        }
    }



    @EventHandler
    public void onItemPickUp(EntityPickupItemEvent e) {

        Item item = e.getItem();
        UUID itemUUID = item.getUniqueId();

        if(itemTimers.containsKey(itemUUID)) {

            BukkitRunnable timerTask = itemTimers.get(itemUUID);
            timerTask.cancel();
            itemTimers.remove(itemUUID);
            Bukkit.getLogger().log(Level.WARNING, "SIZE " + ItemGlow.teams.size());
        }
    }
}
