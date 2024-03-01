package de.lightplugins.master;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.anvil.PreventAnvilUse;
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
import de.lightplugins.skyhunt.manager.CreateIsland;
import de.lightplugins.util.ColorTranslation;
import de.lightplugins.util.TestEvent;
import de.lightplugins.util.Util;
import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    public static FileManager lootTable;
    public static FileManager stageMenu;
    public static FileManager combineBooks;

    public static ColorTranslation colorTranslation;
    public static Util util;
    public Boolean isWorldGuard = false;
    public Boolean isEcoJobs = false;
    public Boolean isSuperiorSkyblock = false;
    public static Economy vault = null;

    public SkyhuntPlayerData skyhuntPlayerData;

    public static InventoryManager borderMenuManager;
    public static InventoryManager tutorialManager;
    public static InventoryManager stageMenuManager;

    public CreateIsland createIsland;

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
        lootTable = new FileManager(this, "skyhunt/lootTable.yml");
        stageMenu = new FileManager(this, "skyhunt/stageMenu.yml");
        combineBooks = new FileManager(this, "combine-books.yml");

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

            if (pluginName.getName().equals("SuperiorSkyblock2")) {
                Plugin newPlugin = this.getServer().getPluginManager().getPlugin("SuperiorSkyblock2");
                if (newPlugin instanceof SuperiorSkyblock) {
                    getLogger().info("[lightAshura] Successfully hooked into SuperiorSkyblock");
                    isSuperiorSkyblock = true;
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

        if (!setupEconomy() ) {
            getLogger().severe("[lightAshura] Missing Vault. Disabling lightAshura ...");
            getServer().getPluginManager().disablePlugin(this);
            return;
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
        pm.registerEvents(new OnJoinCommands(), this);
        pm.registerEvents(new OnFirstJoin(), this);
        pm.registerEvents(new BoxesOpener(), this);
        //pm.registerEvents(new WorldInit(), this);
        //pm.registerEvents(new ItemDrop(), this);
        pm.registerEvents(new PlayerJoinMessageHandler(), this);
        // a "wheat" y thing xd lol
        pm.registerEvents(new DropManipulation(), this);
        pm.registerEvents(new AllowedCommands(), this);

        pm.registerEvents(new TestEvent(), this);
        pm.registerEvents(new PreventAnvilUse(), this);

        borderMenuManager = new InventoryManager(this);
        tutorialManager = new InventoryManager(this);
        stageMenuManager = new InventoryManager(this);

        borderMenuManager.init();
        tutorialManager.init();
        stageMenuManager.init();

        if(isSuperiorSkyblock) {
            HookIntoSkyblock hookIntoSkyblock = new HookIntoSkyblock();
            hookIntoSkyblock.hook(pm);
            createIsland = new CreateIsland();
        }


        Bukkit.getLogger().log(Level.FINE, "[lightAshura] Successfully started lightAshrua.");

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vault = rsp.getProvider();
        return vault != null;
    }

    public void onDisable() { }
}