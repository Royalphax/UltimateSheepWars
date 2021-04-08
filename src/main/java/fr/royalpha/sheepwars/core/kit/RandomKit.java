package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.util.RandomUtils;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.SheepWarsKit;

public class RandomKit extends SheepWarsKit {

	public RandomKit() {
		super(9, Message.Messages.KIT_RANDOM_NAME, true, new ItemBuilder().setSkullTexture("http://textures.minecraft.net/texture/797955462e4e576664499ac4a1c572f6143f19ad2d6194776198f8d136fdb2"), new RandomKitLevel());
	}

	public static class RandomKitLevel extends SheepWarsKitLevel {

		public RandomKitLevel() {
			super(Message.Messages.KIT_RANDOM_DESCRIPTION, "", 0, 0);
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
