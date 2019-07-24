/**
 * 
 */
package fr.asynchronous.sheepwars.core.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.WorldManager;
import fr.asynchronous.sheepwars.core.util.FileUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * @author Therence
 */
public class PlayableMap {

	private static List<PlayableMap> maps = new ArrayList<>();

	private File folder;
	private World world = null;
	private boolean readyToPlay = false;

	private String displayName = "";
	private Map<VirtualLocation, SheepWarsTeam> teamSpawns = new HashMap<>();
	private List<VirtualLocation> boosterSpawns = new ArrayList<>();

	private File file;
	private FileConfiguration config;

	public PlayableMap(File folder, Logger log) {
		this.folder = folder;

		if (isMinecraftMapDirectory()) {
			if (getPlayableMap(folder) != null) {
				log.info("- Map " + getDisplayName() + " already registred.");
				return;
			}
			maps.add(this);
			loadConfig();
			loadData();
			log.info("- Map " + getDisplayName() + " registred (path: " + folder.getPath() + ")");
		} else {
			log.info("- " + folder.getName() + " isn't a folder or doesn't contains all required files. (path: " + folder.getPath() + ")");
		}
	}

	public void loadConfig() {
		file = new File(folder, "sheepwars_data.yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				ExceptionManager.register(e, true);
			}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@SuppressWarnings("deprecation")
	public void loadData() {
		if (!config.contains("team") && !config.contains("booster") && folder.getName().equals(WorldManager.LOBBY_MAP_NAME)) {
			for (VirtualLocation loc : ConfigManager.getLocations(Field.BLUE_SPAWNS)) {
				teamSpawns.put(loc, SheepWarsTeam.BLUE);
			}
			for (VirtualLocation loc : ConfigManager.getLocations(Field.RED_SPAWNS)) {
				teamSpawns.put(loc, SheepWarsTeam.RED);
			}
			for (VirtualLocation loc : ConfigManager.getLocations(Field.SPEC_SPAWNS)) {
				teamSpawns.put(loc, SheepWarsTeam.SPEC);
			}
			for (VirtualLocation loc : ConfigManager.getLocations(Field.BOOSTERS)) {
				boosterSpawns.add(loc);
			}
		} else {
			for (SheepWarsTeam team : Arrays.asList(SheepWarsTeam.RED, SheepWarsTeam.BLUE, SheepWarsTeam.SPEC)) {
				List<String> positions = config.getStringList("team." + team.getName() + ".spawns");
				for (String position : positions)
					teamSpawns.put(VirtualLocation.fromString(this, position), team);
			}
			List<String> positions = config.getStringList("booster.spawns");
			for (String position : positions)
				boosterSpawns.add(VirtualLocation.fromString(this, position));
			this.displayName = config.getString("display-name", "");
		}
	}

	public void uploadData() {
		for (SheepWarsTeam team : Arrays.asList(SheepWarsTeam.RED, SheepWarsTeam.BLUE, SheepWarsTeam.SPEC)) {
			List<String> positions = new ArrayList<>();
			for (VirtualLocation position : teamSpawns.keySet()) {
				if (teamSpawns.get(position) == team)
					positions.add(position.toPlayableMapConfigString());
			}
			config.set("team." + team.getName() + ".spawns", (List<String>) positions);
		}
		List<String> positions = new ArrayList<>();
		for (VirtualLocation position : boosterSpawns)
			positions.add(position.toPlayableMapConfigString());
		config.set("booster.spawns", (List<String>) positions);
		if (!this.displayName.equals(""))
			config.set("display-name", this.displayName);
		try {
			config.save(file);
		} catch (IOException e) {
			ExceptionManager.register(e, true);
		}
	}

	/* POSITION DES SPAWNS DE TEAM */

	public VirtualLocationList getTeamSpawns(SheepWarsTeam team) {
		List<VirtualLocation> virtualList = new ArrayList<>();
		for (VirtualLocation position : teamSpawns.keySet()) {
			if (teamSpawns.get(position) == team)
				virtualList.add(position);
		}
		return new VirtualLocationList(virtualList);
	}

	public void addTeamSpawn(SheepWarsTeam team, Location loc) {
		final VirtualLocation vLoc = VirtualLocation.fromBukkitLocation(loc);
		if (teamSpawns.containsKey(vLoc))
			teamSpawns.remove(vLoc);
		teamSpawns.put(vLoc, team);
	}

	public void clearTeamSpawns(SheepWarsTeam team) {
		Map<VirtualLocation, SheepWarsTeam> map = new HashMap<>(teamSpawns);
		for (VirtualLocation loc : map.keySet()) {
			if (map.get(loc).equals(team))
				teamSpawns.remove(loc);
		}
	}

	/* POSITIONS DES BOOSTERS */

	public VirtualLocationList getBoosterSpawns() {
		return new VirtualLocationList(boosterSpawns);
	}

	public void addBoosterSpawn(Location loc) {
		boosterSpawns.add(VirtualLocation.fromBukkitLocation(loc));
	}

	public void clearBoosterSpawns() {
		boosterSpawns.clear();
	}

	/** FIN DE LA SECTION CONFIG **/

	public File getFolder() {
		return folder;
	}

	public String getDisplayName() {
		if (!this.displayName.equals(""))
			return ChatColor.translateAlternateColorCodes('&', this.displayName);
		return getRawName();
	}
	
	public String getRawName() {
		final String str = folder.getName().replaceAll("-", " ");
		final String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
		return cap;
	}
	
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public boolean isMinecraftMapDirectory() {
		if (!this.folder.isDirectory())
			return false;
		final File data = new File(this.folder, "data");
		if (!data.exists())
			return false;
		final File playerdata = new File(this.folder, "playerdata");
		if (!playerdata.exists())
			return false;
		final File stats = new File(this.folder, "stats");
		if (!stats.exists())
			return false;
		final File region = new File(this.folder, "region");
		if (!region.exists()) {
			return false;
		} else {
			int mcaCount = 0;
			for (File file : region.listFiles()) {
				if (file.isFile() && file.getName().endsWith(".mca"))
					mcaCount++;
			}
			if (mcaCount == 0)
				return false;
		}
		final File sessionlock = new File(this.folder, "session.lock");
		if (!sessionlock.exists())
			return false;
		final File leveldat = new File(this.folder, "level.dat");
		if (!leveldat.exists())
			return false;
		return true;
	}

	public void loadWorld() throws IOException {
		if (this.world != null)
			return;
		final File destination = new File(Bukkit.getWorldContainer(), this.folder.getName());
		if (destination.exists())
			FileUtils.delete(destination);
		FileUtils.copyFolder(this.folder, destination);
		final WorldCreator worldCreator = new WorldCreator(this.folder.getName());
		this.world = worldCreator.createWorld();
		Bukkit.getLogger().info("[UltimateSheepWars > WorldManager] World \"" + getRawName() + "\" loaded.");
	}
	
	public void unloadWorld() {
		if (this.world == null)
			return;
		if (!this.world.getPlayers().isEmpty())
			return;
		Bukkit.unloadWorld(this.world, false);
		final File world = new File(Bukkit.getWorldContainer(), this.folder.getName());
		if (world.exists())
			FileUtils.delete(world);
		this.world = null;
		Bukkit.getLogger().info("[UltimateSheepWars > WorldManager] World \"" + getRawName() + "\" unloaded.");
	}

	public void loadWorld(World world) {
		this.world = world;
	}

	public boolean isWorldLoaded() {
		return this.world != null;
	}

	public boolean isReadyToPlay() {
		return this.readyToPlay;
	}

	/**
	 * Check if a playable map is ready to be used.
	 * 
	 * @return red team, blue team, spectators and boosters spawn counts
	 */
	public int[] checkReadyToPlay() {
		boolean readyToPlay = true;

		List<Integer> outputList = new ArrayList<>();
		for (SheepWarsTeam team : Arrays.asList(SheepWarsTeam.RED, SheepWarsTeam.BLUE, SheepWarsTeam.SPEC)) {
			int count = getTeamSpawns(team).size();
			if (count == 0)
				readyToPlay = false;
			outputList.add(count);
		}

		if (boosterSpawns.size() == 0)
			readyToPlay = false;

		this.readyToPlay = readyToPlay;

		int[] output = {outputList.get(0), outputList.get(1), outputList.get(2), boosterSpawns.size()};
		return output;
	}

	public World getWorld() {
		return this.world;
	}

	public static PlayableMap getPlayableMap(String name) {
		for (PlayableMap map : maps)
			if (map.getFolder().getName().equals(name))
				return map;
		return null;
	}

	public static PlayableMap getPlayableMap(World world) {
		for (PlayableMap map : maps)
			if (map.isWorldLoaded() && map.getWorld().equals(world))
				return map;
		return null;
	}

	public static PlayableMap getPlayableMap(File folder) {
		return getPlayableMap(folder.getName());
	}

	public static List<PlayableMap> getReadyMaps() {
		List<PlayableMap> output = new ArrayList<>(maps);
		for (PlayableMap map : maps) {
			if (!map.isReadyToPlay())
				output.remove(map);
		}
		return output;
	}

	public static List<PlayableMap> getPlayableMaps() {
		return maps;
	}
}
