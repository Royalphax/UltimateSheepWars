package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.usw.SheepLaunchEvent;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class ArmoredSheepKit extends KitManager {

	public ArmoredSheepKit() {
		super(0, MsgEnum.KIT_ARMORED_SHEEP_NAME, MsgEnum.KIT_BETTER_BOW_DESCRIPTION, "sheepwars.kit.betterbow", 10, 10, new ItemBuilder(Material.BOW));
	}

	@Override
	public boolean onEquip(Player player) {
		return true;
	}


	@EventHandler
	public void onSheepLaunch(final SheepLaunchEvent event) {
		final PlayerData data = PlayerData.getPlayerData(event.getLauncher());
		if (data.getKit().getId() == this.getId()) {
			UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(event.getEntity(), ConfigManager.getDouble(Field.SHEEP_HEALTH) + 6.0);
			event.getEntity().setMetadata("armored_sheep", new FixedMetadataValue(getPlugin(), true));
		}
	}
}
