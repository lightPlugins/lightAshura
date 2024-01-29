package de.lightplugins.skyhunt.manager;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.TestableItem;
import de.lightplugins.master.Ashura;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class LootManager {

    private final Player player;
    private final Island island;
    private final FileConfiguration lootTable;
    private final Location entityLocation;

    public LootManager(Player player, Island island, Location entityLocation) {
        this.player = player;
        this.island = island;
        this.lootTable = Ashura.lootTable.getConfig();
        this.entityLocation = entityLocation;
    }

    public void init() {
        iterateLoot();
    }

    private void iterateLoot() {

        for(String stageDrops : Objects.requireNonNull(lootTable.getConfigurationSection(
                "lootTable." + getStageName())).getKeys(false)) {

            double chance = lootTable.getDouble(
                    "lootTable." + getStageName() + "." + stageDrops + ".chance");


            String[] reward = Objects.requireNonNull(lootTable.getString(
                    "lootTable." + getStageName() + "." + stageDrops + ".reward")).split(";");

            Bukkit.getLogger().log(Level.WARNING, "reward " + Arrays.toString(reward));

            List<String> actions = lootTable.getStringList(
                    "lootTable." + getStageName() + "." + stageDrops + ".actions");

            if(generateAndCheckChance(chance)) {
                switch (reward[0]) {

                    case "VAULT" : {
                        double amount = Double.parseDouble(reward[1]);
                        if(depositAmount(amount).transactionSuccess()) {
                            actions.forEach(singleAction -> {
                                String[] splitActions = singleAction.split(";");
                                executeAction(splitActions, String.valueOf(amount));
                            });
                        }
                        break;
                    }

                    case "TALISMAN" : {
                        initEcoLookUp(reward, actions);
                        break;
                    }

                    case "ECOITEM" : {
                        initEcoLookUp(reward, actions);
                        break;
                    }

                    case "EXP" : {
                        player.giveExp(Integer.parseInt(reward[1]));
                        actions.forEach(singleAction -> {
                            String[] splitActions = singleAction.split(";");
                            executeAction(splitActions, reward[1]);
                        });
                        break;
                    }

                    case "CONSOLE" : {
                        Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reward[1]);
                        });
                        actions.forEach(singleAction -> {
                            String[] splitActions = singleAction.split(";");
                            executeAction(splitActions, "");
                        });
                        break;
                    }
                }
            } else {
                Bukkit.getLogger().log(Level.SEVERE, "Not Passed for reward " + reward[0]);
            }
        }
    }

    private void initEcoLookUp(String[] reward, List<String> actions) {
        ItemStack ecoItem = getEcoItem(reward[1]).getItem();
        ItemMeta ecoItemMeta = ecoItem.getItemMeta();
        String itemName;

        if(ecoItemMeta != null) {
            itemName = ecoItemMeta.getDisplayName();
        } else {
            itemName = "unknown";
        }

        String finalItemName = itemName;
        actions.forEach(singleAction -> {
            String[] splitActions = singleAction.split(";");
            executeAction(splitActions, finalItemName);
        });

        if(!Ashura.util.isInventoryFull(player)) {
            player.getInventory().addItem(ecoItem);
            return;
        }
        Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
            player.getWorld().dropItemNaturally(player.getLocation(), ecoItem);
        });
    }

    private void executeAction(String[] action, String data) {

        switch (action[0]) {

            case "ACTIONBAR": {
                sendActionBarMessage(action[1].replace("#data#", data));
                break;
            }

            case "SOUND": {
                playSound(action[1],
                        Double.parseDouble(action[2]),
                        Double.parseDouble(action[3]));
                break;
            }

            case "MESSAGE": {
                sendMessage(action[1].replace("#data#", data));
                break;
            }

            case "TITLE": {
                String topTitle = Ashura.colorTranslation.hexTranslation(action[1]
                        .replace("#data#", data));
                String lowerTitle = Ashura.colorTranslation.hexTranslation(action[2]
                        .replace("#data#", data));
                player.sendTitle(topTitle, lowerTitle, 20,80,20);
                break;
            }

            case "EFFECT": {
                Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
                    player.playEffect(EntityEffect.valueOf(action[1]));
                });
                break;
            }

            case "TARGET_PARTICLE": {
                if(entityLocation.getWorld() != null) {
                    Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
                        player.spawnParticle(Particle.valueOf(action[1]), entityLocation, 1);
                    });
                }
                break;
            }

            case "CONSOLE": {
                Bukkit.getScheduler().runTask(Ashura.getInstance, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), action[1]
                            .replace("#player#", player.getName())
                            .replace("#data#", data));
                });
                break;
            }
        }
    }

    private EconomyResponse depositAmount(double amount) {
        return Ashura.vault.depositPlayer(player, amount);
    }

    private String getStageName() {
        return island.getSchematicName();
    }

    private void sendActionBarMessage(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                Ashura.colorTranslation.hexTranslation(message)));
    }

    private void playSound(String stringSound, double pitch, double volume) {
        Sound sound = Sound.valueOf(stringSound);
        player.playSound(player.getLocation(), sound, (float)volume, (float)pitch);
    }

    private TestableItem getEcoItem(String id) {return Items.lookup(id); }

    private void sendMessage(String message) {
        Ashura.util.sendMessage(player, message);
    }


    private boolean generateAndCheckChance(double targetChance) {
        Random random = new Random();
        double generatedChance = random.nextDouble() * 100.0;
        return generatedChance <= targetChance;
    }
}
