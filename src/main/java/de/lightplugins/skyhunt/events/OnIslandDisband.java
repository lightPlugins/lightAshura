package de.lightplugins.skyhunt.events;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandLeaveEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import de.lightplugins.master.Ashura;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;


public class OnIslandDisband implements Listener {

    @EventHandler
    public void onIslandDisband(IslandDisbandEvent event) {
        deleteIsland(event.getIsland(), event.getPlayer());
    }

    private void deleteIsland(Island island,SuperiorPlayer superiorPlayer) {

        for (File file : Objects.requireNonNull(MythicBukkit.inst().getSpawnerManager().getSpawnerFolder().listFiles())) {
            if(file.getName().contains(island.getName())) {

                Bukkit.getLogger().log(Level.WARNING, "FILENAME: " + file.getName());

                SpawnerManager spawnerManager = MythicBukkit.inst().getSpawnerManager();
                MythicSpawner mythicSpawner = spawnerManager.getSpawnerByName(file.getName().replace(".yml", ""));
                spawnerManager.removeSpawner(mythicSpawner);

            }
        }

        Ashura.getInstance.localSkyhuntData.remove(island.getName());
        Ashura.getInstance.skyhuntPlayerData.deleteIsland(island.getName()).thenAccept(result -> {
            if(result) {
                // SuperiorPlayer.asPlayeR() is null lol
                return;
            }
        });
    }
}
