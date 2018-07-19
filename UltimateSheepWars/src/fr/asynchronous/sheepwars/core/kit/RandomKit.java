package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class RandomKit extends KitManager {

	public RandomKit() {
		super(9, MsgEnum.KIT_RANDOM_NAME, MsgEnum.KIT_RANDOM_DESCRIPTION, "", 0, 0, new ItemBuilder(Material.SKULL_ITEM).setSkullTexture("http://textures.minecraft.net/texture/797955462e4e576664499ac4a1c572f6143f19ad2d6194776198f8d136fdb2"));
	}

	@Override
	public boolean onEquip(Player player) {
		final PlayerData data = PlayerData.getPlayerData(player);
		if (data.getKits().isEmpty()) {
			data.setKit(new NoneKit());
		} else {
			KitManager kit = RandomUtils.getRandom(data.getKits());
			data.setKit(kit);
		}
		return data.getKit().onEquip(player);
	}
}
