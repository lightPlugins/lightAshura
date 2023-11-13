package de.lightplugins.master;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.willfp.ecojobs.EcoJobsPlugin;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.commands.AshuraCommandManager;
import de.lightplugins.commands.essentials.*;
import de.lightplugins.commands.tabcompletion.AshuraTabCompletion;
import de.lightplugins.database.DatabaseConnection;
import de.lightplugins.events.BoxesOpener;
import de.lightplugins.events.PlayerJoinMessageHandler;
import de.lightplugins.itemdrop.ItemDrop;
import de.lightplugins.events.WorldInit;
import de.lightplugins.events.OnFirstJoin;
import de.lightplugins.files.FileManager;
import de.lightplugins.util.ColorTranslation;
import de.lightplugins.util.Util;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class Ashura extends JavaPlugin {

    public static Ashura getInstance;
    public static final String consolePrefix = "§r[light§cAshura§r] ";

    public HikariDataSource ds;
    public DatabaseConnection hikari;

    public static FileManager settings;
    public static FileManager messages;
    public static FileManager boxes;
    public static FileManager trades;
    public static FileManager border;

    public static ColorTranslation colorTranslation;
    public static Util util;
    public Boolean isWorldGuard = false;
    public Boolean isEcoJobs = false;

    public static InventoryManager borderMenuManager;



    public void onLoad() {

        Bukkit.getLogger().log(Level.FINE, "[lightAshura] Starting lightAshura");

        getInstance = this;

        settings = new FileManager(this, null, "settings.yml");
        messages = new FileManager(this, null, "messages.yml");
        boxes = new FileManager(this, null, "boxes.yml");
        trades = new FileManager(this, null, "trades.yml");
        border = new FileManager(this, null, "borders.yml");

        colorTranslation = new ColorTranslation();

        util = new Util();

    }

    public void onEnable() {

        /*  Initalize Database and connect driver  */

        this.hikari = new DatabaseConnection();
        // hikari.connectToDataBaseViaMariaDB();


        /*  SetUp WorldGuard */

        for(Plugin pluginName : Bukkit.getServer().getPluginManager().getPlugins()) {

            if (pluginName.getName().equals("WorldGuard")) {
                Plugin newPlugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
                if (newPlugin instanceof WorldGuardPlugin) {
                    getLogger().info("[lightAshura] Successfully hooked into WorldGuard");
                    isWorldGuard = true;
                }
            }

            if (pluginName.getName().equals("EcoJobs")) {
                Plugin newPlugin = this.getServer().getPluginManager().getPlugin("EcoJobs");
                if (newPlugin instanceof EcoJobsPlugin) {
                    getLogger().info("[lightAshura] Successfully hooked into EcoJobs");
                    isEcoJobs = true;
                }
            }

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPI().register(); // initial lightEconomy placeholder
                Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Hooked into PlaceholderAPI");

            }
        }

        /*######################################*/

        Bukkit.getLogger().log(Level.INFO, "[lightAshura] Register Commands and TabCompletions ...");

        Objects.requireNonNull(this.getCommand("a")).setExecutor(new AshuraCommandManager(this));
        Objects.requireNonNull(this.getCommand("a")).setTabCompleter(new AshuraTabCompletion());

        Objects.requireNonNull(this.getCommand("day")).setExecutor(new DayTimeCommand());
        Objects.requireNonNull(this.getCommand("night")).setExecutor(new NightTimeCommand());
        Objects.requireNonNull(this.getCommand("gmc")).setExecutor(new CreativeCommand());
        Objects.requireNonNull(this.getCommand("gms")).setExecutor(new SurvivalCommand());
        Objects.requireNonNull(this.getCommand("speed")).setExecutor(new SpeedCommand());
        Objects.requireNonNull(this.getCommand("heal")).setExecutor(new HealCommand());
        Objects.requireNonNull(this.getCommand("sun")).setExecutor(new SunCommand());


        PluginManager pm = Bukkit.getPluginManager();
        //pm.registerEvents(new OnJoinCommands(), this);
        pm.registerEvents(new OnFirstJoin(), this);
        pm.registerEvents(new BoxesOpener(), this);
        pm.registerEvents(new WorldInit(), this);
        //pm.registerEvents(new ItemDrop(), this);
        pm.registerEvents(new PlayerJoinMessageHandler(), this);

        borderMenuManager = new InventoryManager(this);
        borderMenuManager.init();

        Bukkit.getLogger().log(Level.FINE, "[lightAshura] Successfully started lightAshrua.");

    }

    public void onDisable() { }
}