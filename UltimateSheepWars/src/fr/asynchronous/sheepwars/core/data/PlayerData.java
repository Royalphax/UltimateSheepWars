package fr.asynchronous.sheepwars.core.data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.event.player.PlayerJoin;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class PlayerData extends DataManager {

	private static Map<OfflinePlayer, PlayerData> dataMap;
	private static ArrayList<OfflinePlayer> particlePlayers;

	static {
		dataMap = new HashMap<>();
		particlePlayers = new ArrayList<>();
	}

	private boolean loaded = false;

	private OfflinePlayer player;
	private String uid;
	private String name;
	private Language language;
	private Boolean particle;
	private int wins;
	private int kills;
	private int actualKills;
	private int deaths;
	private int games;
	private int sheepThrown;
	private int sheepKilled;
	private int totalTime;
	private KitManager kit;
	private Map<KitManager, Integer> kits;
	private TeamManager team;
	private String winRate;
	private String kdRatio;
	private Date updatedAt;
	private Date createdAt;

	public PlayerData(final OfflinePlayer player) {
		this.player = player;
		this.uid = player.getUniqueId().toString().replace("-", "");
		this.name = player.getName();
		this.language = Language.getDefaultLanguage();
		this.particle = true;
		this.wins = 0;
		this.kills = 0;
		this.actualKills = 0;
		this.deaths = 0;
		this.games = 0;
		this.sheepThrown = 0;
		this.sheepKilled = 0;
		this.totalTime = 0;
		this.kit = new NoneKit();
		this.kits = new HashMap<>();
		this.kits.put(this.kit, 0);
		this.team = TeamManager.NULL;
		this.winRate = "0.0";
		this.kdRatio = "0.0";
		final Date now = new Date(System.currentTimeMillis());
		this.updatedAt = now;
		this.createdAt = now;
	}

	public OfflinePlayer getOfflinePlayer() {
		return this.player;
	}

	public Player getPlayer() {
		return this.player.getPlayer();
	}

	public String getUID() {
		return this.uid;
	}

	public String getName() {
		return this.name;
	}

	public Language getLanguage() {
		return this.language;
	}

	public Boolean getAllowedParticles() {
		return this.particle;
	}

	public int getWins() {
		return this.wins;
	}

	public int getKills() {
		return this.kills;
	}

	public int getActualKills() {
		return this.actualKills;
	}

	public KitManager getKit() {
		return this.kit;
	}

	public List<KitManager> getKits() {
		List<KitManager> kits = new ArrayList<>(this.kits.keySet());
		for (KitManager kit : this.kits.keySet())
			if (kit.isFreeKit())
				kits.remove(kit);
		return kits;
	}

	public String getKitsString() {
		StringBuilder output = new StringBuilder("");
		for (KitManager k : this.kits.keySet()) {
			if (k.getId() != this.kit.getId()) {
				output.append(k.getId());
				output.append(this.kits.get(k));
				output.append("-");
			}
		}
		output.append(this.kit.getId());
		output.append(this.kits.get(this.kit));
		return output.toString().trim();
	}

	public TeamManager getTeam() {
		return this.team;
	}

	public int getDeaths() {
		return this.deaths;
	}

	public int getGames() {
		return this.games;
	}

	public int getSheepThrown() {
		return this.sheepThrown;
	}

	public int getSheepKilled() {
		return this.sheepKilled;
	}

	public int getTotalTime() {
		return this.totalTime;
	}

	public String getKDRatio() {
		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		this.kdRatio = decimalFormat.format((double) this.kills / this.deaths);
		return this.kdRatio;
	}

	public String getWinRate() {
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		this.winRate = decimalFormat.format((double) this.wins * 100 / this.games);
		return this.winRate;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public void setUpdatedAt(final Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAllowParticles(final Boolean particle) {
		this.particle = particle;
		if (particle) {
			if (!particlePlayers.contains(this.player))
				particlePlayers.add(this.player);
			if (this.player.isOnline())
				this.getPlayer().resetPlayerWeather();
		} else {
			if (particlePlayers.contains(this.player))
				particlePlayers.remove(this.player);
			if (this.player.isOnline())
				this.getPlayer().setPlayerWeather(WeatherType.CLEAR);
		}
	}

	public void setLanguage(final Language lang) {
		this.language = lang;
		if (GameState.isStep(GameState.WAITING)) {
			PlayerJoin.equip(PlayerData.getPlayerData(player));
		}
		if (player.isOnline()) {
			getPlayer().setScoreboard(lang.getScoreboardWrapper().getScoreboard());
			getPlayer().sendMessage(ChatColor.GRAY + lang.getIntro());
		}
	}

	public void setKills(final int i) {
		this.kills = i;
	}

	public int getKitLevel(KitManager kit) {
		if (hasKit(kit)) {
			return this.kits.get(kit);
		} else {
			return -1;
		}
	}

	public int getKitLevel() {
		return getKitLevel(this.kit);
	}

	public boolean hasKit(KitManager kit) {
		return this.kits.containsKey(kit);
	}

	public void setKit(KitManager kit, Integer level) {
		setKit(kit, level, false);
	}

	public void setKit(KitManager kit, Integer level, boolean lastOne) {
		kit = KitManager.getInstanceKit(kit);
		this.kit = kit;
		addKit(this.kit, level);
		if (this.player.isOnline()) {
			String message;
			if (!lastOne) {
				message = Message.getMessage(this.player.getPlayer(), MsgEnum.KIT_CHOOSE_MESSAGE);
			} else {
				message = Message.getMessage(this.player.getPlayer(), MsgEnum.KIT_LAST_SELECTED);
			}
			message = message.replaceAll("%KIT_NAME%", kit.getName(this.language));
			String levelMessage;
			if (kit.getLevels().size() > 1) {
				levelMessage = new String(kit.getLevel(level).getName(this.language));
			} else {
				levelMessage = "";
			}
			message = message.replaceAll("%LEVEL_NAME%", levelMessage);
			this.player.getPlayer().sendMessage(message);
			Sounds.playSound(getPlayer(), Sounds.STEP_WOOD, 1f, 0f);
		}
	}

	public void addKit(KitManager kit, Integer level) {
		kit = KitManager.getInstanceKit(kit);
		if (this.kits.containsKey(kit))
			this.kits.remove(kit);
		this.kits.put(kit, level);
	}

	public void removeKit(KitManager kit) {
		kit = KitManager.getInstanceKit(kit);
		if (this.kits.containsKey(kit))
			this.kits.remove(kit);
		if (this.kit == kit)
			setKit(new NoneKit(), 0);
	}

	public void setTeam(TeamManager team) {
		if (this.player.isOnline()) {
			if (this.team != null)
				this.team.removePlayer(this.player.getPlayer());
			this.team = team;
			team.addPlayer(this.player.getPlayer());
		}
	}

	public void setWins(final int i) {
		this.wins = i;
	}

	public void setDeaths(final int i) {
		this.deaths = i;
	}

	public void setGames(final int i) {
		this.games = i;
	}

	public void setSheepThrown(final int i) {
		this.sheepThrown = i;
	}

	public void setSheepKilled(final int i) {
		this.sheepKilled = i;
	}

	public void setTotalTime(final int i) {
		this.totalTime = i;
	}

	public void increaseWins(final int wins) {
		this.wins += wins;
	}

	public void increaseKills(final int kills) {
		this.kills += kills;
		this.actualKills++;
	}

	public void increaseDeaths(final int deaths) {
		this.deaths += deaths;
	}

	public void increaseGames(final int games) {
		this.games += games;
	}

	public void increaseSheepThrown(final int sheepThrown) {
		this.sheepThrown += sheepThrown;
	}

	public void increaseSheepKilled(final int sheepKilled) {
		this.sheepKilled += sheepKilled;
	}

	public void increaseTotalTime(final int totalTime) {
		this.totalTime += totalTime;
	}

	public boolean hasTeam() {
		return (this.team != null && this.team != TeamManager.SPEC && this.team != TeamManager.NULL);
	}

	public boolean isSpectator() {
		return (this.team != null && this.team == TeamManager.SPEC);
	}

	@Override
	public String toString() {
		return "PlayerData(" + "uid=" + this.uid + ", name=" + this.name + ", locale=" + this.language.getLocale() + ", particle=" + this.particle + ", wins=" + this.wins + ", kills=" + this.kills + ", deaths=" + this.deaths + ", games=" + this.games + ", sheepThrown=" + this.sheepThrown + ", sheepKilled=" + this.sheepKilled + ", totalTime=" + this.totalTime + ", actualKills=" + this.actualKills + ", lastKit=" + this.kit.getId() + ", winRate=" + this.winRate + ", kdRatio=" + this.kdRatio + ", updatedAt=" + this.updatedAt + ", createdAt=" + this.createdAt + ")";
	}

	/**
	 * Get player's data.
	 * 
	 * @return player's data.
	 */
	public static PlayerData getPlayerData(final OfflinePlayer player) {
		PlayerData playerData = new PlayerData(player);
		if (!dataMap.containsKey(player))
			playerData.loadData(player);
		return dataMap.get(player);
	}

	@Override
	public void loadData(final OfflinePlayer player) {
		if (connectedToDatabase) {
			new Thread(() -> {
				String identifier = Bukkit.getServer().getOnlineMode() ? "uuid=UNHEX('" + this.uid + "')" : "name='" + this.name + "'";
				try {
					ResultSet res = database.querySQL("SELECT * FROM players WHERE " + identifier);
					if (res.first()) {
						setDeaths(res.getInt("deaths"));
						setGames(res.getInt("games"));
						setKills(res.getInt("kills"));
						setAllowParticles(res.getInt("particles") == 1);
						setWins(res.getInt("wins"));
						setSheepThrown(res.getInt("sheep_thrown"));
						setSheepKilled(res.getInt("sheep_killed"));
						setTotalTime(res.getInt("total_time"));
						if (res.getString("kits").contains("-")) {
							String[] availableKits = res.getString("kits").split("-");
							for (int i = 0; i < availableKits.length - 1; i++) {
								String kitString = availableKits[i];
								String[] kitStringSplitted = kitString.split("");
								final int kitId = Integer.parseInt(kitStringSplitted[0]);
								final int kitLevel = Integer.parseInt(kitStringSplitted[1]);
								if (KitManager.existKit(kitId)) {
									addKit(KitManager.getFromId(kitId), kitLevel);
								}
							}
							String[] lastKit = availableKits[availableKits.length - 1].split("");
							setKit(KitManager.getFromId(Integer.parseInt(lastKit[0])), Integer.parseInt(lastKit[1]), true);
						} else {
							String[] availableKits = res.getString("kits").split("");
							for (String kitId : availableKits)
								if (KitManager.existKit(Integer.parseInt(kitId)))
									addKit(KitManager.getFromId(Integer.parseInt(kitId)), 0);
							setKit(KitManager.getFromId(Integer.parseInt(availableKits[availableKits.length - 1])), 0);

						}
						setUpdatedAt(res.getDate("updated_at"));
						setCreatedAt(res.getDate("created_at"));
					}
					res.close();
				} catch (ClassNotFoundException | SQLException | NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					new ExceptionManager(ex).register(true);
				}
				this.loaded = true;
			}).start();
		} else {
			this.loaded = true;
		}
		particlePlayers.add(player);
		dataMap.put(player, this);
	}

	@Override
	public void uploadData(final OfflinePlayer player) {
		if (connectedToDatabase) {
			new Thread(() -> {
				String identifier = Bukkit.getServer().getOnlineMode() ? "uuid=UNHEX('" + this.uid + "')" : "name='" + this.name + "'";
				try {
					ResultSet res = database.querySQL("SELECT * FROM players WHERE " + identifier);
					if (res.first()) {
						database.updateSQL("UPDATE players SET name='" + this.name + "', wins=" + this.wins + ", kills=" + this.kills + ", deaths=" + this.deaths + ", games=" + this.games + ", sheep_thrown=" + this.sheepThrown + ", sheep_killed=" + this.sheepKilled + ", total_time=" + this.totalTime + ", particles=" + (this.particle ? "1" : "0") + ", kits='" + getKitsString() + "', updated_at=NOW() WHERE " + identifier);
					} else {
						database.updateSQL("INSERT INTO players(name, uuid, wins, kills, deaths, games, sheep_thrown, sheep_killed, total_time, particles, kits, created_at, updated_at) VALUES('" + this.name + "', UNHEX('" + this.uid + "'), " + this.wins + ", " + this.kills + ", " + this.deaths + ", " + this.games + ", " + this.sheepThrown + ", " + this.sheepKilled + ", " + this.totalTime + ", " + (this.particle ? "1" : "0") + ", '" + getKitsString() + "', NOW(), NOW())");
					}
					res.close();
				} catch (ClassNotFoundException | SQLException ex) {
					new ExceptionManager(ex).register(true);
				}
			}).start();
		}
	}

	public static List<OfflinePlayer> getParticlePlayers() {
		return particlePlayers;
	}

	public static Set<OfflinePlayer> getPlayers() {
		return dataMap.keySet();
	}

	public static Set<Entry<OfflinePlayer, PlayerData>> getData() {
		return dataMap.entrySet();
	}

	public static boolean hasEnabledParticles(Player player) {
		return particlePlayers.contains(player);
	}

	public enum DataType {

		GAMES_PLAYED(0, MsgEnum.STATS_GAME_PLAYED, "games"),
		TOTAL_DEATHS(1, MsgEnum.STATS_DEATH, "deaths"),
		PLAYERS_KILLED(2, MsgEnum.STATS_KILL, "kills"),
		SHEEP_THROWN(3, MsgEnum.STATS_SHEEP_THROWN, "sheep_thrown"),
		SHEEP_KILLED(4, MsgEnum.STATS_SHEEP_KILLED, "sheep_killed"),
		TOTAL_TIME(5, MsgEnum.STATS_TOTAL_TIME, "total_time"),
		GAMES_WON(6, MsgEnum.STATS_VICTORY, "wins");

		private int id;
		private Message message;
		private String tableColumn;
		private HashMap<String, Integer> playerTop;

		private DataType(int id, MsgEnum msgEnum, String tableColumn) {
			this.id = id;
			this.message = Message.getMessage(msgEnum);
			this.tableColumn = tableColumn;
			this.playerTop = new HashMap<>();
		}

		public Message getMessage() {
			return this.message;
		}

		public int getTopSize() {
			return this.playerTop.size();
		}

		public int after() {
			int after = this.id + 1;
			if (after >= values().length) {
				return 0;
			} else {
				return after;
			}
		}

		public int before() {
			int before = this.id - 1;
			if (before < 0) {
				return (values().length - 1);
			} else {
				return before;
			}
		}

		public void generateRanking() {
			ResultSet res = null;
			try {
				res = database.querySQL("SELECT `name`,`" + this.tableColumn + "` FROM `players` ORDER BY `" + this.tableColumn + "` DESC ;");
				while (res.next())
					this.playerTop.put(res.getString("name"), res.getInt(this.tableColumn));
			} catch (SQLException | ClassNotFoundException ex) {
				new ExceptionManager(ex).register(true);
			} finally {
				try {
					if (res != null)
						res.close();
				} catch (SQLException ex) {
					new ExceptionManager(ex).register(true);
				}
			}
		}

		public Map<String, Integer> getRanking(int limit) {
			Map<String, Integer> output = new HashMap<>();
			int i = 0;
			Iterator<Entry<String, Integer>> iter = this.playerTop.entrySet().iterator();
			while (iter.hasNext() && i < limit) {
				Entry<String, Integer> curr = iter.next();
				output.put(curr.getKey(), curr.getValue());
				i++;
			}
			return sortByValue(output);

		}

		public static DataType getFromId(int id) {
			for (DataType data : values())
				if (data.id == id)
					return data;
			return null;
		}

		private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

			List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return -(o1.getValue()).compareTo(o2.getValue());
				}
			});

			Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> entry : list) {
				sortedMap.put(entry.getKey(), entry.getValue());
			}

			return sortedMap;
		}
	}
}
