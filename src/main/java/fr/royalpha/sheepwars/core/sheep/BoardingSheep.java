package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.core.handler.SheepAbility;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.api.SheepWarsSheep;

public class BoardingSheep extends SheepWarsSheep {
	public BoardingSheep() {
		super(Message.Messages.BOARDING_SHEEP_NAME, DyeColor.WHITE, -1, false, false, 0.25f, SheepAbility.CONTROLLABLE);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		bukkitSheep.setPassenger(player);
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		return (bukkitSheep.getPassenger() == null || bukkitSheep.isOnGround());
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean bool, Plugin plugin) {
		// Do nothing
	}
}
