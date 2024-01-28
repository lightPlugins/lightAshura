package de.lightplugins.skyhunt.manager;

import com.bgsoftware.superiorskyblock.api.island.Island;
import de.lightplugins.master.Ashura;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class LootManager {

    private final Player player;
    private final Island island;
    private FileConfiguration lootTable;
    private double vaultAmount;

    public LootManager(Player player, Island island) {
        this.player = player;
        this.island = island;
        this.lootTable = Ashura.lootTable.getConfig();
    }

    public void init() {
        iterateLoot();
    }

    private void iterateLoot() {

        for(String stageDrops : Objects.requireNonNull(lootTable.getConfigurationSection(
                "lootTable." + getStageName())).getKeys(false)) {

            List<String> test = new ArrayList<>();

            double chance = lootTable.getDouble(
                    "lootTable." + getStageName() + "." + stageDrops + ".chance");

            if(!generateAndCheckChance(chance)) {
                return;
            }

            Bukkit.getLogger().log(Level.WARNING, "Success boolean");

            String[] reward = Objects.requireNonNull(lootTable.getString(
                    "lootTable." + getStageName() + "." + stageDrops + ".reward")).split(";");

            Bukkit.getLogger().log(Level.WARNING, "reward " + Arrays.toString(reward));

            List<String> actions = lootTable.getStringList(
                    "lootTable." + getStageName() + "." + stageDrops + ".actions");

            switch (reward[0]) {

                case "VAULT" : {
                    double amount = Double.parseDouble(reward[1]);
                    vaultAmount = amount;
                    if(depositAmount(amount).transactionSuccess()) {
                        actions.forEach(singleAction -> {
                            String[] splitActions = singleAction.split(";");
                            executeAction(splitActions);
                        });
                    }
                    break;
                }

                case "TALISMAN" : {
                    break;
                }
                    // eco lookup for talismans
            }
        }
    }

    private void executeAction(String[] action) {

        switch (action[0]) {

            case "ACTIONBAR": {
                sendActionBarMessage(action[1].replace("#amount#", String.valueOf(vaultAmount)));
                break;
            }

            case "SOUND": {
                playSound(action[1],
                        Double.parseDouble(action[2]),
                        Double.parseDouble(action[3]));
                break;
            }

            case "MESSAGE": {
                sendMessage(action[1].replace("#amount#", String.valueOf(vaultAmount)));
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

    private void sendMessage(String message) {
        Ashura.util.sendMessage(player, message);
    }


    private boolean generateAndCheckChance(double targetChance) {
        Random random = new Random();
        double generatedChance = random.nextDouble() * 100.0;
        return generatedChance >= targetChance;
    }
}
