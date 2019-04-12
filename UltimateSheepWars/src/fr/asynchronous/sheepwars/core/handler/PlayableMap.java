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

import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.FileUtils;

/**
 * @author Therence
 */
public class PlayableMap {

	private static List<PlayableMap> maps = new ArrayList<>();

	private File folder;
	private World world = null;
	private boolean readyToPlay = false;

	private Map<VirtualLocation, TeamManager> teamSpawns = new HashMap<>();
	private List<VirtualLocation> boosterSpawns = new ArrayList<>();

	private File file;
	private FileConfiguration config;

	public PlayableMap(File folder, Logger log) {
		this.folder = folder;

		if (isMinecraftMapDirectory()) {
			if (getPlayableMap(folder) != null) {
				log.info("- Map in folder 'sheepwars-maps/" + folder.getName() + "' already registred.");
				return;
			}
			maps.add(this);
			loadConfig();
			loadData();
			log.info("- Map in folder 'sheepwars-maps/" + folder.getName() + "' registred !");
		} else {
			log.info("- 'sheepwars-maps/" + folder.getName() + "' isn't a folder or doesn't contains all required files.");
		}
	}

	public void loadConfig() {
		file = new File(folder, "sheepwars_data.yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				new ExceptionManager(e).register(true);
			}
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void loadData() {
		for (TeamManager team : Arrays.asList(TeamManager.RED, TeamManager.BLUE, TeamManager.SPEC)) {
			List<String> positions = config.getStringList("team." + team.getName() + ".spawns");
			for (String position : positions)
				teamSpawns.put(VirtualLocation.fromString(this, position), team);
		}
		List<String> positions = config.getStringList("booster.spawns");
		for (String position : positions)
			boosterSpawns.add(VirtualLocation.fromString(this, position));
	}

	public void uploadData() {
		for (TeamManager team : Arrays.asList(TeamManager.RED, TeamManager.BLUE, TeamManager.SPEC)) {
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
		try {
			config.save(file);
		} catch (IOException e) {
			new ExceptionManager(e).register(true);
		}
	}
	
	/* POSITION DES SPAWNS DE TEAM */

	public VirtualLocationList getTeamSpawns(TeamManager team) {
		List<VirtualLocation> virtualList = new ArrayList<>();
		for (VirtualLocation position : teamSpawns.keySet()) {
			if (teamSpawns.get(position) == team)
				virtualList.add(position);
		}
		return new VirtualLocationList(virtualList);
	}
	
	public void addTeamSpawn(TeamManager team, Location loc) {
		final VirtualLocation vLoc = VirtualLocation.fromBukkitLocation(loc);
		if (teamSpawns.containsKey(vLoc))
			teamSpawns.remove(vLoc);
		teamSpawns.put(vLoc, team);
	}
	
	public void clearTeamSpawns(TeamManager team) {
		Map<VirtualLocation, TeamManager> map = new HashMap<>(teamSpawns); 
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

	public String getName() {
		final String str = folder.getName().replaceAll("-", " ");
		final String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
		return cap;
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
		final File uid = new File(this.folder, "uid.dat");
		if (!uid.exists())
			return false;
		final File leveldat = new File(this.folder, "level.dat");
		if (!leveldat.exists())
			return false;
		final File leveldatold = new File(this.folder, "level.dat_old");
		if (!leveldatold.exists())
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
		/**
		 * List<Field> fields = new ArrayList<>(); fields.add(TeamManager.RED.getSpawns()); fields.add(TeamManager.BLUE.getSpawns()); fields.add(TeamManager.SPEC.getSpawns()); fields.add(Field.BOOSTERS);
		 * 
		 * boolean readyToPlay = true;
		 * 
		 * List<Integer> outputList = new ArrayList<>(); for (Field field : fields) { List<VirtualLocation> locations = ConfigManager.getLocations(field); int count = 0; for (VirtualLocation location : locations) if (location.getWorld().equals(folder.getName())) count++; outputList.add(count); if (readyToPlay && count == 0) readyToPlay = false; }
		 * 
		 * this.readyToPlay = readyToPlay;
		 * 
		 * int[] output = {outputList.get(0), outputList.get(1), outputList.get(2), outputList.get(3)}; return output;
		 **/
		boolean readyToPlay = true;

		List<Integer> outputList = new ArrayList<>();
		for (TeamManager team : Arrays.asList(TeamManager.RED, TeamManager.BLUE, TeamManager.SPEC)) {
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
