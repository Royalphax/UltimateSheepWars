package fr.asynchronous.sheepwars.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.command.ContributorCommand;
import fr.asynchronous.sheepwars.core.command.HubCommand;
import fr.asynchronous.sheepwars.core.command.LangCommand;
import fr.asynchronous.sheepwars.core.command.MainCommand;
import fr.asynchronous.sheepwars.core.command.StatsCommand;
import fr.asynchronous.sheepwars.core.data.AccountManager;
import fr.asynchronous.sheepwars.core.data.DataRegister;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.event.block.BlockBreak;
import fr.asynchronous.sheepwars.core.event.block.BlockPlace;
import fr.asynchronous.sheepwars.core.event.block.BlockSpread;
import fr.asynchronous.sheepwars.core.event.entity.CreatureSpawn;
import fr.asynchronous.sheepwars.core.event.entity.EntityBlockForm;
import fr.asynchronous.sheepwars.core.event.entity.EntityChangeBlock;
import fr.asynchronous.sheepwars.core.event.entity.EntityDamage;
import fr.asynchronous.sheepwars.core.event.entity.EntityDamageByPlayer;
import fr.asynchronous.sheepwars.core.event.entity.EntityDeath;
import fr.asynchronous.sheepwars.core.event.entity.EntityExplode;
import fr.asynchronous.sheepwars.core.event.entity.EntityTarget;
import fr.asynchronous.sheepwars.core.event.entity.FoodLevelChange;
import fr.asynchronous.sheepwars.core.event.inventory.InventoryClick;
import fr.asynchronous.sheepwars.core.event.inventory.InventoryOpenEvent;
import fr.asynchronous.sheepwars.core.event.player.AsyncPlayerChat;
import fr.asynchronous.sheepwars.core.event.player.PlayerArmorStandManipulate;
import fr.asynchronous.sheepwars.core.event.player.PlayerCommandPreprocess;
import fr.asynchronous.sheepwars.core.event.player.PlayerDamage;
import fr.asynchronous.sheepwars.core.event.player.PlayerDamageByEntity;
import fr.asynchronous.sheepwars.core.event.player.PlayerDeath;
import fr.asynchronous.sheepwars.core.event.player.PlayerDropItem;
import fr.asynchronous.sheepwars.core.event.player.PlayerInteract;
import fr.asynchronous.sheepwars.core.event.player.PlayerInteractAtEntity;
import fr.asynchronous.sheepwars.core.event.player.PlayerJoin;
import fr.asynchronous.sheepwars.core.event.player.PlayerKick;
import fr.asynchronous.sheepwars.core.event.player.PlayerLogin;
import fr.asynchronous.sheepwars.core.event.player.PlayerMove;
import fr.asynchronous.sheepwars.core.event.player.PlayerPickupItem;
import fr.asynchronous.sheepwars.core.event.player.PlayerQuit;
import fr.asynchronous.sheepwars.core.event.player.PlayerRespawn;
import fr.asynchronous.sheepwars.core.event.player.PlayerSwapItem;
import fr.asynchronous.sheepwars.core.event.projectile.ProjectileHit;
import fr.asynchronous.sheepwars.core.event.projectile.ProjectileLaunch;
import fr.asynchronous.sheepwars.core.event.server.ServerCommand;
import fr.asynchronous.sheepwars.core.event.server.ServerListPing;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.DataManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.RewardsManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.URLManager;
import fr.asynchronous.sheepwars.core.manager.VersionManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.task.GameTask;
import fr.asynchronous.sheepwars.core.task.ScoreboardTask;
import fr.asynchronous.sheepwars.core.util.FileUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class UltimateSheepWarsPlugin extends JavaPlugin {

	/** Static fields **/
	public static final String PACKAGE = "fr.asynchronous.sheepwars";
	public static final File DATAFOLDER = new File("plugins/UltimateSheepWars/");
	
	/** Spigot premium placeholders **/
	public static final String user_id = "%%__USER__%%";
	public static final String download_id = "%%__NONCE__%%";
	
	/** Instance variables **/
	private AccountManager accountManager;
	private RewardsManager rewardManager;
	
    /** Providers & Soft Dependencies **/
    private Boolean vaultInstalled;
    private Boolean leaderHeadsInstalled;
    
    private Permission permissionProvider;
    private Economy economyProvider;
    private Chat chatProvider;
    
    /** Settings file & config **/
    private FileConfiguration settingsConfig;
    private File settingsFile;
    
    /** USW **/
    private static VersionManager versionManager;
    private Boolean enablePlugin;
    private BeginCountdown preGameTask;
	private GameTask gameTask;
    private Boolean localhost;
    private World world;
	
	public UltimateSheepWarsPlugin() {
        this.economyProvider = null;
        this.enablePlugin = true;
        this.vaultInstalled = false;
        
        /** Ne pas oublier de changer la valeur **/
        this.localhost = true;
	}
	
	public void disablePlugin(Level level, String reason)
	{
		this.enablePlugin = false;
		this.getLogger().log(level, reason);
		this.getPluginLoader().disablePlugin(this);
	}
	
	public static Boolean isSpigotServer(){
        try{
            Class.forName("org.bukkit.entity.Player$Spigot");
            return true;
        }catch(Exception e){
            return false;
        }
    }
	
	@Override
	public void onLoad() {
		if (!isSpigotServer()) {
			disablePlugin(Level.WARNING, "This server isn't running with Spigot build. The plugin can't be load.");
			return;
		}
		try {
			versionManager = new VersionManager();
		} catch (ReflectiveOperationException e) {
			disablePlugin(Level.WARNING, "ReflectiveOperationException occurs. Please contact the developer.");
			new ExceptionManager(e).register(false);
			return;
		}
		if (!versionManager.getVersion().inRange(MinecraftVersion.v1_8_R3, MinecraftVersion.v1_11_R1))
		{
			disablePlugin(Level.WARNING, "UltimateSheepWars doesn't support your server version (" + versionManager.getVersion().toString() + ")");
			return;
		}
		if (!Bukkit.getWorlds().get(0).getName().equals("world"))
		{
			disablePlugin(Level.WARNING, "The server properties level name must be \"world\". Stop your server, change \"level-name\" in server.properties file, and switch back on your server.");
			return;
		}
		try {
            Bukkit.unloadWorld("world", true);
            this.getLogger().info("Loading directories ...");
            final File worldContainer = this.getServer().getWorldContainer();
            final File worldFolder = new File(worldContainer, "world");
            final File copyFolder = new File(worldContainer, "sheepwars-backup");
            if (copyFolder.exists()) {
            	this.getLogger().info("World is reseting ...");
                ReflectionUtils.getClass("RegionFileCache", ReflectionUtils.PackageType.MINECRAFT_SERVER).getMethod("a", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                FileUtils.delete(worldFolder);
                FileUtils.copyFolder(copyFolder, worldFolder);
                this.getLogger().info("World reset !");
            } else {
            	this.getLogger().info("Didn't find the world backup save, creating it ...");
            	FileUtils.copyFolder(worldFolder, copyFolder);
            	this.getLogger().info("Backup save created !");
            }
        }
        catch (Exception ex) {
        	disablePlugin(Level.SEVERE, "*** An error occured when reseting the map. Please contact the developer. ***");
			new ExceptionManager(ex).register(true);
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		/** Init custom sheeps **/
		versionManager.getCustomEntities().registerEntities();
		
		/** Init Game **/ 
		GameState.setCurrentStep(GameState.WAITING);
		
		/** Init world settings **/
        this.world = Bukkit.getWorlds().get(0);
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setTime(6000L);
        this.world.setStorm(false);
        this.world.setThundering(false);
        
        /** Init commands **/
        this.getCommand("sheepwars").setExecutor(new MainCommand(this));
        this.getCommand("lang").setExecutor(new LangCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("hub").setExecutor(new HubCommand(this));
        this.getCommand("contributor").setExecutor(new ContributorCommand(this));
        
        /** Register Events **/
        this.register(BlockBreak.class, BlockPlace.class, BlockSpread.class, 
        		
        		CreatureSpawn.class, EntityBlockForm.class, EntityChangeBlock.class, 
        		EntityDamageByPlayer.class, EntityDeath.class, EntityExplode.class, 
        		EntityTarget.class, FoodLevelChange.class, EntityDamage.class,
        		
        		InventoryClick.class, 
        		
        		AsyncPlayerChat.class, PlayerArmorStandManipulate.class, 
        		PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByEntity.class,
        		PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerJoin.class, 
        		PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class,
        		PlayerQuit.class, PlayerRespawn.class, PlayerInteractAtEntity.class, InventoryOpenEvent.class, 
        		
        		ProjectileHit.class, ProjectileLaunch.class, 
        		
        		ServerCommand.class, ServerListPing.class);
        if (versionManager.getVersion().newerThan(MinecraftVersion.v1_9_R1))
        	this.register(PlayerSwapItem.class);
        
		/** Load most common things **/
		this.load();
		
		/** Init stats & mySQL things at the end (cause it can take time ... ) **/
		DataManager.initDatabaseConnections(this);
		new BukkitRunnable()
		{
			public void run()
			{
				try {
					if (!URLManager.checkVersion(getDescription().getVersion(), false, URLManager.Link.GITHUB_PATH)) {
						getLogger().info("A new version is available, with following new functionalitie(s), improvement(s) and fixe(s) : ");
						List<String> news = URLManager.getInfoVersion(URLManager.Link.GITHUB_PATH);
						for (int i = 0; i < news.size(); i++) {
							String newsLine = news.get(i);
							Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + (i == (news.size()-1) ? "\\_/ " : (newsLine.startsWith("#") ? " + " : " |  ")) + ChatColor.RESET + (newsLine.startsWith("#") ? ChatColor.YELLOW + newsLine.replaceFirst("#", "") : newsLine));
						}
						getLogger().info("Please stay updated at https://www.spigotmc.org/resources/17393/");
					} else {
						getLogger().info("Plugin is up-to-date.");
					}
				} catch (FileNotFoundException ex) {
					new ExceptionManager(ex).register(false);
					disablePlugin(Level.SEVERE, "You don't have a valid internet connection, please connect to the internet for the plugin to work.");
				} catch (IOException ex) {
					// Do nothing
				}
			}
		}.runTaskAsynchronously(this);
		
		this.accountManager = new AccountManager(this, user_id);
		new DataRegister(this, this.localhost, true);
	}
	
	private void register(@SuppressWarnings("unchecked") final Class<? extends UltimateSheepWarsEventListener>... classes) {
		try {
			for (final Class<? extends UltimateSheepWarsEventListener> clazz : classes) {
				final Constructor<? extends UltimateSheepWarsEventListener> constructor = clazz.getConstructor(UltimateSheepWarsPlugin.class);
				Bukkit.getPluginManager().registerEvents((Listener) constructor.newInstance(this), (Plugin) this);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			new ExceptionManager(ex).register(true);
			try {
				throw ex;
			} catch (java.lang.Exception exx) {
				new ExceptionManager(exx).register(true);
			}
		}
	}
	
	public void load()
	{
        this.saveDefaultConfig();
        this.initClasses();
        
        /** Setup Vault **/
		vaultInstalled = Bukkit.getPluginManager().isPluginEnabled("Vault");
		if (vaultInstalled)
			getLogger().info("Vault hooked !");
		
		/** Setup Providers **/
		this.setupProviders();
        boolean ecop = (this.economyProvider != null);
        boolean permp = (this.permissionProvider != null);
		
        /** Setup Soft-Depends (LeaderHeads) **/
		leaderHeadsInstalled = Bukkit.getPluginManager().isPluginEnabled("LeaderHeads");
		if (leaderHeadsInstalled)
			getLogger().info("LeaderHeads hooked !");
		if (this.leaderHeadsInstalled && DataManager.isConnected())
        {
        	String[] list = {"Deaths", "Games", "KDRatio", "Kills", "SheepKilled", "SheepThrown", "TotalTime", "WinRate", "Wins"};
        	try {
        		for (String strng : list)
        			loadLeaderHeadsModule("UltimateSheepWars" + strng);
			} catch (ReflectiveOperationException ex) {
				new ExceptionManager(ex).register(true);
			}
        }
		
        /** Check for config issues **/
        if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP))
        {
        	ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, true);
        	if (vaultInstalled)
        	{
        		if (!ecop) {
        			getLogger().info("KIT: You have enable ingame-shop but there is no economy plugin. This is conflicting :(");
        			ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        		} else if (!permp) {
        			getLogger().info("KIT: You have enable ingame-shop but there is no permission plugin. This is conflicting :(");
        			ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        		}
        	} else {
        		getLogger().info("KIT: You have enable ingame-shop but vault isn't installed. This is conflicting :(");
        		ConfigManager.setBoolean(Field.ENABLE_KIT_PERMISSIONS, false);
        	}
        }
        if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS) && !DataManager.isConnected())
        {
        	getLogger().info("KIT: You have enable required-wins but no database was detected. This is conflicting :(");
        	ConfigManager.setBoolean(Field.ENABLE_KIT_REQUIRED_WINS, false);
        }

        /** Setup settings config file **/
        settingsFile = new File(getDataFolder(), "settings.yml");
        if (!settingsFile.exists())
        {
        	getLogger().info("Generating settings file ...");
        	try {
        		settingsFile.createNewFile();
        		getLogger().info("Settings file was generated with success ! (You do not need to modify this file. The plugin manages it automatically)");
			} catch (IOException ex) {
				new ExceptionManager(ex).register(true);
			}
        }
        this.settingsConfig = YamlConfiguration.loadConfiguration(this.settingsFile);
        ConfigManager.Field.init(this.settingsConfig);
        
        /** Set 3 settings about world **/
        this.world.setSpawnLocation(ConfigManager.getLocation(Field.LOBBY).getBlockX(), ConfigManager.getLocation(Field.LOBBY).getBlockY(), ConfigManager.getLocation(Field.LOBBY).getBlockZ());
        this.world.setKeepSpawnInMemory(true);//Peut faire freeze le load
        this.world.getSpawnLocation().getChunk().load(true);
        
        /** Setup BungeeCord **/
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        
        /** Check for conflict values **/
        if (ConfigManager.getString(Field.SCOREBOARD_DECORATION).length() <= 21)
    	{
    		new ScoreboardTask(ConfigManager.getString(Field.SCOREBOARD_DECORATION), this);
    	} else {
    		getLogger().warning("[!] The scoreboard-decoration's length is higher than maximum allowed (" + ConfigManager.getString(Field.SCOREBOARD_DECORATION).length() + " > 21)");
    	}
        
        /** Initialize other classes **/
        for (TeamManager team : TeamManager.values())
        	team.updateScoreboardTeamCount();
	}
	
	@Override
	public void onDisable() {
		if (DataManager.isConnected())
			DataManager.closeConnection();
		if (this.enablePlugin) {
			this.save();
			versionManager.getCustomEntities().unregisterEntities();
		}
	}
	
	private void save() {
		/** Save the Lobby loc **/
    	this.settingsConfig.set("lobby", Utils.toString(ConfigManager.getLocation(Field.LOBBY)));
    	/** Save the Boosters loc **/
    	for (int i = 0; i < ConfigManager.getLocations(Field.BOOSTERS).size(); ++i) {
        	this.settingsConfig.set("boosters." + i, Utils.toString(ConfigManager.getLocations(Field.BOOSTERS).get(i)));
        }
    	/** Save the Teams spawns **/
        for (TeamManager team : TeamManager.values())
          for (int i = 0; i < team.getSpawns().size(); i++)
        	  this.settingsConfig.set("teams." + team.getName() + ".spawns." + i, Utils.toString(team.getSpawns().get(i)));
        /** Save the file **/
        try {
			this.settingsConfig.save(this.settingsFile);
		} catch (IOException e) {
			new ExceptionManager(e).register(true);
		}
    }
	
	private void initClasses()
	{
		ConfigManager.initConfig(this);
		Message.initMessages();
		Language.loadStartupConfiguration(this);
		
		try {
			File boosterFile = new File(this.getDataFolder(), "boosters.yml");
			if (!boosterFile.exists())
				boosterFile.createNewFile();
			BoosterManager.setupConfig(boosterFile);

			File kitFile = new File(this.getDataFolder(), "kits.yml");
			if (!kitFile.exists())
				kitFile.createNewFile();
			KitManager.setupConfig(kitFile);

			File sheepFile = new File(this.getDataFolder(), "sheeps.yml");
			if (!sheepFile.exists())
				sheepFile.createNewFile();
			SheepManager.setupConfig(sheepFile);
		} catch (IOException ex) {
			new ExceptionManager(ex).register(true);
			disablePlugin(Level.WARNING, "Something prevent the plugin to create important configuration files. Maybe it doesn't have the required permission.");
		}
		
		this.rewardManager = new RewardsManager(this);
	}
	
	private void setupProviders()
    {
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
    }

	@SuppressWarnings("unchecked")
	private <T> T loadLeaderHeadsModule(String name) throws ReflectiveOperationException {
        return (T) ReflectionUtils.instantiateObject(Class.forName(UltimateSheepWarsPlugin.PACKAGE + ".leaderheads." + name));
    }
	
	public void givePermission(Player player, String permission) {
		try {
			permissionProvider.playerAdd(player, permission);
		} catch (NoClassDefFoundError | NullPointerException e) {
		}
	}
	
    public void stop()
    {
    	GameState.setCurrentStep(GameState.TERMINATED);
    	String commd = ConfigManager.getString(Field.DISPATCH_COMMAND);
    	if (!commd.equals("stop") && !commd.equals("restart")) {
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
    	} else {
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commd);
    	}
    }
    
	public File getPluginJar() {
		return this.getFile();
	}
	
	public AccountManager getAccountManager() {
		return this.accountManager;
	}
	
	public RewardsManager getRewardsManager() {
		return this.rewardManager;
	}
	
	public static VersionManager getVersionManager() {
		return versionManager;
	}
	
	public BeginCountdown getPreGameTask() {
		return this.preGameTask;
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
		return (this.chatProvider != null);
	}
	
	public Chat getChatProvider() {
		return this.chatProvider;
	}
	
	public Economy getEconomyProvider() {
		return this.economyProvider;
	}
	
	public void setPreGameTask(BeginCountdown task) {
		this.preGameTask = task;
	}
	
	public void setGameTask(GameTask task) {
		this.gameTask = task;
	}
}