package de.lightplugins.skyhunt.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import de.lightplugins.database.querys.SkyhuntPlayerData;
import de.lightplugins.master.Ashura;
import de.lightplugins.skyhunt.manager.CreateSpawner;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class OnIslandCreate implements Listener {

    @EventHandler
    public void onIslandCreate(IslandCreateEvent event) {

        BukkitScheduler scheduler = Bukkit.getScheduler();
        SuperiorPlayer superiorPlayer = event.getPlayer();
        Player physicalPlayer = event.getPlayer().asPlayer();

        SkyhuntPlayerData skyhuntPlayerData = Ashura.getInstance.skyhuntPlayerData;

        String islandID = event.getIsland().getName();

        if(Ashura.getInstance.localSkyhuntData.containsKey(islandID)) {
            superiorPlayer.asPlayer().sendMessage("§4Du hast bereits eine Insel mit dem Namen §c" + islandID);
            event.setCancelled(true);
            return;
        }

        event.setTeleport(false); //disable the auto teleport on island create

        Ashura.getInstance.localSkyhuntData.put(islandID, 0);

        skyhuntPlayerData.createNewStageForPlayer(islandID, 0).thenAccept(result -> {

            if(!result) {
                physicalPlayer.sendMessage("§cSomething went wrong on database execute query. Check logs");
                return;
            }

            Runnable task = () -> {
                Island targetIsland = SuperiorSkyblockAPI.getGrid().getIslandAt(superiorPlayer.getLocation());
                if (event.canTeleport() && targetIsland != null) {
                    Location teleportLocation = targetIsland.getTeleportLocation(World.Environment.NORMAL);
                    double radius = 200;

                    if(teleportLocation.getWorld() == null) {
                        return;
                    }

                    Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
                        List<Entity> nearbyEntities = (List<Entity>) teleportLocation.getWorld()
                                .getNearbyEntities(teleportLocation, radius, radius, radius, entity -> {
                                    if (entity instanceof ArmorStand) {
                                        ArmorStand armorStand = (ArmorStand) entity;
                                        return armorStand.getCustomName() != null
                                                && armorStand.getCustomName().contains("spawn"); //contains anstelle von equals wegen cursive schriftart !!!
                                    }
                                    return false;
                                });

                        if(nearbyEntities.isEmpty()) {
                            Bukkit.getLogger().log(Level.WARNING, "nerby entitie list is empty");
                            return;
                        }

                        AtomicInteger counter = new AtomicInteger();
                        nearbyEntities.forEach(singleEntity -> {
                            Bukkit.getLogger().log(Level.WARNING, "delete armostand: " + counter);
                            counter.getAndIncrement();
                            singleEntity.remove();
                            CreateSpawner spawnInterval = new CreateSpawner();
                            spawnInterval.createMythicSpawner(
                                    singleEntity.getLocation(), islandID + "_" + counter, targetIsland.getSchematicName());

                        });
                    });
                }
            };

            int taskid = scheduler.runTaskTimerAsynchronously(Ashura.getInstance, task, 0, 20).getTaskId();

            Bukkit.getScheduler().runTaskLater(Ashura.getInstance, () -> {

                SpawnerManager spawnerManager = MythicBukkit.inst().getSpawnerManager();
                spawnerManager.getSpawners().forEach(singleSpawner -> {
                    if(singleSpawner.getName().contains(superiorPlayer.getUniqueId().toString())) {
                        singleSpawner.Enable();
                        Bukkit.getLogger().log(Level.WARNING, "Spawner enabled für " + superiorPlayer.getUniqueId().toString());
                    }
                });

                physicalPlayer.sendMessage("Timer canceled and finished");
                scheduler.cancelTask(taskid);

            }, 200);
        });
    }
}
