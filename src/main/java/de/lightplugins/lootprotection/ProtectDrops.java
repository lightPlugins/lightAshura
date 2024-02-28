package de.lightplugins.lootprotection;

import de.lightplugins.master.Ashura;
import de.lightplugins.util.ItemGlow;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtectDrops implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if(event.getEntity() instanceof Player player) {

            Item item = event.getItem();

            if(item.getOwner() != null) {
                if(item.getOwner().equals(player.getUniqueId())) {
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFishCatch(PlayerFishEvent event) {

        FileConfiguration settings = Ashura.settings.getConfig();

        boolean enableByFish = settings.getBoolean("settings.protectItems.onFishing");

        if (!enableByFish) {
            return;
        }

        int timer = settings.getInt("settings.protectItems.timer") * 20;

        if(event.getCaught() instanceof Item item) {
            item.setOwner(event.getPlayer().getUniqueId());
            item.setGlowing(true);
            ItemGlow.setGlowColor(ChatColor.RED, item);

            new BukkitRunnable() {
                @Override
                public void run() {
                    item.setOwner(null);
                    item.setGlowing(true);
                    ItemGlow.setGlowColor(ChatColor.GREEN, item);
                }
            }.runTaskLater(Ashura.getInstance, timer);
        }
    }
}
