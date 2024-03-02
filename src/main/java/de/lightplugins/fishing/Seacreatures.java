package de.lightplugins.fishing;

import com.willfp.ecoskills.EcoSkillsPlugin;
import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.stats.Stat;
import com.willfp.ecoskills.stats.Stats;
import de.lightplugins.master.Ashura;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Seacreatures implements Listener {

    @EventHandler
    public void onFishCatch(PlayerFishEvent event) {

        Bukkit.getLogger().log(Level.WARNING, "PlayerFishEvent fired: " + event.getCaught());

        Player player = event.getPlayer();

        if(event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {

            Stat stat = Stats.INSTANCE.getByID("seacreatures");
            if(stat == null) {
                Bukkit.getLogger().log(Level.WARNING, "stat is null");
                return;
            }

            int percentageBySkill = EcoSkillsAPI.getStatLevel(player, stat);

            List<MythicMob> mobList = Arrays.asList(getMobByName("Cryonic_Crab"), getMobByName("Aurorafowl"));

            if(Ashura.util.calculateProbability(percentageBySkill)) {

                Bukkit.getLogger().log(Level.WARNING, "50% hitted");

                Random randomizer = new Random();
                MythicMob spawnedMob = mobList.get(randomizer.nextInt(mobList.size()));

                if(spawnedMob == null) {
                    Bukkit.getLogger().log(Level.WARNING, "spawnedMob is null");
                    return;
                }

                if(event.getCaught() == null) {
                    Bukkit.getLogger().log(Level.WARNING, "event.getCaught() is null");
                    return;
                }

                ActiveMob activeMob = spawnedMob.spawn(BukkitAdapter.adapt(event.getCaught().getLocation()),1);
                Bukkit.getLogger().log(Level.WARNING, "spawned Mob: " + activeMob.getName());
            }
        }
    }

    public MythicMob getMobByName(String name) {
        return MythicBukkit.inst().getMobManager().getMythicMob(name).orElse(null);
    }
}
