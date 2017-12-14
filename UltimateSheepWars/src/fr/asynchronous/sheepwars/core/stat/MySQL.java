package fr.asynchronous.sheepwars.core.stat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.KitManager;

public class MySQL extends Database {

	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	public MySQL(String hostname, Integer port, String database, String username, String password) {
		this(hostname, Integer.toString(port), database, username, password);
	}
	
	public MySQL(String hostname, String port, String database, String username, String password) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}
	
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?useSSL=false", this.user, this.password);
		return this.connection;
	}

	public void alterPlayerDataTable(UltimateSheepWarsPlugin plugin) {
		try {
			this.openConnection();
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			this.updateSQL("ALTER TABLE `players` DROP `lang`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			this.updateSQL("ALTER TABLE `players` ADD `total_time` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			this.updateSQL("ALTER TABLE `players` ADD `sheep_killed` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			this.updateSQL("ALTER TABLE `players` ADD `sheep_thrown` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			this.updateSQL("ALTER TABLE `players` ADD `last_kit` INT(1) NOT NULL DEFAULT '0' AFTER `particles`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
	}

	public void updatePlayerData(PlayerData data) {
		String identifier = "";
		identifier = Bukkit.getServer().getOnlineMode() ? "uuid=UNHEX('" + data.getUID() + "')" : "name='" + data.getName() + "'";
		try {
			ResultSet res = this.querySQL("SELECT * FROM players WHERE " + identifier);
			if (res.first()) {
				this.updateSQL("UPDATE players SET name='" + data.getName() + "', wins=" + data.getWins() + ", kills=" + data.getKills() + ", deaths=" + data.getDeaths() + ", games=" + data.getGames() + ", sheep_thrown=" + data.getSheepThrown() + ", sheep_killed=" + data.getSheepKilled() + ", total_time=" + data.getTotalTime() + ", particles=" + (data.getAllowedParticles() != false ? "1" : "0") + ", last_kit=" + data.getKit().getId() + ", updated_at=NOW() WHERE " + identifier);
			} else {
				this.updateSQL("INSERT INTO players(name, uuid, wins, kills, deaths, games, sheep_thrown, sheep_killed, total_time, particles, last_kit, created_at, updated_at) VALUES('" + data.getName() + "', UNHEX('" + data.getUID().toString().replace("-", "") + "'), " + data.getWins() + ", " + data.getKills() + ", " + data.getDeaths() + ", " + data.getGames() + ", " + data.getSheepThrown() + ", " + data.getSheepKilled() + ", " + data.getTotalTime() + ", " + (data.getAllowedParticles() != false ? "1" : "0") + ", " + data.getKit().getId() + ", NOW(), NOW())");
			}
			res.close();
		} catch (ClassNotFoundException | SQLException ex) {
			new ExceptionManager(ex).register(true);
		}
	}

	public PlayerData getPlayerData(PlayerData data, Player player) {
		String identifier = "";
		String uuid = player.getUniqueId().toString().replace("-", "");
		identifier = Bukkit.getServer().getOnlineMode() ? "uuid=UNHEX('" + uuid + "')" : "name='" + player.getName() + "'";
		try {
			ResultSet res = this.querySQL("SELECT * FROM players WHERE " + identifier);
			if (res.first()) {
				data.setDeaths(res.getInt("deaths"));
				data.setGames(res.getInt("games"));
				data.setKills(res.getInt("kills"));
				data.setAllowParticles(res.getInt("particles") == 1);
				data.setWins(res.getInt("wins"));
				data.setSheepThrown(res.getInt("sheep_thrown"));
				data.setSheepKilled(res.getInt("sheep_killed"));
				data.setTotalTime(res.getInt("total_time"));
				data.setKit(KitManager.getFromId(res.getInt("last_kit")));
				data.setUpdatedAt(res.getDate("updated_at"));
				data.setCreatedAt(res.getDate("created_at"));
			}
			res.close();
		} catch (ClassNotFoundException | SQLException ex) {
			new ExceptionManager(ex).register(true);
		}
		data.setName(player.getName());
		return data;
	}

	/*public void getPlayerTop(PlayerData.DATA_TYPE datatype, UltimateSheepWarsPlugin plugin) {
		ResultSet res = null;
		try {
			try {
				res = this.querySQL("SELECT * FROM players ORDER BY " + datatype.column + " DESC LIMIT " + plugin.RANKING_TOP);
				int i = 0;
				while (res.next()) {
					if (res.getInt(datatype.column) == 0)
						continue;
					++i;
					for (Language lang : Language.getLanguages()) {
						int[] totalTimes;
						String previousValue = "";
						if (datatype.ranking.get(lang) != null) {
							previousValue = datatype.ranking.get(lang);
							datatype.ranking.remove(lang);
						}
						String value = "0";
						value = datatype == PlayerData.DATA_TYPE.TOTAL_TIME ? lang.getMessage(Message.STATS_TOTAL_TIME_FORMAT).replace("%HOURS%", ChatColor.YELLOW + "" + totalTimes[0] + " " + ChatColor.GREEN + lang.getMessage((totalTimes = Utils.splitToComponentTimes(res.getInt(datatype.column)))[0] > 1 ? Message.HOURS : Message.HOUR)).replace("%MINUTES%", ChatColor.YELLOW + "" + totalTimes[1] + " " + ChatColor.GREEN + lang.getMessage(totalTimes[1] > 1 ? Message.MINUTES : Message.MINUTE)).replace("%SECONDS%", ChatColor.YELLOW + totalTimes[2] + " " + ChatColor.GREEN + lang.getMessage(totalTimes[2] > 1 ? Message.SECONDS : Message.SECOND)) : String.valueOf(res.getInt(datatype.column));
						datatype.ranking.put(lang, String.valueOf(previousValue) + "\n" + lang.getMessage(Message.RANKING_FORMAT).replace("%RANK%", new StringBuilder(String.valueOf(i)).toString()).replace("%PLAYER%", res.getString("name")).replace("%VALUE%", value));
					}
				}
				if (++i <= plugin.RANKING_TOP) {
					for (Language lang : Language.getLanguages()) {
						int n = i;
						while (n <= plugin.RANKING_TOP) {
							String previousValue = "";
							if (datatype.ranking.get(lang) != null) {
								previousValue = datatype.ranking.get(lang);
								datatype.ranking.remove(lang);
							}
							datatype.ranking.put(lang, String.valueOf(previousValue) + "\n" + lang.getMessage(Message.RANKING_FORMAT).replace("%RANK%", new StringBuilder(String.valueOf(n)).toString()).replace("%PLAYER%", "------").replace("%VALUE%", "0"));
							++n;
						}
					}
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				try {
					res.close();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
		} finally {
			try {
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}*/
}
