package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class RandomKit extends KitManager {

	public RandomKit() {
		super(1, MsgEnum.KIT_RANDOM_NAME, MsgEnum.KIT_RANDOM_DESCRIPTION, "", 0, 0, new ItemBuilder(Material.SKULL_ITEM).setSkullTexture("http://textures.minecraft.net/texture/cc7d1b18398acd6e7e692a833a2217aea6b5a770f42c43513e4358cacd1b9c"));
	}

	@Override
	public boolean onEquip(Player player) {
		// Do nothing
		return false;
	}

	@Override
	public void onEvent(Player player, Event event, TriggerKitAction triggerAction) {
		// Do nothing
	}

}
