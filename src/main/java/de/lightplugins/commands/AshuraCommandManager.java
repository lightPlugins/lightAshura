package de.lightplugins.commands;

import de.lightplugins.commands.border.BorderMenuCommand;
import de.lightplugins.commands.boxes.GiveBoxesCommand;
import de.lightplugins.commands.main.ReloadCommand;
import de.lightplugins.commands.trades.CustomTradeCommand;
import de.lightplugins.master.Ashura;
import de.lightplugins.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AshuraCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    public Ashura plugin;
    public AshuraCommandManager(Ashura plugin) {
        this.plugin = plugin;
        //  Subcommand register here
        subCommands.add(new ReloadCommand());
        subCommands.add(new GiveBoxesCommand());
        subCommands.add(new CustomTradeCommand());
        subCommands.add(new BorderMenuCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {

                        try {
                            if(getSubCommands().get(i).perform(player, args)) {
                                //  hello, im a dummy comment - nothing to see here
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }
}
