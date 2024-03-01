package de.lightplugins.util;

import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.willfp.eco.core.events.EntityDeathByEntityEvent;
import de.lightplugins.master.Ashura;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class TestEvent implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        Entity droppedItem = event.getItemDrop();

        event.getItemDrop().setOwner(event.getPlayer().getUniqueId());
        droppedItem.setVisibleByDefault(false);
        event.getPlayer().showEntity(Ashura.getInstance, droppedItem);

    }

    @EventHandler
    public void onDrop(EntityDeathByEntityEvent event) {

        List<ItemStack> test = event.getDrops();

        test.forEach(single -> {

            if(single instanceof Item item) {
                if(event.getKiller() instanceof Player player) {
                    item.setOwner(player.getUniqueId());
                    item.setVisibleByDefault(false);
                    player.showEntity(Ashura.getInstance, item);
                }
            }
        });
    }

    @EventHandler
    public void onDrop(PlayerFishEvent event) {

        Entity droppedItem = event.getCaught();

        if(event.getCaught() instanceof Item item) {
            item.setOwner(event.getPlayer().getUniqueId());
            item.setVisibleByDefault(false);
            event.getPlayer().showEntity(Ashura.getInstance, droppedItem);
        }
    }

    @EventHandler
    public void onDrop(BlockDropItemEvent event) {

        List<Item> test = event.getItems();

        test.forEach(single -> {

            single.setOwner(event.getPlayer().getUniqueId());
            single.setVisibleByDefault(false);
            event.getPlayer().showEntity(Ashura.getInstance, single);
        });
    }

    @EventHandler
    public void onItemCollect(EntityPickupItemEvent event) {

        if(event.getEntity() instanceof Player player) {

            if(event.getItem().getOwner() == null) {
                return;
            }

            if(!event.getItem().getOwner().equals(player.getUniqueId())) {
                event.setCancelled(true);
            }

        }
    }
}
