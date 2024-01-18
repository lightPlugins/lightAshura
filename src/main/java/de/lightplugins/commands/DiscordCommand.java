package de.lightplugins.commands;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        FileConfiguration messages = Ashura.messages.getConfig();

        if(!(commandSender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(Ashura.consolePrefix +
                    "This command can only be executed by an player");
            return false;
        }

        Player player = (Player) commandSender;

        if(args.length == 0) {

            Ashura.util.sendMessageList(player, messages.getStringList("discordMessage"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, (float)1.8, (float)1.0);
            return false;

        }

        return false;
    }
}
