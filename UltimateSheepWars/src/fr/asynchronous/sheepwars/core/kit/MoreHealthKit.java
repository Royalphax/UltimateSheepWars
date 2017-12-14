package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class MoreHealthKit extends KitManager {

	public MoreHealthKit() {
		super(1, MsgEnum.KIT_MORE_HEALTH_NAME, MsgEnum.KIT_MORE_HEALTH_DESCRIPTION, "sheepwars.kit.morehealth", 15.0, 5, new ItemBuilder(Material.APPLE));
	}

	@Override
	public boolean onEquip(Player player) {
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		// Do nothing
	}

}
