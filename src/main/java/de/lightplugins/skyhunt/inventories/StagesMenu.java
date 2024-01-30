package de.lightplugins.skyhunt.inventories;

import de.lightplugins.inventories.TutorialGuide;
import de.lightplugins.master.Ashura;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
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
        FileConfiguration tutorial = Ashura.tutorial.getConfig();

        int tutorialCounter =
                Objects.requireNonNull(tutorial.getConfigurationSection("guide")).getKeys(false).size();

        ClickableItem[] levelItems = new ClickableItem[tutorialCounter];

        ItemStack glass  = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;

        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        inventoryContents.fill(ClickableItem.empty(glass));

        ItemStack firstGuide = new ItemStack(Material.WRITABLE_BOOK, 1);
        ItemMeta firstGuideMeta = firstGuide.getItemMeta();

        int i = 0;
        for(String path : Objects.requireNonNull(tutorial.getConfigurationSection("guide")).getKeys(false)) {
            i ++;

            Material material = Material.valueOf(tutorial.getString("guide." + path + ".material"));
            String title = tutorial.getString("guide." + path + ".displayname");

            ItemStack is = new ItemStack(material);
            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setDisplayName(Ashura.colorTranslation.hexTranslation(title));

            List<String> lore = new ArrayList<>();

            tutorial.getStringList("guide." + path + ".lore").forEach(line -> {
                lore.add(Ashura.colorTranslation.hexTranslation(line));
            });

            im.setLore(lore);
            is.setItemMeta(im);


            levelItems[i - 1] = ClickableItem.of(is, e -> {

            });
        }

        pagination.setItems(levelItems);
        pagination.setItemsPerPage(7);

        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1,1));

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

        inventoryContents.set(2, 2, ClickableItem.of(previousPage, e -> {
            INVENTORY.open(player, pagination.previous().getPage());
        }));

        inventoryContents.set(2, 6, ClickableItem.of(nextPage, e -> {
            INVENTORY.open(player, pagination.next().getPage());
        }));

        inventoryContents.set(2, 4, ClickableItem.of(backButton, e -> {
            player.closeInventory();
        }));

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
