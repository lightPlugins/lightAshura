package de.lightplugins.master;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import de.lightplugins.skyhunt.events.*;
import de.lightplugins.skyhunt.skyCommands.CreateStageCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

public class HookIntoSkyblock {


    public void hook(PluginManager pm) {

        if(Ashura.settings.getConfig().getBoolean("settings.skyhunt.enable")) {
            Bukkit.getLogger().log(Level.WARNING, "Hooking into skyblock");
            SuperiorSkyblockAPI.registerCommand(new CreateStageCommand());
            pm.registerEvents(new OnIslandCreate(), Ashura.getInstance);
            pm.registerEvents(new OnIslandDisband(), Ashura.getInstance);
            pm.registerEvents(new HandleMobHealthBar(), Ashura.getInstance);
            pm.registerEvents(new OnMobKill(), Ashura.getInstance);
            pm.registerEvents(new OnPlayerJoin(), Ashura.getInstance);
        }
    }



}
