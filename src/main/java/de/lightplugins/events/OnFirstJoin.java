package de.lightplugins.events;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.lightplugins.boxes.BoxesManager;
import de.lightplugins.enums.MessagePath;
import de.lightplugins.master.Ashura;
import de.lightplugins.master.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class OnFirstJoin implements Listener {


    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();



        FileConfiguration playerdata = Ashura.playerdata.getConfig();
        FileConfiguration settings = Ashura.settings.getConfig();

        List<String> playerlist = playerdata.getStringList("data");

        if(!settings.getBoolean("settings.starterBox.enable")) {
            return;
        }

        String boxID = settings.getString("settings.starterBox.boxID");

        if(playerlist.isEmpty()) {

            playerlist.add(uuid.toString());
            playerdata.set("data", playerlist);
            Ashura.playerdata.saveConfig();
            getStarterBox(player, boxID);
            return;

        }

        if(playerlist.contains(uuid.toString())) {
            return;
        }

        playerlist.add(uuid.toString());
        playerdata.set("data", playerlist);
        Ashura.playerdata.saveConfig();
        getStarterBox(player, boxID);

    }

    private void getStarterBox(Player player, String boxID) {


        new BukkitRunnable() {
            @Override
            public void run() {
                BoxesManager boxesManager = new BoxesManager();
                ItemStack is = boxesManager.getBox(boxID);
                if(is == null) {
                    Ashura.util.sendMessage(player,
                            "&cEs gibt probleme in der boxes.yml &7- &cBitte überprüfen&7!");
                    return;
                }
                player.getInventory().addItem(is);
                Ashura.util.sendMessage(player, MessagePath.getBoxOnFirstJoin.getPath());
            }
        }.runTaskLaterAsynchronously(Ashura.getInstance, 3*20);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if(!(e.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getEntity();

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if(e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            if(query.testState(BukkitAdapter.adapt(player.getLocation()), localPlayer, WorldGuardHook.NO_LAVA_DAMAGE)) {
                e.setCancelled(true);
            }
        }
    }
/*


    @EventHandler
    public void onTabCompletion(TabCompleteEvent e) {

        FileConfiguration allowedCommands = Ashura.allowedCommands.getConfig();

        if(!(e.getSender() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getSender();

        if(player.hasPermission("lightashura.admin")) {
            return;
        }
        for(String path : allowedCommands.getConfigurationSection("commands.data").getKeys(false)) {
            List<String> cmdList = allowedCommands.getStringList("commands.data." + path + ".commandList");

            e.getCompletions().clear();
            e.setCompletions(cmdList);
        }
    }
*/
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSelect(PlayerCommandSendEvent e) {

        FileConfiguration allowedCommands = Ashura.allowedCommands.getConfig();

        Player player = e.getPlayer();

        if(player.hasPermission("lightashura.admin")) {
            return;
        }

        e.getCommands().clear();

        for(String path : allowedCommands.getConfigurationSection("commands.data").getKeys(false)) {
            List<String> cmdList = allowedCommands.getStringList("commands.data." + path + ".commandList");
            cmdList.forEach(singleCommand -> {
                singleCommand = singleCommand.replaceFirst("/", "");
                if(!e.getCommands().contains(singleCommand)) {
                    e.getCommands().add(singleCommand);
                }
            });
        }
    }
}
