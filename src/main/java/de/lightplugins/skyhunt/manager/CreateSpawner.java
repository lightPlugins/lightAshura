package de.lightplugins.skyhunt.manager;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import org.bukkit.Location;

public class CreateSpawner {

    public void createMythicSpawner(Location location, String islandID, String stage) {
        SpawnerManager spawnerManager = MythicBukkit.inst().getSpawnerManager();
        spawnerManager.createSpawner(islandID, location, stage);
        spawnerManager.getSpawnerByName(islandID).setMaxMobs(PlaceholderInt.of("8"));
        spawnerManager.getSpawnerByName(islandID).setCooldownSeconds(30);
        spawnerManager.getSpawnerByName(islandID).setActivationRange(150);
        spawnerManager.getSpawnerByName(islandID).setSpawnRadius(3);
        spawnerManager.getSpawnerByName(islandID).setMobsPerSpawn(8);
        spawnerManager.getSpawnerByName(islandID).setLeashRange(150);
        spawnerManager.getSpawnerByName(islandID).setShowFlames(true);

    }
}
