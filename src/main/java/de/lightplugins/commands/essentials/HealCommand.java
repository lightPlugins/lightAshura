package de.lightplugins.commands.essentials;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;

        if(args.length == 1 ) {

            if(!player.hasPermission("ashura.admin.command.heal.other")) {
                Ashura.util.sendMessage(player, "&cDu hast für diesen Befehl keine Berechtigung&7!");
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null) { return false; }

            double maxHealth = target.getMaxHealth();

            target.setHealth(maxHealth);
            target.setFoodLevel(20);
            target.setSaturation(20);
            Ashura.util.sendMessage(player, "&7Du hast #dc143d" + args[0] + " &7geheilt.");
            Ashura.util.sendMessage(target, "&7Du wurdest von #dc143d" + player.getName() + " &7geheilt.");
            return false;
        }

        if(!player.hasPermission("ashura.admin.command.heal")) {
            Ashura.util.sendMessage(player, "&cDu hast für diesen Befehl keine Berechtigung&7!");
            return false;
        }

        double maxHealth = player.getMaxHealth();

        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        Ashura.util.sendMessage(player, "&7Du hast dich #dc143derfolgreich &7geheilt.");
        return false;
    }
}
