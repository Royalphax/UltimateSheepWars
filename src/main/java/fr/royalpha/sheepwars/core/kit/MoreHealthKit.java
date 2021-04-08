package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.SheepWarsKit;

public class MoreHealthKit extends SheepWarsKit {

	public static final int HEARTS_MORE = 2;

	public MoreHealthKit() {
		super(6, Message.Messages.KIT_MORE_HEALTH_NAME, new ItemBuilder(Material.APPLE), new MoreHealthKitLevel());
	}

	public static class MoreHealthKitLevel extends SheepWarsKitLevel {

		public MoreHealthKitLevel() {
			super(Message.Messages.KIT_MORE_HEALTH_DESCRIPTION, "sheepwars.kit.morehealth", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			SheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(player, 20.0 + (HEARTS_MORE * 2.0));
			return true;
		}
	}
}
