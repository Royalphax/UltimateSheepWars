package fr.royalpha.sheepwars.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import fr.royalpha.sheepwars.core.handler.PlayableMap;
import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.handler.Contributor;
import fr.royalpha.sheepwars.core.handler.DisplayStyle;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.PlayerData.DataType;

public class Utils {

	private Utils() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean areSimilar(ItemStack i1, ItemStack i2) {
		if (i1 == null || i2 == null)
			return false;
		if (i1.hasItemMeta() && i2.hasItemMeta()) {
			if (!i1.getItemMeta().getDisplayName().equals(i2.getItemMeta().getDisplayName()))
				return false;
		} else {
			if (!i1.getType().equals(i2.getType()))
				return false;
		}
		return true;
	}

	public static ArrayList<String> getPlayerStats(PlayerData data, Language lang, DisplayStyle style) {
		ChatColor color = data.getTeam().getColor();
		ArrayList<String> output = new ArrayList<>();
		switch (style) {
			case HOVER :
				output.add(data.getLanguage().getMessage(Message.Messages.RECORDS).replaceAll("%PLAYER%", color + "" + ChatColor.BOLD + data.getPlayer().getName()));
				output.add("");
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_GAME_PLAYED) + ": " + ChatColor.GRAY + data.getGames());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_DEATH) + ": " + ChatColor.GRAY + data.getDeaths());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_KILL) + ": " + ChatColor.GRAY + data.getKills());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_VICTORY) + ": " + ChatColor.GRAY + data.getWins());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_SHEEP_THROWN) + ": " + ChatColor.GRAY + data.getSheepThrown());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_SHEEP_KILLED) + ": " + ChatColor.GRAY + data.getSheepKilled());
				break;

			case CHAT :
				final String barChat = ChatColor.GRAY + "----------------------";
				output.add(barChat);
				output.add(data.getLanguage().getMessage(Message.Messages.RECORDS).replaceAll("%PLAYER%", color + "" + ChatColor.BOLD + data.getPlayer().getName()));
				output.add("");
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_GAME_PLAYED) + ": " + ChatColor.GRAY + data.getGames());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_DEATH) + ": " + ChatColor.GRAY + data.getDeaths());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_KILL) + ": " + ChatColor.GRAY + data.getKills());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_VICTORY) + ": " + ChatColor.GRAY + data.getWins());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_SHEEP_THROWN) + ": " + ChatColor.GRAY + data.getSheepThrown());
				output.add(ChatColor.WHITE + lang.getMessage(Message.Messages.STATS_SHEEP_KILLED) + ": " + ChatColor.GRAY + data.getSheepKilled());
				output.add(barChat);
				break;

			case INVENTORY :
				final String barInventory = ChatColor.YELLOW + "---------------------------";
				output.add(ChatColor.GOLD + "Stats : " + Contributor.getPrefix(data.getPlayer()) + data.getName());
				output.add(barInventory);
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_GAME_PLAYED) + ": " + ChatColor.YELLOW + data.getGames());
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_DEATH) + ": " + ChatColor.YELLOW + data.getDeaths());
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_KILL) + ": " + ChatColor.YELLOW + data.getKills());
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_VICTORY) + ": " + ChatColor.YELLOW + data.getWins());
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_SHEEP_THROWN) + ": " + ChatColor.YELLOW + data.getSheepThrown());
				output.add(ChatColor.AQUA + lang.getMessage(Message.Messages.STATS_SHEEP_KILLED) + ": " + ChatColor.YELLOW + data.getSheepKilled());
				output.add(barInventory);
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(Message.Messages.STATS_WIN_RATE) + ": " + ChatColor.YELLOW + data.getWinRate() + ChatColor.GREEN + " %");
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(Message.Messages.STATS_KD_RATIO) + ": " + ChatColor.YELLOW + data.getKDRatio());
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(Message.Messages.STATS_TOTAL_TIME) + ": " + ChatColor.YELLOW + formatTime(lang, data.getTotalTime()));
				break;
		}
		return output;
	}

	public static List<String> getPlayerStats(OfflinePlayer playersData, Player playerToShow, DisplayStyle style) {
		final PlayerData data = PlayerData.getPlayerData(playersData);
		final Language lang = PlayerData.getPlayerData(playerToShow).getLanguage();
		return getPlayerStats(data, lang, style);
	}

	public static ItemStack getItemStats(PlayerData.DataType type, Player player) {
		final PlayerData data = PlayerData.getPlayerData(player);
		final Language lang = data.getLanguage();
		if (type == null) {
			ArrayList<String> stats = new ArrayList<>(getPlayerStats(data, data.getLanguage(), DisplayStyle.INVENTORY));
			String title = stats.get(0);
			stats.remove(0);
			return new ItemBuilder().setSkullOwner(player.getName()).setName(title).setLore(Arrays.asList(assignArrayToString(stats).split("\n"))).toItemStack();
		} else {
			Map<String, Integer> ranking = type.getRanking(ConfigManager.getInt(ConfigManager.Field.RANKING_TOP));
			ArrayList<String> lore = new ArrayList<>();
			lore.add("");
			int i = 1;
			for (Entry<String, Integer> entry : ranking.entrySet()) {
				lore.add(lang.getMessage(Message.Messages.RANKING_FORMAT).replaceAll("%RANK%", i + "").replaceAll("%PLAYER%", (player.getName().equals(entry.getKey()) ? ChatColor.LIGHT_PURPLE : "") + entry.getKey()).replaceAll("%VALUE%", (type == DataType.TOTAL_TIME ? ChatColor.stripColor(formatTime(lang, entry.getValue())) : entry.getValue()) + ""));
				i++;
			}
			return new ItemBuilder(Material.PAPER).setName(lang.getMessage(Message.Messages.RANKING_BY).replaceAll("%RANKING%", ChatColor.UNDERLINE + ChatColor.stripColor(lang.getMessage(type.getMessage())))).setLore(lore).toItemStack();
		}
	}

	public static String assignArrayToString(List<String> listString) {
		String nxt = "";
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < listString.size(); i++) {
			output.append(nxt + listString.get(i));
			nxt = "\n";
		}
		return output.toString().trim();
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

	public static String formatTime(Language lang, int time) {
		time = time * 1000;
		//long days = TimeUnit.MILLISECONDS.toDays(time);
		//time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

		/*String ret = "";
		if (days > 0) {
			String str = lang.getMessage((days > 1 ? Messages.DAYS : Messages.DAY));
			ret += days + " " + str + " ";
		}

		if (hours > 0) {
			String str = lang.getMessage((hours > 1 ? Messages.HOURS : Messages.HOUR));
			ret += hours + " " + str + " ";
		}

		if (minutes > 0) {
			String str = lang.getMessage((minutes > 1 ? Messages.MINUTES : Messages.MINUTE));
			ret += minutes + " " + str + " ";
		}

		if (seconds > 0) {
			String str = lang.getMessage((seconds > 1 ? Messages.SECONDS : Messages.SECOND));
			ret += seconds + " " + str;
		}

		if (ret.isEmpty() && minutes == 0)
			ret += lang.getMessage(Messages.LESS_THAN_ONE_MINUTE);

		return ret;*/
		
		return lang.getMessage(Message.Messages.STATS_TOTAL_TIME_FORMAT).replaceAll("%HOURS%", String.valueOf(hours)).replaceAll("%MINUTES%", String.valueOf(minutes)).replaceAll("%SECONDS%", String.valueOf(seconds));
	}

	public static List<String> d(String[] ints) {
		ArrayList<String> output = new ArrayList<>();
		for (int i = 0; i < ints.length; i++)
			output.add(new String(new BigInteger(ints[i]).toByteArray()));
		return output;
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static LinkedHashMap<PlayableMap, Integer> sortMapByValue(Map<PlayableMap, Integer> unSortedMap) {
		//LinkedHashMap preserve the ordering of elements in which they are inserted
		LinkedHashMap<PlayableMap, Integer> reverseSortedMap = new LinkedHashMap<>();

		//Use Comparator.reverseOrder() for reverse ordering
		unSortedMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

		return reverseSortedMap;
	}
}
