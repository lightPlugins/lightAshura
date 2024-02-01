package de.lightplugins.skyhunt.util;

import com.willfp.eco.core.items.Items;
import de.lightplugins.master.Ashura;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ItemBuilder {

    public ItemStack getItemByStage(Player player, String stage) {
        FileConfiguration stageMenu = Ashura.stageMenu.getConfig();
        FileConfiguration stages = Ashura.stages.getConfig();
        FileConfiguration lootTable = Ashura.lootTable.getConfig();

        ItemStack is = new ItemStack(Material.COBBLED_DEEPSLATE);

        String islandID = player.getUniqueId() + "#" + stage;

        for (String path : Objects.requireNonNull(stageMenu.getConfigurationSection("stageMenu")).getKeys(false)) {

            if (path.equalsIgnoreCase("stage-" + stage)) {

                Material material = Material.valueOf(stageMenu.getString("stageMenu." + path + ".material"));
                is.setType(material);

                ItemMeta im = is.getItemMeta(); // Neue ItemMeta-Instanz für jedes Item

                if (im == null) {
                    return new ItemStack(Material.SPONGE, 1);
                }

                String displayName = Ashura.colorTranslation.hexTranslation(
                        Objects.requireNonNull(stageMenu.getString("stageMenu." + path + ".displayName")).replace("#stage#", stage));

                im.setDisplayName(displayName);

                List<String> finalLore = new ArrayList<>();
                List<String> configLore = stageMenu.getStringList("stageMenu." + path + ".lore");

                try {
                    int killsFuture = Ashura.getInstance.skyhuntPlayerData.getKills(islandID).get();

                    configLore.forEach(singleLine -> {
                        singleLine = singleLine.replace("#currentKills#", String.valueOf(killsFuture));
                        singleLine = singleLine.replace("#neededKills#",
                                Objects.requireNonNull(stages.getString("stages.stage-" + stage + ".killsForNextStage")));

                        if (singleLine.contains("#lootTable#")) {
                            singleLine = singleLine.replace("#lootTable#", "");

                            for (String singleLootPath : Objects.requireNonNull(
                                    lootTable.getConfigurationSection("lootTable.stage-" + stage)).getKeys(false)) {

                                String[] reward = Objects.requireNonNull(lootTable.getString(
                                                "lootTable." + "stage-" + stage + "." + singleLootPath + ".reward"))
                                        .split(";");

                                if (reward[0].equalsIgnoreCase("VAULT")) {
                                    double amount = Double.parseDouble(reward[1]);
                                    String message = " &7▪ #ffdc73" + amount + " &7Coins";
                                    finalLore.add(Ashura.colorTranslation.hexTranslation(message));
                                }

                                if (reward[0].equalsIgnoreCase("EXP")) {
                                    double amount = Double.parseDouble(reward[1]);
                                    String message = " &7▪ #ffdc73" + amount + " &7EXP";
                                    finalLore.add(Ashura.colorTranslation.hexTranslation(message));
                                }

                                if (reward[0].equalsIgnoreCase("ECOITEM") ||
                                        reward[0].equalsIgnoreCase("TALISMAN")) {
                                    String ecoID = reward[1];
                                    String ecoDisplayName = ecoLookUpDisplayName(ecoID);
                                    String message = " &7▪ " + ecoDisplayName;
                                    finalLore.add(Ashura.colorTranslation.hexTranslation(message));
                                }
                            }
                        }
                        finalLore.add(Ashura.colorTranslation.hexTranslation(singleLine));
                    });

                    im.setLore(finalLore);
                    is.setItemMeta(im);
                    return is;

                }catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException("error", e);
                }
            }
        }
        Bukkit.getLogger().log(Level.SEVERE, "fail returning empty is " + stage);
        return is;
    }

    public ItemStack ecoLookup(String id) {
        ItemStack is = Items.lookup(id).getItem();
        if(is != null) {
            return is;
        }
        return new ItemStack(Material.STONE, 1);
    }

    public String ecoLookUpDisplayName(String id) {
        ItemStack is = ecoLookup(id);
        ItemMeta im = is.getItemMeta();

        if(im != null) {
            return im.getDisplayName();
        }
        return "item meta is null";
    }
}
