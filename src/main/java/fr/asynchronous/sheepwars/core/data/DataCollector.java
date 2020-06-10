package fr.asynchronous.sheepwars.core.data;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Roytreo28
 */
public class DataCollector {

    // Ne pas oublier de rajouter debug à l'endroit ou il faut
    private static final boolean DEBUG = false;

    private final Plugin plugin;
    private final boolean localhost;

    public DataCollector(final SheepWarsPlugin plugin, final Boolean localhost) {
        this.plugin = plugin;
        this.localhost = localhost;

        Thread th = new Thread(() -> {
            try {
                send();
            } catch (Exception ex) {
                if (DEBUG)
                    ex.printStackTrace();
            }
        });
        th.run();
    }

    public void send() throws IOException {
        if (SpigotConfig.disableStatSaving)
            return;

        URL url = new URL("http://local.therenceforot.fr/plugin_collector.php");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("server_id", getServerID());
        for (DataType data : getData()) {
            params.put(data.getRow(), data.getData(plugin));
        }

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        if (DEBUG) {
            StringBuilder answer = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                answer.append((char) c);

            System.out.println(answer.toString().replaceAll("<br/>", "\n"));
        }
    }

    private List<DataType> getData() throws IOException {
        final List<DataType> output = new ArrayList<>();
        for (String str : new UpdateManager(UpdateManager.Link.DATA_TO_COLLECT, localhost).read().split(",")) {
            String[] subSplit = str.split(":");
            DataType data = DataType.getDataType(subSplit[0].trim().replaceAll("\n", ""));
            String row = subSplit.length >= 2 ? subSplit[1].trim().replaceAll("\n", "") : subSplit[0].toLowerCase().trim().replaceAll("\n", "");
            data.setRow(row);
            output.add(data);
        }
        return output;
    }

    private final static String unknown = "unknown";

    private enum DataType {

        SERVER_IP("SERVER_IP", new ServerIpData()),
        SERVER_PORT("SERVER_PORT", new ServerPortData()),
        SERVER_LOCATION("SERVER_LOCATION", new ServerLocationData()), // Yes
        SERVER_GAME_VERSION("SERVER_GAME_VERSION", new ServerGameVersionData()), // Yes
        SERVER_GAME_ONLINE_PLAYERS("SERVER_GAME_ONLINE_PLAYERS", new ServerGameOnlinePlayersData()),
        SERVER_NAME("SERVER_NAME", new ServerNameData()),
        SERVER_PLUGINS_COUNT("SERVER_PLUGINS_COUNT", new ServerPluginsCountData()),
        SERVER_PLUGINS_LIST("SERVER_PLUGINS_LIST", new ServerPluginsListData()), // Yes
        SERVER_OS_NAME("SERVER_OS_NAME", new ServerOSNameData()), // Yes
        SERVER_OS_ARCH("SERVER_OS_ARCH", new ServerOSArchData()),
        SERVER_OS_VERSION("SERVER_OS_VERSION", new ServerOSVersionData()),
        SERVER_JAVA_VERSION("SERVER_JAVA_VERSION", new ServerJavaVersionData()), // Yes
        SERVER_JAVA_VENDOR("SERVER_JAVA_VENDOR", new ServerJavaVendorData()),
        SERVER_USER_NAME("SERVER_USER_NAME", new ServerUserNameData()),
        SERVER_MAC("SERVER_MAC", new ServerMACData()),
        USER_ID("USER_ID", new UserIDData()),
        USER_NAME("USER_NAME", new UserNameData()),
        DOWNLOAD_ID("DOWNLOAD_ID", new DownloadIDData()),
        PLUGIN_NAME("PLUGIN_NAME", new PluginNameData()), // Yes
        PLUGIN_VERSION("PLUGIN_VERSION", new PluginVersionData()); // Yes

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
            return SheepWarsPlugin.user_id;
        }
    }

    private static class UserNameData implements Data {

        @Override
        public String getData(Plugin plugin) {
            return SheepWarsPlugin.getAccount().getOwner();
        }
    }

    private static class DownloadIDData implements Data {

        @Override
        public String getData(Plugin plugin) {
            return SheepWarsPlugin.download_id;
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

    public static String getServerID() {
        File spigotFile = new File("spigot.yml");
        FileConfiguration spigotConfig = YamlConfiguration.loadConfiguration(spigotFile);
        spigotConfig.options().copyDefaults(true);
        UUID rdmUUID = UUID.randomUUID();
        String serverID = spigotConfig.getString("stats.server-id", "null");
        try {
            if (serverID.equals("null") || UUID.fromString(serverID) == null) {
                serverID = rdmUUID.toString();
                spigotConfig.set("stats.server-id", serverID);
                spigotConfig.save(spigotFile);
            }
        } catch (Exception ex) {
            serverID = rdmUUID.toString();
            spigotConfig.set("stats.server-id", serverID);
            try {
                spigotConfig.save(spigotFile);
            } catch (IOException e) {
                // Do nothing
            }
        }
        return serverID;
    }
}
