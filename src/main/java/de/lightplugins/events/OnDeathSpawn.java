package de.lightplugins.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnDeathSpawn implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Bukkit.getServer().dispatchCommand(
                Bukkit.getServer().getConsoleSender(), "spawn " + player.getName());
    }
}
