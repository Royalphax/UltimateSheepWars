package fr.royalpha.sheepwars.core.message;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.core.exception.UIDException;
import fr.royalpha.sheepwars.core.version.ATitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;

public class Message {

	private static List<Message> map = new ArrayList<>();

	private String strId;
	private String msg;

	public Message(Messages msgEnum) {
		this(getRaw(msgEnum.toString()), msgEnum.getMessage());
	}

	private Message(String messageId, String message) {
		this.strId = messageId;
		this.msg = message;

		if (getMessageByStringId(messageId) != null) {
			new UIDException("You can't register two messages with the same string ID.").printStackTrace();
		} else {
			map.add(this);
		}
	}

	public Message(String message) {
		this(getRaw(message), message);
		//->Pas sur^
		//this.strId = getRaw(message);
		//this.msg = message;
	}

	public String getStringId() {
		return this.strId;
	}

	public String getMessage() {
		return this.msg;
	}
	
	public String getMessage(Language lang) {
		return lang.getMessage(this);
	}

	public String getMessage(Player player) {
		PlayerData data = PlayerData.getPlayerData(player);
		return data.getLanguage().getMessage(this);
	}

	public String colorized() {
		return ChatColor.translateAlternateColorCodes('&', this.msg);
	}

	public String uncolorized() {
		return this.msg.replaceAll("Â§", "&");
	}

	public static String getRaw(String input) {
		String output = Normalizer.normalize(input, Normalizer.Form.NFD);
		output = output.replaceAll("[^\\p{ASCII}]", "");
		output = output.replaceAll("[+.^:,%$@*&]", "");
		output = output.replaceAll("/", "");
		output = output.replaceAll("\\\\", "");
		output = output.trim();
		output = output.replaceAll(" ", "_");
		output = output.replaceAll("_", "-");
		output = output.toLowerCase();
		return output;
	}

	public static void broadcast(Messages message) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, "", message, "");
	}

	public static void broadcast(String prefix, Messages message, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, prefix, message, suffix);
	}

	public static void sendMessage(Player player, Messages message) {
		sendMessage(player, "", message, "");
	}

	public static void sendMessage(Player player, String prefix, Messages message, String suffix) {
		player.sendMessage(prefix + getMessage(player, message) + suffix);
	}
	
	public static void broadcast(Messages message, String regex, String replacement) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, message, regex, replacement);
	}
	
	public static void broadcast(Messages message, List<String> regex, List<String> replacement) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, message, regex, replacement);
	}
	
	public static void sendMessage(Player player, Messages message, String regex, String replacement) {
		sendMessage(player, message, Arrays.asList(regex), Arrays.asList(replacement));
	}
	
	public static void sendMessage(Player player, Messages message, List<String> regex, List<String> replacement) {
		String msg = getMessage(player, message);
		int size = (regex.size() > replacement.size() ? replacement.size() : regex.size());
		for (int i = 0; i < size; i++) {
			msg = msg.replaceAll(regex.get(i), replacement.get(i));
		}
		player.sendMessage(msg);
	}

	public static void broadcastTitle(Messages title, Messages subtitle) {
		broadcastTitle("", title, "", "", subtitle, "");
	}

	public static void broadcastTitle(String prefix1, Messages title, String suffix1, String prefix2, Messages subtitle, String suffix2) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendTitle(online, prefix1, title, suffix1, prefix2, subtitle, suffix2);
	}

	public static void sendTitle(Player player, String prefix1, Messages title, String suffix1, String prefix2, Messages subtitle, String suffix2) {
		SheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(ATitleUtils.Type.TITLE, player, prefix1 + getMessage(player, title) + suffix1, prefix2 + getMessage(player, subtitle) + suffix2);
	}

	public static void broadcastAction(Messages action) {
		broadcastAction("", action, "");
	}

	public static void broadcastAction(String prefix, Messages action, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendAction(online, prefix, action, suffix);
	}

	public static void sendAction(Player player, String prefix, Messages action, String suffix) {
		SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, prefix + getMessage(player, action) + suffix);
	}

	public static String getMessage(Player player, String prefix, Messages msg, String suffix) {
		return prefix + getMessage(player, msg) + suffix;
	}

	public static String getMessage(Player player, Messages msgEnum) {
		PlayerData data = PlayerData.getPlayerData(player);
		Message msg = getMessage(msgEnum);
		return (msg != null ? data.getLanguage().getMessage(msg) : "MESSAGE NOT LOADED");
	}

	public static Message getMessage(Messages msgEnum) {
		String strId = msgEnum.toString().toLowerCase().replaceAll("_", "-");
		return getMessageByStringId(strId);
	}

	private static Message getMessageByStringId(String strId) {
		for (Message msg : map)
			if (msg.getStringId().equals(strId))
				return msg;
		return null;
	}

	public static String getDecoration() {
		return ChatColor.YELLOW + "" + ChatColor.MAGIC + "|" + ChatColor.AQUA + "" + ChatColor.MAGIC + "|" + ChatColor.GREEN + "" + ChatColor.MAGIC + "|" + ChatColor.RED + "" + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "|" + ChatColor.RESET;
	}

	public static List<Message> getMessages() {
		return map;
	}

	public static void initMessages() {
		for (Messages msgEnum : Messages.values())
			new Message(msgEnum);
	}

	public enum Messages {

		JOIN_TITLE("&6UltimateSheepWars&8: &e%ONLINE_PLAYERS%&7/&e%MAX_PLAYERS%"),
		JOIN_SUBTITLE("&aChoose your team and kit"),
		BOARDING_TITLE("&5\u2620 &fAway boarders &5\u2620"),
		BOARDING_SUBTITLE("&6One boarding sheep every minute !"),
		BOOSTERS_MESSAGE("&5&k||&a Bonus Wool &5&k||&6 Hit magical blocks !"),
		FINISH_EQUALITY(getDecoration() + ChatColor.AQUA + ChatColor.BOLD + " Equality " + getDecoration()),
		GAME_END_EQUALITY_DESCRIPTION("&aTime is up!"),
		PLAYER_JOIN_MESSAGE("&e%PLAYER% &ahas joined the game !"),
		TEAM_JOIN_MESSAGE("&7You join the %TEAM% team"),
		STATS_WIN_RATE("&dAverage Win Rate"),
		STATS_KD_RATIO("&dKills/Deaths Ratio"),
		STATS_KILL("&bEnemies Killed"),
		STATS_TOTAL_TIME("&dTotal Time"),
		STATS_TOTAL_TIME_FORMAT("&e%HOURS%h&a, &e%MINUTES%m&a, &e%SECONDS%s"),
		STATS_SHEEP_THROWN("&bSheep Thrown"),
		STATS_SHEEP_KILLED("&bSheep Killed"),
		STATS_DEATH("&bTotal Deaths"),
		STATS_VICTORY("&bGames won"),
		STATS_GAME_PLAYED("&bGames played"),
		RANKING_BY("&bRanking by %RANKING%"),
		RANKING_FORMAT("&e%RANK%&7> &a%PLAYER% &7(&f%VALUE%&7)"),
		CLOSE_INVENTORY_ITEM("&cClose"),
		SWITCH_TO_RANKING_LORE("&b&l&n>>> Click to display rankings"),
		SWITCH_TO_KITS_SELECTION_LORE("&b&l&n>>> Click to display kits"),
		STARTING_GAME("&6&lGame starts in &e&l%TIME%"),
		HOURS("hours"),
		HOUR("hour"),
		SECONDS("seconds"),
		SECOND("second"),
		MINUTES("minutes"),
		MINUTE("minute"),
		DAYS("days"),
		DAY("day"),
		LESS_THAN_ONE_MINUTE("Less than one minute"),
		BLUE_NAME("&9Blue"),
		RED_NAME("&cRed"),
		SPEC_NAME("&7Spectator"),
		HUB_TELEPORTATION("&aTeleporting you to the Hub ..."),
		CONNECTION_FAILED("&cConnection failed."),
		SCOREBOARD_TEAM_RED("&cTeam Red &8(&e%SIZE%&8) "),
		SCOREBOARD_TEAM_BLUE("&9Team Blue &8(&e%SIZE%&8) "),
		RECORDS("%PLAYER%'s stats"),
		BOOSTER_ACTION("%PLAYER% activate %BOOSTER%"),
		BOOSTER_ARROW_KNOCKBACK("&7&lKnockback Arrows &e(&b15 &eseconds)"),
		BOOSTER_ARROW_FIRE("&6&lFire Arrows &e(&b15 &eseconds)"),
		BOOSTER_MORE_SHEEP("&b&lMore Sheep &e(&b+1 &esheep!)"),
		BOOSTER_BLOCKING_SHEEP("&8&lBlocking Sheep &e(&b8 &eseconds)"),
		PLAYER_CANT_LAUNCH_SHEEP("&cYou can't launch sheep right now."),
		BOOSTER_NAUSEA("&a&lNausea &e(&b10 &eseconds)"),
		BOOSTER_POISON("&2&lPoison &e(&b4 &eseconds)"),
		BOOSTER_REGENERATION("&d&lRegeneration &e(&b6 &eseconds)"),
		BOOSTER_RESISTANCE("&f&lResistance &e(&b30 &eseconds)"),
		DIED_MESSAGE("%VICTIM% &7died."),
		SLAYED_MESSAGE("%VICTIM% &7has been slayed by %KILLER%."),
		KIT("kit"),
		LEVEL("level"),
		KIT_LAST_SELECTED("&7&oLast selected kit: &6%KIT_NAME% &e%LEVEL_NAME%"),
		KIT_CHOOSE_MESSAGE("&7&oSelected kit: &6%KIT_NAME% &e%LEVEL_NAME%"),
		KITS_ITEM("&6Kits & Stats &7(Right-Click)"),
		VOTING_ITEM("&6Vote for a Map &7(Right-Click)"),
		MOST_WANTED_VOTE_MAP_NAME("&eVote for &a&l%MAP_NAME% &5&k|&d&lMOST WANTED&5&k|"),
		VOTE_MAP_NAME("&eVote for &a&l%MAP_NAME%"),
		VOTE_MAP_LORE("&7This map has &b%VOTE_COUNT% &7vote(s)"),
		VOTE_RANDOM_MAP_LORE("&b%VOTE_COUNT% &7player(s) didn't vote or want a random map"),
		VOTE_END("&a&lVoting has ended ! &eThe map &6%MAP_NAME% &ehas won."),
		VOTE_SUCCESS("&a&lVote received. &eYour map has now &6%VOTE_COUNT% &evotes."),
		VOTE_CLOSED("&cVotes are currently closed."),
		VOTE_ACTION_BAR_BROADCAST("&e%PLAYER% &7voted for &a%MAP_NAME% &7!"),
		VOTE_INVENTORY_NAME("&8Choose a map"),
		RANDOM_MAP_NAME("Random map"),
		KIT_BETTER_BOW_NAME("&6Better bow"),
		KIT_MORE_HEALTH_NAME("&6More health"),
		KIT_BETTER_SWORD_NAME("&6Better sword"),
		KIT_MOBILITY_NAME("&6Mobility"),
		KIT_BUILDER_NAME("&6Builder"),
		KIT_DESTROYER_NAME("&6Destroyer"),
		KIT_ARMORED_SHEEP_NAME("&6Armored sheep"),
		KIT_MORE_SHEEP_NAME("&6More sheeps"),
		KIT_ICON_NAME_FORMAT("%KIT_NAME% %LEVEL_NAME%"),
		KIT_RANDOM_NAME("&6Random"),
		KIT_NULL_NAME("&6None"),
		KIT_MORE_SHEEP_DESCRIPTION("&b+15% &7to receive one more sheep"),
		KIT_BETTER_BOW_DESCRIPTION("&b+20%&7 chance to punch\n&b+10% &7chance to critical"),
		KIT_MORE_HEALTH_DESCRIPTION("&7Increases health by &b2 &7hearts"),
		KIT_BETTER_SWORD_DESCRIPTION("&b+5% &7chance to critical"),
		KIT_MOBILITY_DESCRIPTION("&7Speed &bI\n&7Jump &bI\n&7Feather falling &bII"),
		KIT_BUILDER_DESCRIPTION("&bx5 &7anvil\n&bx5 &7bricks\n&bx5 &7sand blocks"),
		KIT_DESTROYER_DESCRIPTION("&bx2 &7TNT blocks\n&b+5% &7chance to throw fire arrows"),
		KIT_ARMORED_SHEEP_DESCRIPTION("&7Sheeps health increased by &b+50%"),
		KIT_RANDOM_DESCRIPTION("&7Choose a kit randomly from those you have."),
		KIT_NULL_DESCRIPTION("&7Do nothing."),
		KIT_INVENTORY_NAME("Kit: &6%KIT_NAME% &e%LEVEL_NAME%"),
		KIT_AVAILABLE("&aYou can use it !"),
		KIT_NOT_UNLOCKED_MESSAGE("&cYou don't have unlocked this %KIT_OR_LEVEL% yet."),
		KIT_BOUGHT("&aYou have successfully unlocked this %KIT_OR_LEVEL% !"),
		KIT_CANT_BUY("&aYou can't buy this %KIT_OR_LEVEL%."),
		KIT_PRICE("&7Price: &e%COST%"),
		KIT_REQUIRED_WINS("&7Required wins: &e%REQUIRED_WINS%"),
		KIT_PERMISSION("&7Permission: &e%PERMISSION%"),
		KIT_LORE_BUY_IT("&eRight-click &7to buy this %KIT_OR_LEVEL%"),
		KIT_LORE_WHEEL_CLICK_DISPLAY_LEVELS("&eWheel-click &7to display levels"),
		KIT_LORE_TOO_EXPENSIVE("&7&mPrice: &e&m%COST%\n&cYou can't buy this %KIT_OR_LEVEL%.\n&cYou need &e%NEEDED% &cmore."),
		KIT_LORE_NOT_PERMISSION("&cYou have not the permission\n&crequired to use this %KIT_OR_LEVEL%."),
		KIT_LORE_NEED_WINS("&cYou need &e%VICTORIES% &cvictory more\n&cto use this %KIT_OR_LEVEL%."),
		KIT_NEXT_LEVEL_INCLUDES("&aNext level includes :"),
		KIT_LEFT_CLICK_TO_SELECT("&eLeft-click &7to select this %KIT_OR_LEVEL%"),
		KIT_LEVEL_DEFAULT_NAME("&e%LEVEL_ID%"),
		RETURN_TO_KIT_INVENTORY("&cBack to Kits & Stats inventory"),
		SHEEP_GET_DOWN("Sneak to exit the sheep"),
		BOARDING_SHEEP_NAME("&fBoarding sheep"),
		DARK_SHEEP_NAME("&8Dark sheep"),
		DISTORSION_SHEEP_NAME("&5Distorsion sheep"),
		EARTHQUAKE_SHEEP_NAME("&6Earth Quake sheep"),
		EXPLOSIVE_SHEEP_NAME("&cExplosive sheep"),
		FRAGMENTATION_SHEEP_NAME("&8Fragmentation sheep"),
		FROZEN_SHEEP_NAME("&bFrozen sheep"),
		HEALER_SHEEP_NAME("&dHealer sheep"),
		INCENDIARY_SHEEP_NAME("&6Incendiary sheep"),
		GLUTTON_SHEEP_NAME("&2Glutton sheep"),
		INTERGALACTIC_SHEEP_NAME("&1&lINTERGALACTIC SHEEP"),
		INTERGALACTIC_SHEEP_LAUNCHED("%PLAYER% launched %SHEEP%"),
		GLOWING_SHEEP_NAME("&7Glowing sheep"),
		LIGHTNING_SHEEP_NAME("&eThunder sheep"),
		SEEKER_SHEEP_NAME("&aSeeker sheep"),
		SWAP_SHEEP_NAME("&5Swap sheep"),
		SWAP_SHEEP_ACTION_NOPLAYER("&cNo player arround your Swap sheep"),
		SWAP_SHEEP_ACTION_TELEPORTATION("&b&k|&a&k|&7 You were swapped &b&k|&a&k|"),
		ELIMINATED("&f\u2620 &c&lEliminated&f \u2620"),
		KILLED_MESSAGE("&7You were killed by &a%PLAYER%"),
		ALREADY_IN_THIS_TEAM("&7You're already in this team."),
		JOIN_RED_ITEM("&cJoin red team"),
		JOIN_BLUE_ITEM("&9Join blue team"),
		GHOST_DESCRIPTION("&7You are now a &fGhost&7, to leave, type &e/hub\n&7Only &fGhosts &7can hear your speak !"),
		SHEEP_RECEIVED("&7Received: %SHEEP_NAME%"),
		SEVERAL_SHEEP_RECEIVED("&7Received: %SHEEP_NAME% &e(x%AMOUNT%)"),
		PRE_START_COUNTDOWN_SUBTITLE("&6Prepare to fight !"),
		GAME_PRE_START_TITLE("&bTeleporting ..."),
		GAME_PRE_START_SUBTITLE("&6Don't forget to choose a &akit &6!"),
		GAME_START_TITLE("&bThe game is starting !"),
		GAME_START_SUBTITLE("&6One sheep every &a%TIME% &6seconds"),
		GHOST_MESSAGE("&7You're a &fghost"),
		PARTICLES_ON("&6Particles \u00bb &aActivated &7(Right-Click)"),
		PARTICLES_OFF("&6Particles \u00bb &cDeactivated &7(Right-Click)"),
		CANT_JOIN_FULL_TEAM("&cUnable to join this team, too many players!"),
		PLAYERS_DEFICIT("&cThere's not enough players."),
		LEAVE_ITEM("&6Back to Hub &7(Right-Click)"),
		OUT_OF_THE_GAME("&cReturn to the fighting area !"),
		SCOREBOARD_WAITING_TITLE("&8- &eSheepWars &8-"),
		SCOREBOARD_INGAME_TITLE("&8- &eSheepWars &a%MINUTES%:%SECONDS% &8-"),
		SCOREBOARD_NEXT_SHEEP_COUNTDOWN("&aNext Sheep: &e%TIME%"),
		SCOREBOARD_NEXT_BOOSTER_COUNTDOWN("&aNext Booster: &e%TIME%"),
		ACTION_KILLS_STATS("&aKills: &e%KILLS%"),
		VICTORY("Team %WINNER% won"),
		CONGRATULATIONS("Congratulations"),
		GAME_END_TITLE("Game Over"),
		USER_DATA_LOADING("&2Data loading ..."),
		USER_DATA_LOADED("&aData loaded!"),
		USER_DATA_UNREACHABLE("&cData unreachable."),
		DATABASE_NOT_CONNECTED("&cNo database connection."),
		PLAYER_NOT_CONNECTED("&cThere's no \"%PLAYER%\" on this server.");

		private String msg;
		private Messages(String msg) {
			this.msg = msg;
		}

		public String getMessage() {
			return this.msg;
		}

	}
}
