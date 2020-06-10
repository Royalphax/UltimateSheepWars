package fr.asynchronous.sheepwars.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.asynchronous.sheepwars.api.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.legacy.LegacyBanner;
import fr.asynchronous.sheepwars.core.legacy.LegacyItem;
import fr.asynchronous.sheepwars.core.legacy.LegacyMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils.PackageType;

public enum SheepWarsTeam {
	BLUE("blue", Message.getMessage(Messages.BLUE_NAME), ConfigManager.getMaterial(Field.TEAM_BLUE_MATERIAL), DyeColor.BLUE, ChatColor.BLUE, 85, 85, 255),
	RED("red", Message.getMessage(Messages.RED_NAME), ConfigManager.getMaterial(Field.TEAM_RED_MATERIAL), DyeColor.RED, ChatColor.RED, 255, 50, 50),
	SPEC("spec", Message.getMessage(Messages.SPEC_NAME), Material.STONE, DyeColor.GRAY, ChatColor.WHITE, 0, 0, 0),
	NULL("null", new Message("null"), Material.AIR, DyeColor.WHITE, ChatColor.WHITE, 255, 255, 255);

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
	private List<Player> players;
	private Boolean blocked;
	private int lastSpawn;

	public static SheepWarsTeam getRandomTeam() {
		SheepWarsTeam rdm = RandomUtils.getRandom(SheepWarsTeam.RED, SheepWarsTeam.BLUE);
		if (SheepWarsTeam.BLUE.getOnlinePlayers().size() == SheepWarsTeam.RED.getOnlinePlayers().size())
			return rdm;
		if (SheepWarsTeam.BLUE.getOnlinePlayers().size() < SheepWarsTeam.RED.getOnlinePlayers().size())
			return SheepWarsTeam.BLUE;
		else
			return SheepWarsTeam.RED;
	}

	public static SheepWarsTeam getTeam(final String name) {
		for (SheepWarsTeam team : values()) {
			if (team.name.equals(name)) {
				return team;
			}
		}
		return null;
	}

	public static SheepWarsTeam getTeam(final ChatColor color) {
		for (SheepWarsTeam team : values()) {
			if (team.color == color) {
				return team;
			}
		}
		return null;
	}

	private SheepWarsTeam(final String name, final Message displayName, final Material material, final DyeColor dyecolor, final ChatColor color, int r, int g, int b) {
		this.name = name;
		this.displayName = displayName;
		this.dyecolor = dyecolor;
		this.blocked = false;
		if (material != null)
			this.material = material;
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
		if (this != SheepWarsTeam.SPEC && this != SheepWarsTeam.NULL)
			updateScoreboardTeamCount();
	}

	public void removePlayer(final Player player) {
		this.players.remove(player);
		for (Language lang : Language.getLanguages())
			lang.getTeam(this.name).removeEntry(player.getName());
		if (this != SheepWarsTeam.SPEC && this != SheepWarsTeam.NULL)
			updateScoreboardTeamCount();
	}

	public void updateScoreboardTeamCount() {
		for (Language langs : Language.getLanguages())
			langs.getScoreboardWrapper().setLine((this == SheepWarsTeam.RED ? redSlot : blueSlot), langs.getMessage((this == SheepWarsTeam.RED ? Message.getMessage(Messages.SCOREBOARD_TEAM_RED) : Message.getMessage(Messages.SCOREBOARD_TEAM_BLUE))).replaceAll("%SIZE%", this.players.size() + ""), true);
	}

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(this.players);
	}

	public void setBlocked(boolean bool) {
		this.blocked = bool;
	}

	public Boolean isBlocked() {
		return this.blocked;
	}

	public Location getNextSpawn() {
		List<Location> spawns = SheepWarsPlugin.getWorldManager().getVoteResult().getTeamSpawns(this).getBukkitLocations();
		if (spawns.isEmpty())
			return ConfigManager.getLocation(Field.LOBBY).toBukkitLocation();
		if (this.lastSpawn >= spawns.size())
			this.lastSpawn = 0;
		return spawns.get(this.lastSpawn++);
	}

	public void inGameRules() {
		redSlot = 7;
		blueSlot = 6;
		if (this != SheepWarsTeam.SPEC)
			updateScoreboardTeamCount();
		Boolean bool = SheepWarsPlugin.getVersionManager().getVersion().newerOrEqualTo(MinecraftVersion.v1_9_R1);
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
					ExceptionManager.register(ex, true);
				}
			}
		}
	}

	public String getDisplayName(Player player) {
		return this.displayName.getMessage(player);
	}

	public ItemStack getIcon(Player p) {
		if (this.material == null)
			return new ItemBuilder(Material.STONE).setName("null").toItemStack();
		ItemBuilder item = new ItemBuilder(this.material).setColor(this.dyecolor);
		if (this.material.toString().contains("BANNER")) {
			item = new ItemBuilder(LegacyBanner.setColor(item.toItemStack(), this.dyecolor));
		} else if (this.material.toString().contains("WOOL")) {
			item = new ItemBuilder(new LegacyItem(LegacyMaterial.WOOL, this.dyecolor));
		} else if (this.material.toString().contains("STAINED_GLASS_PANE")) {
			item = new ItemBuilder(new LegacyItem(LegacyMaterial.STAINED_GLASS_PANE, this.dyecolor));
		} else if (this.material.toString().contains("STAINED_GLASS")) {
			item = new ItemBuilder(new LegacyItem(LegacyMaterial.STAINED_GLASS, this.dyecolor));
		}
		item.setName((this == BLUE ? "" + Message.getMessage(p, ChatColor.BLUE + "", Messages.JOIN_BLUE_ITEM, "") : "" + Message.getMessage(p, ChatColor.RED + "", Messages.JOIN_RED_ITEM, "")));
		return item.toItemStack();
	}

	public String getName() {
		return this.name;
	}

	public ChatColor getColor() {
		return this.color;
	}
	
	public net.md_5.bungee.api.ChatColor getBungeeColor() {
		return net.md_5.bungee.api.ChatColor.valueOf(this.color.name());
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

	public static boolean checkTeams() {
		if (SheepWarsTeam.BLUE.getOnlinePlayers().isEmpty() || SheepWarsTeam.RED.getOnlinePlayers().isEmpty()) {
			rebuildTeams();
			return true;
		}
		return false;
	}

	private static void rebuildTeams() {
		/** On initialise **/
		int redTeamCount = 0;
		int blueTeamCount = 0;
		List<Player> noTeamPlayers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerData data = PlayerData.getPlayerData(player);
			if (data.hasTeam()) {
				if (data.getTeam() == RED) {
					redTeamCount++;
				} else if (data.getTeam() == BLUE) {
					blueTeamCount++;
				}
			} else {
				noTeamPlayers.add(player);
			}
		}
		int noTeamCount = noTeamPlayers.size();

		/** DEBUT DE L'ALGORITHME **/
		
		if (redTeamCount == 0 && blueTeamCount == 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				PlayerData data = PlayerData.getPlayerData(player);
				data.setTeam(getRandomTeam());
			}
		} else if (blueTeamCount == 0) {
			if ((noTeamCount + 1) >= redTeamCount && (noTeamCount - 1) <= redTeamCount) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					PlayerData data = PlayerData.getPlayerData(player);
					if (!data.hasTeam())
						data.setTeam(SheepWarsTeam.BLUE);
				}
			} else if (noTeamCount < redTeamCount) {
				/** On trie les joueurs de l'équipe pour obtenir une jolie liste triée en fonction de si ils ont la perm ou pas **/
				List<Player> nonPrioPlayers = new ArrayList<>();
				List<Player> prioPlayers = new ArrayList<>();
				for (Player redPlayer : SheepWarsTeam.RED.getOnlinePlayers()) {
					if (Permissions.USW_BYPASS_TEAMS.hasPermission(redPlayer)) {
						prioPlayers.add(redPlayer);
					} else {
						nonPrioPlayers.add(redPlayer);
					}
				}
				List<Player> redPlayers = new ArrayList<>();
				for (Player player : nonPrioPlayers)
					redPlayers.add(player);
				for (Player player : prioPlayers)
					redPlayers.add(player);
				/** Maintenant, on ajoute le nbre de joueurs necessaires dans l'equipe bleu en plus de ceux qui ont pas de team **/
				for (Player player : noTeamPlayers)
					PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.BLUE);
				for (Player player : redPlayers) {
					if (SheepWarsTeam.BLUE.getOnlinePlayers().size() < SheepWarsTeam.RED.getOnlinePlayers().size()) {
						PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.BLUE);
					} else {
						break;
					}
				}
			} else if (noTeamCount > redTeamCount) {
				/** On trie les joueurs de l'équipe pour obtenir une jolie liste triée en fonction de si ils ont la perm ou pas **/
				List<Player> nonPrioPlayers = new ArrayList<>();
				List<Player> prioPlayers = new ArrayList<>();
				for (Player noTeamPlayer : noTeamPlayers) {
					if (Permissions.USW_BYPASS_TEAMS.hasPermission(noTeamPlayer)) {
						prioPlayers.add(noTeamPlayer);
					} else {
						nonPrioPlayers.add(noTeamPlayer);
					}
				}
				/** Maintenant on regarde lesquels on va foutre dans l'equipe rouge pour equilibrer **/
				List<Player> bluePlayers = new ArrayList<>();
				for (Player player : nonPrioPlayers)
					bluePlayers.add(player);
				for (Player player : prioPlayers)
					bluePlayers.add(player);
				/** Maintenant, on ajoute le nbre de joueurs necessaires dans l'equipe rouge en plus de ceux qui ont pas de team **/
				for (Player player : noTeamPlayers)
					PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.BLUE);
				for (Player player : bluePlayers) {
					if (SheepWarsTeam.BLUE.getOnlinePlayers().size() > SheepWarsTeam.RED.getOnlinePlayers().size()) {
						PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.RED);
					} else {
						break;
					}
				}
			}
		} else if (redTeamCount == 0) {
			if ((noTeamCount + 1) >= blueTeamCount && (noTeamCount - 1) <= blueTeamCount) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					PlayerData data = PlayerData.getPlayerData(player);
					if (!data.hasTeam())
						data.setTeam(SheepWarsTeam.RED);
				}
			} else if (noTeamCount < blueTeamCount) {
				/** On trie les joueurs de l'équipe pour obtenir une jolie liste triée en fonction de si ils ont la perm ou pas **/
				List<Player> nonPrioPlayers = new ArrayList<>();
				List<Player> prioPlayers = new ArrayList<>();
				for (Player bluePlayer : SheepWarsTeam.BLUE.getOnlinePlayers()) {
					if (Permissions.USW_BYPASS_TEAMS.hasPermission(bluePlayer)) {
						prioPlayers.add(bluePlayer);
					} else {
						nonPrioPlayers.add(bluePlayer);
					}
				}
				List<Player> bluePlayers = new ArrayList<>();
				for (Player player : nonPrioPlayers)
					bluePlayers.add(player);
				for (Player player : prioPlayers)
					bluePlayers.add(player);
				/** Maintenant, on ajoute le nbre de joueurs necessaires dans l'equipe rouge en plus de ceux qui ont pas de team **/
				for (Player player : noTeamPlayers)
					PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.RED);
				for (Player player : bluePlayers) {
					if (SheepWarsTeam.BLUE.getOnlinePlayers().size() > SheepWarsTeam.RED.getOnlinePlayers().size()) {
						PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.RED);
					} else {
						break;
					}
				}
			} else if (noTeamCount > blueTeamCount) {
				/** On trie les joueurs de l'équipe pour obtenir une jolie liste triée en fonction de si ils ont la perm ou pas **/
				List<Player> nonPrioPlayers = new ArrayList<>();
				List<Player> prioPlayers = new ArrayList<>();
				for (Player noTeamPlayer : noTeamPlayers) {
					if (Permissions.USW_BYPASS_TEAMS.hasPermission(noTeamPlayer)) {
						prioPlayers.add(noTeamPlayer);
					} else {
						nonPrioPlayers.add(noTeamPlayer);
					}
				}
				/** Maintenant on regarde lesquels on va foutre dans l'equipe rouge pour equilibrer **/
				List<Player> redPlayers = new ArrayList<>();
				for (Player player : nonPrioPlayers)
					redPlayers.add(player);
				for (Player player : prioPlayers)
					redPlayers.add(player);
				/** Maintenant, on ajoute tout les joueurs sans team dans la rouge et on vient equilibrer en en envoyant dans la bleu **/
				for (Player player : noTeamPlayers)
					PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.RED);
				for (Player player : redPlayers) {
					if (SheepWarsTeam.BLUE.getOnlinePlayers().size() < SheepWarsTeam.RED.getOnlinePlayers().size()) {
						PlayerData.getPlayerData(player).setTeam(SheepWarsTeam.BLUE);
					} else {
						break;
					}
				}
			}
		}
	}
}
