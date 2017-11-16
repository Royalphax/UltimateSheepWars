package fr.asynchronous.sheepwars.core.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.PlayerData.DataType;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.URLManager.Link;
import fr.asynchronous.sheepwars.core.stat.DataRegister;
import fr.asynchronous.sheepwars.core.stat.MySQL;

public abstract class DataManager {

	protected static MySQL database;
	protected static boolean connectedToDatabase;
	
	public DataManager() {
		// Do nothing
	}
	
	public abstract void loadData(OfflinePlayer player);
	
	public abstract void uploadData(OfflinePlayer player);
	
	public static void initDatabaseConnections(UltimateSheepWarsPlugin plugin) {
		final boolean localhost = plugin.LOCALHOST;
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
    					database = new MySQL((localhost ? "localhost" : contentSplitted[0]), contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4]);
    					database.openConnection();
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Connected to Free hosted Database (%ss)!", stop);
    					connectedToDatabase = true;
    					return;
    				} catch (ClassNotFoundException | SQLException | IOException ex) {
    					plugin.getLogger().info("Free hosted Database unreachable (" + ex.getMessage() + ")!");
    				}
    			}
    			if (ConfigManager.getBoolean(Field.ENABLE_MYSQL)) {
    				String host = ConfigManager.getString(Field.MYSQL_HOST);
    				Integer port = ConfigManager.getInt(Field.MYSQL_PORT);
    				String db = ConfigManager.getString(Field.MYSQL_DATABASE);
    				String user = ConfigManager.getString(Field.MYSQL_USER);
    				String pass = ConfigManager.getString(Field.MYSQL_PASSWORD);
    				database = new MySQL(host, port, db, user, pass);
    				try {
    					database.openConnection();
    					database.updateSQL("CREATE TABLE IF NOT EXISTS `players` ( `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(30) NOT NULL, `uuid` varbinary(32) NOT NULL, `wins` int(11) NOT NULL, `kills` int(11) NOT NULL, `deaths` int(11) NOT NULL, `games` int(11) NOT NULL, `sheep_thrown` int(11) NOT NULL DEFAULT '0', `sheep_killed` int(11) NOT NULL DEFAULT '0', `total_time` int(11) NOT NULL DEFAULT '0', `particles` int(1) NOT NULL DEFAULT '1', `last_kit` int(1) NOT NULL DEFAULT '0', `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
    					database.alterPlayerDataTable(plugin);
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Connected to Database (%ss)!", stop);
    					connectedToDatabase = true;
    				} catch (ClassNotFoundException | SQLException ex) {
    					plugin.MySQL_ENABLE = false;
    					new ExceptionManager(ex).register(true);
    					Double stop = (double) (System.currentTimeMillis() - start) / 1000.0;
    					plugin.getLogger().log(Level.INFO, "Database unreachable (%ss)!", stop);
    					return;
    				}
    			}
    			initRanking();
    			connectedToDatabase = false;
    		}
    	}.runTaskAsynchronously(plugin);
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
}
