package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.core.legacy.LegacyItem;
import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.SheepWarsKit;

public class NoneKit extends SheepWarsKit {

	public NoneKit() {
		super(8, Message.Messages.KIT_NULL_NAME, true, new ItemBuilder(new LegacyItem(LegacyMaterial.STAINED_GLASS_PANE, DyeColor.RED, 1, (byte) DyeColor.RED.ordinal())), new NullKitLevel());
	}

	public static class NullKitLevel extends SheepWarsKitLevel {

		public NullKitLevel() {
			super(Message.Messages.KIT_NULL_DESCRIPTION, "", 0, 0);
		}

		@Override
		public boolean onEquip(Player player) {
			return true;
		}
	}
}
