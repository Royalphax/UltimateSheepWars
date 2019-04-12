package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitLevel;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class NoneKit extends SheepWarsKit {

	public NoneKit() {
		super(8, MsgEnum.KIT_NULL_NAME, true, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) DyeColor.RED.ordinal()), new NullKitLevel());
	}

	public static class NullKitLevel extends KitLevel {

		public NullKitLevel() {
			super(MsgEnum.KIT_NULL_DESCRIPTION, "", 0, 0);
		}

		@Override
		public boolean onEquip(Player player) {
			return true;
		}
	}
}
