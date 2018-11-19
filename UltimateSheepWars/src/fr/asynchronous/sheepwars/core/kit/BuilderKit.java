package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class BuilderKit extends KitManager {

	public BuilderKit() {
		super(3, MsgEnum.KIT_BUILDER_NAME, MsgEnum.KIT_BUILDER_DESCRIPTION, "sheepwars.kit.builder", 10, 10, new ItemBuilder(Material.BRICK));
	}

	@Override
	public boolean onEquip(Player player) {
		player.getInventory().setItem(2, new ItemStack(Material.BRICK, 5));
		player.getInventory().setItem(3, new ItemStack(Material.SAND, 5, (short) 1));
		player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 5));
		return true;
	}
}