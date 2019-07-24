package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class RandomKit extends SheepWarsKit {

	public RandomKit() {
		super(9, Messages.KIT_RANDOM_NAME, true, new ItemBuilder(Material.SKULL_ITEM).setSkullTexture("http://textures.minecraft.net/texture/797955462e4e576664499ac4a1c572f6143f19ad2d6194776198f8d136fdb2"), new RandomKitLevel());
	}

	public static class RandomKitLevel extends SheepWarsKitLevel {

		public RandomKitLevel() {
			super(Messages.KIT_RANDOM_DESCRIPTION, "", 0, 0);
		}

		@Override
		public boolean onEquip(Player player) {
			final PlayerData data = PlayerData.getPlayerData(player);
			data.setRandomKitSelection(true);
			SheepWarsKit kit;
			if (data.getKits().isEmpty()) {
				kit = new NoneKit();
				data.setKit(kit, 0);
			} else {
				kit = RandomUtils.getRandom(data.getKits());
				data.setKit(kit, data.getKitLevel(kit));
			}
			return kit.getLevel(data.getKitLevel()).onEquip(player);
		}
	}
}
