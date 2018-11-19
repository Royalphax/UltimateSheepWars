package fr.asynchronous.sheepwars.core.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roytreo28
 */
public class URLManager {

	private URL url;
	
	private static boolean isUpdated = true;
	private static List<String> infoVersion = new ArrayList<>();

	public enum Link {
		BASE_URL("roytreo28.ddns.net"),
		DB_ACCESS("https://roytreo28.github.io/UltimateSheepWars/auto-updater/db_acc.txt"), 
		DB_SEQUENCE("https://roytreo28.github.io/UltimateSheepWars/auto-updater/db_seq.txt"), 
		GITHUB_PATH("https://roytreo28.github.io/UltimateSheepWars/auto-updater"),
		FREE_HOSTED_DB_ACCESS("https://roytreo28.github.io/UltimateSheepWars/auto-updater/free_hosted_db.txt");

		private String url;

		private Link(String url) {
			this.url = url;
		}

		public String getURL() {
			return this.url;
		}
	}
	
	public URLManager(Link link, Boolean localhost) throws MalformedURLException {
		this(link.getURL(), localhost);
	}

	public URLManager(String url, Boolean localhost) throws MalformedURLException {
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

	public static Boolean checkVersion(String version, Boolean localhost, Link link) throws MalformedURLException, IOException {
		String content;
		content = new URLManager(link.getURL() + "/version.txt", localhost).read();
		if (!content.trim().equals(version.trim())) {
			isUpdated = false;
		}
		return isUpdated;
	}
	
	public static List<String> getInfoVersion(Link link, String version) throws IOException {
		URLManager url = new URLManager(link.getURL() + "/changelog.txt", false);
		String[] split = url.read().split("#" + version + "\n");
		List<String> output = new ArrayList<>();
		if (split.length < 2)
			return output;
		
		String content = url.read().split("#" + version + "\n")[1];
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
}
