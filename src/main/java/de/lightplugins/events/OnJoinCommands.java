package de.lightplugins.events;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class OnJoinCommands implements Listener {




    @EventHandler
    public void onJoinDoCommand(PlayerLoginEvent event) {

        Player player = event.getPlayer();

        FileConfiguration settings = Ashura.settings.getConfig();

        boolean enable = settings.getBoolean("settings.spawnOnJoin.enable");

        if(!enable) {
            return;
        }

        String command = settings.getString("settings.spawnOnJoin.command");

        if(command == null) {
            return;
        }

        command = command.replace("#player#", player.getName());

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);

    }
}
