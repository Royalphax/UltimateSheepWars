package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class MoreHealthKit extends KitManager {

	public static final Double HEARTS_MORE = 2.0;

	public MoreHealthKit() {
		super(6, MsgEnum.KIT_MORE_HEALTH_NAME, new ItemBuilder(Material.APPLE), new MoreHealthKitLevel());
	}

	public static class MoreHealthKitLevel extends KitLevel {

		public MoreHealthKitLevel() {
			super(MsgEnum.KIT_MORE_HEALTH_DESCRIPTION, "sheepwars.kit.morehealth", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(player, 20.0 + (HEARTS_MORE * 2.0));
			return true;
		}
	}
}
