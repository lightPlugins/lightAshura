package de.lightplugins.skyhunt.manager;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import io.lumine.mythic.api.drops.DropManager;
import io.lumine.mythic.bukkit.compatibility.MythicDropsSupport;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.droppables.MythicDropsDrop;
import io.lumine.mythic.core.drops.droppables.MythicItemDrop;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateIsland {

    public void initCreateIslandByStage(Player player, String stage) {

        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        List<Island> allIslands = SuperiorSkyblockAPI.getGrid().getIslands();

        AtomicBoolean hasAlreadyIsland = new AtomicBoolean(false);

        String islandID = player.getUniqueId() + "#" + stage;

        allIslands.forEach(singleIsland -> {

            if(singleIsland.getName().equals(player.getUniqueId() + "#" + stage)) {
                hasAlreadyIsland.set(true);
            }
        });

        if(!hasAlreadyIsland.get()) {
            SuperiorSkyblockAPI.getGrid().createIsland(superiorPlayer, "stage-" + stage,
                    BigDecimal.ONE, Biome.PLAINS, islandID);
        }
    }
}
