package de.lightplugins.skyhunt.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnMobKill implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {


        Player player = event.getEntity().getKiller();
        LivingEntity deathMob = event.getEntity();

        if(player == null)  {
            return;
        }

        if(deathMob.getType().equals(EntityType.SHEEP)) {

            Sheep sheep = (Sheep) deathMob;
            if(sheep.isAdult()) {
                return;
            }
        }


    }
}
