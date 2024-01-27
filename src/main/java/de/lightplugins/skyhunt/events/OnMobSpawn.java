package de.lightplugins.skyhunt.events;

import com.google.common.base.Strings;
import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class OnMobSpawn implements Listener {

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {

        if(event.getEntity() instanceof Animals) {
            Bukkit.getScheduler().runTaskLater(Ashura.getInstance, () -> {
                LivingEntity livingEnt = (LivingEntity) event.getEntity();

                livingEnt.setCustomName(ChatColor.GRAY + "" + getProgressBar(
                        ((int)livingEnt.getHealth() - (int)livingEnt.getLastDamage()),
                        (int)livingEnt.getMaxHealth(),
                        '|',
                        ChatColor.DARK_RED,
                        ChatColor.RED) + ChatColor.GRAY + "");
                livingEnt.setCustomNameVisible(true);

            }, 6);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if(event.getEntity() instanceof Animals) {
            Bukkit.getScheduler().runTaskLater(Ashura.getInstance, () -> {
                LivingEntity livingEnt = (LivingEntity) event.getEntity();

                livingEnt.setCustomName(ChatColor.GRAY + "" + getProgressBar(
                        ((int)livingEnt.getHealth() - (int)livingEnt.getLastDamage()),
                        (int)livingEnt.getMaxHealth(),
                        '|',
                        ChatColor.DARK_RED,
                        ChatColor.RED) + ChatColor.GRAY + "");
                livingEnt.setCustomNameVisible(true);

            }, 5);
        }
    }

    public String getProgressBar(int current, int max, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        if(current < 0) {
            current = 0;
        }
        int totalBars = 10;
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        String output = Strings.repeat(ChatColor.GRAY + "" + completedColor + symbol, progressBars)
                + Strings.repeat(ChatColor.GRAY +"" + notCompletedColor + symbol, totalBars - progressBars);

        int length = output.length();
        int halfLength = length / 2;
        String firstHalf = output.substring(0, halfLength);
        String secondHalf = output.substring(halfLength);
        String insertedString = " " + current + " ";
        return firstHalf + insertedString + secondHalf;

    }
}
