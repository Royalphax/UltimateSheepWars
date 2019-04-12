package fr.asynchronous.sheepwars.core.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public enum Permissions {

	USW_ADMIN(makeList("usw.admin", "sheepwars.admin")),
	USW_START(makeList("usw.startgame"), USW_ADMIN),
	USW_VIP(makeList("usw.vip", "sheepwars.vip"), USW_ADMIN),
	USW_BYPASS_LOGIN(makeList("usw.login.bypass", "sheepwars.login.bypass"), USW_ADMIN, USW_VIP),
	USW_BYPASS_TEAMS(makeList("usw.teams.bypass", "sheepwars.teams.bypass"), USW_ADMIN),
	USW_DEVELOPER(makeList("usw.developer"), USW_ADMIN),
	USW_GIVE_X(makeList("usw.give.*"), USW_ADMIN),
	USW_GIVE_SELF(makeList("usw.give.self"), USW_ADMIN, USW_DEVELOPER, USW_GIVE_X),
	USW_GIVE_OTHER(makeList("usw.give.other", "usw.give"), USW_ADMIN, USW_GIVE_X),
	USW_GIVE_ALL(makeList("usw.give.all"), USW_ADMIN, USW_GIVE_X);

	public List<String> permissions;
	public List<Permissions> parents;
	public List<String> allPermissions;

	private Permissions(List<String> permissions, Permissions... parents) {
		this.permissions = new ArrayList<>(permissions);
		this.parents = new ArrayList<>(makeList(parents));
		this.allPermissions = new ArrayList<>(permissions);
		for (Permissions permission : parents) {
			for (String perm : permission.permissions) {
				this.allPermissions.add(perm);
			}
		}
	}
	
	public List<String> getPermissions() {
		return this.permissions;
	}
	
	public List<Permissions> getParents() {
		return parents;
	}
	
	public List<String> getAllPermissions() {
		return allPermissions;
	}

	public boolean hasPermission(CommandSender sender) {
		return hasPermission(sender, false);
	}

	public boolean hasPermission(CommandSender sender, boolean warn) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			boolean output = false;
			for (String perm : this.allPermissions) {
				if (player.hasPermission(perm)) {
					output = true;
					break;
				}
			}
			if (!output && warn)
				warn(player);
			return output;
		} else {
			return true;
		}
	}

	public static void warn(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Sorry, but you don't have the permission to do that.");
	}

	@SafeVarargs
	private static <T> List<T> makeList(T... params) {
		List<T> output = new ArrayList<>();
		for (T param : params)
			output.add(param);
		return output;
	}
}
