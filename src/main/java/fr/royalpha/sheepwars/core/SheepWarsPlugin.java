package fr.royalpha.sheepwars.core;

import fr.royalpha.sheepwars.api.*;
import fr.royalpha.sheepwars.api.event.GameEndEvent;
import fr.royalpha.sheepwars.api.event.UltimateSheepWarsLoadedEvent;
import fr.royalpha.sheepwars.core.boosters.*;
import fr.royalpha.sheepwars.core.calendar.event.*;
import fr.royalpha.sheepwars.core.command.CommandManager;
import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.event.block.BlockBreak;
import fr.royalpha.sheepwars.core.event.block.BlockExplode;
import fr.royalpha.sheepwars.core.event.block.BlockPlace;
import fr.royalpha.sheepwars.core.event.block.BlockSpread;
import fr.royalpha.sheepwars.core.event.entity.*;
import fr.royalpha.sheepwars.core.event.inventory.InventoryClick;
import fr.royalpha.sheepwars.core.event.player.*;
import fr.royalpha.sheepwars.core.event.projectile.ProjectileHit;
import fr.royalpha.sheepwars.core.event.projectile.ProjectileLaunch;
import fr.royalpha.sheepwars.core.event.server.ServerCommand;
import fr.royalpha.sheepwars.core.event.server.ServerListPing;
import fr.royalpha.sheepwars.core.event.weather.WeatherChange;
import fr.royalpha.sheepwars.core.exception.PlayableMapException;
import fr.royalpha.sheepwars.core.gui.guis.KitsInventory;
import fr.royalpha.sheepwars.core.handler.MinecraftVersion;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import fr.royalpha.sheepwars.core.handler.VirtualLocation;
import fr.royalpha.sheepwars.core.kit.*;
import fr.royalpha.sheepwars.core.manager.*;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.sheep.*;
import fr.royalpha.sheepwars.core.task.GameTask;
import fr.royalpha.sheepwars.core.task.ScoreboardTask;
import fr.royalpha.sheepwars.core.task.TerminatedGameTask;
import fr.royalpha.sheepwars.core.task.WaitingTask;
import fr.royalpha.sheepwars.core.util.EntityUtils;
import fr.royalpha.sheepwars.core.util.RandomUtils;
import fr.royalpha.sheepwars.core.util.ReflectionUtils;
import fr.royalpha.sheepwars.core.version.VersionManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

public class SheepWarsPlugin extends JavaPlugin {

    /**
     * Static fields
     **/
    public static final String PACKAGE = "fr.royalpha.sheepwars";
    public static final File DATAFOLDER = new File("plugins/UltimateSheepWars/");

    /**
     * Instance variables
     **/
    private static SheepWarsPlugin instance;
    private static CommandManager commandManager;
    private static VersionManager versionManager;
    private static ConfigManager configManager;
    private static WorldManager worldManager;
    private RewardsManager rewardManager;

    /**
     * Providers & Soft Dependencies
     **/
    private Boolean vaultInstalled;

    private static Permission permissionProvider = null;
    private static Economy economyProvider = null;
    private static Chat chatProvider = null;

    /**
     * Settings file & config
     **/
    private FileConfiguration settingsConfig;
    private File settingsFile;

    /**
     * USW
     **/
    private Boolean enablePlugin;
    private Boolean isConfigured;
    private String disableMessage;
    private WaitingTask waitingTask;
    private GameTask gameTask;
    private Boolean localhost;
    private World world;

    public SheepWarsPlugin() {
        instance = this;
        this.enablePlugin = true;
        this.isConfigured = false;
        this.disableMessage = "";
        this.vaultInstalled = false;

        /** Ne pas oublier de changer la valeur **/
        this.localhost = false;
    }

    public void disablePluginLater(String reason) {
        this.enablePlugin = false;
        this.disableMessage = reason;
    }

    public void disablePlugin(Level level, String reason) {
        this.enablePlugin = false;
        this.getLogger().log(level, reason);
        this.getPluginLoader().disablePlugin(this);
    }

    public Boolean isSpigotServer() {
        try {
            Class.forName("org.bukkit.entity.Player$Spigot");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onLoad() {
        /** On fait toutes les verifs pour etre sur que tout va bien se passer **/
        if (!isSpigotServer()) {
            disablePlugin(Level.WARNING, "This server isn't running with spigot build so the plugin can't be load. Get one at https://getbukkit.org/download/spigot.");
            return;
        }
        final MinecraftVersion mcVersion = MinecraftVersion.getVersion();
        final List<MinecraftVersion> supportedVersions = new ArrayList<>();
        supportedVersions.add(MinecraftVersion.v1_8_R3);
        supportedVersions.add(MinecraftVersion.v1_9_R1);
        supportedVersions.add(MinecraftVersion.v1_9_R2);
        supportedVersions.add(MinecraftVersion.v1_12_R1);
        supportedVersions.add(MinecraftVersion.v1_15_R1);
        // supportedVersions.add(MinecraftVersion.v1_10_R1);
        // supportedVersions.add(MinecraftVersion.v1_11_R1);
        // supportedVersions.add(MinecraftVersion.v1_12_R1);
        // supportedVersions.add(MinecraftVersion.v1_13_R2);
        if (!mcVersion.inRange(supportedVersions)) {
            disablePluginLater("Sorry, but this version of UltimateSheepWars doesn't support your server version (" + mcVersion.toString().replaceAll("_", ".") + ") yet.");
            return;
        }
        try {
            versionManager = new VersionManager(mcVersion);
        } catch (ReflectiveOperationException e) {
            ExceptionManager.register(e, true);
            disablePluginLater("ReflectiveOperationException occurs. Please contact the developer.");
            return;
        }

        /** Setup world manager **/
        worldManager = new WorldManager(this);
    }

    @Override
    public void onEnable() {
        getLogger().info("Anti-piracy checking ... (depends on your internet speed)");
        /** On verif que rien n'a nécéssité la désactivation du plugin **/
        if (!this.enablePlugin) {
            disablePlugin(Level.WARNING, this.disableMessage);
            return;
        }

        /** On fait les verif qui necessite d'etre faites plus tard **/
        if (!Bukkit.getWorlds().get(0).getName().equals("world")) {
            disablePlugin(Level.WARNING, "The server properties level name must be \"world\". Stop your server, change \"level-name\" in server.properties file, and switch back on your server.");
            return;
        }

        /** Init custom sheeps **/
        versionManager.getCustomEntities().registerEntities();

        /** Init world settings **/
        this.world = Bukkit.getWorlds().get(0);
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(6000L);
        this.world.setStorm(false);
        this.world.setThundering(false);

        /** Register Events **/
        this.register(BlockBreak.class, BlockPlace.class, BlockSpread.class, BlockExplode.class,

                CreatureSpawn.class, EntityBlockForm.class, EntityDamageByPlayer.class, EntityDeath.class, EntityExplode.class, FoodLevelChange.class, EntityDamage.class, EntityShootBow.class,

                InventoryClick.class,

                AsyncPlayerChat.class, PlayerArmorStandManipulate.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByEntity.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerInteractAtEntity.class, PlayerJoin.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, PlayerRespawn.class, PlayerToggleSneak.class,

                ProjectileHit.class, ProjectileLaunch.class,

                ServerCommand.class, ServerListPing.class,

                WeatherChange.class);
        if (versionManager.getVersion().newerOrEqualTo(MinecraftVersion.v1_9_R1))
            this.register(PlayerSwapHandItems.class);

        /** Setup settings config file **/
        configManager = new ConfigManager(this);
        configManager.initConfig();

        /** On empeche tout joueur de se connecter (forcement après config car besoin de charger la config first) **/
        //Pas besoin car par défaut dans l'enum, c'est sur Restarting
        //GameState.setCurrentStep(GameState.RESTARTING);

        settingsFile = new File(getDataFolder(), "settings.yml");

        if (!settingsFile.exists()) {
            getLogger().info("Generating settings file ...");
            try {
                settingsFile.createNewFile();
                getLogger().info("Settings file was generated with success ! (You do not need to modify this file. The plugin manages it automatically)");
            } catch (IOException ex) {
                ExceptionManager.register(ex, true);
            }
        }
        this.settingsConfig = YamlConfiguration.loadConfiguration(this.settingsFile);
        configManager.init(this.settingsFile);

        /** Load most common things **/
        this.load();

        /** Init stats **/
        DataManager.initDatabaseConnections(this);

        /** Check if plugin is up-to-date **/
        UpdateManager.checkAsync(this);

        /** bStats **/
        try {
            Class.forName("com.google.gson.JsonElement");
            new Metrics(this, 11020);
        } catch( ClassNotFoundException e ) {
            // Do nothing
        }
    }

    private void register(final Class<? extends UltimateSheepWarsEventListener>... classes) {
        try {
            for (final Class<? extends UltimateSheepWarsEventListener> clazz : classes) {
                final Constructor<? extends UltimateSheepWarsEventListener> constructor = clazz.getConstructor(SheepWarsPlugin.class);
                Bukkit.getPluginManager().registerEvents((Listener) constructor.newInstance(this), (Plugin) this);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ExceptionManager.register(ex, true);
            try {
                throw ex;
            } catch (java.lang.Exception exx) {
                ExceptionManager.register(ex, true);
            }
        }
    }

    public void load() {
        getLogger().info("Preparing the game ...");
        /** Pas besoin malotru ! this.saveDefaultConfig(); **/

        /** On init les class **/
        this.initClasses();

        /** Setup Vault **/
        vaultInstalled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (vaultInstalled)
            getLogger().info("Vault hooked !");

        /** Setup Providers **/
        this.setupProviders();
        boolean ecop = (economyProvider != null);
        boolean permp = (permissionProvider != null);

        /** Check for config issues **/
        if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_INGAME_SHOP)) {
            if (!ConfigManager.getBoolean(ConfigManager.Field.ENABLE_KIT_PERMISSIONS))
                ConfigManager.setBoolean(ConfigManager.Field.ENABLE_KIT_PERMISSIONS, true);
            if (vaultInstalled) {
                if (!ecop) {
                    getLogger().info("[CONFIG] You have enable ingame-shop but there is no economy plugin. This is conflicting :");
                    ConfigManager.setBoolean(ConfigManager.Field.ENABLE_KIT_PERMISSIONS, false);
                }
                if (!permp) {
                    getLogger().info("[CONFIG] You have enable ingame-shop but there is no permission plugin. This is conflicting :(");
                    ConfigManager.setBoolean(ConfigManager.Field.ENABLE_KIT_PERMISSIONS, false);
                }
            } else {
                getLogger().info("[CONFIG] You have enable ingame-shop but vault isn't installed. This is conflicting :(");
                ConfigManager.setBoolean(ConfigManager.Field.ENABLE_KIT_PERMISSIONS, false);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!DataManager.isTryingToConnect()) {
                    if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_KIT_REQUIRED_WINS) && !DataManager.isConnected()) {
                        getLogger().info("[CONFIG] You have enable required-wins but no database was detected. This is conflicting :(");
                        ConfigManager.setBoolean(ConfigManager.Field.ENABLE_KIT_REQUIRED_WINS, false);
                    }
                    this.cancel();
                }
            }
        }.runTaskAsynchronously(this);

        /** On initialise les commandes **/
        commandManager = new CommandManager(this);

        /** Set 2 settings about world **/
        VirtualLocation loc = ConfigManager.getLocation(ConfigManager.Field.LOBBY);
        this.world.setSpawnLocation(Math.round((float) loc.getX()), Math.round((float) loc.getY()), Math.round((float) loc.getZ()));
        this.world.getSpawnLocation().getChunk().load(true);

        /** Setup BungeeCord **/
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");

        /** Check for conflict values **/
        if (ConfigManager.getString(ConfigManager.Field.SCOREBOARD_DECORATION).length() <= 21) {
            new ScoreboardTask(ConfigManager.getString(ConfigManager.Field.SCOREBOARD_DECORATION), this);
        } else {
            getLogger().warning("[!] The scoreboard-decoration's length is higher than maximum allowed (" + ConfigManager.getString(ConfigManager.Field.SCOREBOARD_DECORATION).length() + " > 21)");
        }

        getLogger().info("Loading maps ...");

        /** On enregistre toutes les playable maps **/
        for (File supposedMapFolder : worldManager.mapsFolder.listFiles())
            if (supposedMapFolder.isDirectory())
                new PlayableMap(supposedMapFolder, this.getLogger());

        /** Verifier qu'on a au moins une playable map **/
        if (PlayableMap.getPlayableMaps().isEmpty()) {
            ExceptionManager.register(new PlayableMapException("There's no playable map in the folder 'sheepwars-maps'. Please delete corrupted folders."), true);
        }

        /** On met a jour le world de la map qui a été load **/
        PlayableMap.getPlayableMap(WorldManager.LOBBY_MAP_NAME).loadWorld(Bukkit.getWorlds().get(0));

        worldManager.checkVoteMode(null);
        getLogger().info("Vote mode " + (worldManager.isVoteModeEnable() ? "enabled" : "disabled") + ".");
        if (PlayableMap.getReadyMaps().size() > 0) {
            isConfigured = true;
        }

        /** Init Game **/
        GameState.setCurrentStep(GameState.WAITING);

        /** Launch Calendar Events task **/
        CalendarEvent.startCheckTask(this);

        /** Plus qu'a faire l'event **/
        final UltimateSheepWarsLoadedEvent event = new UltimateSheepWarsLoadedEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onDisable() {
        if (DataManager.isConnected()) {
            //Long now = System.currentTimeMillis();
            for (PlayerData data : PlayerData.getDatas())
                data.uploadData();
            /*while (DataManager.isUploadingData()) {
                if (System.currentTimeMillis() - now > 15000) {
                    getLogger().warning("*** UPLOADING DATA TIMED OUT ***");
                    break;
                }
            }*/
            DataManager.closeConnection();
            getLogger().info("Database connection closed.");
        }
        if (this.enablePlugin) {
            for (PlayableMap map : PlayableMap.getPlayableMaps())
                map.uploadData();
            configManager.save();
            if (worldManager.isVoteModeEnable() && worldManager.getVotedMap() != null) {
                worldManager.getVotedMap().unloadWorld();
            }
            versionManager.getCustomEntities().unregisterEntities();
        }
    }

    private void initClasses() {
        Message.initMessages();
        Language.loadStartupConfiguration(this);
        SheepWarsAPI.setKitsInventory(KitsInventory.class);

        try {
            File boosterFile = new File(this.getDataFolder(), "boosters.yml");
            if (!boosterFile.exists())
                boosterFile.createNewFile();
            SheepWarsBooster.setupConfig(boosterFile, this);

            File kitFile = new File(this.getDataFolder(), "kits.yml");
            if (!kitFile.exists())
                kitFile.createNewFile();
            SheepWarsKit.setupConfig(kitFile, this);

            File sheepFile = new File(this.getDataFolder(), "sheeps.yml");
            if (!sheepFile.exists())
                sheepFile.createNewFile();
            SheepWarsSheep.setupConfig(sheepFile, this);
        } catch (IOException ex) {
            ExceptionManager.register(ex, true);
            disablePlugin(Level.WARNING, "Something prevent the plugin to create important configuration files. Maybe it doesn't have the required permission.");
        }

        this.rewardManager = new RewardsManager(this);

        /** Register Kits **/
        SheepWarsAPI.registerKits(this, new ArmoredSheepKit(), new BetterBowKit(), new BetterSwordKit(), new BuilderKit(), new DestroyerKit(), new MobilityKit(), new MoreHealthKit(), new MoreSheepKit(), new NoneKit(), new RandomKit());
        /** Register Sheeps **/
        SheepWarsAPI.registerSheeps(new BoardingSheep(), new DarkSheep(), new DistorsionSheep(), new EarthQuakeSheep(), new ExplosiveSheep(), new FragmentationSheep(), new FrozenSheep(), new HealerSheep(), new IncendiarySheep(), new IntergalacticSheep(), new LightningSheep(), new SeekerSheep(), new SwapSheep(), new GluttonSheep());
        if (versionManager.getVersion().newerOrEqualTo(MinecraftVersion.v1_9_R1))
            SheepWarsAPI.registerSheep(new GlowingSheep());
        /** Register Boosters **/
        SheepWarsAPI.registerBoosters(new ArrowBackBooster(), new ArrowFireBooster(), new BlockingSheepBooster(), new MoreSheepBooster(), new NauseaBooster(), new PoisonBooster(), new RegenerationBooster(), new ResistanceBooster());
        /** Register Calendar Events **/
        SheepWarsAPI.registerCalendarEvents(this, new AprilFoolEvent(), new ChristmassMonthEvent(), new EasterEggEvent(), new HappyNewYearEvent(), new HalloweenDaysEvent());
    }

    private void setupProviders() {
        try {
            RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permProvider != null) {
                permissionProvider = permProvider.getProvider();
            }

            RegisteredServiceProvider<Economy> ecoProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (ecoProvider != null) {
                economyProvider = ecoProvider.getProvider();
            }

            RegisteredServiceProvider<Chat> cProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (cProvider != null) {
                chatProvider = cProvider.getProvider();
            }
        } catch (NoClassDefFoundError ex) {
            // Do Nothing
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadLeaderHeadsModule(String name) throws ReflectiveOperationException {
        return (T) ReflectionUtils.instantiateObject(Class.forName(SheepWarsPlugin.PACKAGE + ".leaderheads." + name));
    }

    public void givePermission(Player player, String permission) {
        try {
            permissionProvider.playerAdd(player, permission);
        } catch (NoClassDefFoundError | NullPointerException e) {
        }
    }

    public void removePermission(Player player, String permission) {
        try {
            permissionProvider.playerRemove(player, permission);
        } catch (NoClassDefFoundError | NullPointerException e) {
        }
    }

    public void stop() {
        GameState.setCurrentStep(GameState.RESTARTING);
        String commd = ConfigManager.getString(ConfigManager.Field.DISPATCH_COMMAND);
        if (!commd.equals("stop") && !commd.equals("restart")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
        }
    }

    public void setSpectator(final Player player, final boolean lose) {
        final PlayerData data = PlayerData.getPlayerData(player);
        if (!data.isSpectator()) {
            if (lose) {
                removePlayer(player);
                data.increaseDeaths(1);
            }
            data.setTeam(SheepWarsTeam.SPEC);
        }
        EntityUtils.resetPlayer(player, GameMode.SPECTATOR);
    }

    public void removePlayer(final Player player) {
        final PlayerData data = PlayerData.getPlayerData(player);
        final SheepWarsTeam team = data.getTeam();
        if (!data.isSpectator()) {
            data.setTeam(SheepWarsTeam.NULL);
            if (GameState.isStep(GameState.WAITING)) {
                player.kickPlayer(ChatColor.RED + "You're not supposed to die before the game starts");
            } else if (GameState.isStep(GameState.INGAME) && team.getOnlinePlayers().isEmpty()) {
                final SheepWarsTeam winnerTeam = team == SheepWarsTeam.BLUE ? SheepWarsTeam.RED : SheepWarsTeam.BLUE;
                final SheepWarsTeam winnerSureTeam = stopGame(winnerTeam);
                new BukkitRunnable() {
                    private int ticks = 30;

                    public void run() {
                        if (this.ticks == 0) {
                            this.cancel();
                            return;
                        }
                        Location location = RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getBoosterSpawns().getBukkitLocations());
                        location.add(RandomUtils.random.nextInt(11) - 5, RandomUtils.random.nextInt(11) - 5, RandomUtils.random.nextInt(11) - 5);
                        ArrayList<Player> onlines = new ArrayList<>();
                        for (Player online : Bukkit.getOnlinePlayers())
                            onlines.add(online);
                        Random rdm = new Random();
                        FireworkEffect effect = FireworkEffect.builder().flicker(rdm.nextBoolean()).withColor(winnerSureTeam.getLeatherColor()).with(Type.BALL_LARGE).build();
                        SheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(location, effect, onlines);
                        this.ticks--;
                    }
                }.runTaskTimer(this, 0, 10);
            }
        }
    }

    public SheepWarsTeam stopGame(SheepWarsTeam winnerTeam) {
        GameEndEvent event = new GameEndEvent(winnerTeam);
        Bukkit.getPluginManager().callEvent(event);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setAllowFlight(true);
            online.setFlying(true);
            online.setGameMode(GameMode.CREATIVE);
        }
        if (winnerTeam != event.getWinnerTeam()) {
            winnerTeam = event.getWinnerTeam();
        }
        new TerminatedGameTask(this);
        for (Entry<OfflinePlayer, PlayerData> entry : PlayerData.getEntries()) {
            final OfflinePlayer offPlayer = entry.getKey();
            final PlayerData data = entry.getValue();
            if (winnerTeam != null && winnerTeam != SheepWarsTeam.SPEC && offPlayer.isOnline()) {
                Player player = (Player) offPlayer;
                if (data.getTeam() == winnerTeam) {
                    data.increaseWins(1);
                    this.getRewardsManager().rewardPlayer(RewardsManager.Events.ON_WIN, data.getPlayer());
                } else {
                    this.getRewardsManager().rewardPlayer(RewardsManager.Events.ON_LOOSE, data.getPlayer());
                }
                player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + data.getLanguage().getMessage(Message.Messages.VICTORY).replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(player)) + " " + Message.getDecoration() + ChatColor.AQUA + " " + ChatColor.BOLD + data.getLanguage().getMessage(Message.Messages.CONGRATULATIONS) + " " + Message.getDecoration());
                SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 5, 10 * 20, 20, ChatColor.YELLOW + "" + data.getLanguage().getMessage(Message.Messages.GAME_END_TITLE), Message.getDecoration() + "" + ChatColor.GOLD + " " + ChatColor.BOLD + data.getLanguage().getMessage(Message.Messages.VICTORY).replaceAll("%WINNER%", winnerTeam.getColor() + winnerTeam.getDisplayName(player)) + " " + Message.getDecoration());
            }
        }
        return winnerTeam;
    }

    public File getPluginJar() {
        return this.getFile();
    }

    public RewardsManager getRewardsManager() {
        return this.rewardManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static VersionManager getVersionManager() {
        return versionManager;
    }

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public WaitingTask getWaitingTask() {
        return this.waitingTask;
    }

    public boolean hasWaitingTaskStarted() {
        if (this.waitingTask == null)
            return false;
        return this.waitingTask.hasStarted();
    }

    public boolean isConfigured() {
        return this.isConfigured;
    }

    public GameTask getGameTask() {
        return this.gameTask;
    }

    public FileConfiguration getSettingsConfig() {
        return this.settingsConfig;
    }

    public File getSettingsFile() {
        return this.settingsFile;
    }

    public boolean isUpToDate() {
        return this.isUpToDate();
    }

    public boolean isLocalhostConnection() {
        return this.localhost;
    }

    public boolean isChatProviderInstalled() {
        return (chatProvider != null);
    }

    public static Chat getChatProvider() {
        return chatProvider;
    }

    public static Economy getEconomyProvider() {
        return economyProvider;
    }

    public static Permission getPermissionProvider() {
        return permissionProvider;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public void setWaitingTask(WaitingTask task) {
        this.waitingTask = task;
    }

    public void setGameTask(GameTask task) {
        this.gameTask = task;
    }

    public static SheepWarsPlugin getInstance() {
        return instance;
    }

    public static void debug(String message) {
        if (ConfigManager.getBoolean(ConfigManager.Field.ALLOW_DEBUG))
            getInstance().getLogger().info("[Debug] " + message);
    }
}