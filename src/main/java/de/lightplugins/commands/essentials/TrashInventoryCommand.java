package de.lightplugins.commands.essentials;

import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class TrashInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        String title = Ashura.colorTranslation.hexTranslation("#ffdc73ASHURA &8| &8Mülleimer");

        if(args.length == 1) {

            Inventory inv = Bukkit.createInventory(null, 27, title);
            player.openInventory(inv);
            return false;
        }

        return false;
    }
}
