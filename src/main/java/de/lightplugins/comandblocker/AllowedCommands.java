package de.lightplugins.comandblocker;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Objects;

public class AllowedCommands implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {

        Player player = e.getPlayer();

        if(player.hasPermission("ashura.blockcommands.bypass")) {
            return;
        }

        FileConfiguration allowedCommands = Ashura.allowedCommands.getConfig();

        if(!allowedCommands.getBoolean("commands.enable")) {
            return;
        }

        Objects.requireNonNull(allowedCommands.getConfigurationSection("commands.data")).
        getKeys(false).forEach(data -> {

            List<String> allows = allowedCommands.getStringList("commands.data." + data + ".commandList");

            if(allows.contains(e.getMessage().toLowerCase())) {
                return;
            }

            String input = e.getMessage().toLowerCase();

            for(String cmd : allows) {

                if(input.startsWith(cmd)) {
                    return;
                }
            }

            String denyMessage = allowedCommands.getString("commands.data." + data + ".deny-message");

            Ashura.util.sendMessage(player, Ashura.colorTranslation.hexTranslation(denyMessage));
            e.setCancelled(true);

        });
    }
}
