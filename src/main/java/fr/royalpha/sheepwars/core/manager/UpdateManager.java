package fr.royalpha.sheepwars.core.manager;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roytreo28
 */
public class UpdateManager {

	public static String pluginName = SheepWarsPlugin.getInstance().getDescription().getName();
	private URL url;
	
	private static boolean isUpdated = true;
	private static List<String> infoVersion = new ArrayList<>();

	public enum Link {
		PROTECTOR("http://plugins.therenceforot.fr/protector.php"),
		ERROR("http://plugins.therenceforot.fr/error.php"),
		REASON("http://plugins.therenceforot.fr/reason.txt"),
		GITHUB_ROOT("https://royalphax.github.io/" + pluginName + "/"),
		DATA_TO_COLLECT("https://royalphax.github.io/" + pluginName + "/auto-updater/collected_data.txt"),
		GITHUB_PATH("https://royalphax.github.io/" + pluginName + "/auto-updater"),
		FREE_HOSTED_DB_ACCESS("https://royalphax.github.io/" + pluginName + "/auto-updater/free_hosted_db.txt");

		private String url;

		private Link(String url) {
			this.url = url;
		}

		public String getURL() {
			return this.url;
		}

		public boolean netIsAvailable() {
			try {
				final URL url = new URL(this.url);
				final URLConnection conn = url.openConnection();
				conn.connect();
				conn.getInputStream().close();
				return true;
			} catch (MalformedURLException | IllegalArgumentException e) {
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	}
	
	public UpdateManager(Link link, Boolean localhost) throws MalformedURLException {
		this(link.getURL(), localhost);
	}

	public UpdateManager(String url, Boolean localhost) throws MalformedURLException {
		String urlCopy = url;
		String[] urlSplit = url.split("/");
		if (localhost && (!urlSplit[2].equals("localhost"))) {
			urlCopy = urlCopy.replaceAll(urlSplit[2], "localhost");
		}
		for (Link link : Link.values()) {
			if (urlCopy.contains("%" + link.toString() + "%"))
				urlCopy = urlCopy.replaceAll("%" + link.toString() + "%", link.getURL());
		}
		this.url = new URL(urlCopy);
	}

	public String read() throws IOException {
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toByteArray(), encoding);
	}

	private static Boolean checkVersion(String version, Boolean localhost, Link link) throws IOException {
		String content;
		content = new UpdateManager(link.getURL() + "/version.txt", localhost).read();
		if (!content.trim().equals(version.trim())) {
			isUpdated = false;
		}
		return isUpdated;
	}

	private static List<String> getInfoVersion(Link link, String version) throws IOException {
		UpdateManager url = new UpdateManager(link.getURL() + "/changelog.txt", false);
		String[] split = url.read().split("#" + version + "\n");
		List<String> output = new ArrayList<>();
		if (split.length < 2)
			return output;
		
		String content = split[1];
		boolean next = false;
		for (String s : content.split("\n")) {
			if (!next && s.contains("#"))
				next = true;
			if (next)
				output.add(s);
		}
		
		infoVersion = new ArrayList<>(output);
		return infoVersion;
	}
	
	public static boolean isUpToDate() {
		return isUpdated;
	}

	public static List<String> getInfoVersion() {
		return infoVersion;
	}

	public static void checkAsync(final Plugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				check(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	public static void check(Plugin plugin) {
		try {
			if (!checkVersion(plugin.getDescription().getVersion(), false, Link.GITHUB_PATH)) {
				List<String> news = getInfoVersion(Link.GITHUB_PATH, plugin.getDescription().getVersion());
				if (!news.isEmpty()) {
					plugin.getLogger().info("A new version of the plugin is available.");
					Bukkit.getConsoleSender().sendMessage("Changelog :");
					for (int i = 0; i < news.size(); i++) {
						String newsLine = news.get(i);
						Bukkit.getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + ChatColor.GREEN + (i == (news.size() - 1) ? "\\_/ " : (newsLine.startsWith("#") ? " + " : " |  ")) + ChatColor.RESET + (newsLine.startsWith("#") ? ChatColor.YELLOW + newsLine.replaceFirst("#", "") : newsLine));
					}
				}
				plugin.getLogger().info("You're not running the latest update. Please stay updated at https://www.spigotmc.org/resources/17393/");
			} else {
				plugin.getLogger().info("Plugin is up-to-date.");
			}
		} catch (FileNotFoundException | UnknownHostException ex) {
			ExceptionManager.register(ex, false);
			plugin.getLogger().info("You don't have a valid internet connection, you will not be notified of the new updates.");
		} catch (IOException ex) {
			// Do nothing
		}
	}
}
