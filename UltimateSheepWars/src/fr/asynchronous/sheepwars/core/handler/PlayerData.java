package fr.asynchronous.sheepwars.core.handler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.DataManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.EntityUtils;

public class PlayerData extends DataManager {
	
	private static Map<OfflinePlayer, PlayerData> dataMap;
	private static ArrayList<OfflinePlayer> particlePlayers;

	static {
		dataMap = new HashMap<>();
		particlePlayers = new ArrayList<>();
	}

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
	private ArrayList<KitManager> kits;
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
		this.kits = new ArrayList<>();
		this.team = null;
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
		return this.kits;
	}
	
	public String getKitsString() {
		StringBuilder output = new StringBuilder("");
		for (KitManager k : this.kits)
			if (k.getId() != this.kit.getId())
				output.append(k.getId());
		output.append(this.kit.getId());
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
		particlePlayers.remove(this.player);
		if (particle) {
			particlePlayers.add(this.player);
		} else {
			EntityUtils.setWeatherPlayer(WeatherType.CLEAR, this.player);
		}
	}

	public void setLanguage(final Language lang) {
		this.language = lang;
	}

	public void setKills(final int i) {
		this.kills = i;
	}

	public void setKit(final KitManager kit) {
		this.kit = kit;
		if (this.player.isOnline())
			this.player.getPlayer().sendMessage(Message.getMessage(this.player.getPlayer(), MsgEnum.KIT_CHOOSE_MESSAGE).replace("%KIT%", kit.getName(this.player.getPlayer())));
	}
	
	public void addKit(final KitManager kit) {
		if (!this.kits.contains(kit))
			this.kits.add(kit);
	}
	
	public void setTeam(TeamManager team) {
		if (this.player.isOnline()) {
			if (this.team != null)
				this.team.removePlayer(this.player.getPlayer());
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
		return (this.team != null);
	}

	@Override   
	public String toString() {
		return "PlayerData(" + "uid=" + this.uid + ", name=" + this.name + ", locale=" + this.language.getLocale() + ", particle=" + this.particle + ", wins=" + this.wins + ", kills=" + this.kills + ", deaths=" + this.deaths + ", games=" + this.games + ", sheepThrown=" + this.sheepThrown + ", sheepKilled=" + this.sheepKilled + ", totalTime=" + this.totalTime + ", actualKills=" + this.actualKills + ", lastKit=" + this.kit.getId() + ", winRate=" + this.winRate + ", kdRatio=" + this.kdRatio + ", updatedAt=" + this.updatedAt + ", createdAt=" + this.createdAt + ")";
	}

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
						String[] availableKits = res.getString("kits").split("");
						for (String kitId : availableKits)
							addKit(KitManager.getFromId(Integer.parseInt(kitId)));
						setKit(KitManager.getFromId(Integer.parseInt(availableKits[availableKits.length-1])));
						setUpdatedAt(res.getDate("updated_at"));
						setCreatedAt(res.getDate("created_at"));
					}
					res.close();
				} catch (ClassNotFoundException | SQLException ex) {
					new ExceptionManager(ex).register(true);
				}
			}).start();
		}
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

	public static boolean hasEnabledParticles(Player player) {
		return particlePlayers.contains(player);
	}

	public enum DataType {
		
		GAMES_PLAYED(0, MsgEnum.RANKING_GAME_PLAYED, "games"),
		TOTAL_DEATHS(1, MsgEnum.RANKING_DEATH, "deaths"),
		PLAYERS_KILLED(2, MsgEnum.RANKING_KILL, "kills"),
		SHEEP_THROWN(3, MsgEnum.RANKING_SHEEP_THROWN, "sheep_thrown"),
		SHEEP_KILLED(4, MsgEnum.RANKING_SHEEP_KILLED, "sheep_killed"),
		TOTAL_TIME(5, MsgEnum.RANKING_TOTAL_TIME, "total_time"),
		GAMES_WON(6, MsgEnum.RANKING_VICTORY, "wins");

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

		public DataType after() {
			int after = this.id + 1;
			if (getFromId(after) == null) {
				return getFromId(0);
			} else {
				return getFromId(after);
			}
		}

		public DataType before() {
			int before = this.id - 1;
			if (before < 0) {
				return getFromId((values().length - 1));
			} else {
				return getFromId(before);
			}
		}
		
		public void generateRanking() {
			int rankingSize = ConfigManager.getInt(Field.RANKING_TOP);
			ResultSet res = null;
			try {
				res = database.querySQL("SELECT `name`,`" + this.tableColumn + "` FROM `players` ORDER BY `" + this.tableColumn + "` DESC LIMIT " + rankingSize + " ;");
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
		
		public static DataType getFromId(int id) {
			for (DataType data : values())
				if (data.id == id)
					return data;
			return null;
		}
	}
}
