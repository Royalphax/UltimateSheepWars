package fr.royalpha.sheepwars.core.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ConfigManager.Field;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.manager.UpdateManager;
import fr.royalpha.sheepwars.core.util.Utils;

public abstract class DataManager {

	private static String createDatabaseRequest(String table) {
		return "CREATE TABLE IF NOT EXISTS `" + table + "` (`id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(16) NOT NULL, `uuid` varchar(36) NOT NULL, `wins` int(11) NOT NULL DEFAULT '0', `kills` int(11) NOT NULL DEFAULT '0',"
				+ "`deaths` int(11) NOT NULL DEFAULT '0', `games` int(11) NOT NULL DEFAULT '0', `sheep_thrown` int(11) NOT NULL DEFAULT '0', `sheep_killed` int(11) NOT NULL DEFAULT '0', `total_time` int(11) NOT NULL DEFAULT '0',"
				+ "`particles` int(1) NOT NULL DEFAULT '1', `kits` text NOT NULL, `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, primary key (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
	}
	
	protected static MySQLConnector database;
	protected static boolean connectedToDatabase;
	private static boolean tryingToConnect = false;
	private static SheepWarsPlugin plugin;

	public abstract void loadData();

	public abstract void uploadData();

	public static void initDatabaseConnections(SheepWarsPlugin plugin) {
		DataManager.plugin = plugin;
		final boolean localhost = plugin.isLocalhostConnection();
		final Long start = System.currentTimeMillis();
		tryingToConnect = true;
		if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_MYSQL_FREE_HOST)) {
			plugin.getLogger().info("Connecting to free hosted Database ...");
			try {
				final String[] contentSplitted = new UpdateManager(UpdateManager.Link.FREE_HOSTED_DB_ACCESS, localhost).read().split(",");
				database = new MySQLConnector((localhost ? "localhost" : contentSplitted[0]), contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4],
						"useSSL=false&autoReconnect=true");
				database.openConnection();
				database.updateSQL(createDatabaseRequest("players"));
				alterPlayerDataTable();
				Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
				plugin.getLogger().log(Level.INFO, "Connected to Free hosted Database (" + stop + "s)!");
				connectedToDatabase = true;
			} catch (ClassNotFoundException | SQLException | IOException ex) {
				plugin.getLogger().info("Free hosted Database unreachable (" + ex.getMessage() + ")!");
				connectedToDatabase = false;
			}
		}
		if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_MYSQL) && !connectedToDatabase) {
			String host = ConfigManager.getString(ConfigManager.Field.MYSQL_HOST);
			Integer port = ConfigManager.getInt(ConfigManager.Field.MYSQL_PORT);
			String db = ConfigManager.getString(ConfigManager.Field.MYSQL_DATABASE);
			String user = ConfigManager.getString(ConfigManager.Field.MYSQL_USER);
			String pass = ConfigManager.getString(ConfigManager.Field.MYSQL_PASSWORD);
			String options = ConfigManager.getString(ConfigManager.Field.MYSQL_OPTIONS);
			database = new MySQLConnector(host, port, db, user, pass, options);
			try {
				database.openConnection();
				alterPlayerDataTable();
				Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
				plugin.getLogger().log(Level.INFO, "Connected to Database (" + stop + "s)!");
				connectedToDatabase = true;
			} catch (ClassNotFoundException | SQLException ex) {
				ExceptionManager.register(ex, true);
				Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
				plugin.getLogger().log(Level.INFO, "Database unreachable (" + stop + "s)!");
				connectedToDatabase = false;
			}
		}
		tryingToConnect = false;
		initRanking();
	}

	public static void initDatabaseConnections() {
		if (plugin != null)
			initDatabaseConnections(plugin);
	}

	private static void alterPlayerDataTable() {
		final String table = ConfigManager.getString(ConfigManager.Field.MYSQL_TABLE);
		Calendar cal = Calendar.getInstance();
		if (ConfigManager.getBoolean(Field.ENABLE_MYSQL_BACKUP)) {
			try {
				database.updateSQL("DROP TABLE IF EXISTS " + table + "_monthly" + ((cal.get(Calendar.MONTH) - 1 < 0 ? 11 : cal.get(Calendar.MONTH) - 1)) + "_backup;");
				database.updateSQL("CREATE TABLE IF NOT EXISTS " + table + "_monthly" + cal.get(Calendar.MONTH) + "_backup SELECT * FROM players;");
			} catch (ClassNotFoundException | SQLException exception) {
				// Do nothing
			}
		}
		try {
			Connection co = database.getConnection();
			PreparedStatement stm = co.prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_schema = ? AND table_name = ?;");
			stm.setString(1, ConfigManager.getString(Field.MYSQL_DATABASE));
			stm.setString(2, ConfigManager.getString(Field.MYSQL_TABLE));
			ResultSet rs = stm.executeQuery();
			List<String> columns = new ArrayList<>();
			while (rs.next())
				columns.add(rs.getString("column_name"));

			if (columns.isEmpty()) { // should create
				database.updateSQL(createDatabaseRequest(table));
			} else {
				// remove old columns
				if (columns.contains("lang"))
					database.updateSQL("ALTER TABLE `" + table + "` DROP `lang`;");
				if (columns.contains("last_kit"))
					database.updateSQL("ALTER TABLE `" + table + "` DROP `last_kit`;");
				// add latest
				if (!columns.contains("total_time"))
					database.updateSQL("ALTER TABLE `" + table + "` ADD `total_time` INT(11) NOT NULL DEFAULT '0' AFTER `games`;");
				if (!columns.contains("sheep_killed"))
					database.updateSQL("ALTER TABLE `" + table + "` ADD `sheep_killed` INT(11) NOT NULL DEFAULT '0' AFTER `games`;");
				if (!columns.contains("sheep_thrown"))
					database.updateSQL("ALTER TABLE `" + table + "` ADD `sheep_thrown` INT(11) NOT NULL DEFAULT '0' AFTER `games`;");
				// change
				if (columns.contains("kits"))
					database.updateSQL("ALTER TABLE `" + table + "` CHANGE `kits` `kits` TEXT NOT NULL;");
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load database tables.", e);
		}
		try {
			ResultSet resultSet = database.querySQL("SELECT * FROM " + table + ";");
			ResultSetMetaData rSMD = resultSet.getMetaData();
			if (rSMD.getColumnType(3) == Types.VARBINARY) {
				plugin.getLogger().info("[Optimization] Converting database 'uuid' column from VARBINARY to VARCHAR ...");
				Map<String, String> map = new HashMap<>();
				SheepWarsPlugin.debug("-> Fetching players uuid to convert them manually:");
				while (resultSet.next()) {
					String name = resultSet.getString("name");
					byte[] uuid = resultSet.getBytes("uuid");
					String hexUuid = Utils.bytesToHex(uuid).toLowerCase().trim();
					map.put(name, hexUuid);
					SheepWarsPlugin.debug(name + " <-> " + hexUuid);
				}
				database.updateSQL("ALTER TABLE `" + table + "` CHANGE `uuid` `uuid` VARCHAR(36) NOT NULL;");
				for (String name : map.keySet()) {
					String hexUuid = map.get(name);
					database.updateSQL("UPDATE " + table + " SET uuid='" + hexUuid + "' WHERE name='" + name + "';");
				}
			}
			resultSet.close();

		} catch (ClassNotFoundException | SQLException exception) {
			exception.printStackTrace();
		}
	}

	private static void initRanking() {
		if (!connectedToDatabase)
			return;
		for (PlayerData.DataType data : PlayerData.DataType.values())
			data.generateRanking();
	}

	public static boolean isConnected() {
		return connectedToDatabase;
	}

	public static void checkConnection() {
		boolean isConnected = false;
		try {
			isConnected = (database.getConnection() != null && !database.getConnection().isClosed() && database.getConnection().isValid(5));
		} catch (SQLException e) {
			// Do nothing
		}
		if (!isConnected)
			initDatabaseConnections();
	}

	public static boolean isTryingToConnect() {
		return tryingToConnect;
	}

	public static boolean closeConnection() {
		try {
			return database.closeConnection();
		} catch (SQLException e) {
			ExceptionManager.register(e, true);
			return false;
		}
	}

	/*
	 * public static boolean isUploadingData() { return uploadingData.size() > 0; }
	 */
}
