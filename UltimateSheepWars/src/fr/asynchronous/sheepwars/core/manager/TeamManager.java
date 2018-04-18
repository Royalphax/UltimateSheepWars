package fr.asynchronous.sheepwars.core.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils.PackageType;

public enum TeamManager {
	BLUE((byte) 11, "blue", Message.getMessage(MsgEnum.BLUE_NAME), ConfigManager.getMaterial(Field.TEAM_BLUE_MATERIAL), ConfigManager.getLocations(Field.BLUE_SPAWNS), DyeColor.BLUE, ChatColor.BLUE, 85, 85, 255),
	RED((byte) 14, "red", Message.getMessage(MsgEnum.RED_NAME), ConfigManager.getMaterial(Field.TEAM_RED_MATERIAL), ConfigManager.getLocations(Field.RED_SPAWNS), DyeColor.RED, ChatColor.RED, 255, 50, 50),
	SPEC((byte) 2, "spec", Message.getMessage(MsgEnum.SPEC_NAME), Material.STONE, ConfigManager.getLocations(Field.SPEC_SPAWNS), DyeColor.SILVER, ChatColor.GRAY, 0, 0, 0);

	public static int redSlot;
	public static int blueSlot;

	static {
		redSlot = 4;
		blueSlot = 3;
	}

	private String name;
	private byte byteColor;
	private Material material;
	private final Message displayName;
	private DyeColor dyecolor;
	private final ChatColor color;
	private final Color leatherColor;
	private List<Location> spawns;
	private List<Player> players;
	private Boolean blocked;
	private int lastSpawn;

	public static TeamManager getRandomTeam() {
		int rdm = new Random().nextInt(2);
		return ((TeamManager.BLUE.getOnlinePlayers().size() == TeamManager.RED.getOnlinePlayers().size()) ? (rdm == 1 ? TeamManager.RED : TeamManager.BLUE) : ((TeamManager.BLUE.getOnlinePlayers().size() < TeamManager.RED.getOnlinePlayers().size()) ? TeamManager.BLUE : TeamManager.RED));
	}

	public static TeamManager getTeam(final String name) {
		for (TeamManager team : values()) {
			if (team.name.equals(name)) {
				return team;
			}
		}
		return null;
	}

	public static TeamManager getTeam(final ChatColor color) {
		for (TeamManager team : values()) {
			if (team.color == color) {
				return team;
			}
		}
		return null;
	}

	private TeamManager(final byte byteColor, final String name, final Message displayName, final Material material, final List<Location> spawns, final DyeColor dyecolor, final ChatColor color, int r, int g, int b) {
		this.name = name;
		this.byteColor = byteColor;
		this.displayName = displayName;
		this.dyecolor = dyecolor;
		this.blocked = false;
		if (material != null)
			this.material = material;
		this.spawns = spawns;
		this.color = color;
		this.leatherColor = Color.fromRGB(r, g, b);
		this.lastSpawn = 0;
		this.spawns = new ArrayList<>();
		this.players = new ArrayList<>();
	}

	public void addPlayer(final Player player) {
		this.players.add(player);
		player.setPlayerListName(this.color + ((player.getName().length() > 14) ? player.getName().substring(0, 14) : player.getName()));
		for (Language lang : Language.getLanguages())
			lang.getTeam(this.name).addEntry(player.getName());
		if (this != TeamManager.SPEC) 
			updateScoreboardTeamCount();
	}

	public void removePlayer(final Player player) {
		this.players.remove(player);
		for (Language lang : Language.getLanguages())
			lang.getTeam(this.name).removeEntry(player.getName());
		if (this != TeamManager.SPEC)
			updateScoreboardTeamCount();
	}

	public void updateScoreboardTeamCount() {
		for (Language langs : Language.getLanguages())
			langs.getScoreboardWrapper().setLine((this == TeamManager.RED ? redSlot : blueSlot), langs.getMessage((this == TeamManager.RED ? Message.getMessage(MsgEnum.SCOREBOARD_TEAM_RED) : Message.getMessage(MsgEnum.SCOREBOARD_TEAM_BLUE))).replaceAll("%SIZE%", this.players.size() + ""), true);
	}

	public List<Player> getOnlinePlayers() {
		return this.players;
	}

	public void setBlocked(boolean bool) {
		this.blocked = bool;
	}

	public Boolean isBlocked() {
		return this.blocked;
	}

	public void broadcastMessage(final String msg) {
		for (final Player player : this.getOnlinePlayers()) {
			player.sendMessage(msg);
		}
	}

	public Location getNextSpawn() {
		if (this.spawns.isEmpty()) {
			return null;
		}
		if (this.spawns.size() == this.lastSpawn) {
			this.lastSpawn = 0;
		}
		return this.spawns.get(this.lastSpawn++);
	}

	public void inGameRules() {
		redSlot = 7;
		blueSlot = 6;
		if (this != TeamManager.SPEC)
			updateScoreboardTeamCount();
		Boolean bool = UltimateSheepWarsPlugin.getVersionManager().getVersion().newerThan(MinecraftVersion.v1_9_R1);
		for (Language lang : Language.getLanguages()) {
			final Scoreboard scoreboard = lang.getScoreboardWrapper().getScoreboard();
			org.bukkit.scoreboard.Team team = scoreboard.getTeam(this.name);
			if (bool) {
				try {
					Class<?> clazzOption = ReflectionUtils.getClass("Team$Option", PackageType.BUKKIT_SCOREBOARD);
					Class<?> clazzOptionStatus = ReflectionUtils.getClass("Team$OptionStatus", PackageType.BUKKIT_SCOREBOARD);

					Object objOption = clazzOption.getMethod("valueOf", String.class).invoke(clazzOption, "COLLISION_RULE");
					Object objOptionStatus = clazzOptionStatus.getMethod("valueOf", String.class).invoke(clazzOptionStatus, "FOR_OTHER_TEAMS");

					Method method = team.getClass().getMethod("setOption", clazzOption, clazzOptionStatus);
					method.setAccessible(true);
					method.invoke(team, objOption, objOptionStatus);
				} catch (Exception ex) {
					new ExceptionManager(ex).register(true);
				}
			}
		}
	}

	public String getDisplayName(Player player) {
		return this.displayName.getMessage(player);
	}

	public ItemStack getIcon(Player p) {
		ItemStack i = new ItemBuilder(Material.STONE).setName("NPE").toItemStack();
		if (material == null)
			return i;
		i = new ItemStack(material, 1, this.byteColor);
		if (material == Material.BANNER) {
			BannerMeta iMeta = (BannerMeta) i.getItemMeta();
			iMeta.setDisplayName((this == BLUE ? "" + Message.getMessage(p, ChatColor.BLUE + "", MsgEnum.JOIN_BLUE_ITEM, "") : "" + Message.getMessage(p, ChatColor.RED + "", MsgEnum.JOIN_RED_ITEM, "")));
			iMeta.setBaseColor(this.dyecolor);
			iMeta.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.CREEPER)));
			i.setItemMeta(iMeta);
		} else {
			ItemMeta iMeta = i.getItemMeta();
			iMeta.setDisplayName((this == BLUE ? "" + Message.getMessage(p, ChatColor.BLUE + "", MsgEnum.JOIN_BLUE_ITEM, "") : "" + Message.getMessage(p, ChatColor.RED + "", MsgEnum.JOIN_RED_ITEM, "")));
			i.setItemMeta(iMeta);
		}
		return i;
	}

	public String getName() {
		return this.name;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public Material getMaterial() {
		return this.material;
	}

	public Color getLeatherColor() {
		return this.leatherColor;
	}

	public DyeColor getDyeColor() {
		return this.dyecolor;
	}

	public List<Location> getSpawns() {
		return this.spawns;
	}
}
