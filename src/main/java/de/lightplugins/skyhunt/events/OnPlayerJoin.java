package de.lightplugins.skyhunt.events;
import de.lightplugins.master.Ashura;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {


    @EventHandler
    public void createFirstIslandOnPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Ashura.getInstance.createIsland.initCreateIslandByStage(player, "1");

    }
}
