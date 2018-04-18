package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class MoreSheepKit extends KitManager {

	public static final Integer CHANCE_TO_GET_ONE_MORE_SHEEP = 30;
	
	public MoreSheepKit() {
		super(0, MsgEnum.KIT_MORE_SHEEP_NAME, MsgEnum.KIT_MORE_SHEEP_DESCRIPTION, "sheepwars.kit.moresheep", 10, 10, new ItemBuilder(Material.WOOL));
	}

	@Override
	public boolean onEquip(Player player) {
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		
	}

}
