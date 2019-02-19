package fr.asynchronous.sheepwars.core.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand.DescriptionType;
import fr.asynchronous.sheepwars.core.command.commands.ContributorCommand;
import fr.asynchronous.sheepwars.core.command.commands.HubCommand;
import fr.asynchronous.sheepwars.core.command.commands.LangCommand;
import fr.asynchronous.sheepwars.core.command.commands.StatsCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.AddBoosterSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.AddTeamSpawnSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.CheckSetupSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.ClearBoostersSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.ClearTeamSpawnsSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.GiveSheepSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.GoToWorldSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.SetLobbySubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.ShowBoostersSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.ShowKitsSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.ShowSheepsSubCommand;
import fr.asynchronous.sheepwars.core.command.subcommands.StartGameSubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.URLManager;
import fr.asynchronous.sheepwars.core.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Command manager.
 *
 * @author Royalpha
 */
public class CommandManager implements CommandExecutor {

	/**
	 * List of the registered commands.
	 */
	private List<SubCommand> commands = new ArrayList<>();

	private UltimateSheepWarsPlugin ultimateSheepWars;

	public CommandManager(UltimateSheepWarsPlugin ultimateSheepWars) {
		this.ultimateSheepWars = ultimateSheepWars;
		final Server server = this.ultimateSheepWars.getServer();
		server.getPluginCommand("ultimatesheepwars").setExecutor(this);
		registerExternalCommands(server);
		registerCommands(ultimateSheepWars);
	}

	/**
	 * Registers a command.
	 *
	 * @param meCommand
	 *            The command to register.
	 */
	public void registerCommand(SubCommand meCommand) {
		commands.add(meCommand);
	}

	public void showHelp(CommandSender commandSender, int page) {
		final boolean isPlayer = commandSender instanceof Player;
		if (isPlayer) {
			commandSender.sendMessage("");
			commandSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "============" + ChatColor.RESET + " " + ChatColor.YELLOW + ChatColor.BOLD + "ULTIMATE SHEEP WARS " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============");
			commandSender.sendMessage(ChatColor.ITALIC + "Hover commands for more informations");
		} else {
			commandSender.sendMessage(ChatColor.RED + "UltimateSheepWars help menu (page " + page + "/" + getMaxPages() + ")");
		}
		int from = 1;
		if (page > 1)
			from = 8 * (page - 1) + 1;
		int to = 8 * page;
		for (int h = from; h <= to; h++) {
			if (h > commands.size())
				break;
			final SubCommand sub = commands.get(h - 1);
			final String line = sub.getUsage() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + sub.getDescription(DescriptionType.SHORT);
			if (isPlayer) {
				TextComponent lineComponent = new TextComponent(TextComponent.fromLegacyText(line));
				lineComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(sub.getDescription(DescriptionType.FULL))));
				final Player player = (Player) commandSender;
				player.spigot().sendMessage(lineComponent);
			} else {
				commandSender.sendMessage(line);
			}
		}
		if (isPlayer)
			commandSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "============" + ChatColor.RESET + " " + ChatColor.YELLOW + ChatColor.BOLD + "PAGE " + ChatColor.WHITE + ChatColor.BOLD + page + ChatColor.YELLOW + ChatColor.BOLD + " ON " + ChatColor.WHITE + ChatColor.BOLD + getMaxPages() + ChatColor.GRAY + "(/usw <page>) " + ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============");
	}

	public void showCredits(CommandSender commandSender) {
		commandSender.sendMessage("∙ " + ChatColor.GRAY + "Plugin " + ChatColor.GREEN + "UltimateSheepWars v" + (URLManager.isUpToDate() ? "" : ChatColor.RED) + this.ultimateSheepWars.getDescription().getVersion() + ChatColor.GRAY + " by " + ChatColor.GREEN + "The Asynchronous" + ChatColor.GRAY + ".");

		if (commandSender instanceof Player) {
			final Player player = (Player) commandSender;
			TextComponent message1 = new TextComponent(TextComponent.fromLegacyText("∙ " + ChatColor.GRAY + "Special thanks to all the following contributors : "));
			TextComponent hover = new TextComponent("(hover)");
			hover.setColor(net.md_5.bungee.api.ChatColor.GREEN);
			hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "- @Royalpha (" + ChatColor.RED + "Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @KingRider26 (" + ChatColor.RED + "Co-Developer" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @6985jjorda (" + ChatColor.GOLD + "English Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @felibouille (" + ChatColor.GOLD + "German Translation" + ChatColor.GRAY + ")\n" + ChatColor.GRAY + "- @jeussa (" + ChatColor.YELLOW + "Instant Explosion Firework Effect" + ChatColor.GRAY + ")")));
			message1.addExtra(hover);
			player.spigot().sendMessage(message1);

			TextComponent message2 = new TextComponent(TextComponent.fromLegacyText("∙ " + ChatColor.GRAY + "If you encounter any issue, come and talk to us : "));
			TextComponent click = new TextComponent("(click)");
			click.setColor(net.md_5.bungee.api.ChatColor.AQUA);
			click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/nZthcPh"));
			message2.addExtra(click);
			player.spigot().sendMessage(message2);
		}

		commandSender.sendMessage("∙ " + ChatColor.GRAY + "Use " + ChatColor.WHITE + "/usw 1 " + ChatColor.GRAY + "to see the first page of all available commands.");
	}

	/**
	 * Gets the max amount of pages.
	 *
	 * @return the maximum amount of pages.
	 */
	private int getMaxPages() {
		int max = 8;
		int i = commands.size();
		if (i % max == 0)
			return i / max;
		double j = i / 8;
		int h = (int) Math.floor(j * 100) / 100;
		return h + 1;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			return false;
		}

		if (arguments == null || arguments.length == 0) {
			showCredits(sender);
			return true;
		}

		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("help")) {
				showHelp(sender, 1);
				return true;
			}
			if (Utils.isInteger(arguments[0])) {
				showHelp(sender, Math.max(1, Math.min(Integer.parseInt(arguments[0]), getMaxPages())));
				return true;
			}
		}

		for (SubCommand comm : commands) {
			if (comm.is(arguments[0])) {

				Iterator<Permissions> it = comm.getPermissions().iterator();
				while (true) {
					if (it.hasNext()) {
						Permissions perm = it.next();
						if (perm.hasPermission(sender))
							break;
					} else {
						Permissions.warn(sender);
						return true;
					}
				}

				if (sender instanceof Player) {
					comm.onExePlayer((Player) sender, arguments);
				} else {
					comm.onExeConsole((ConsoleCommandSender) sender, arguments);
				}
				return true;
			}
		}
		return true;
	}

	public List<SubCommand> getCommands() {
		return commands;
	}

	private void registerCommands(UltimateSheepWarsPlugin ultimateSheepWars) {
		registerCommand(new SetLobbySubCommand(ultimateSheepWars));
		registerCommand(new AddTeamSpawnSubCommand(ultimateSheepWars));
		registerCommand(new ClearTeamSpawnsSubCommand(ultimateSheepWars));
		registerCommand(new AddBoosterSubCommand(ultimateSheepWars));
		registerCommand(new ClearBoostersSubCommand(ultimateSheepWars));
		registerCommand(new GoToWorldSubCommand(ultimateSheepWars));
		registerCommand(new CheckSetupSubCommand(ultimateSheepWars));
		registerCommand(new ShowBoostersSubCommand(ultimateSheepWars));
		registerCommand(new ShowKitsSubCommand(ultimateSheepWars));
		registerCommand(new ShowSheepsSubCommand(ultimateSheepWars));
		registerCommand(new GiveSheepSubCommand(ultimateSheepWars));
		registerCommand(new StartGameSubCommand(ultimateSheepWars));
	}

	private void registerExternalCommands(Server server) {
		server.getPluginCommand("lang").setExecutor(new LangCommand());
		server.getPluginCommand("stats").setExecutor(new StatsCommand());
		server.getPluginCommand("contributor").setExecutor(new ContributorCommand(this.ultimateSheepWars));
		if (ConfigManager.getBoolean(Field.ENABLE_HUB_COMMAND))
			server.getPluginCommand("hub").setExecutor(new HubCommand(this.ultimateSheepWars));
	}
}
