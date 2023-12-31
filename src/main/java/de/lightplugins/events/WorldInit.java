package de.lightplugins.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.logging.Level;

public class WorldInit implements Listener {

    String defaultConfig = "settings.worldGuard.generateWorldGuardFlagsOnInit.worlds";


    @EventHandler
    public void onDungeonServerCreate(WorldLoadEvent e) {
        World world = e.getWorld();

        Bukkit.getLogger().log(Level.INFO,
                "[lightAshura] Founding world " + world.getName());
        Bukkit.getLogger().log(Level.INFO,
                "[lightAshura] Try to add flags to world " + world.getName());

        FileConfiguration settings = Ashura.settings.getConfig();

        if(!Ashura.getInstance.isWorldGuard) { return; }

        new BukkitRunnable() {
            @Override
            public void run() {

                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(BukkitAdapter.adapt(world));

                if(regions == null) { return; }

                ProtectedRegion region = regions.getRegion("__global__");

                if(region == null) { return; }

                region.setFlag(Flags.DENY_MESSAGE, "");

                for(String path : Objects.requireNonNull(settings.getConfigurationSection(defaultConfig)).getKeys(false)) {


                    String targetWorldName = settings.getString(defaultConfig + "." + path + ".world");
                    if(targetWorldName == null) { return; }
                    String passthroughState = settings.getString(defaultConfig + "." + path + ".passthrough");
                    if(passthroughState == null) { return; }
                    String interactState = settings.getString(defaultConfig + "." + path + ".use");
                    if(interactState == null) { return; }
                    String chestAccessState = settings.getString(defaultConfig + "." + path + ".chestAccess");
                    if(chestAccessState == null) { return; }


                    if(world.getName().contains(targetWorldName)) {
                        if(passthroughState.equalsIgnoreCase("deny")) {
                            region.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Deny passthrough on world " + world.getName());
                        }

                        if(passthroughState.equalsIgnoreCase("allow")) {
                            region.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Allow passthrough on world " + world.getName());
                        }

                        if(interactState.equalsIgnoreCase("deny")) {
                            region.setFlag(Flags.USE, StateFlag.State.DENY);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Deny use on world " + world.getName());
                        }

                        if(interactState.equalsIgnoreCase("allow")) {
                            region.setFlag(Flags.USE, StateFlag.State.ALLOW);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Allow use on world " + world.getName());
                        }

                        if(chestAccessState.equalsIgnoreCase("deny")) {
                            region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Deny chestAccess on world " + world.getName());
                        }

                        if(chestAccessState.equalsIgnoreCase("allow")) {
                            region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
                            Bukkit.getLogger().log(Level.INFO,
                                    "[lightAshura] Allow chestAccess on world " + world.getName());
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(Ashura.getInstance, 10);
    }

    @EventHandler
    public void onCraftingTableInteract(PlayerInteractEvent e) {

        FileConfiguration settings = Ashura.settings.getConfig();

        Player player = e.getPlayer();
        if(e.getClickedBlock() == null) { return; }
        Material material = (e.getClickedBlock()).getType();

        if(material.equals(Material.CRAFTING_TABLE)){

            for(String path : Objects.requireNonNull(settings.getConfigurationSection(defaultConfig)).getKeys(false)) {

                String targetWorldName =
                        settings.getString(defaultConfig + "." + path + ".world");
                String antiCraftingTableState =
                        settings.getString(defaultConfig + "." + path + ".craftingTableUse");

                if(targetWorldName == null) { return; }
                if(antiCraftingTableState == null) { return; }

                if(!player.getWorld().getName().contains(targetWorldName)) {
                    return;
                }
                if(antiCraftingTableState.equalsIgnoreCase("deny")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
