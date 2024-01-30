package de.lightplugins.skyhunt.util;

import de.lightplugins.master.Ashura;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemBuilder {

    public ItemStack getItemByStage(Player player, String stage) {

        FileConfiguration stageMenu = Ashura.stageMenu.getConfig();
        FileConfiguration stages = Ashura.stages.getConfig();
        FileConfiguration lootTable = Ashura.lootTable.getConfig();

        String islandID = player.getUniqueId() + "#" + stage;

        for(String path : Objects.requireNonNull(
                stageMenu.getConfigurationSection("stageMenu")).getKeys(false)) {

            if(path.equalsIgnoreCase("stage-" + stage)) {

                Material material = Material.valueOf(stageMenu.getString("stageMenu." + path + ".material"));
                ItemStack is = new ItemStack(material, 1);
                ItemMeta im = is.getItemMeta();

                if(im == null) {
                    return new ItemStack(Material.DEEPSLATE, 1);
                }

                String displayName = Ashura.colorTranslation.hexTranslation(
                        stageMenu.getString("stageMenu." + path + ".displayName"));

                im.setDisplayName(displayName);

                List<String> finalLore = new ArrayList<>();
                List<String> configLore = stageMenu.getStringList("stageMenu." + path + ".lore");

                Ashura.getInstance.skyhuntPlayerData.getKills(islandID).thenAccept(result -> {
                    configLore.forEach(singleLine -> {

                        singleLine = singleLine.replace("#currentKills#", String.valueOf(result));
                        singleLine = singleLine.replace("#neededKills#",
                                Objects.requireNonNull(stages.getString("stages.stage-" + stage + ".killsForNextStage")));

                        List<String> lootTableList = new ArrayList<>();

                        for(String singleLootPath : Objects.requireNonNull(
                                lootTable.getConfigurationSection("lootTable.stage-" + stage)).getKeys(false)) {

                            //TODO: weiter machen mit dem placeholder #looTable# in der Lore - iterieren duch die aktuellen lootTables
                            //TODO: und die Values auslesen inklusive ecoLookup für den displayname der talismane


                        }

                    });

                    return is;


                });
            }
        }
        return new ItemStack(Material.STONE, 1);
    }
}
