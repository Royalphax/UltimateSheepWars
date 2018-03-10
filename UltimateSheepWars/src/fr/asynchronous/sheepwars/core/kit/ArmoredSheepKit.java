package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class ArmoredSheepKit extends KitManager {

	public ArmoredSheepKit() {
		super(id, name, description, permission, price, requiredWins, icon);
	}

	@Override
	public boolean onEquip(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		// TODO Auto-generated method stub
		
	}

}
