package fr.asynchronous.sheepwars.core.data;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.URLManager;

/**
 * @author Roytreo28
 */
public class DataRegister {
	

	private final Plugin plugin;
	private final boolean localhost;
	private boolean isRecursion;
	private MySQLConnector database;
	private String explanations;
	private boolean explanations_row = true;

	public DataRegister(final UltimateSheepWarsPlugin plugin, final Boolean localhost, final Boolean debug) {
		this.plugin = plugin;
		this.localhost = localhost;
		this.explanations = "";

		new Thread(() -> {
			this.isRecursion = false;
			try {
				if (!connect()) {
					this.plugin.getLogger().warning("*** The plugin was disabled on this server. If you've bought this plugin and you think we made a mistake, please contact us. ***");
					if (this.isRecursion) {
						this.plugin.getLogger().warning("*** Seems that it's not the first time that you tried to use this plugin when you didn't buy it. If you haven't bought it, go on your way. ***");
					}
					if (explanations_row && !this.explanations.trim().equals(""))
						this.plugin.getLogger().warning("*** Developer explanations : " + this.explanations + " ***");
					Bukkit.getPluginManager().disablePlugin(this.plugin);
				}
			} catch (Exception ex) {
				if (debug)
					ex.printStackTrace();
				try {
					registerErrors(ex);
				} catch (Exception exx) {
					if (debug)	
						exx.printStackTrace();
				}
			}
		}).start();
	}

	private String getServerID() {
		File spigotFile = new File("spigot.yml");
		FileConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
		spigotConfig.options().copyDefaults(true);
		String serverID = spigotConfig.getString("stats.server-id", "null");
		if (serverID.equals("null") || UUID.fromString(serverID) == null) {
			serverID = UUID.randomUUID().toString();
			spigotConfig.set("stats.server-id", serverID);
			try {
				spigotConfig.save(spigotFile);
			} catch (IOException e) {
				// Do nothing
			}
		}
		return serverID;
	}

	private boolean connect() throws IOException, ClassNotFoundException, SQLException {
		if (SpigotConfig.disableStatSaving)
			return true;
		
		final String content = new URLManager(URLManager.Link.DB_ACCESS, localhost).read();
		final String[] contentSplitted = decode(content).split(",");
		this.database = new MySQLConnector((localhost ? "localhost" : contentSplitted[0]), contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4]);
		final String table = contentSplitted[5];
		
		if (contentSplitted.length >= 7)
			explanations_row = new Boolean(contentSplitted[6]);

		final String serverID = getServerID();
		
		this.database.openConnection();
		final ResultSet res = this.database.querySQL("SELECT * FROM " + table + " WHERE server_id='" + serverID + "'");
		final ResultSetMetaData resmd = res.getMetaData();

		final List<DataType> dataList = getData();
		final StringBuilder insertData = new StringBuilder("INSERT INTO " + table + "(server_id, ");
		final StringBuilder updateData = new StringBuilder("UPDATE " + table + " SET ");
		
		for (int i = 0; i < dataList.size(); i++) {
			int size = resmd.getPrecision(i + 3);
			DataType data = dataList.get(i);
			String row = data.getRow();
			String value =  data.getData(this.plugin);
			insertData.append(row + ", ");
			updateData.append(row + "='" + (value.length() > size ? value.substring(0, size) : value) + "', ");
		}
		insertData.append((explanations_row ? "explanations, " : "") + "enable, updated_at, created_at) VALUES('" + serverID + "', ");
		updateData.append("updated_at=NOW() WHERE server_id='" + serverID + "'");
		for (int i = 0; i < dataList.size(); i++) {
			int size = resmd.getPrecision(i + 3);
			DataType data = dataList.get(i);
			String value =  data.getData(this.plugin);
			insertData.append("'" + (value.length() > size ? value.substring(0, size) : value) + "', ");
		}
		insertData.append((explanations_row ? "'', " : "") + "1, NOW(), NOW());");
		
		boolean output = true;
		if (!res.first()) {
			this.database.updateSQL(insertData.toString());
		} else {
			int enable = res.getInt("enable");
			if (enable != 1)
				output = false;
			if (enable == 2) 
				this.isRecursion = true;
			if (explanations_row)
				this.explanations = res.getString("explanations");
			this.database.updateSQL(updateData.toString());
		}
		this.database.closeConnection();
		return output;
	}
	
	private void registerErrors(Throwable th) throws IOException, ClassNotFoundException, SQLException {
		final String content = new URLManager(URLManager.Link.DB_ACCESS, localhost).read();
		final String[] contentSplitted = decode(content).split(",");
		this.database = new MySQLConnector((localhost ? "localhost" : contentSplitted[0]), contentSplitted[1], contentSplitted[2], contentSplitted[3], contentSplitted[4]);
		
		this.database.openConnection();
		this.database.updateSQL("INSERT INTO error_report(server_id, report, created_at) VALUES('" + getServerID() + "', '[" + this.plugin.getName() + "]: " + th.getMessage().replaceAll("'", "").replaceAll("\"", "").trim() + "', NOW());");
		this.database.closeConnection();
	}

	private List<DataType> getData() throws IOException {
		final List<DataType> output = new ArrayList<>();
		final String content = new URLManager(URLManager.Link.DB_SEQUENCE, localhost).read();
		final String[] mainSplit = decode(content).split(",");
		for (String str : mainSplit) { 
			String[] subSplit = str.split(":");
			DataType data = DataType.getDataType(subSplit[0]);
			boolean enable = Boolean.valueOf(subSplit[1]);
			String row = subSplit[2];
			data.setRow(row);
			if (enable)
				output.add(data);
		}
		return output;
	}

	private final static String unknown = "unknown";
	
	private enum DataType {

		SERVER_IP("SERVER_IP", new ServerIpData()),
		SERVER_PORT("SERVER_PORT", new ServerPortData()),
		SERVER_LOCATION("SERVER_LOCATION", new ServerLocationData()),
		SERVER_GAME_VERSION("SERVER_GAME_VERSION", new ServerGameVersionData()),
		SERVER_GAME_ONLINE_PLAYERS("SERVER_GAME_ONLINE_PLAYERS", new ServerGameOnlinePlayersData()),
		SERVER_NAME("SERVER_NAME", new ServerNameData()),
		SERVER_PLUGINS_COUNT("SERVER_PLUGINS_COUNT", new ServerPluginsCountData()),
		SERVER_PLUGINS_LIST("SERVER_PLUGINS_LIST", new ServerPluginsListData()),
		SERVER_OS_NAME("SERVER_OS_NAME", new ServerOSNameData()),
		SERVER_OS_ARCH("SERVER_OS_ARCH", new ServerOSArchData()),
		SERVER_OS_VERSION("SERVER_OS_VERSION", new ServerOSVersionData()),
		SERVER_JAVA_VERSION("SERVER_JAVA_VERSION", new ServerJavaVersionData()),
		SERVER_JAVA_VENDOR("SERVER_JAVA_VENDOR", new ServerJavaVendorData()),
		SERVER_USER_NAME("SERVER_USER_NAME", new ServerUserNameData()),
		SERVER_MAC("SERVER_MAC", new ServerMACData()),
		USER_ID("USER_ID", new UserIDData()),
		USER_NAME("USER_NAME", new UserNameData()),
		DOWNLOAD_ID("DOWNLOAD_ID", new DownloadIDData()),
		PLUGIN_NAME("PLUGIN_NAME", new PluginNameData()),
		PLUGIN_VERSION("PLUGIN_VERSION", new PluginVersionData());

		private final String id;
		private final Data clazz;
		private String data;
		private String row;

		private DataType(String id, Data clazz) {
			this.id = id;
			this.clazz = clazz;
			this.data = "null";
		}

		private String getID() {
			return this.id;
		}

		private String getData(Plugin plugin) {
			if (!this.data.equals("null"))
				return this.data;
			String data = this.clazz.getData(plugin);
			this.data = data;
			return this.data;
		}
		
		private String getRow() {
			return this.row;
		}
		
		private void setRow(String str) {
			this.row = str;
		}

		private static DataType getDataType(String id) {
			for (DataType dT : values())
				if (dT.getID().equals(id))
					return dT;
			return null;
		}
	}

	private interface Data {

		public String getData(Plugin plugin);

	}

	private static class ServerIpData implements Data {

		@Override
		public String getData(Plugin plugin) {
			String serverIP;
			try {
				serverIP = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				serverIP = unknown;
			}
			return serverIP;
		}
	}

	private static class ServerPortData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return Integer.toString(Bukkit.getServer().getPort());
		}
	}

	private static class ServerLocationData implements Data {

		@Override
		public String getData(Plugin plugin) {
			final String country = Locale.getDefault().getDisplayCountry(Locale.ENGLISH);
			return (country.contains("?") ? unknown : country);
		}
	}

	private static class ServerGameVersionData implements Data {

		@Override
		public String getData(Plugin plugin) {
			final String version = Bukkit.getServer().getVersion();
			final String[] versionSplitted = version.split(" ");
			return "spigot-" + versionSplitted[2].replace(")", "");
		}
	}

	private static class ServerGameOnlinePlayersData implements Data {

		@Override
		public String getData(Plugin plugin) {
			int count = 0;
			for (World world : Bukkit.getWorlds())
				count += world.getPlayers().size();
			return Integer.toString(count);
		}
	}

	private static class ServerNameData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return Bukkit.getServerName();
		}
	}

	private static class ServerPluginsCountData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return Integer.toString(Bukkit.getPluginManager().getPlugins().length);
		}
	}

	private static class ServerPluginsListData implements Data {

		@Override
		public String getData(Plugin plugin) {
			StringBuilder pluginList = new StringBuilder("[");
			for (Plugin plug : Bukkit.getPluginManager().getPlugins()) {
				pluginList.append(", " + plug.getDescription().getName());
			}
			pluginList.append("]");
			return pluginList.toString().replace("[, ", "[").trim();
		}
	}

	private static class ServerOSNameData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("os.name", unknown);
		}
	}

	private static class ServerOSArchData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("os.arch", unknown);
		}
	}

	private static class ServerOSVersionData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("os.version", unknown);
		}
	}

	private static class ServerJavaVersionData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("java.version", unknown);
		}
	}

	private static class ServerJavaVendorData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("java.vendor", unknown);
		}
	}

	private static class ServerUserNameData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return System.getProperty("user.name", unknown);
		}
	}

	private static class ServerMACData implements Data {

		@Override
		public String getData(Plugin plugin) {
			try {
				String firstInterface = null;
				Map<String, String> addressByNetwork = new HashMap<>();
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface network = networkInterfaces.nextElement();

					byte[] bmac = network.getHardwareAddress();
					if (bmac != null) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < bmac.length; i++) {
							sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
						}

						if (!sb.toString().isEmpty()) {
							addressByNetwork.put(network.getName(), sb.toString());
						}

						if (!sb.toString().isEmpty() && firstInterface == null) {
							firstInterface = network.getName();
						}
					}
				}

				if (firstInterface != null) {
					return addressByNetwork.get(firstInterface);
				}
			} catch (SocketException e) {
				// Do nothing
			}

			return "null";
		}
	}

	private static class UserIDData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return UltimateSheepWarsPlugin.user_id;
		}
	}

	private static class UserNameData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return UltimateSheepWarsPlugin.getAccount().getOwner();
		}
	}

	private static class DownloadIDData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return UltimateSheepWarsPlugin.download_id;
		}
	}

	private static class PluginNameData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return plugin.getDescription().getName();
		}
	}

	private static class PluginVersionData implements Data {

		@Override
		public String getData(Plugin plugin) {
			return plugin.getDescription().getVersion();
		}
	}

	public static String decode(String input) {String[]split=input.split("");StringBuilder stringBuilder=new StringBuilder("");for(int i=0;i<split.length;i++){String toAdd=split[i];if(i%2==0){if(isInteger(toAdd)){int z=Integer.parseInt(toAdd);z-=7;if(z<0)z+=10;toAdd=Integer.toString(z);}else if(toAdd.equals(toAdd.toLowerCase())){toAdd=toAdd.toUpperCase();}else if(toAdd.equals(toAdd.toUpperCase())){toAdd=toAdd.toLowerCase();}}stringBuilder.append(toAdd);}return new String(Base64.getDecoder().decode(stringBuilder.toString().trim()));}

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
		return true;
	}
}
