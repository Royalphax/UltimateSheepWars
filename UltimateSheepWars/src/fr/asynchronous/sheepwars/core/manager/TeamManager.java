package fr.asynchronous.sheepwars.core.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Scoreboard;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils.PackageType;

public enum TeamManager {
	BLUE("blue", Message.getMessage(MsgEnum.BLUE_NAME), ConfigManager.getMaterial(Field.TEAM_BLUE_MATERIAL), Field.BLUE_SPAWNS, DyeColor.BLUE, ChatColor.BLUE, 85, 85, 255),
	RED("red", Message.getMessage(MsgEnum.RED_NAME), ConfigManager.getMaterial(Field.TEAM_RED_MATERIAL), Field.RED_SPAWNS, DyeColor.RED, ChatColor.RED, 255, 50, 50),
	SPEC("spec", Message.getMessage(MsgEnum.SPEC_NAME), Material.STONE, Field.SPEC_SPAWNS, DyeColor.SILVER, ChatColor.GRAY, 0, 0, 0),
	NULL("null", new Message("null"), Material.AIR, null, DyeColor.WHITE, ChatColor.WHITE, 255, 255, 255);

	public static int redSlot;
	public static int blueSlot;

	static {
		redSlot = 4;
		blueSlot = 3;
	}

	private String name;
	private Material material;
	private final Message displayName;
	private DyeColor dyecolor;
	private final ChatColor color;
	private final Color leatherColor;
	private Field configField;
	private List<Player> players;
	private Boolean blocked;
	private int lastSpawn;

	public static TeamManager getRandomTeam() {
		TeamManager rdm = RandomUtils.getRandom(TeamManager.RED, TeamManager.BLUE);
		if (TeamManager.BLUE.getOnlinePlayers().size() == TeamManager.RED.getOnlinePlayers().size())
			return rdm;
		if (TeamManager.BLUE.getOnlinePlayers().size() < TeamManager.RED.getOnlinePlayers().size())
			return TeamManager.BLUE;
		else
			return TeamManager.RED;
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

	private TeamManager(final String name, final Message displayName, final Material material, final Field field, final DyeColor dyecolor, final ChatColor color, int r, int g, int b) {
		this.name = name;
		this.displayName = displayName;
		this.dyecolor = dyecolor;
		this.blocked = false;
		if (material != null)
			this.material = material;
		this.configField = field;
		this.color = color;
		this.leatherColor = Color.fromRGB(r, g, b);
		this.lastSpawn = 0;
		this.players = new ArrayList<>();
	}

	public void addPlayer(final Player player) {
		this.players.add(player);
		player.setPlayerListName(this.color + ((player.getName().length() > 14) ? player.getName().substring(0, 14) : player.getName()));
		for (Language lang : Language.getLanguages())
			lang.getTeam(this.name).addEntry(player.getName());
		if (this != TeamManager.SPEC && this != TeamManager.NULL)
			updateScoreboardTeamCount();
	}

	public void removePlayer(final Player player) {
		this.players.remove(player);
		for (Language lang : Language.getLanguages())
			lang.getTeam(this.name).removeEntry(player.getName());
		if (this != TeamManager.SPEC && this != TeamManager.NULL)
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

	public Location getNextSpawn() {
		List<Location> spawns = getSpawns();
		if (spawns.isEmpty())
			return null;
		if (spawns.size() == this.lastSpawn)
			this.lastSpawn = 0;
		return spawns.get(this.lastSpawn++);
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
		ItemStack i = new ItemBuilder(Material.STONE).setName("null").toItemStack();
		if (material == null)
			return i;
		i = new ItemStack(material, 1);
		if (material == Material.BANNER) {
			BannerMeta iMeta = (BannerMeta) i.getItemMeta();
			iMeta.setBaseColor(this.dyecolor);
			iMeta.setPatterns(Arrays.asList(new Pattern(DyeColor.WHITE, PatternType.CREEPER)));
			i.setItemMeta(iMeta);
		} else if (material == Material.WOOL) {
			MaterialData data = new Wool(this.dyecolor);
			i.setData(data);
		} else if (material == Material.INK_SACK) {
			MaterialData data = UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().getDye(this.dyecolor);
			i.setData(data);
		}
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName((this == BLUE ? "" + Message.getMessage(p, ChatColor.BLUE + "", MsgEnum.JOIN_BLUE_ITEM, "") : "" + Message.getMessage(p, ChatColor.RED + "", MsgEnum.JOIN_RED_ITEM, "")));
		i.setItemMeta(iMeta);
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
		if (this.configField == null)
			return new ArrayList<>();
		return ConfigManager.getLocations(this.configField);
	}

	public void addSpawn(Location location) {
		ConfigManager.addLocation(this.configField, location);
	}
	
	public static rebuildTeams() {
		
	}
}
