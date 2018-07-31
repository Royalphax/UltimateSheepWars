package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class MoreHealthKit extends KitManager {

	public static final Double HEARTS_MORE = 2.0;
	
	public MoreHealthKit() {
		super(6, MsgEnum.KIT_MORE_HEALTH_NAME, MsgEnum.KIT_MORE_HEALTH_DESCRIPTION, "sheepwars.kit.morehealth", 15.0, 5, new ItemBuilder(Material.APPLE));
	}

	@Override
	public boolean onEquip(Player player) {
		player.setMaxHealth(20.0 + (HEARTS_MORE * 2.0));
		player.setHealthScaled(false);
		player.setHealth(20.0 + (HEARTS_MORE * 2.0));
		return true;
	}
}
