package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.core.legacy.LegacyItem;
import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.api.event.SheepGiveEvent;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.util.RandomUtils;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import fr.royalpha.sheepwars.api.SheepWarsKit;

public class MoreSheepKit extends SheepWarsKit {

	public static final Integer PERCENT_TO_GET_ONE_MORE_SHEEP = 15;

	public MoreSheepKit() {
		super(7, Message.Messages.KIT_MORE_SHEEP_NAME, new ItemBuilder(new LegacyItem(LegacyMaterial.WOOL, DyeColor.WHITE)), new MoreSheepKitLevel());
	}

	public static class MoreSheepKitLevel extends SheepWarsKitLevel {

		public MoreSheepKitLevel() {
			super(Message.Messages.KIT_MORE_SHEEP_DESCRIPTION, "sheepwars.kit.moresheep", 10, 10);
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
