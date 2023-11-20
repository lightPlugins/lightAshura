package de.lightplugins.events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;


public class DropManipulation implements Listener {


    @EventHandler
    public void onWheatBreak(BlockBreakEvent e) {

        World world = e.getPlayer().getWorld();

        if(!world.getName().equalsIgnoreCase("domarsk_10")) {
            return;
        }

        if(e.getBlock().getType().equals(Material.WHEAT)) {
            Ageable ageable = (Ageable) e.getBlock().getBlockData();
            if(ageable.getAge() == 7) {
                e.setDropItems(false);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.WHEAT, 1));
            }
        } else if (e.getBlock().getType().equals(Material.BEETROOT)) {
            Ageable ageable = (Ageable) e.getBlock().getBlockData();
            if(ageable.getAge() == 3) {
                e.setDropItems(false);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.BEETROOT, 1));
            }

        }
    }
}
