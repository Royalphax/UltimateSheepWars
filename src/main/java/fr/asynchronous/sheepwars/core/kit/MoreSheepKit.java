package fr.asynchronous.sheepwars.core.kit;

import fr.asynchronous.sheepwars.core.legacy.LegacyItem;
import fr.asynchronous.sheepwars.core.legacy.LegacyMaterial;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.api.event.SheepGiveEvent;
import fr.asynchronous.sheepwars.api.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.api.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class MoreSheepKit extends SheepWarsKit {

	public static final Integer PERCENT_TO_GET_ONE_MORE_SHEEP = 15;

	public MoreSheepKit() {
		super(7, Messages.KIT_MORE_SHEEP_NAME, new ItemBuilder(new LegacyItem(LegacyMaterial.WOOL, DyeColor.WHITE)), new MoreSheepKitLevel());
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
