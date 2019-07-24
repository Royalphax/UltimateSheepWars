package fr.asynchronous.sheepwars.core.kit.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepLaunchEvent;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class ArmoredSheepKit extends SheepWarsKit {

	public ArmoredSheepKit() {
		super(0, Messages.KIT_ARMORED_SHEEP_NAME, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE), new ArmoredSheepKitLevel());
	}
	
	public static class ArmoredSheepKitLevel extends SheepWarsKitLevel {

		public ArmoredSheepKitLevel() {
			super(Messages.KIT_ARMORED_SHEEP_DESCRIPTION, "sheepwars.kit.armoredsheep", 10, 10);
		}

		@Override
		public boolean onEquip(Player player) {
			return true;
		}

		@EventHandler
		public void onSheepLaunch(final SheepLaunchEvent event) {
			final PlayerData data = PlayerData.getPlayerData(event.getLauncher());
			if (data.getKit().getId() == this.getKitId() && data.getKitLevel() == 0) {
				SheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(event.getEntity(), event.getSheep().getHealth() + (event.getSheep().getHealth() / 2.0));
				event.getEntity().setMetadata("armored_sheep", new FixedMetadataValue(getPlugin(), true));
			}
		}
	}
}
