package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class MoreSheepKit extends KitManager {

	public static final Integer CHANCE_TO_GET_ONE_MORE_SHEEP = 30;
	
	public MoreSheepKit() {
		super(id, name, description, permission, price, requiredWins, icon);
	}

	@Override
	public boolean onEquip(Player player) {
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		
	}

}
