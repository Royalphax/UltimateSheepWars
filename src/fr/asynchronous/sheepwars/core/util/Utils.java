package fr.asynchronous.sheepwars.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;(MsgEnum.MsgEnum;

public class Utils {

	private Utils() {
		throw new IllegalStateException("Utility class");
	}

	public static List<String> getPlayerStats(Player player, PlayerData data, boolean inventoryStyle) {
		PlayerData playerData = PlayerData.getPlayerData(player);
		ChatColor color = playerData.getTeam().getColor();
		ArrayList<String> output = new ArrayList<>();
		if (inventoryStyle) {
			int[] totalTimes = splitToComponentTimes(data.getTotalTime());
			String bar = ChatColor.YELLOW + "-------------------------------------";
			output.add(ChatColor.GOLD + "Stats : " + Contributor.getPrefix(data.getPlayer()) + data.getName());
			output.add(bar);
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_GAME_PLAYED)) + ": " + ChatColor.YELLOW + data.getGames());
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_DEATH)) + ": " + ChatColor.YELLOW + data.getDeaths());
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_KILL)) + ": " + ChatColor.YELLOW + data.getKills());
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_VICTORY)) + ": " + ChatColor.YELLOW + data.getWins());
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_SHEEP_THROWN)) + ": " + ChatColor.YELLOW + data.getSheepThrown());
			output.add(ChatColor.AQUA + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_SHEEP_KILLED)) + ": " + ChatColor.YELLOW + data.getSheepKilled());
			output.add(bar);
			output.add(ChatColor.LIGHT_PURPLE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_WIN_RATE)) + ": " + ChatColor.YELLOW + data.getWinRate() + ChatColor.GREEN + " %");
			output.add(ChatColor.LIGHT_PURPLE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_KD_RATIO)) + ": " + ChatColor.YELLOW + data.getKDRatio());
			output.add(ChatColor.LIGHT_PURPLE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_TOTAL_TIME)) + ": " + Language.getMessageByLanguage(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_TOTAL_TIME_FORMAT).replace("%HOURS%", ChatColor.YELLOW + "" + totalTimes[0] + " " + ChatColor.GREEN + Language.getMessageByLanguage(playerData.getLocale(), (totalTimes[0] > 1 ? Message.getMessageByEnum(MsgEnum.HOURS : Message.getMessageByEnum(MsgEnum.HOUR))).replace("%MINUTES%", ChatColor.YELLOW + "" + totalTimes[1] + " " + ChatColor.GREEN + Language.getMessageByLanguage(playerData.getLocale(), (totalTimes[1] > 1 ? Message.getMessageByEnum(MsgEnum.MINUTES : Message.getMessageByEnum(MsgEnum.MINUTE))).replace("%SECONDS%", ChatColor.YELLOW + "" + totalTimes[2] + " " + ChatColor.GREEN + Language.getMessageByLanguage(playerData.getLocale(), (totalTimes[2] > 1 ? Message.getMessageByEnum(MsgEnum.SECONDS : Message.getMessageByEnum(MsgEnum.SECOND))));
		} else {
			output.add(Language.getMessageByLanguage(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.RECORDS).replaceAll("%PLAYER%", color + "" + ChatColor.BOLD + data.getPlayer().getName()));
			output.add("");
			output.add(ChatColor.WHITE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_GAME_PLAYED)) + ": " + ChatColor.GRAY + data.getGames());
			output.add(ChatColor.WHITE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_DEATH)) + ": " + ChatColor.GRAY + data.getDeaths());
			output.add(ChatColor.WHITE + Language.getMessageByLocale(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_KILL)) + ": " + ChatColor.GRAY + data.getKills());
			output.add(ChatColor.WHITE + Language.getMessageByLanguage(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_VICTORY)) + ": " + ChatColor.GRAY + data.getWins());
			output.add(ChatColor.WHITE + Language.getMessageByLanguage(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_SHEEP_THROWN)) + ": " + ChatColor.GRAY + data.getSheepThrown());
			output.add(ChatColor.WHITE + Language.getMessageByLanguage(playerData.getLocale(), Message.getMessageByEnum(MsgEnum.STATS_SHEEP_KILLED)) + ": " + ChatColor.GRAY + data.getSheepKilled());
		}
		return output;
	}

	public static ItemStack getItemStats(PlayerData.DATA_TYPE type, Player player, PlayerData data, UltimateSheepWarsPlugin plugin) {
		Language lang = Language.getLanguage(data.getLocale());
		if (type == null) {
			ArrayList<String> stats = new ArrayList<>(getPlayerRatio(player, data, true, plugin));
			String title = stats.get(0);
			stats.remove(0);
			return new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(player.getName()).setName(title).setLore(Arrays.asList(assignArrayToString(stats).split("\n"))).toItemStack();
		} else {
			String[] lore = PlayerData.DATA_TYPE.getRanking(type, lang);
			for (String array : lore)
				if (array.contains(player.getName()))
					array.replaceAll(player.getName(), ChatColor.LIGHT_PURPLE + player.getName());
			return new ItemBuilder(Material.ITEM_FRAME).setName(ChatColor.GOLD + "Stats : " + lang.getMessage(Message.getMessageByEnum(MsgEnum.SCOREBOARD_TITLE)).setLore(lore).toItemStack();
		}
	}

	public static Location toLocation(final String string) {
		final String[] splitted = string.split("_");
		World world = Bukkit.getWorld(splitted[0]);
		if (world == null || splitted.length < 6) {
			world = Bukkit.getWorlds().get(0);
		}
		return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
	}

	public static String toString(final Location location) {
		final World world = location.getWorld();
		return String.valueOf(world.getName()) + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ() + "_" + location.getYaw() + "_" + location.getPitch();
	}

	public static String assignArrayToString(ArrayList<String> listString) {
		String nxt = "";
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < listString.size(); i++) {
			output.append(nxt + listString.get(i));
			nxt = "\n";
		}
		return output.toString().trim();
	}

	public static boolean isPluginConfigured(UltimateSheepWarsPlugin plugin) {
		if (ConfigManager.getLocation(Field.LOBBY) == getDefaultLocation() 
				|| ConfigManager.getLocations(Field.BOOSTERS).isEmpty() 
				|| TeamManager.BLUE.getSpawns().isEmpty() 
				|| TeamManager.RED.getSpawns().isEmpty() 
				|| TeamManager.SPEC.getSpawns().isEmpty()) {
			return false;
		}
		return true;
	}

	public static boolean inventoryContains(Player player, Material mat) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null)
				continue;
			if (item.getType() == mat)
				return true;
		}
		return false;
	}

	public static int[] splitToComponentTimes(int seconds) {
		BigDecimal biggy = new BigDecimal(seconds);
		long longVal = biggy.longValue();
		int hours = (int) longVal / 3600;
		int remainder = (int) longVal - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;
		int[] ints = {hours, mins, secs};
		return ints;
	}

	public static int halfSplit(String value) {
		int mid = value.length() / 2 + 1;
		String prefix = value.substring(0, mid);
		if (prefix.split("")[(prefix.length() - 1)].equals("§")) {
			return mid + 1;
		}
		return mid;
	}

	public static String formatTime(int time) {
		time = time * 1000;
		long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

		String ret = "";
		if (days > 0)
			ret += days + " jours ";

		if (hours > 0)
			ret += hours + " heures ";

		if (minutes > 0)
			ret += minutes + " minutes ";

		if (seconds > 0)
			ret += seconds + " secondes";

		if (ret.isEmpty() && minutes == 0)
			ret += "moins d'une minute";

		return ret;
	}
	
	public static Location getDefaultLocation() {
		return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	}

	public static ArrayList<String> d(String[] ints) {
		ArrayList<String> output = new ArrayList<>();
		for (int i = 0; i < ints.length; i++)
			output.add(new String(new BigInteger(ints[i]).toByteArray()));
		return output;
	}
}
