package de.lightplugins.traders;

import com.willfp.eco.core.items.Items;
import org.bukkit.inventory.ItemStack;
import teammt.villagerguiapi.classes.VillagerTrade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EcoLookup {

    public CompletableFuture<List<VillagerTrade>> getVillagerTrade(List<String> list) {

        return CompletableFuture.supplyAsync(() -> {

            List<VillagerTrade> villagerTrades = new ArrayList<>();

            if(list.isEmpty()) {
                return villagerTrades;
            }

            list.forEach(singleTrade -> {

                String[] tradeArgs = singleTrade.split(";");

                if(tradeArgs.length == 8) {

                    ItemStack firstItem = Items.lookup(tradeArgs[0]).getItem();
                    firstItem.setAmount(Integer.parseInt(tradeArgs[1]));
                    ItemStack secondItem = Items.lookup(tradeArgs[2]).getItem();
                    secondItem.setAmount(Integer.parseInt(tradeArgs[3]));
                    ItemStack resultItem = Items.lookup(tradeArgs[4]).getItem();
                    resultItem.setAmount(Integer.parseInt(tradeArgs[5]));
                    int maxUses = Integer.parseInt(tradeArgs[6]);
                    boolean showTrade = Boolean.parseBoolean(tradeArgs[7]);

                    if(showTrade) {
                        villagerTrades.add(new VillagerTrade(firstItem, secondItem, resultItem, maxUses));
                    }
                }

                if(tradeArgs.length == 6) {

                    ItemStack firstItem = Items.lookup(tradeArgs[0]).getItem();
                    firstItem.setAmount(Integer.parseInt(tradeArgs[1]));
                    ItemStack resultItem = Items.lookup(tradeArgs[2]).getItem();
                    resultItem.setAmount(Integer.parseInt(tradeArgs[3]));
                    int maxUses = Integer.parseInt(tradeArgs[4]);
                    boolean showTrade = Boolean.parseBoolean(tradeArgs[5]);

                    if(showTrade) {
                        villagerTrades.add(new VillagerTrade(firstItem, resultItem, maxUses));
                    }
                }

            });

            return villagerTrades;
        });
    }
}
