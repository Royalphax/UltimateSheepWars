package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class BuilderKit extends SheepWarsKit {

	public BuilderKit() {
		super(3, Messages.KIT_BUILDER_NAME, new ItemBuilder(Material.BRICK), new BuilderKitLevel1(), new BuilderKitLevel2(), new BuilderKitLevel3());
	}

	public static class BuilderKitLevel1 extends SheepWarsKitLevel {

		public BuilderKitLevel1() {
			super(Messages.KIT_BUILDER_DESCRIPTION, "sheepwars.kit.builder1", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 5));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 5, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 5));
			return true;
		}
	}
	
	public static class BuilderKitLevel2 extends SheepWarsKitLevel {

		public BuilderKitLevel2() {
			super("&bx7 &7anvil\n&bx7 &7bricks\n&bx7 &7sand blocks", "sheepwars.kit.builder2", 15, 20);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 7));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 7, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 7));
			return true;
		}
	}
	
	public static class BuilderKitLevel3 extends SheepWarsKitLevel {

		public BuilderKitLevel3() {
			super("&bx9 &7anvil\n&bx9 &7bricks\n&bx9 &7sand blocks", "sheepwars.kit.builder3", 20, 30);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 9));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 9, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 9));
			return true;
		}
	}
}