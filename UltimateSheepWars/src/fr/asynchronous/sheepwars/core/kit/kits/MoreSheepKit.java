package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepGiveEvent;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class MoreSheepKit extends SheepWarsKit {

	public static final Integer PERCENT_TO_GET_ONE_MORE_SHEEP = 15;

	public MoreSheepKit() {
		super(7, Messages.KIT_MORE_SHEEP_NAME, new ItemBuilder(Material.WOOL), new MoreSheepKitLevel());
	}

	public static class MoreSheepKitLevel extends SheepWarsKitLevel {

		public MoreSheepKitLevel() {
			super(Messages.KIT_MORE_SHEEP_DESCRIPTION, "sheepwars.kit.moresheep", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			return true;
		}

		@EventHandler
		public void onGiveSheep(SheepGiveEvent event) {
			final PlayerData data = PlayerData.getPlayerData(event.getPlayer());
			if (data.getKit().getId() == this.getKitId() && RandomUtils.getRandomByPercent(PERCENT_TO_GET_ONE_MORE_SHEEP)) {
				SheepWarsSheep.giveRandomSheep(event.getPlayer());
				Sounds.playSound(event.getPlayer(), Sounds.VILLAGER_YES, 1.0f, 1.5f);
			}
		}
	}
}
