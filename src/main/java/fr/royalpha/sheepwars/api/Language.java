package fr.royalpha.sheepwars.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.royalpha.sheepwars.core.message.Message;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.event.EquipSelectionItemsEvent;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.handler.MinecraftVersion;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ConfigManager.Field;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.manager.ScoreboardManager;
import fr.royalpha.sheepwars.core.message.Message.Messages;
import fr.royalpha.sheepwars.core.util.ReflectionUtils;
import fr.royalpha.sheepwars.core.util.ReflectionUtils.PackageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Language {

	private static ArrayList<Language> languages;
	private static File DATA_FOLDER;
	private static File DEFAULT_LANGUAGE_FILE;

	static {
		languages = new ArrayList<>();
	}

	private HashMap<Message, String> messages;
	private File file;
	private FileConfiguration config;
	private String name;
	private String file_name;
	private String intro;
	private ScoreboardManager scoreboard_wrapper;

	public Language(File file) {
		this.messages = new HashMap<>();
		if (verify(file.getName().replace(".yml", "")) != null)
			return;
		this.file = file;
		this.config = YamlConfiguration.loadConfiguration(file);
		this.file_name = file.getName().replace(".yml", "");
		this.name = this.config.getString("language-name", "Default");
		this.intro = ChatColor.translateAlternateColorCodes('&', (this.config.getString("language-intro", "Current language: &a%NAME%").replaceAll("%NAME%", this.name)));
		for (Message m : Message.getMessages()) {
			final String path = "messages." + m.getStringId();
			final String value = this.config.getString(path, "null");
			if (!value.equals("null")) {
				messages.put(m, ChatColor.translateAlternateColorCodes('&', value));
			} else {
				this.config.set(path, m.uncolorized());
				messages.put(m, m.colorized());
			}
		}
		try {
			this.config.save(this.file);
		} catch (IOException ex) {
			ExceptionManager.register(ex, true);
		}
		final String title = getMessage(Messages.SCOREBOARD_WAITING_TITLE);
		this.scoreboard_wrapper = new ScoreboardManager(title, this.name);
		this.scoreboard_wrapper.setLine(2, ChatColor.BLUE + "", true);
		this.scoreboard_wrapper.setLine(5, ChatColor.WHITE + "", true);
		if (GameState.isStep(GameState.INGAME))
			this.scoreboard_wrapper.setLine(8, ChatColor.RED + "", true);
		this.initTeams();
		languages.add(this);
		
		// Don't forget to init scoreboard teams count
		SheepWarsTeam.RED.updateScoreboardTeamCount();
        SheepWarsTeam.BLUE.updateScoreboardTeamCount();
	}

	public String getName() {
		return this.name;
	}

	public String getLocale() {
		return this.file_name;
	}

	public File getFile() {
		return this.file;
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public ScoreboardManager getScoreboardWrapper() {
		return this.scoreboard_wrapper;
	}

	public void refreshSheepCountdown(int countdown) {
		this.scoreboard_wrapper.setLine(3, getMessage(Message.getMessage(Messages.SCOREBOARD_NEXT_SHEEP_COUNTDOWN)).replace("%TIME%", countdown + ""), true);
	}

	public void refreshBoosterCountdown(int countdown) {
		this.scoreboard_wrapper.setLine(4, getMessage(Message.getMessage(Messages.SCOREBOARD_NEXT_BOOSTER_COUNTDOWN)).replace("%TIME%", countdown + ""), true);
	}

	public org.bukkit.scoreboard.Team getTeam(String name) {
		return (this.scoreboard_wrapper.getScoreboard().getTeam(name) == null ? this.scoreboard_wrapper.getScoreboard().registerNewTeam(name) : this.scoreboard_wrapper.getScoreboard().getTeam(name));
	}

	public String getIntro() {
		return this.intro;
	}

	private void initTeams() {
		Boolean bool = SheepWarsPlugin.getVersionManager().getVersion().newerOrEqualTo(MinecraftVersion.v1_9_R1);
		for (SheepWarsTeam team : SheepWarsTeam.values()) {
			org.bukkit.scoreboard.Team bukkitTeam = this.scoreboard_wrapper.getScoreboard().getTeam(team.getName());
			if (bukkitTeam == null) {
				bukkitTeam = this.scoreboard_wrapper.getScoreboard().registerNewTeam(team.getName());
			}
			bukkitTeam.setPrefix(team.getColor().toString());
			bukkitTeam.setDisplayName(team.getName());
			bukkitTeam.setAllowFriendlyFire(false);
			bukkitTeam.setCanSeeFriendlyInvisibles(true);
			if (bool) {
				try {
					Class<?> clazzOption = ReflectionUtils.getClass("Team$Option", PackageType.BUKKIT_SCOREBOARD);
					Class<?> clazzOptionStatus = ReflectionUtils.getClass("Team$OptionStatus", PackageType.BUKKIT_SCOREBOARD);

					Object objOption = clazzOption.getMethod("valueOf", String.class).invoke(clazzOption, "COLLISION_RULE");
					Object objOptionStatus = clazzOptionStatus.getMethod("valueOf", String.class).invoke(clazzOptionStatus, "NEVER");

					Method method = bukkitTeam.getClass().getMethod("setOption", clazzOption, clazzOptionStatus);
					method.setAccessible(true);
					method.invoke(bukkitTeam, objOption, objOptionStatus);

					objOption = clazzOption.getMethod("valueOf", String.class).invoke(clazzOption, "NAME_TAG_VISIBILITY");
					objOptionStatus = clazzOptionStatus.getMethod("valueOf", String.class).invoke(clazzOptionStatus, "ALWAYS");

					method = bukkitTeam.getClass().getMethod("setOption", clazzOption, clazzOptionStatus);
					method.setAccessible(true);
					method.invoke(bukkitTeam, objOption, objOptionStatus);
				} catch (Exception ex) {
					ExceptionManager.register(ex, false);
				}
			}
		}
	}
	
	public void equipPlayer(Player player) {
		final PlayerData data = PlayerData.getPlayerData(player);
		final Map<Integer, ItemStack> items = new HashMap<>();
		items.put(0, SheepWarsTeam.RED.getIcon(player));
		items.put(1, SheepWarsTeam.BLUE.getIcon(player));
		int particleSlot = 4;
		if (SheepWarsPlugin.getWorldManager().isVoteModeEnable()) {
			items.put(3, new ItemBuilder(ConfigManager.getItemStack(Field.VOTING_ITEM)).setName(this.getMessage(Messages.VOTING_ITEM)).toItemStack());
			particleSlot = 5;
		}
		items.put(7, new ItemBuilder(ConfigManager.getItemStack(Field.KIT_ITEM)).setName(this.getMessage(Messages.KITS_ITEM)).toItemStack());
		items.put(8, new ItemBuilder(ConfigManager.getItemStack(Field.RETURN_TO_HUB_ITEM)).setName(this.getMessage(Messages.LEAVE_ITEM)).toItemStack());
		if (data.getAllowedParticles()) {
			items.put(particleSlot, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM)).setName(this.getMessage(Messages.PARTICLES_ON)).toItemStack());
		} else {
			items.put(particleSlot, new ItemBuilder(ConfigManager.getItemStack(Field.PARTICLES_OFF_ITEM)).setName(this.getMessage(Messages.PARTICLES_OFF)).toItemStack());
		}
		
		final EquipSelectionItemsEvent event = new EquipSelectionItemsEvent(player, items);
		Bukkit.getServer().getPluginManager().callEvent(event);
		event.equip();
		
		player.setScoreboard(this.getScoreboardWrapper().getScoreboard());
	}

	public static boolean existLanguage(File f) {
		for (Language lang : languages)
			if (lang.getFile().getAbsolutePath().equals(f.getAbsolutePath()))
				return true;
		return false;
	}

	public static Language getLanguage(String locale) {
		String first = locale.split("_")[0]; 
		for (Language lang : languages)
			if (lang.getLocale().equalsIgnoreCase(locale))
				return lang;
		for (Language lang : languages)
			if (lang.getLocale().equalsIgnoreCase(first + "_X"))
				return lang;
		if (!ConfigManager.getBoolean(Field.AUTO_GENERATE_LANGUAGES)) 
			return getDefaultLanguage();
		return createLanguageIfNotExist(locale, "", "", true);
	}
	
	public static Language verify(String s) {
		for (Language lang : languages)
			if (lang.getLocale().equals(s))
				return lang;
		return null;
	}

	public String getMessage(Message m) {
		if (!this.messages.containsKey(m)) {
			this.config.set("messages." + m.getStringId(), m.uncolorized());
			messages.put(m, m.colorized());
			try {
				this.config.save(this.file);
			} catch (IOException ex) {
				ExceptionManager.register(ex, true);
			}
		}
		return this.messages.get(m);
	}
	
	public String getMessage(Messages en) {
		Message m = Message.getMessage(en);
		return getMessage(m);
	}

	public HashMap<Message, String> getMessages() {
		return this.messages;
	}

	public static ArrayList<Language> getLanguages() {
		return languages;
	}

	public static String getMessageByLocale(String locale, Message message) {
		if (message == null)
			return "";
		return getLanguage(locale).getMessage(message);
	}
	
	public static Boolean isLanguageFile(File f) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if (config.getString("language-name") != null && config.getString("language-intro") != null && f.getName().contains("_"))
			return true;
		return false;
	}

	public static void listAvailableLanguages(Player player) {
		Language lang = PlayerData.getPlayerData(player).getLanguage();
		player.sendMessage(ChatColor.GRAY + lang.getIntro());
		player.sendMessage(ChatColor.YELLOW + "Available languages :");
		for (Language langs : Language.getLanguages()) {
			TextComponent msg = new TextComponent(TextComponent.fromLegacyText(ChatColor.YELLOW + "- " + langs.getName() + " " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "???" + ChatColor.DARK_GRAY + "]"));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lang " + langs.getLocale().replace(".yml", "")));
			msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.YELLOW + "Click to select!")));
			player.spigot().sendMessage(msg);
		}
		player.sendMessage(ChatColor.YELLOW + "Click on the wanted language to change");
	}

	public static void loadStartupConfiguration(SheepWarsPlugin plugin) {
		DATA_FOLDER = new File(plugin.getDataFolder(), "languages/");
		if (!DATA_FOLDER.exists())
			DATA_FOLDER.mkdirs();
		DEFAULT_LANGUAGE_FILE = new File(DATA_FOLDER, "x_X.yml");
		if (!DEFAULT_LANGUAGE_FILE.exists()) {
			try {
				DEFAULT_LANGUAGE_FILE.createNewFile();
				FileConfiguration config = YamlConfiguration.loadConfiguration(DEFAULT_LANGUAGE_FILE);
				config.set("language-name", "English (default)");
				config.set("language-intro", "Current language: &aEnglish &7(default)");
				try {
					config.save(DEFAULT_LANGUAGE_FILE);
				} catch (IOException ex) {
					ExceptionManager.register(ex, true);
				}
			} catch (IOException ex) {
				ExceptionManager.register(ex, true);
			}
		}
		if (ConfigManager.getBoolean(Field.AUTO_GENERATE_LANGUAGES))
			createLanguageIfNotExist("en_EN", "English", "Current language: &a%NAME%", false);
		for (File file : DATA_FOLDER.listFiles())
			if (file.getName().endsWith(".yml"))
				new Language(file);
	}

	public static Language getDefaultLanguage() {
		return getLanguage("x_X");
	}

	public static Language createLanguageIfNotExist(String locale, String name, String intro, boolean createNewInstance) {
		File newLanguage = new File(DATA_FOLDER, locale + ".yml");
		if (!newLanguage.exists()) {
			try {
				FileUtils.copyFile(DEFAULT_LANGUAGE_FILE, newLanguage);
				FileConfiguration config = YamlConfiguration.loadConfiguration(newLanguage);
				config.set("language-name", (name.trim().equals("") ? locale : name));
				config.set("language-intro", (intro.trim().equals("") ? "Current language: &a%NAME%" : intro));
				try {
					config.save(newLanguage);
				} catch (IOException ex) {
					ExceptionManager.register(ex, true);
				}
				Bukkit.getLogger().info("[UltimateSheepWars > Multi-Language Manager] A new language file has been created: " + newLanguage.getAbsolutePath());
			} catch (IOException ex) {
				ExceptionManager.register(ex, true);
			}
		}
		return (createNewInstance ? new Language(newLanguage) : null);
	}
}
