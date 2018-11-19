package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepGiveEvent;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class MoreSheepKit extends KitManager {

	public static final Integer PERCENT_TO_GET_ONE_MORE_SHEEP = 15;
	
	public MoreSheepKit() {
		super(7, MsgEnum.KIT_MORE_SHEEP_NAME, MsgEnum.KIT_MORE_SHEEP_DESCRIPTION, "sheepwars.kit.moresheep", 10, 10, new ItemBuilder(Material.WOOL));
	}

	@Override
	public boolean onEquip(Player player) {
		return true;
	}

	@EventHandler
	public void onGiveSheep(SheepGiveEvent event) {
		final PlayerData data = PlayerData.getPlayerData(event.getPlayer());
		if (data.getKit().getId() == this.getId() && RandomUtils.getRandomByPercent(PERCENT_TO_GET_ONE_MORE_SHEEP)) {
			SheepManager.giveRandomSheep(event.getPlayer());
			Sounds.playSound(event.getPlayer(), Sounds.VILLAGER_YES, 1.0f, 1.5f);
		}
	}
}
