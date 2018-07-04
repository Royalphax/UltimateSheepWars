package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class NoneKit extends KitManager {

	public NoneKit() {
		super(8, MsgEnum.KIT_NULL_NAME, MsgEnum.KIT_NULL_DESCRIPTION, "", 0, 0, new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.RED));
	}

	@Override
	public boolean onEquip(Player player) {
		return true;
	}
}
