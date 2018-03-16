package fr.asynchronous.sheepwars.core.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataRegister;
import fr.asynchronous.sheepwars.core.data.MySQLConnector;
import fr.asynchronous.sheepwars.core.handler.PlayerData.DataType;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.URLManager.Link;

public abstract class DataManager {

	private static final String CREATE_DATABASE_REQUEST = "\"CREATE TABLE IF NOT EXISTS `players` ( `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(30) NOT NULL, `uuid` varbinary(32) NOT NULL, `wins` int(11) NOT NULL, `kills` int(11) NOT NULL, `deaths` int(11) NOT NULL, `games` int(11) NOT NULL, `sheep_thrown` int(11) NOT NULL DEFAULT '0', `sheep_killed` int(11) NOT NULL DEFAULT '0', `total_time` int(11) NOT NULL DEFAULT '0', `particles` int(1) NOT NULL DEFAULT '1', `kits` int NOT NULL DEFAULT '0', `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;";
	
	protected static MySQLConnector database;
	protected static boolean connectedToDatabase;
	
	public DataManager() {
		// Do nothing
	}
	
	public abstract void loadData(OfflinePlayer player);
	
	public abstract void uploadData(OfflinePlayer player);
	
	public static void initDatabaseConnections(UltimateSheepWarsPlugin plugin) {
		final boolean localhost = plugin.isLocalhostConnection();
		final Long start = System.currentTimeMillis();
		new BukkitRunnable()
    	{
    		public void run()
    		{
    			if (ConfigManager.getBoolean(Field.ENABLE_MYSQL_FREE_HOST)) {
    				plugin.getLogger().info("Connecting to free hosted Database ...");
    				try {
    					String content = new URLManager(Link.FREE_HOSTED_DB_ACCESS, localhost).read();
    					final String[] contentSplitted = DataRegister.decode(content).split(",");
    					database = new MySQLConnector((localhost ? "localhost" : contentSplitted[0]), contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4]);
    					database.openConnection();
    					database.updateSQL(CREATE_DATABASE_REQUEST);
    					alterPlayerDataTable();
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Connected to Free hosted Database (%ss)!", stop);
    					connectedToDatabase = true;
    				} catch (ClassNotFoundException | SQLException | IOException ex) {
    					plugin.getLogger().info("Free hosted Database unreachable (" + ex.getMessage() + ")!");
    					connectedToDatabase = false;
    				}
    			} 
    			if (ConfigManager.getBoolean(Field.ENABLE_MYSQL) && !connectedToDatabase) {
    				String host = ConfigManager.getString(Field.MYSQL_HOST);
    				Integer port = ConfigManager.getInt(Field.MYSQL_PORT);
    				String db = ConfigManager.getString(Field.MYSQL_DATABASE);
    				String user = ConfigManager.getString(Field.MYSQL_USER);
    				String pass = ConfigManager.getString(Field.MYSQL_PASSWORD);
    				database = new MySQLConnector(host, port, db, user, pass);
    				try {
    					database.openConnection();
    					database.updateSQL(CREATE_DATABASE_REQUEST);
    					alterPlayerDataTable();
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Connected to Database (%ss)!", stop);
    					connectedToDatabase = true;
    				} catch (ClassNotFoundException | SQLException ex) {
    					new ExceptionManager(ex).register(true);
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Database unreachable (%ss)!", stop);
    					connectedToDatabase = false;
    				}
    			}
    		}
    	}.runTaskAsynchronously(plugin);
    	initRanking();
	}
	
	private static void alterPlayerDataTable() {
		try {
			database.updateSQL("ALTER TABLE `players` DROP `lang`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			database.updateSQL("ALTER TABLE `players` ADD `total_time` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			database.updateSQL("ALTER TABLE `players` ADD `sheep_killed` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			database.updateSQL("ALTER TABLE `players` ADD `sheep_thrown` INT(11) NOT NULL DEFAULT '0' AFTER `games`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			database.updateSQL("ALTER TABLE `players` ADD `last_kit` INT(1) NOT NULL DEFAULT '0' AFTER `particles`");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
		try {
			database.updateSQL("ALTER TABLE `players` CHANGE `last_kit` `kits` INT NOT NULL DEFAULT '0'");
		} catch (ClassNotFoundException | SQLException exception) {
			// Do nothing
		}
	}
	
	private static void initRanking() {
		if (!connectedToDatabase)
			return;
		for (DataType data : DataType.values())
			data.generateRanking();
	}
	
	public static boolean isConnected() {
		return connectedToDatabase;
	}
	
	public static boolean closeConnection() {
		try {
			return database.closeConnection();
		} catch (SQLException e) {
			new ExceptionManager(e).register(true);
			return false;
		}
	}
}
