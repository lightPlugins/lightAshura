package de.lightplugins.skyhunt.inventories;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import de.lightplugins.inventories.TutorialGuide;
import de.lightplugins.master.Ashura;
import de.lightplugins.skyhunt.util.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StagesMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("STAGE_MENU")
            .provider(new StagesMenu())
            .size(6,9)
            .title(Ashura.colorTranslation.hexTranslation("#ffdc43&lStages Menü"))
            .manager(Ashura.stageMenuManager)
            .build();


    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        Pagination pagination = inventoryContents.pagination();
        FileConfiguration stageMenu = Ashura.stageMenu.getConfig();

        int stageCounter =
                Objects.requireNonNull(stageMenu.getConfigurationSection("stageMenu")).getKeys(false).size();

        ClickableItem[] stageItems = new ClickableItem[stageCounter];

        ItemStack glass  = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;

        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        inventoryContents.fill(ClickableItem.empty(glass));

        int i = 0;
        for(String path : Objects.requireNonNull(stageMenu.getConfigurationSection("stageMenu")).getKeys(false)) {
            i ++;

            String stageID = path.replace("stage-", "");
            ItemBuilder itemBuilder = new ItemBuilder();
            ItemStack stageItem = itemBuilder.getItemByStage(player, stageID);


            stageItems[i - 1] = ClickableItem.of(stageItem, e -> {

                String islandID = player.getUniqueId() + "#" + stageID;
                Island island = SuperiorSkyblockAPI.getIsland(islandID);
                Location islandSpawnLocation = island.getTeleportLocation(World.Environment.NORMAL);
                player.teleport(islandSpawnLocation);

            });
        }

        pagination.setItems(stageItems);
        pagination.setItemsPerPage(21);

        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1 ,1)
                .blacklist(1, 8)
                .blacklist(2, 8)
                .blacklist(2, 0)
                .blacklist(3, 0)
        );

        ItemStack previousPage = new ItemStack(Material.ARROW);
        ItemMeta previousPageMeta = previousPage.getItemMeta();
        assert previousPageMeta != null;
        previousPageMeta.setDisplayName(Ashura.colorTranslation.hexTranslation("&7Zurück"));

        previousPage.setItemMeta(previousPageMeta);

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextPageMeta = previousPage.getItemMeta();
        assert nextPageMeta != null;
        nextPageMeta.setDisplayName(Ashura.colorTranslation.hexTranslation("&7Nächste Seite"));

        nextPage.setItemMeta(nextPageMeta);

        ItemStack backButton = new ItemStack(Material.REDSTONE);
        ItemMeta backButtonMeta = previousPage.getItemMeta();
        assert backButtonMeta != null;
        backButtonMeta.setDisplayName(Ashura.colorTranslation.hexTranslation("&cSchließen"));

        backButton.setItemMeta(backButtonMeta);

        // i = up to down && I1 = left to right

        inventoryContents.set(5, 2, ClickableItem.of(previousPage, e -> {
            INVENTORY.open(player, pagination.previous().getPage());
        }));

        inventoryContents.set(5, 6, ClickableItem.of(nextPage, e -> {
            INVENTORY.open(player, pagination.next().getPage());
        }));

        inventoryContents.set(5, 4, ClickableItem.of(backButton, e -> {
            player.closeInventory();
        }));

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
