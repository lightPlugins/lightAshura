package de.lightplugins.skyhunt.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import de.lightplugins.master.Ashura;
import de.lightplugins.skyhunt.manager.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Objects;
import java.util.logging.Level;

public class OnMobKill implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {

        Player player = event.getEntity().getKiller();
        LivingEntity deathMob = event.getEntity();

        if(player == null)  {
            Bukkit.getLogger().log(Level.WARNING, "player in OnMobKill is null");
            return;
        }

        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);

        if(superiorPlayer == null) {
            Bukkit.getLogger().log(Level.WARNING, "superiorPlayer in OnMobKill is null");
            return;
        }

        Island island = SuperiorSkyblockAPI.getIslandAt(superiorPlayer.getLocation());

        if(!island.getOwner().equals(superiorPlayer)) {
            Ashura.util.sendMessage(player, "Auf dieser Insel kannst du deine Kills nicht sehen!");
        }

        if(!Objects.equals(event.getEntity().getKiller(), player)) {
            Bukkit.getLogger().log(Level.WARNING, "Entity death by non player");
            return;
        }

        Ashura.getInstance.skyhuntPlayerData.getKills(island.getName()).thenAccept(currentKills -> {

            currentKills = currentKills + 1;

            Ashura.getInstance.skyhuntPlayerData.updateKills(island.getName(), currentKills).thenAccept(result -> {
                if(result) {
                    Bukkit.getLogger().log(Level.WARNING, "kills successfully updated");
                    LootManager lootManager = new LootManager(player, island, event.getEntity().getLocation());
                    lootManager.init();
                }
            });
        });
    }
}
