package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class MoreHealthKit extends SheepWarsKit {

	public static final int HEARTS_MORE = 2;

	public MoreHealthKit() {
		super(6, Messages.KIT_MORE_HEALTH_NAME, new ItemBuilder(Material.APPLE), new MoreHealthKitLevel());
	}

	public static class MoreHealthKitLevel extends SheepWarsKitLevel {

		public MoreHealthKitLevel() {
			super(Messages.KIT_MORE_HEALTH_DESCRIPTION, "sheepwars.kit.morehealth", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			SheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(player, 20.0 + (HEARTS_MORE * 2.0));
			return true;
		}
	}
}
