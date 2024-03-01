package de.lightplugins.anvil;

import de.lightplugins.master.Ashura;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PreventAnvilUse implements Listener {

    @EventHandler
    public void onShiftClick(InventoryClickEvent event) {
        if(event.getInventory().getType().equals(InventoryType.ANVIL)) {
            if(event.getClick().equals(ClickType.SHIFT_LEFT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent event) {

        FileConfiguration combineBooks = Ashura.combineBooks.getConfig();

        List<HumanEntity> viewersCopy = new ArrayList<>(event.getViewers());

        viewersCopy.forEach(singleUser -> {

            if(singleUser instanceof Player player) {

                ItemStack leftItem = event.getInventory().getItem(0);
                ItemStack rightItem = event.getInventory().getItem(1);

                if(leftItem == null || rightItem == null) {
                    return;
                }

                ItemMeta leftItemMeta = leftItem.getItemMeta();
                ItemMeta rightItemMeta = rightItem.getItemMeta();

                if(leftItemMeta == null || rightItemMeta == null) {
                    return;
                }

                Map<Enchantment, Integer> leftEnchants = leftItemMeta.getEnchants();
                Map<Enchantment, Integer> rightEnchants = rightItemMeta.getEnchants();


                for(Enchantment leftEnchantment : leftEnchants.keySet()) {

                    String leftEnchantKey = leftEnchantment.getKey().getKey();

                    for(Enchantment rightEnchantment : rightEnchants.keySet()) {

                        String rightEnchantKey = rightEnchantment.getKey().getKey();

                        if(leftEnchantKey.equals(rightEnchantKey)) {

                            //  here are the same enchantments at once

                            combineBooks.getStringList("combine.disableEnchantCombine.fromList").forEach(singleEnchant -> {

                                if(leftEnchantKey.equals(singleEnchant)) {

                                    List<ItemStack> newStacks = Arrays.asList(leftItem, rightItem);

                                    event.getInventory().setItem(0, new ItemStack(Material.AIR, 1));
                                    event.getInventory().setItem(1, new ItemStack(Material.AIR, 1));

                                    newStacks.forEach(singleNewItem -> {
                                        if (Ashura.util.isInventoryFull(player)) {
                                            player.getWorld().dropItemNaturally(player.getLocation(), singleNewItem);
                                            return;

                                        }
                                        player.getInventory().addItem(singleNewItem);

                                    });

                                    String[] title = Objects.requireNonNull(combineBooks.getString(
                                            "combine.disableEnchantCombine.title")).split(";");

                                    String[] sound = Objects.requireNonNull(combineBooks.getString(
                                            "combine.disableEnchantCombine.sound")).split(";");

                                    String capitalizedEnchant = Ashura.util.capitalizeFirstLetter(leftEnchantKey);

                                    event.getView().close();
                                    player.sendTitle(
                                            Ashura.colorTranslation.hexTranslation(title[0]),
                                            Ashura.colorTranslation.hexTranslation(title[1]
                                                    .replace("#enchantment#", capitalizedEnchant)),
                                            0, 60, 35);
                                    player.playSound(
                                            player.getLocation(),
                                            Sound.valueOf(sound[0]),
                                            // volume
                                            Float.parseFloat(sound[1]),
                                            // pitch
                                            Float.parseFloat(sound[2]));
                                }
                            });
                        }
                    }
                }
            }
        });
    }


}
