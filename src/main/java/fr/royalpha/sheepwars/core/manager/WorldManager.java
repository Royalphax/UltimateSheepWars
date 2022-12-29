package fr.royalpha.sheepwars.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.exception.PlayableMapException;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import fr.royalpha.sheepwars.core.util.FileUtils;
import fr.royalpha.sheepwars.core.util.RandomUtils;
import fr.royalpha.sheepwars.core.util.ReflectionUtils;
import net.md_5.bungee.api.ChatColor;

public class WorldManager {

	public final SheepWarsPlugin plugin;

	public static final String LOBBY_MAP_NAME = "sheepwars-lobby";

	public final File worldContainer, worldFolder, mapsFolder;

	private boolean isVoteEnable = false;
	private PlayableMap votedMap = null;

	public WorldManager(SheepWarsPlugin plugin) {
		this.plugin = plugin;
		this.worldContainer = plugin.getServer().getWorldContainer();
		this.worldFolder = new File(this.worldContainer, "world");
		this.mapsFolder = new File(this.worldContainer, "sheepwars-maps");
		if (!this.worldFolder.exists()) {
			plugin.disablePluginLater("You need to let the server generate the world before starting UltimateSheepWars for the first time. Please restart your server.");
			return;
		}

		try {
			init(plugin.getLogger());
		} catch (IOException ex) {
			ExceptionManager.register(ex, true);
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
		final File world = new File(this.mapsFolder, LOBBY_MAP_NAME);
		if (this.mapsFolder.listFiles().length <= 1 || !world.exists()) {
			if (copyFolder.exists()) {
				logger.info("Moving 'sheepwars-backup' into the new 'sheepwars-maps' folder ...");
				FileUtils.copyFolder(copyFolder, world);
				FileUtils.delete(copyFolder);
				logger.info("Move done !");
				// Backup les langages
				File langFolder = new File(this.worldContainer, "plugins/UltimateSheepWars/languages");
				for (File file : langFolder.listFiles()) {
					File backup = new File(this.worldContainer, "plugins/UltimateSheepWars/languages/" + file.getName().replaceAll(".yml", "") + "-backup.yml");
					org.apache.commons.io.FileUtils.copyDirectory(file, backup);
					file.delete();
					logger.info("Backup '" + file.getName() + "' to '" + backup.getName() + "'");
				}
			} else {
				logger.info("Didn't found 'sheepwars-lobby' in the 'sheepwars-maps' folder, duplicating 'world' ...");
				try {
					org.apache.commons.io.FileUtils.copyDirectory(this.worldFolder, world);
				} catch (IOException e) {
					ExceptionManager.register(e, true);
				}
				logger.info("Duplication successful !");
			}
		}
		try {
			resetWorld(world);
		} catch (Exception e) {
			ExceptionManager.register(e, true);
		}
	}

	private void resetWorld(File copyFolder) throws IOException {
		this.plugin.getLogger().info("Resetting world ...");
		Bukkit.unloadWorld(this.worldFolder.getName(), true);
		try {
			ReflectionUtils.getClass("RegionFileCache", ReflectionUtils.PackageType.MINECRAFT_SERVER).getMethod("a", new Class[0]).invoke(null, new Object[0]);
		} catch (Exception e) {
			// Do nothing
		}
		FileUtils.delete(this.worldFolder);
		FileUtils.copyFolder(copyFolder, this.worldFolder);
		this.plugin.getLogger().info("World reseted !");
	}

	private void writeREADME() throws FileNotFoundException, UnsupportedEncodingException {
		final File readme = new File(this.mapsFolder, "README.txt");
		if (!readme.exists()) {
			PrintWriter writer = new PrintWriter(readme, "UTF-8");
			writer.println("----- Basic Informations -----");
			writer.println("> This folder contains the worlds used to play Ultimate Sheep Wars on your server.");
			writer.println("> If you put more than one world directory here and you have setup them ingame, the plugin will automatically ask players to vote for the map to use.");
			writer.println("> Otherwise, the alone world will be automatically choosen.");
			writer.println("NOTE : The 'sheepwars-lobby' folder contains the world you used to play on before.");
			writer.println("");
			writer.println("----- About the process ------");
			writer.println("> When a map has been voted by the players, it will be copied to the superior folder (the same folder which contains the directory \"plugins/\").");
			writer.println("Then, this map will be load to allow players to play on it. So, the folder \"sheepwars-maps\" always contains the clean version of each map.");
			writer.println("");
			writer.print("------------------------------");
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
					ExceptionManager.register(e, true);
					return;
				}
			}
			player.sendMessage(ChatColor.GREEN + "Teleporting to \"" + world + "\" ...");
			Location loc = new Location(map.getWorld(), 0, 64, 0);
			if (map.getWorld().getSpawnLocation() != null)
				loc = map.getWorld().getSpawnLocation();
			final Block block = loc.subtract(0, 1, 0).getBlock();
			if (block.isLiquid() || block.isEmpty()) {
				player.teleport(map.getWorld().getSpawnLocation());
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		}
	}

	public void checkVoteMode(@Nullable CommandSender sender) {
		int readyCount = 0;
		send(sender, "");
		for (PlayableMap map : PlayableMap.getPlayableMaps()) {
			int[] ints = map.checkReadyToPlay();
			boolean ready = map.isReadyToPlay();
			send(sender, ChatColor.WHITE + "∙ " + ChatColor.YELLOW + map.getFolder().getName() + ChatColor.GRAY + " (" + (ready ? ChatColor.GREEN + "Ready To Play" : ChatColor.RED + "Not Ready To Play") + ChatColor.GRAY + ")");
			String details = ChatColor.GRAY + " Red spawns: " + (ints[0] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[0];
			details += ChatColor.GRAY + "  Blue spawns: " + (ints[1] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[1];
			details += ChatColor.GRAY + "  Spec spawns: " + (ints[2] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[2];
			details += ChatColor.GRAY + "  Boosters: " + (ints[3] > 0 ? ChatColor.GREEN : ChatColor.RED) + ints[3];
			send(sender, details);
			if (ready)
				readyCount++;
		}
		send(sender, "");
		send(sender, ChatColor.WHITE + "∙ " + ChatColor.GRAY + "Vote Mode : " + (readyCount > 1 ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
		isVoteEnable = readyCount > 1;
	}

	private void send(@Nullable CommandSender sender, String message) {
		if (sender != null)
			sender.sendMessage(message);
	}

	public PlayableMap getVoteResult() {
		if (votedMap == null) {
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
					} else if (voteCount == max && pMap.isReadyToPlay()) {
						finalRandom.add(pMap);
					}
				}
				votedMap = RandomUtils.getRandom(finalRandom);
			} else { // Si le vote n'est pas activé, ça veut dire que de toute façon il n'y a qu'une seule map de valide
				votedMap = PlayableMap.getPlayableMaps().get(0);
			}
		}
		return votedMap;
	}

	public boolean isVoteModeEnable() {
		return isVoteEnable;
	}

	public PlayableMap getVotedMap() {
		return votedMap;
	}
	
	public void unloadVotedMap() {
		votedMap.unloadWorld();
		votedMap = null;
	}

	public int getVoteCount(PlayableMap map) {
		int result = 0;
		for (Player online : Bukkit.getOnlinePlayers()) {
			PlayerData data = PlayerData.getPlayerData(online);
			if ((map != null && data.getVotedMap() != null && data.getVotedMap() == map) || (map == null && data.getVotedMap() == null)) {
				result++;
			}
		}
		return result;
	}
}
