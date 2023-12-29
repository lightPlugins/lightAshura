package de.lightplugins.events;

import com.willfp.eco.core.items.Items;
import de.lightplugins.enums.PersistentDataPaths;
import de.lightplugins.master.Ashura;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;

public class BoxesOpener implements Listener {


    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        ItemStack is = e.getItem();

        if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if(is == null) {  return; }


            ItemMeta im = is.getItemMeta();

            if(im == null) {
                return;
            }

            PersistentDataContainer data = im.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Ashura.getInstance, PersistentDataPaths.ashuraBoxID.getType());


            if(key.getKey().equalsIgnoreCase(PersistentDataPaths.ashuraBoxID.getType())) {
                if(!data.has(key, PersistentDataType.STRING)) {
                    return;
                }

                FileConfiguration boxes = Ashura.boxes.getConfig();

                Set<String> boxIDs = boxes.getConfigurationSection("boxes").getKeys(false);
                String currentBoxID = data.get(key, PersistentDataType.STRING);
                int currentInvSlot = e.getPlayer().getInventory().getHeldItemSlot();

                if(!boxIDs.contains(currentBoxID)) {
                    return;
                }

                e.setCancelled(true);

                if(is.getAmount() > 1) {
                    is.setAmount(is.getAmount() - 1);
                    player.getInventory().setItem(currentInvSlot, is);
                } else {
                    player.getInventory().removeItem(is);
                }




                /*   @here the magic happens   */

                List<String> content = boxes.getStringList("boxes." + currentBoxID + ".content");

                if(content.size() < 1) {
                    return;
                }

                content.forEach(single -> {

                    /*       @param splitContent. ecoitems:itemName;1;100 -> [0];[1];[2]      */
                    /*       @param splitContent. [0] = triggerType      */
                    /*       @param splitContent. [1] = amount (integer or double)      */
                    /*       @param splitContent. [2] = triggerChance (double)    */

                    String[] splitContent = single.split(";");

                    String firstLine = splitContent[0];
                    String[] firstLineSplit = firstLine.split(":");

                    try {

                        /*       @param firstLineSplit = the item type. ecoitems:itemName -> [0]:[1]       */
                        /*       @param firstLineSplit = the item type. vanilla:itemName -> [0]:[1]       */
                        /*       @param firstLineSplit = the item type. command:commandName -> [0]:[1]       */
                        /*       @param firstLineSplit = the item type. console:consoleCommand -> [0]:[1]       */

                        double singleChance = Double.parseDouble(splitContent[2]);
                        int amount = Integer.parseInt(splitContent[1]);

                        String contentType = firstLineSplit[0];

                        switch (contentType) {

                            case "ecoitems":

                                if(Ashura.util.calculateProbability(singleChance)) {

                                    ItemStack ecoItems = Items.lookup(splitContent[0]).getItem();
                                    ItemMeta ecoItemsMeta = ecoItems.getItemMeta();
                                    assert ecoItemsMeta != null;

                                    if(amount > 1) {
                                        ecoItems.setAmount((int)amount);
                                    }

                                    if(Ashura.util.isInventoryFull(player)) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), ecoItems);
                                        Ashura.util.sendMessage(player, "&7Du hast #dc143d" + amount + " &7x "
                                                + ecoItems.getItemMeta().getDisplayName() + " &7bekommen.");
                                        break;

                                    }
                                    player.getInventory().addItem(ecoItems);
                                    Ashura.util.sendMessage(player, "&7Du hast #dc143d" + amount + " &7x "
                                            + ecoItems.getItemMeta().getDisplayName() + " &7bekommen.");

                                    break;
                                }


                            case "ecoarmor" :

                                if(Ashura.util.calculateProbability(singleChance)) {

                                    ItemStack ecoArmor = Items.lookup(splitContent[0]).getItem();
                                    ItemMeta ecoArmorMeta = ecoArmor.getItemMeta();
                                    assert ecoArmorMeta != null;

                                    if(amount > 1) {
                                        ecoArmor.setAmount((int)amount);
                                    }

                                    if(Ashura.util.isInventoryFull(player)) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), ecoArmor);
                                        Ashura.util.sendMessage(player, "&7Du hast #dc143d" + amount + " &7x "
                                                + ecoArmor.getItemMeta().getDisplayName() + " &7bekommen.");
                                        break;

                                    }
                                    player.getInventory().addItem(ecoArmor);
                                    Ashura.util.sendMessage(player, "&7Du hast #dc143d" + amount + " &7x "
                                            + ecoArmor.getItemMeta().getDisplayName() + " &7bekommen.");

                                    break;
                                }

                            case "vanilla" :

                                if(Ashura.util.calculateProbability(singleChance)) {

                                    ItemStack vanilla = new ItemStack(Material.valueOf(firstLineSplit[1]));
                                    ItemMeta vanillaMeta = vanilla.getItemMeta();
                                    if(vanillaMeta == null) {
                                        return;
                                    }

                                    if(amount > 1) {
                                        vanilla.setAmount((int)amount);
                                    }

                                    String itemName = vanilla.getType().toString().replace("_", " ").toLowerCase();

                                    if(Ashura.util.isInventoryFull(player)) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), vanilla);
                                        Ashura.util.sendMessage(player, "&7Du hast #ffdc73" + amount + " &7x #ffdc73"
                                                + itemName + " &7bekommen.");
                                        break;

                                    }
                                    player.getInventory().addItem(vanilla);
                                    Ashura.util.sendMessage(player, "&7Du hast #ffdc73" + amount + " &7x #ffdc73"
                                            + itemName + " &7bekommen.");

                                    break;
                                }


                            case "command" :
                            case "console" :


                        }




                    } catch (NumberFormatException ex) {

                    }
                });
            }
        }
    }

    @EventHandler
    public void onSecondHand(InventoryClickEvent event) {

        if(event.getCurrentItem() != null) {

            ItemStack itemStack = event.getCursor();
            assert itemStack != null;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) {
                return;
            }


            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Ashura.getInstance, PersistentDataPaths.ashuraBoxID.getType());

            if(!data.has(key, PersistentDataType.STRING)) {
                return;
            }

            if(event.getSlot() == 40) {
                event.setCancelled(true);
                Ashura.util.sendMessage((Player) event.getWhoClicked(),
                        "&cDiese Box kannst du nicht in deine Offhand schieben&7!");
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void offHandSwap(PlayerSwapHandItemsEvent event) {

        ItemStack itemStack = event.getOffHandItem();
        assert itemStack != null;
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Ashura.getInstance, PersistentDataPaths.ashuraBoxID.getType());

        if(!data.has(key, PersistentDataType.STRING)) {
            return;
        }

        event.setCancelled(true);

    }
}
