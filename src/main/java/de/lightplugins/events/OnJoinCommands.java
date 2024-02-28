package de.lightplugins.events;

import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorUtils;
import com.willfp.ecojobs.api.EcoJobsAPI;
import com.willfp.ecojobs.api.event.JobEvent;
import com.willfp.ecojobs.jobs.Job;
import com.willfp.ecojobs.jobs.JobLeaveGUI;
import com.willfp.ecojobs.jobs.Jobs;
import com.willfp.ecojobs.libreforge.TriggerLeaveJob;
import de.lightplugins.master.Ashura;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

public class OnJoinCommands implements Listener {

    @EventHandler
    public void onJoinDoCommand(PlayerLoginEvent event) {

        Player player = event.getPlayer();

        List<Job> test = Jobs.INSTANCE.getUnlockedJobs(player);
        test.forEach(singleJob -> {
            if(!EcoJobsAPI.hasJobActive(player, singleJob)) {
                EcoJobsAPI.joinJob(player, singleJob);
            }
        });

        /*
        FileConfiguration settings = Ashura.settings.getConfig();

        boolean enable = settings.getBoolean("settings.spawnOnJoin.enable");

        if(!enable) {
            return;
        }

        String command = settings.getString("settings.spawnOnJoin.command");

        if(command == null) {
            return;
        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
         */

    }
}
