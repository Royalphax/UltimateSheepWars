package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class BuilderKit extends SheepWarsKit {

	public BuilderKit() {
		super(3, Messages.KIT_BUILDER_NAME, new ItemBuilder(Material.BRICK), new BuilderKitLevel());
	}

	public static class BuilderKitLevel extends SheepWarsKitLevel {

		public BuilderKitLevel() {
			super(Messages.KIT_BUILDER_DESCRIPTION, "sheepwars.kit.builder", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 5));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 5, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 5));
			return true;
		}
	}
}