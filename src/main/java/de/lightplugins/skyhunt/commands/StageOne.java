package de.lightplugins.skyhunt.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class StageOne implements SuperiorCommand {

    private static final int MAX_ISLAND_SIZE = 200;

    @Override
    public List<String> getAliases() {
        // A list of aliases. The first argument will be the label of the subcommand.
        return Collections.singletonList("skyhunt");
    }

    @Override
    public String getPermission() {
        // The required permission for the command. If you don't want a specific permission, use "".
        return "lightashura.admin";
    }

    @Override
    public String getUsage(Locale locale) {
        // The usage of the command. Should only include the label & arguments of the command.
        return "skyhunt <playername> <stage>";
    }

    @Override
    public String getDescription(Locale locale) {
        // The description of the command, which will be shown in /is help.
        return "Create an island with the specified schematic file";
    }

    @Override
    public int getMinArgs() {
        // Minimum arguments for the command, including the label.
        return 1;
    }

    @Override
    public int getMaxArgs() {
        // Maximum arguments for the command, including the label.
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        // Whether the command can be executed from Console.
        return true;
    }

    @Override
    public boolean displayCommand() {
        // Whether the command would be displayed in the /is help list.
        return false;
    }

    @Override
    public void execute(SuperiorSkyblock plugin, CommandSender sender, String[] args) {
        // I know that the sender is a player, as I disabled the console from executing.
        // TODO check player is not null
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer((Player) sender);
        List<Island> allIslands = plugin.getGrid().getIslands();

        AtomicBoolean hasAlreadyIsland = new AtomicBoolean(false);
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);

        if(offlinePlayer == null) {
            sender.sendMessage("§cThis player was not found!");
            return;
        }

        String islandID = offlinePlayer.getUniqueId() + "#" + args[2];

        allIslands.forEach(singleIsland -> {

            if(singleIsland.getName().equals(offlinePlayer.getUniqueId() + "_" + args[2])) {
                sender.sendMessage("§cDu hast schon eine Insel!");
                hasAlreadyIsland.set(true);
            }
        });

        if(!hasAlreadyIsland.get()) {
            plugin.getGrid().createIsland(superiorPlayer, "stage-" + args[2],
                    BigDecimal.ONE, Biome.PLAINS, islandID);
            sender.sendMessage(
                    "§aSkyhunt Stage " + args[2] + " für Spieler " +offlinePlayer.getName() + " erfolgreich erstellt!");
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblock plugin, CommandSender sender, String[] args) {
        // I don't want any tab completes for this command.
        return new ArrayList<>();
    }

}
