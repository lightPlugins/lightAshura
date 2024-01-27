package de.lightplugins.enums;

import de.lightplugins.master.Ashura;
import org.bukkit.configuration.file.FileConfiguration;

public enum MessagePath {

    Prefix("prefix"),
    getBoxOnFirstJoin("getBoxOnFirstJoin"),

    ;

    private final String path;

    MessagePath(String path) { this.path = path; }
    public String getPath() {
        FileConfiguration paths = Ashura.messages.getConfig();
        try {
            return paths.getString(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

}
