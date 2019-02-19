package fr.asynchronous.sheepwars.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import net.md_5.bungee.api.ChatColor;

public abstract class SubCommand {

	public static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "USW " + ChatColor.WHITE + "➢ " + ChatColor.RESET;

	private String[] aliases;
	private String shortDescription, longDescription, usage;
	private List<Permissions> permissions;
	private UltimateSheepWarsPlugin ultimateSheepWars;

	public SubCommand(String shortDescription, String longDescription, String usage, Permissions permission, UltimateSheepWarsPlugin ultimateSheepWars, String... aliases) {
		this.aliases = aliases;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permissions = new ArrayList<>();
		this.permissions.add(permission);
		this.usage = usage;
		this.ultimateSheepWars = ultimateSheepWars;
	}
	
	public SubCommand(String shortDescription, String longDescription, String usage, List<Permissions> permissions, UltimateSheepWarsPlugin ultimateSheepWars, String... aliases) {
		this.aliases = aliases;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permissions = permissions;
		this.usage = usage;
		this.ultimateSheepWars = ultimateSheepWars;
	}

	public boolean is(String arg) {
		return Arrays.asList(aliases).contains(arg.toLowerCase());
	}

	public String getUsage() {
		return usage;
	}

	public String getDescription(DescriptionType type) {
		switch (type) {
			case SHORT :
				return this.shortDescription;
			case LONG :
				return this.longDescription;
			case FULL :
				StringBuilder longDesc = new StringBuilder();
				final int maxLineSize = 30;
				int lineSize = 0;
				for (String strElement : this.longDescription.split(" ")) {
					if (strElement.length() + lineSize > maxLineSize) {
						longDesc.append("\n");
						lineSize = 0;
					}
					longDesc.append(strElement);
					lineSize += strElement.length();
				}
				return ChatColor.WHITE + "Description :\n∙ " + ChatColor.GRAY + longDesc.toString() + "\n\n" + ChatColor.WHITE + "Permission required :\n∙ " + ChatColor.GRAY + this.getPermissions().toString().replaceAll("[", "").replaceAll("]", "") + "\n\n" + ChatColor.WHITE + "Aliases :\n∙ " + ChatColor.GRAY + this.aliases.toString().replaceAll("[", "").replaceAll("]", "");
		}
		return "null";
	}

	public static enum DescriptionType {
		SHORT(),
		LONG(),
		FULL();
	}
	
	public List<Permissions> getPermissions() {
		return permissions;
	}

	protected abstract void onExePlayer(Player sender, String... args);

	protected abstract void onExeConsole(ConsoleCommandSender sender, String... args);

	protected void notAllowed(CommandSender commandSender) {
		if (commandSender instanceof Player) {
			commandSender.sendMessage(ChatColor.RED + "This command is not allowed from minecraft client.");
		} else {
			commandSender.sendMessage(ChatColor.RED + "This command is not allowed from console.");
		}
	}

	public UltimateSheepWarsPlugin getUltimateSheepWarsInstance() {
		return ultimateSheepWars;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> List<T> makeList(T... params) {
		List<T> output = new ArrayList<>();
		for (T param : params)
			output.add(param);
		return output;
	}
}
