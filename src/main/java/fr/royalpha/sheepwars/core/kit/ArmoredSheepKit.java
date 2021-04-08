package fr.royalpha.sheepwars.core.kit;

import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.event.SheepLaunchEvent;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.SheepWarsKit;

public class ArmoredSheepKit extends SheepWarsKit {

	public ArmoredSheepKit() {
		super(0, Message.Messages.KIT_ARMORED_SHEEP_NAME, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE), new ArmoredSheepKitLevel());
	}
	
	public static class ArmoredSheepKitLevel extends SheepWarsKitLevel {

		public ArmoredSheepKitLevel() {
			super(Message.Messages.KIT_ARMORED_SHEEP_DESCRIPTION, "sheepwars.kit.armoredsheep", 10, 10);
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
