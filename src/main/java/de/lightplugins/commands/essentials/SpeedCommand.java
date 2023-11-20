package de.lightplugins.commands.essentials;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;

        if(args.length == 2) {

            if(!player.hasPermission("ashura.admin.command.speed.other")) {
                Ashura.util.sendMessage(player, "&cDu hast für diesen Befehl keine Berechtigung&7!");
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                Ashura.util.sendMessage(player,
                        "&cDieser Spieler existiert nicht&7!");
                return false;
            }

            try {

                float speed = (Float.parseFloat(args[0]));

                if(speed < 0f) {
                    Ashura.util.sendMessage(player,
                            "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d5");
                    return false;
                }
                 if(speed > 5f) {
                     Ashura.util.sendMessage(player,
                             "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d5");
                     return false;
                 }

                target.setWalkSpeed((speed / 10) * 2);
                target.setFlySpeed((speed / 10) * 2);

                Ashura.util.sendMessage(player,
                        "&7Du hast dem Spieler #dc143d" + args[1] + " &7Speed #dc143d" + args[0] + " &7gegeben.");
                Ashura.util.sendMessage(target,
                        "&7Deine Geschwindigkeit wurde auf #dc143d" + args[0] + " &7gesetzt");

                return false;

            } catch (NumberFormatException e) {

                Ashura.util.sendMessage(player,
                        "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d10");
                return false;

            }
        }

        if(args.length == 1) {

            if(!player.hasPermission("ashura.admin.command.speed")) {
                Ashura.util.sendMessage(player, "&cDu hast für diesen Befehl keine Berechtigung&7!");
                return false;
            }

            try {
                float speed = (Float.parseFloat(args[0]));

                if(speed < 0f) {
                    Ashura.util.sendMessage(player,
                            "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d10");
                    return false;
                }
                if(speed > 5f) {
                    Ashura.util.sendMessage(player,
                            "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d10");
                    return false;
                }

                player.setWalkSpeed((speed / 10) * 2);
                player.setFlySpeed((speed / 10));

                Ashura.util.sendMessage(player,
                        "&7Du hast deine Geschwindigkeit auf #dc143d" + args[0] + " &7gesetzt");

                return false;

            } catch (NumberFormatException e) {

                Ashura.util.sendMessage(player,
                        "&cUngültige Zahl. Verwende #dc143d1 &7- #dc143d10");
                return false;

            }
        }

        return false;
    }

}
