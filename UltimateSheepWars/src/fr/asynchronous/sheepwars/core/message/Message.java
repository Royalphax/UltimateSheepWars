package fr.asynchronous.sheepwars.core.message;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.exception.StringIdAlreadyUsed;
import fr.asynchronous.sheepwars.core.version.ATitleUtils.Type;

public class Message {

	private static List<Message> map = new ArrayList<>();

	private String strId;
	private String msg;

	public Message(MsgEnum msgEnum) {
		this(msgEnum.toString().toLowerCase().replaceAll("_", "-"), msgEnum.getMessage());
	}

	public Message(String messageId, String message) {
		this.strId = messageId;
		this.msg = message;

		if (getMessageByStringId(messageId) != null) {
			new StringIdAlreadyUsed("You can't register a message with same string ID as another.").printStackTrace();
		} else {
			map.add(this);
		}
	}

	public Message(String message) {
		this.strId = getRaw(message);
		this.msg = message;
	}

	public String getStringId() {
		return this.strId;
	}

	public String getMessage() {
		return this.msg;
	}

	public String getMessage(Player player) {
		PlayerData data = PlayerData.getPlayerData(player);
		return data.getLanguage().getMessage(this);
	}

	public String colorized() {
		return ChatColor.translateAlternateColorCodes('&', this.msg);
	}

	public String uncolorized() {
		return this.msg.replaceAll("§", "&");
	}

	private String getRaw(String input) {
		String output = Normalizer.normalize(input, Normalizer.Form.NFD);
		output = output.replaceAll("[^\\p{ASCII}]", "");
		output = output.replaceAll("[+.^:,%$@*&]", "");
		output = output.replaceAll("/", "");
		output = output.replaceAll("\\\\", "");
		output = output.trim();
		output = output.replaceAll(" ", "_");
		output = output.toLowerCase();
		return output;
	}

	public static void broadcast(MsgEnum message) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, "", message, "");
	}

	public static void broadcast(String prefix, MsgEnum message, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, prefix, message, suffix);
	}

	public static void sendMessage(Player player, MsgEnum message) {
		sendMessage(player, "", message, "");
	}

	public static void sendMessage(Player player, String prefix, MsgEnum message, String suffix) {
		player.sendMessage(prefix + getMessage(player, message) + suffix);
	}

	public static void broadcastTitle(MsgEnum title, MsgEnum subtitle) {
		broadcastTitle("", title, "", "", subtitle, "");
	}

	public static void broadcastTitle(String prefix1, MsgEnum title, String suffix1, String prefix2, MsgEnum subtitle, String suffix2) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendTitle(online, prefix1, title, suffix1, prefix2, subtitle, suffix2);
	}

	public static void sendTitle(Player player, String prefix1, MsgEnum title, String suffix1, String prefix2, MsgEnum subtitle, String suffix2) {
		UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().defaultTitle(Type.TITLE, player, prefix1 + getMessage(player, title) + suffix1, prefix2 + getMessage(player, subtitle) + suffix2);
	}

	public static void broadcastAction(MsgEnum action) {
		broadcastAction("", action, "");
	}

	public static void broadcastAction(String prefix, MsgEnum action, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendAction(online, prefix, action, suffix);
	}

	public static void sendAction(Player player, String prefix, MsgEnum action, String suffix) {
		UltimateSheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, prefix + getMessage(player, action) + suffix);
	}

	public static String getMessage(Player player, String prefix, MsgEnum msg, String suffix) {
		return prefix + getMessage(player, msg) + suffix;
	}

	public static String getMessage(Player player, MsgEnum msgEnum) {
		PlayerData data = PlayerData.getPlayerData(player);
		Message msg = getMessage(msgEnum);
		return (msg != null ? data.getLanguage().getMessage(msg) : "ERROR MESSAGE NOT LOAD - PLEASE CONTACT THE DEVELOPER");
	}

	public static Message getMessage(MsgEnum msgEnum) {
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
		for (MsgEnum msgEnum : MsgEnum.values())
			new Message(msgEnum);
	}

	public enum MsgEnum {

		JOIN_TITLE("&6UltimateSheepWars&8: &e%ONLINE_PLAYERS%&7/&e%MAX_PLAYERS%"),
		JOIN_SUBTITLE("&aChoose your team and kit"),
		BOARDING_TITLE("&5☠ &fAway boarders &5☠"),
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
		STATS_TOTAL_TIME_FORMAT("&e%HOURS%&a, &e%MINUTES%&a, &e%SECONDS%"),
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
		LESS_THAN_ONE_MINUTE("less than one minute"),
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
		KIT_LAST_SELECTED("&7&oLast selected kit: &6%KIT%"),
		KIT_CHOOSE_MESSAGE("&7&oKit selected: &6%KIT%"),
		KITS_ITEM("&6Kits & Stats &7(Right-Click)"),
		KIT_BETTER_BOW_NAME("&eBetter bow"),
		KIT_MORE_HEALTH_NAME("&eMore health"),
		KIT_BETTER_SWORD_NAME("&eBetter sword"),
		KIT_MOBILITY_NAME("&eMobility"),
		KIT_BUILDER_NAME("&eBuilder"),
		KIT_DESTROYER_NAME("&eDestroyer"),
		KIT_ARMORED_SHEEP_NAME("&eArmored sheep"),
		KIT_MORE_SHEEP_NAME("&eMore sheeps"),
		KIT_RANDOM_NAME("&eRandom"),
		KIT_NULL_NAME("&eNone"),
		KIT_MORE_SHEEP_DESCRIPTION("&7Gives chances to have\n&7extra sheeps\n&7\n&b+15% &7to receive one more sheep"),
		KIT_BETTER_BOW_DESCRIPTION("&7Improves your bow\n&7and give it critical & punch\n&7\n&b+20%&7 chance to punch\n&7&b+10% &7chance to critical"),
		KIT_MORE_HEALTH_DESCRIPTION("&7Increases health by &b2 &chearts"),
		KIT_BETTER_SWORD_DESCRIPTION("&7Improves your sword\n&7\n&b+5% &7chance to critical"),
		KIT_MOBILITY_DESCRIPTION("&7Improves your mobility\n&7\n&7Swiftness I\n&7Feather falling I"),
		KIT_BUILDER_DESCRIPTION("&7Gives you an anvil, sand and bricks\n&7\n&bx1 &7anvil\n&bx5 &7bricks\n&bx5 &7sand blocks"),
		KIT_DESTROYER_DESCRIPTION("&7Gives you TNT and improve your bow\n&7Right click to launch TNT !\n&7\n&bx3 &7TNT block\n&7&b+5% &7chance to put your arrows in fire"),
		KIT_ARMORED_SHEEP_DESCRIPTION("&7Increases resistance and\n&7health points of sheeps\n&7\n&b+150% &7health"),
		KIT_RANDOM_DESCRIPTION("&7This is random."),
		KIT_NULL_DESCRIPTION("&7Select no kit."),
		KIT_INVENTORY_NAME("Kit: %KIT%"),
		KIT_AVAILABLE("&aYou can use it !"),
		KIT_NOT_UNLOCKED_MESSAGE("&cYou don't have this kit."),
		KIT_BOUGHT("&aYou have successfully bought this kit !"),
		KIT_CANT_BUY("&aYou can't buy this kit."),
		KIT_LORE_BUY_IT("&7Price: &e%COST%\n&eRight-click &7to buy this kit."),
		KIT_LORE_TOO_EXPENSIVE("&7&mPrice: &e&m%COST%\n&cYou can't buy this kit.\n&cYou need &e%NEEDED% more."),
		KIT_LORE_NOT_PERMISSION("&cYou have not the permission\n&crequired to use this kit."),
		KIT_LORE_NEED_WINS("&cYou need &e%VICTORIES% &cvictory more\n&cto use this kit."),
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
		INTERGALACTIC_SHEEP_NAME("&1&lINTERGALACTIC SHEEP"),
		INTERGALACTIC_SHEEP_LAUNCHED("%PLAYER% launched %SHEEP%"),
		GLOWING_SHEEP_NAME("&7Glowing sheep"),
		LIGHTNING_SHEEP_NAME("&eThunder sheep"),
		SEEKER_SHEEP_NAME("&aSeeker sheep"),
		SWAP_SHEEP_NAME("&5Swap sheep"),
		SWAP_SHEEP_ACTION_NOPLAYER("&cNo player arround your Swap sheep"),
		SWAP_SHEEP_ACTION_TELEPORTATION("&b&k|&a&k|&7 You were swapped &b&k|&a&k|"),
		ELIMINATED("&f☠ &c&lEliminated&f ☠"),
		KILLED_MESSAGE("&7You were killed by &a%PLAYER%"),
		ALREADY_IN_THIS_TEAM("You're already in this team."),
		JOIN_RED_ITEM("&cJoin red team"),
		JOIN_BLUE_ITEM("&9Join blue team"),
		GHOST_DESCRIPTION("&7You are now a &fGhost&7, to leave, type &e/hub\n&7Only &fGhosts &7can hear your speak !"),
		SHEEP_RECEIVED("&7Received: %SHEEP_NAME%"),
		PRE_START_SUBTITLE("&bPrepare to fight !"),
		GAME_START_TITLE("&bThe game is starting !"),
		GAME_START_SUBTITLE("&6one sheep every &a%TIME% &6seconds"),
		GHOST_MESSAGE("&7You're a &fghost"),
		PARTICLES_ON("&6Particles » &aActivated &7(Right-Click)"),
		PARTICLES_OFF("&6Particles » &cDeactivated &7(Right-Click)"),
		CANT_JOIN_FULL_TEAM("&cUnable to join this team, too many players!"),
		PLAYERS_DEFICIT("&cThere's not enough players."),
		LEAVE_ITEM("&6Back to Hub &7(Right-Click)"),
		OUT_OF_THE_GAME("&cReturn to the fighting area !"),
		GAME_DISPLAY_NAME("&6SheepWars"),
		SCOREBOARD_NEXT_SHEEP_COUNTDOWN("&aNext Sheep: &e%TIME%"),
		SCOREBOARD_NEXT_BOOSTER_COUNTDOWN("&aNext Booster: &e%TIME%"),
		ACTION_KILLS_STATS("&aKills: &e%KILLS%"),
		VICTORY("Team %WINNER% won"),
		CONGRATULATIONS("Congratulations"),
		GAME_END_TITLE("Game Over"),
		USER_DATA_LOADING("&2Data loading.."),
		USER_DATA_LOADED("&aData loaded!"),
		USER_DATA_UNREACHABLE("&cData unreachable."),
		DATABASE_NOT_CONNECTED("&cNo database connection.");

		private String msg;
		private MsgEnum(String msg) {
			this.msg = msg;
		}

		public String getMessage() {
			return this.msg;
		}

	}
}
