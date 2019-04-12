package fr.asynchronous.sheepwars.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.DisplayStyle;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class Utils {

	private Utils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static boolean areSimilar(ItemStack i1, ItemStack i2) {
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
				output.add(data.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", color + "" + ChatColor.BOLD + data.getPlayer().getName()));
				output.add("");
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_GAME_PLAYED) + ": " + ChatColor.GRAY + data.getGames());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_DEATH) + ": " + ChatColor.GRAY + data.getDeaths());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_KILL) + ": " + ChatColor.GRAY + data.getKills());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_VICTORY) + ": " + ChatColor.GRAY + data.getWins());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_SHEEP_THROWN) + ": " + ChatColor.GRAY + data.getSheepThrown());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_SHEEP_KILLED) + ": " + ChatColor.GRAY + data.getSheepKilled());
				break;
				
			case CHAT :
				final String barChat = ChatColor.GRAY + "--------------------------------";
				output.add(barChat);
				output.add(data.getLanguage().getMessage(MsgEnum.RECORDS).replaceAll("%PLAYER%", color + "" + ChatColor.BOLD + data.getPlayer().getName()));
				output.add("");
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_GAME_PLAYED) + ": " + ChatColor.GRAY + data.getGames());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_DEATH) + ": " + ChatColor.GRAY + data.getDeaths());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_KILL) + ": " + ChatColor.GRAY + data.getKills());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_VICTORY) + ": " + ChatColor.GRAY + data.getWins());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_SHEEP_THROWN) + ": " + ChatColor.GRAY + data.getSheepThrown());
				output.add(ChatColor.WHITE + lang.getMessage(MsgEnum.STATS_SHEEP_KILLED) + ": " + ChatColor.GRAY + data.getSheepKilled());
				output.add(barChat);
				break;

			case INVENTORY :
				final String barInventory = ChatColor.YELLOW + "--------------------------------";
				output.add(ChatColor.GOLD + "Stats : " + Contributor.getPrefix(data.getPlayer()) + data.getName());
				output.add(barInventory);
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_GAME_PLAYED) + ": " + ChatColor.YELLOW + data.getGames());
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_DEATH) + ": " + ChatColor.YELLOW + data.getDeaths());
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_KILL) + ": " + ChatColor.YELLOW + data.getKills());
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_VICTORY) + ": " + ChatColor.YELLOW + data.getWins());
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_SHEEP_THROWN) + ": " + ChatColor.YELLOW + data.getSheepThrown());
				output.add(ChatColor.AQUA + lang.getMessage(MsgEnum.STATS_SHEEP_KILLED) + ": " + ChatColor.YELLOW + data.getSheepKilled());
				output.add(barInventory);
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(MsgEnum.STATS_WIN_RATE) + ": " + ChatColor.YELLOW + data.getWinRate() + ChatColor.GREEN + " %");
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(MsgEnum.STATS_KD_RATIO) + ": " + ChatColor.YELLOW + data.getKDRatio());
				output.add(ChatColor.LIGHT_PURPLE + lang.getMessage(MsgEnum.STATS_TOTAL_TIME) + ": " + ChatColor.YELLOW + formatTime(lang, data.getTotalTime()));
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
			return new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).setSkullOwner(player.getName()).setName(title).setLore(Arrays.asList(assignArrayToString(stats).split("\n"))).toItemStack();
		} else {
			Map<String, Integer> ranking = type.getRanking(ConfigManager.getInt(Field.RANKING_TOP));
			ArrayList<String> lore = new ArrayList<>();
			lore.add("");
			int i = 1;
			for (Entry<String, Integer> entry : ranking.entrySet()) {
				lore.add(lang.getMessage(MsgEnum.RANKING_FORMAT).replaceAll("%RANK%", i + "").replaceAll("%PLAYER%", (player.getName().equals(entry.getKey()) ? ChatColor.LIGHT_PURPLE : "") + entry.getKey()).replaceAll("%VALUE%", entry.getValue() + ""));
				i++;
			}
			return new ItemBuilder(Material.PAPER).setName(lang.getMessage(MsgEnum.RANKING_BY).replaceAll("%RANKING%", lang.getMessage(type.getMessage()))).setLore(lore).toItemStack();
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
		long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

		String ret = "";
		if (days > 0) {
			String str = lang.getMessage((days > 1 ? MsgEnum.DAYS : MsgEnum.DAY));
			ret += days + " " + str + " ";
		}

		if (hours > 0) {
			String str = lang.getMessage((days > 1 ? MsgEnum.HOURS : MsgEnum.HOUR));
			ret += hours + " " + str + " ";
		}

		if (minutes > 0) {
			String str = lang.getMessage((days > 1 ? MsgEnum.MINUTES : MsgEnum.MINUTE));
			ret += minutes + " " + str + " ";
		}

		if (seconds > 0) {
			String str = lang.getMessage((days > 1 ? MsgEnum.SECONDS : MsgEnum.SECOND));
			ret += seconds + " " + str;
		}

		if (ret.isEmpty() && minutes == 0)
			ret += lang.getMessage(MsgEnum.LESS_THAN_ONE_MINUTE);

		return ret;
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
}
