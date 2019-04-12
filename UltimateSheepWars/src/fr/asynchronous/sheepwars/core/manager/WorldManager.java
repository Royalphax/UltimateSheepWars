package fr.asynchronous.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.exception.PlayableMapException;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.util.FileUtils;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import net.md_5.bungee.api.ChatColor;

public class WorldManager {

	public final SheepWarsPlugin plugin;

	public final File worldContainer, worldFolder, mapsFolder;

	private boolean isVoteEnable = false;
	private PlayableMap votedMap = null;

	public WorldManager(SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.worldContainer = plugin.getServer().getWorldContainer();
		this.worldFolder = new File(this.worldContainer, "world");
		this.mapsFolder = new File(this.worldContainer, "sheepwars-maps");
		try {
			init(plugin.getLogger());
		} catch (IOException ex) {
			new ExceptionManager(ex).register(true);
		} catch (PlayableMapException e) {
			plugin.disablePlugin(Level.WARNING, e.getMessage());
		}
	}

	private void init(Logger logger) throws IOException, PlayableMapException {
		logger.info("Loading directories ...");
		if (!this.mapsFolder.exists()) {
			this.mapsFolder.mkdirs();
			writeREADME();
		}
		/** On gere les fichiers **/
		final File copyFolder = new File(this.worldContainer, "sheepwars-backup");
		final File world = new File(this.mapsFolder, "sheepwars-backup");
		if (this.mapsFolder.listFiles().length <= 1) {
			if (copyFolder.exists()) {
				logger.info("Moving 'sheepwars-backup' into the new 'sheepwars-maps' folder ...");
				FileUtils.copyFolder(copyFolder, world);
				FileUtils.delete(copyFolder);
				logger.info("Move done !");
			} else {
				logger.info("Didn't found any world in the 'sheepwars-maps' folder, duplicating 'world' ...");
				FileUtils.copyFolder(this.worldFolder, world);
				logger.info("Duplication successful !");
			}
		}
		/** On enregistre toutes les playable maps **/
		for (File supposedMapFolder : this.mapsFolder.listFiles())
			if (supposedMapFolder.isDirectory())
				new PlayableMap(supposedMapFolder, logger);
		/** Verifier qu'on a au moins une playable map **/
		if (PlayableMap.getPlayableMaps().isEmpty()) {
			throw new PlayableMapException("There's no playable map in the folder 'sheepwars-maps'. Please delete corrupted folders.");
		}
		/** On reset le world en prenant celui qui est désigné dans la config **/
		PlayableMap map = PlayableMap.getPlayableMap(ConfigManager.getString(Field.LOBBY_MAP_NAME));
		if (map == null) {
			throw new PlayableMapException("The lobby map name in config.yml doesn't match with any playable map in 'sheepwars-maps'.");
		}
		try {
			resetWorld(map.getFolder());
		} catch (Exception e) {
			new ExceptionManager(e).register(true);
		}
	}

	private void resetWorld(File copyFolder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		this.plugin.getLogger().info("Resetting world ...");
		Bukkit.unloadWorld(this.worldFolder.getName(), true);
		ReflectionUtils.getClass("RegionFileCache", ReflectionUtils.PackageType.MINECRAFT_SERVER).getMethod("a", new Class[0]).invoke(null, new Object[0]);
		FileUtils.delete(this.worldFolder);
		FileUtils.copyFolder(copyFolder, this.worldFolder);
		this.plugin.getLogger().info("World reseted !");
	}

	private void writeREADME() throws FileNotFoundException, UnsupportedEncodingException {
		final File readme = new File(this.mapsFolder, "README.txt");
		if (!readme.exists()) {
			PrintWriter writer = new PrintWriter(readme, "UTF-8");
			writer.println("--- Basic Informations ---");
			writer.println("* This folder contains the worlds used to play Ultimate Sheep Wars on your server.");
			writer.println("* If you put more than one world folder and you have setup them ingame, the plugin will automatically ask players to vote for the map to use.");
			writer.println("(The display name for each map will be the name of the folder which contains it)");
			writer.println("* Otherwise, the alone world will be automatically choosen.");
			writer.println("NOTE : The 'sheepwars-backup' folder contains the world you used to play on before.");
			writer.println("");
			writer.println("--- Learn More ---");
			writer.println("* When a map has been voted by the players, it will be copied to the superior folder (the same folder which contains the directory \"plugins/\").");
			writer.print("Then, this map will be load to allow players to play on it. So, the folder \"sheepwars-worlds\" always contains the clean version of each map.");
			writer.close();
		}
	}

	public void teleport(Player player, String world) {
		if (PlayableMap.getPlayableMap(world) == null) {
			player.sendMessage(ChatColor.RED + "Map \"" + world + "\" hasn't been registred (it probably does not exist).");
		} else {
			final PlayableMap map = PlayableMap.getPlayableMap(world);
			if (!map.isWorldLoaded()) {
				player.sendMessage(ChatColor.YELLOW + "Loading world \"" + world + "\" ...");
				try {
					map.loadWorld();
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED + "An error occured while loading the world. Please contact the developer.");
					new ExceptionManager(e).register(true);
					return;
				}
			}
			player.sendMessage(ChatColor.GREEN + "Teleporting to \"" + world + "\" ...");
			player.teleport(map.getWorld().getSpawnLocation());
		}
	}

	public void checkVoteMode(@Nullable CommandSender sender) {
		int readyCount = 0;
		for (PlayableMap map : PlayableMap.getPlayableMaps()) {
			int[] ints = map.checkReadyToPlay();
			boolean ready = map.isReadyToPlay();
			send(sender, ChatColor.WHITE + "∙ " + ChatColor.GRAY + map.getName() + " (" + (ready ? ChatColor.GREEN + "Ready" : ChatColor.RED + "Not Ready") + ChatColor.GRAY + ")");
			String details = ChatColor.GRAY + " Red spawns: " + (ints[0] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[0];
			details += ChatColor.GRAY + " / Blue spawns: " + (ints[1] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[1];
			details += ChatColor.GRAY + " / Spec spawns: " + (ints[2] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[2];
			details += ChatColor.GRAY + " / Boosters: " + (ints[3] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[3];
			send(sender, details);
			if (ready)
				readyCount++;
		}
		send(sender, ChatColor.WHITE + "∙ " + ChatColor.GRAY + "Vote Enable : " + (readyCount > 1 ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
		isVoteEnable = readyCount > 1;
	}

	private void send(@Nullable CommandSender sender, String message) {
		if (sender != null)
			sender.sendMessage(message);
	}

	public PlayableMap getVoteResult() {
		if (isVoteEnable) { // Plusieurs maps valides
			List<PlayableMap> maps = new ArrayList<>(PlayableMap.getReadyMaps());

			Map<PlayableMap, Integer> votes = new HashMap<>();
			for (PlayableMap map : maps) {
				votes.put(map, 0);
			}
			for (Player online : Bukkit.getOnlinePlayers()) {
				PlayerData data = PlayerData.getPlayerData(online);
				if (data.getVotedMap() != null && maps.contains(data.getVotedMap())) {
					int currentVote = votes.get(data.getVotedMap());
					currentVote += 1;
					votes.remove(data.getVotedMap());
					votes.put(data.getVotedMap(), currentVote);
				}
			}
			int max = 0;
			List<PlayableMap> finalRandom = new ArrayList<>();
			for (Entry<PlayableMap, Integer> map : votes.entrySet()) {
				PlayableMap pMap = map.getKey();
				Integer voteCount = map.getValue();
				if (voteCount > max) {
					max = voteCount;
					finalRandom.clear();
					finalRandom.add(pMap);
				} else if (voteCount == max) {
					finalRandom.add(pMap);
				}
			}
			votedMap = RandomUtils.getRandom(finalRandom);
		} else { // Si le vote n'est pas activé, ça veut dire que de toute façon il n'y a qu'une seule map de valide
			votedMap = PlayableMap.getPlayableMaps().get(0);
		}
		return votedMap;
	}

	public boolean isVoteModeEnable() {
		return isVoteEnable;
	}
	
	public PlayableMap getVotedMap() {
		return votedMap;
	}
}
