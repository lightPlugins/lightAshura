package de.lightplugins.master;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.comandblocker.AllowedCommands;
import de.lightplugins.commands.AshuraCommandManager;
import de.lightplugins.commands.DiscordCommand;
import de.lightplugins.commands.MapCommand;
import de.lightplugins.commands.essentials.*;
import de.lightplugins.commands.tabcompletion.AshuraTabCompletion;
import de.lightplugins.commands.tutorial.TutorialCommand;
import de.lightplugins.database.DatabaseConnection;
import de.lightplugins.database.querys.SkyhuntPlayerData;
import de.lightplugins.database.tables.PlayerDataTable;
import de.lightplugins.events.*;
import de.lightplugins.files.FileManager;
import de.lightplugins.skyhunt.skyCommands.CreateStageCommand;
import de.lightplugins.skyhunt.events.OnIslandCreate;
import de.lightplugins.skyhunt.events.OnIslandDisband;
import de.lightplugins.skyhunt.events.HandleMobHealthBar;
import de.lightplugins.util.ColorTranslation;
import de.lightplugins.util.Util;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
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
    public static FileManager allowedCommands;
    public static FileManager tutorial;
    public static FileManager playerdata;
    public static FileManager stages;

    public static ColorTranslation colorTranslation;
    public static Util util;
    public Boolean isWorldGuard = false;
    public Boolean isEcoJobs = false;

    public SkyhuntPlayerData skyhuntPlayerData;

    public static InventoryManager borderMenuManager;
    public static InventoryManager tutorialManager;

    public HashMap<String, Integer> localSkyhuntData = new HashMap<>();



    public void onLoad() {

        Bukkit.getLogger().log(Level.FINE, "[lightAshura] Starting lightAshura");

        getInstance = this;

        settings = new FileManager(this, "settings.yml");
        messages = new FileManager(this, "messages.yml");
        boxes = new FileManager(this, "boxes.yml");
        trades = new FileManager(this, "trades.yml");
        border = new FileManager(this, "borders.yml");
        allowedCommands = new FileManager(this, "allowed-commands.yml");
        tutorial = new FileManager(this, "tutorial.yml");
        playerdata = new FileManager(this, "playerdata.yml");
        stages = new FileManager(this, "skyhunt/stages.yml");

        colorTranslation = new ColorTranslation();

        util = new Util();

        skyhuntPlayerData = new SkyhuntPlayerData(this);

        /*  SetUp WorldGuard */

        for(Plugin pluginName : Bukkit.getServer().getPluginManager().getPlugins()) {

            if (pluginName.getName().equals("WorldGuard")) {
                Plugin newPlugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
                if (newPlugin instanceof WorldGuardPlugin) {
                    getLogger().info("[lightAshura] Successfully hooked into WorldGuard");
                    WorldGuardHook worldGuardHook = new WorldGuardHook();
                    worldGuardHook.setupCustomFlags();
                    isWorldGuard = true;
                }
            }

            if (pluginName.getName().equals("PlaceholderAPI")) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    new PlaceholderAPI().register(); // initial lightEconomy placeholder
                    Bukkit.getLogger().log(Level.INFO, "[lightEconomy] Hooked into PlaceholderAPI");

                }
            }
        }

    }

    public void onEnable() {

        /*  Initalize Database and connect driver  */

        this.hikari = new DatabaseConnection();

        if(settings.getConfig().getBoolean("mysql.enable")) {
            hikari.connectToDataBaseViaMariaDB();
            Bukkit.getLogger().log(Level.INFO, "[lightAshura] Successfully connected to Database");
            PlayerDataTable playerDataTable = new PlayerDataTable();
            playerDataTable.createTable();
        }

        Bukkit.getLogger().log(Level.INFO, "[lightAshura] Created PlayerDataTable");

        /*######################################*/

        Bukkit.getLogger().log(Level.INFO, "[lightAshura] Register Commands and TabCompletions ...");

        Objects.requireNonNull(this.getCommand("a")).setExecutor(new AshuraCommandManager(this));
        Objects.requireNonNull(this.getCommand("a")).setTabCompleter(new AshuraTabCompletion());

        Objects.requireNonNull(this.getCommand("day")).setExecutor(new DayTimeCommand());
        Objects.requireNonNull(this.getCommand("night")).setExecutor(new NightTimeCommand());
        Objects.requireNonNull(this.getCommand("gmc")).setExecutor(new CreativeCommand());
        Objects.requireNonNull(this.getCommand("gms")).setExecutor(new SurvivalCommand());
        Objects.requireNonNull(this.getCommand("speed")).setExecutor(new SpeedCommand());
        Objects.requireNonNull(this.getCommand("h")).setExecutor(new HealCommand());
        Objects.requireNonNull(this.getCommand("sun")).setExecutor(new SunCommand());
        Objects.requireNonNull(this.getCommand("tutorial")).setExecutor(new TutorialCommand());
        Objects.requireNonNull(this.getCommand("trash")).setExecutor(new TrashInventoryCommand());

        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new DiscordCommand());
        Objects.requireNonNull(this.getCommand("karte")).setExecutor(new MapCommand());


        PluginManager pm = Bukkit.getPluginManager();
        //pm.registerEvents(new OnJoinCommands(), this);
        pm.registerEvents(new OnFirstJoin(), this);
        pm.registerEvents(new BoxesOpener(), this);
        //pm.registerEvents(new WorldInit(), this);
        //pm.registerEvents(new ItemDrop(), this);
        pm.registerEvents(new PlayerJoinMessageHandler(), this);
        // a "wheat" y thing xd lol
        pm.registerEvents(new DropManipulation(), this);
        pm.registerEvents(new AllowedCommands(), this);

        borderMenuManager = new InventoryManager(this);
        tutorialManager = new InventoryManager(this);
        borderMenuManager.init();
        tutorialManager.init();

        if(settings.getConfig().getBoolean("settings.skyhunt.enable")) {
            SuperiorSkyblockAPI.registerCommand(new CreateStageCommand());
            pm.registerEvents(new OnIslandCreate(), this);
            pm.registerEvents(new OnIslandDisband(), this);
            pm.registerEvents(new HandleMobHealthBar(), this);
        }

        Bukkit.getLogger().log(Level.FINE, "[lightAshura] Successfully started lightAshrua.");

    }

    public void onDisable() { }
}