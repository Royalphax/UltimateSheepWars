package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitLevel;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class BuilderKit extends SheepWarsKit {

	public BuilderKit() {
		super(3, MsgEnum.KIT_BUILDER_NAME, new ItemBuilder(Material.BRICK), new BuilderKitLevel0(), new BuilderKitLevel1(), new BuilderKitLevel2());
	}

	public static class BuilderKitLevel0 extends KitLevel {

		public BuilderKitLevel0() {
			super(MsgEnum.KIT_BUILDER_DESCRIPTION, "sheepwars.kit.builder", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 5));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 5, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 5));
			return true;
		}
	}
	
	public static class BuilderKitLevel1 extends KitLevel {

		public BuilderKitLevel1() {
			super("&bx6 &7anvil\n&bx6 &7bricks\n&bx6 &7sand blocks", "sheepwars.kit.builder1", 11, 11);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 6));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 6, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 6));
			return true;
		}
	}
	
	public static class BuilderKitLevel2 extends KitLevel {

		public BuilderKitLevel2() {
			super("&bx7 &7anvil\n&bx7 &7bricks\n&bx7 &7sand blocks", "sheepwars.kit.builder2", 12, 12);
		}

		@Override
		public boolean onEquip(Player player) {
			player.getInventory().setItem(2, new ItemStack(Material.BRICK, 7));
			player.getInventory().setItem(3, new ItemStack(Material.SAND, 7, (short) 1));
			player.getInventory().setItem(4, new ItemStack(Material.ANVIL, 7));
			return true;
		}
	}
}