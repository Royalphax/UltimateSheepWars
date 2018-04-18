package fr.asynchronous.sheepwars.core.message;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.exception.StringIdAlreadyUsed;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
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
		output = output.replaceAll("[+.^:,%$@*Â§]", "");
		output = output.replaceAll("/", "");
		output = output.replaceAll("\\\\", "");
		output = output.trim();
		output = output.replaceAll(" ", "_");
		output = output.toLowerCase();
		return output;
	}
	
	public static void broadcast(MsgEnum message) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, "", message, "", null, "");
	}

	public static void broadcast(String prefix, MsgEnum message, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, prefix, message, suffix, null, "");
	}

	public static void broadcast(String prefix, MsgEnum message1, String between, MsgEnum message2, String suffix) {
		for (Player online : Bukkit.getOnlinePlayers())
			sendMessage(online, prefix, message1, between, message2, suffix);
	}
	
	public static void sendMessage(Player player, MsgEnum message) {
		sendMessage(player, "", message, "");
	}

	public static void sendMessage(Player player, String prefix, MsgEnum message, String suffix) {
		sendMessage(player, prefix, message, suffix, null, "");
	}

	public static void sendMessage(Player player, String prefix, MsgEnum message1, String between, MsgEnum message2, String suffix) {
		player.sendMessage(prefix + getMessage(player, message1) + between + getMessage(player, message2) + suffix);
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
		return (msg != null ? data.getLanguage().getMessage(msg) : "NullPointerException");
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
		
		JOIN_TITLE("Â§6UltimateSheepWarsÂ§8: Â§e%ONLINE_PLAYERS%Â§7/Â§e%MAX_PLAYERS%"),
		JOIN_SUBTITLE("Â§aChoose your team and kit"),
		BOARDING_TITLE("Â§5â˜ Â§f Away boarders Â§5â˜ "),
		BOARDING_SUBTITLE("Â§6One boarding sheep every minute !"),
		BOOSTERS_MESSAGE("Â§5Â§k||Â§a Bonus Wool Â§5Â§k||Â§6 Hit magical blocks !"),
		FINISH_EQUALITY(getDecoration() + ChatColor.AQUA + ChatColor.BOLD + " Equality " + getDecoration()),
		GAME_END_EQUALITY_DESCRIPTION("Â§aTime is up"),
		PLAYER_JOIN_MESSAGE("%PLAYER% Â§ahas joined the game !"),
		TEAM_JOIN_MESSAGE("You join the %TEAM% team"),
		STATS_WIN_RATE("Average Win Rate"),
		STATS_KD_RATIO("Kills/Deaths Ratio"),
		STATS_KILL("Enemies Killed"),
		STATS_TOTAL_TIME("Total Time"),
		STATS_TOTAL_TIME_FORMAT("Â§e%HOURS%, Â§e%MINUTES%, Â§e%SECONDS%"),
		STATS_SHEEP_THROWN("Sheep Thrown"),
		STATS_SHEEP_KILLED("Sheep Killed"),
		STATS_DEATH("Total Deaths"),
		STATS_VICTORY("Games won"),
		STATS_GAME_PLAYED("Games played"),
		RANKING_BY("Â§bRanking by %RANKING%"),
		RANKING_GOTO_RIGHT("Â§bÂ»"),
		RANKING_GOTO_LEFT("Â§bÂ«"),
		RANKING_FORMAT("Â§e%RANK%Â§7> Â§a%PLAYER% Â§7(Â§f%VALUE%Â§7)"),
		RANKING_KILL("Â§eKills"),
		RANKING_TOTAL_TIME("Â§eTotal Time"),
		RANKING_SHEEP_THROWN("Â§eSheep Thrown"),
		RANKING_SHEEP_KILLED("Â§eSheep Killed"),
		RANKING_DEATH("Â§eDeaths"),
		RANKING_VICTORY("Â§eVictories"),
		RANKING_GAME_PLAYED("Â§eGame Played"),
		STARTING_GAME("Â§6Â§lGame start in Â§eÂ§l%TIME%"),
		HOURS("hours"),
		HOUR("hour"),
		SECONDS("seconds"),
		SECOND("second"),
		MINUTES("minutes"),
		MINUTE("minute"),
		DAYS("days"),
		DAY("day"),
		LESS_THAN_ONE_MINUTE("less than one minute"),
		BLUE_NAME("Blue"),
		RED_NAME("Red"),
		SPEC_NAME("Spectator"),
		HUB_TELEPORTATION("Â§aTeleporting you to the Hub ..."),
		CONNECTION_FAILED("Â§cConnection failed."),
		SCOREBOARD_TEAM_RED("Â§cTeam Red Â§8(Â§e%SIZE%Â§8) "),
		SCOREBOARD_TEAM_BLUE("Â§9Team Blue Â§8(Â§e%SIZE%Â§8) "),
		RECORDS("%PLAYER%'s stats"),
		BOOSTER_ACTION("%PLAYER% activate %BOOSTER%"),
		BOOSTER_ARROW_KNOCKBACK("Â§7Â§lKnockback Arrows Â§e(Â§b10 Â§eseconds)"),
		BOOSTER_ARROW_FIRE("Â§6Â§lFire Arrows Â§e(Â§b15 Â§eseconds)"),
		BOOSTER_MORE_SHEEP("Â§bÂ§lMore Sheep Â§e(Â§b+1 Â§esheep!)"),
		BOOSTER_BLOCKING_SHEEP("Â§8Â§lBlocking Sheep Â§e(Â§b8 Â§eseconds)"),
		PLAYER_CANT_LAUNCH_SHEEP("Â§cYou can't launch sheep right now."),
		BOOSTER_NAUSEA("Â§aÂ§lNausea Â§e(Â§b10 Â§eseconds)"),
		BOOSTER_POISON("Â§2Â§lPoison Â§e(Â§b4 Â§eseconds)"),
		BOOSTER_REGENERATION("Â§dÂ§lRegeneration Â§e(Â§b6 Â§eseconds)"),
		BOOSTER_RESISTANCE("Â§fÂ§lResistance Â§e(Â§b30 Â§eseconds)"),
		DIED_MESSAGE("%VICTIM% Â§7died."),
		SLAYED_MESSAGE("%VICTIM% Â§7has been slayed by %KILLER%."),
		KIT_LAST_SELECTED("Â§7Â§oLast selected kit: Â§6%KIT%"),
		KIT_CHOOSE_MESSAGE("Â§7Â§oKit selected: Â§6%KIT%"),
		KITS_ITEM("Â§6Kits Â§7(Right-Click)"),
		KIT_BETTER_BOW_NAME("Â§eBetter bow"),
		KIT_MORE_HEALTH_NAME("Â§eMore health"),
		KIT_BETTER_SWORD_NAME("Â§eBetter sword"),
		KIT_MOBILITY_NAME("Â§eMobility"),
		KIT_BUILDER_NAME("Â§eBuilder"),
		KIT_DESTROYER_NAME("Â§eDestroyer"),
		KIT_AMORED_SHEEP_NAME("Â§eArmored sheep"),
		KIT_MORE_SHEEP_NAME("Â§eMore sheeps"),
		KIT_RANDOM_NAME("Â§eRandom"),
		KIT_NULL_NAME("Â§eNone"),
		KIT_MORE_SHEEP_DESCRIPTION("Â§7Gives chances to have\nÂ§7extra sheeps\nÂ§7\nÂ§b+15% Â§7to receive one more sheep"),
		KIT_BETTER_BOW_DESCRIPTION("Â§7Improves your bow\nÂ§7and give it critical & punch\nÂ§7\nÂ§b20%Â§7 chance to punch\nÂ§7Â§b10% Â§7chance to critical"),
		KIT_MORE_HEALTH_DESCRIPTION("Â§7Increases health by Â§b2 Â§câ�¤"),
		KIT_BETTER_SWORD_DESCRIPTION("Â§7Improves your sword\nÂ§7\nÂ§b5% Â§7chance to critical"),
		KIT_MOBILITY_DESCRIPTION("Â§7Improves your mobility\nÂ§7\nÂ§7Swiftness I\nÂ§7Feather falling I"),
		KIT_BUILDER_DESCRIPTION("Â§7Gives you an anvil, sand and bricks\nÂ§7\nÂ§bx1 Â§7anvil\nÂ§bx5 Â§7bricks\nÂ§bx5 Â§7sand blocks"),
		KIT_DESTROYER_DESCRIPTION("Â§7Gives you TNT and improve your bow\nÂ§7Right click to launch TNT !\nÂ§7\nÂ§bx3 Â§7TNT block\nÂ§7Â§b5% Â§7chance to put your arrows in fire"),
		KIT_AMORED_SHEEP_DESCRIPTION("Â§7Increases resistance and\nÂ§7health points of sheeps\nÂ§7\nÂ§b+150% Â§7health"),
		KIT_RANDOM_DESCRIPTION("Â§7This is random."),
		KIT_NULL_DESCRIPTION("Â§7Select no kit."),
		KIT_INVENTORY_NAME("Kit: %KIT%"),
		KIT_AVAILABLE("Â§aYou can use it !"),
		KIT_NOT_UNLOCKED_MESSAGE("Â§cYou don't have this kit."),
		KIT_BOUGHT("Â§aYou have successfully bought this kit !"),
		KIT_CANT_BUY("Â§aYou can't buy this kit."),
		KIT_LORE_BUY_IT("&7Price: &e%COST%\n&eRight-click &7to buy this kit."),
		KIT_LORE_TOO_EXPENSIVE("&7&mPrice: &e&m%COST%\n&cYou can't buy this kit.\n&cYou need &e%NEEDED% more."),
		KIT_LORE_NOT_PERMISSION("Â§cYou have not the permission\n&crequired to use this kit."),
		KIT_LORE_NEED_WINS("Â§cYou need &e%VICTORIES% &cvictory more\n&cto use this kit."),
		SHEEP_GET_DOWN("Sneak to exit the sheep"),
		BOARDING_SHEEP_NAME("Â§fBoarding sheep"),
		DARK_SHEEP_NAME("Â§8Dark sheep"),
		DISTORSION_SHEEP_NAME("Â§5Distorsion sheep"),
		EARTHQUAKE_SHEEP_NAME("Â§6Earth Quake sheep"),
		EXPLOSIVE_SHEEP_NAME("Â§cExplosive sheep"),
		FRAGMENTATION_SHEEP_NAME("Â§8Fragmentation sheep"),
		FROZEN_SHEEP_NAME("Â§bFrozen sheep"),
		HEALER_SHEEP_NAME("Â§dHealer sheep"),
		INCENDIARY_SHEEP_NAME("Â§6Incendiary sheep"),
		INTERGALACTIC_SHEEP_NAME("Â§1Â§lINTERGALACTIC SHEEP"),
		INTERGALACTIC_SHEEP_LAUNCHED("%PLAYER% launched %SHEEP%"),
		GLOWING_SHEEP_NAME("Â§7Glowing sheep"),
		LIGHTNING_SHEEP_NAME("Â§eThunder sheep"),
		REMOTE_SHEEP_NAME("Â§2Remote sheep"),
		SEEKER_SHEEP_NAME("Â§aSeeker sheep"),
		SWAP_SHEEP_NAME("Â§5Swap sheep"),
		SWAP_SHEEP_ACTION_NOPLAYER("Â§cNo player arround your Swap sheep"),
		SWAP_SHEEP_ACTION_TELEPORTATION("Â§bÂ§k|Â§aÂ§k|Â§7 You were swapped Â§bÂ§k|Â§aÂ§k|"),
		ELIMINATED("Â§fâ˜  Â§cÂ§lEliminatedÂ§f â˜ "),
		ALREADY_IN_THIS_TEAM("You're already in this team."),
		JOIN_RED_ITEM("Join red team"),
		JOIN_BLUE_ITEM("Join blue team"),
		GHOST_MESSAGE_1("Â§7You are now a Â§fGhostÂ§7, to leave, type Â§e/hub"),
		GHOST_MESSAGE_2("Â§7Only Â§fGhosts Â§7can hear your speak !"),
		SHEEP_RECEIVED("Â§7Received: %SHEEP_NAME%"),
		PRE_START_SUBTITLE("Prepare to fight !"),
		GAME_START_TITLE("Â§bThe game is starting !"),
		GAME_START_SUBTITLE("Â§6one sheep every Â§a%TIME% Â§6seconds"),
		GHOST_MESSAGE("You're a Â§fghost"),
		PARTICLES_ON("Particles Â» Â§aactivated"),
		PARTICLES_OFF("Particles Â» Â§cdeactivated"),
		CANT_JOIN_FULL_TEAM("Â§cUnable to join this team, too many players!"),
		PLAYERS_DEFICIT("Â§cThere's not enough players."),
		LEAVE_ITEM("Back to Hub"),
		OUT_OF_THE_GAME("Â§cReturn to the fighting area !"),
		GAME_DISPLAY_NAME("Â§6SheepWars"),
		SCOREBOARD_NEXT_SHEEP_COUNTDOWN("Â§aNext Sheep: Â§e%TIME%"),
		SCOREBOARD_NEXT_BOOSTER_COUNTDOWN("Â§aNext Booster: Â§e%TIME%"),
		ACTION_KILLS_STATS("Â§aKills: Â§e%KILLS%"),
		VICTORY("Team %WINNER% won"),
		CONGRATULATIONS("Congratulations"),
		GAME_END_TITLE("Game Over"),
		USER_DATA_LOADING("Â§2Data loading.."),
		USER_DATA_LOADED("Â§aData loaded!"),
		USER_DATA_UNREACHABLE("Â§cData unreachable."),
		DATABASE_NOT_CONNECTED("Â§cNo database connection.");
		
		private String msg;
		private MsgEnum(String msg) {
			this.msg = msg;
		}
		
		public String getMessage() {
			return this.msg;
		}
		
	}
}
