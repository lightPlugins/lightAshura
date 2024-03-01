package de.lightplugins.util;

import de.lightplugins.enums.MessagePath;
import de.lightplugins.master.Ashura;
import org.bukkit.entity.Player;
import org.joml.RoundingMode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class Util {

    public void sendMessage(Player player, String message) {
        String prefix = MessagePath.Prefix.getPath();
        player.sendMessage(Ashura.colorTranslation.hexTranslation(prefix + message));
    }

    /*  Send a message List to player without Prefix  */

    public void sendMessageList(Player player, List<String> list) {
        for(String s : list) {
            player.sendMessage(Ashura.colorTranslation.hexTranslation(s));
        }
    }

    // Return scaled doubled by 2 scales

    public double fixDouble(double numberToFix) {
        BigDecimal bd = new BigDecimal(numberToFix).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean isNumber(String number) {
        try {
            Double dummy = Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Return double formated as String
    public String finalFormatDouble(double numberToRound) { return formatDouble(fixDouble(numberToRound));
    }

    private String formatDouble(double numberToFormat) {
        return String.format("%,.2f", numberToFormat);
    }

    public boolean isInventoryFull(Player p) { return p.getInventory().firstEmpty() == -1; }

    public boolean calculateProbability(double desiredProbability) {
        if (desiredProbability < 0.0 || desiredProbability > 100.0) {
            throw new IllegalArgumentException("Probability must be between 0 and 100.");
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * 100.0; // Zufallszahl zwischen 0 und 100

        return randomValue < desiredProbability;
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Rückgabe des ursprünglichen Strings, wenn er null oder leer ist
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
