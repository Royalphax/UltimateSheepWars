package fr.asynchronous.sheepwars.core.gui.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class VoteMapInventory extends GuiScreen {

	public Map<Integer, PlayableMap> slotMap = new HashMap<>();

	public VoteMapInventory() {
		super(2, (int) Math.ceil((PlayableMap.getReadyMaps().size() + 1) / 9.0), true);
	}

	@Override
	public void drawScreen() {
		List<String> urls = new ArrayList<>();
		urls.add("http://textures.minecraft.net/texture/797955462e4e576664499ac4a1c572f6143f19ad2d6194776198f8d136fdb2");
		urls.add("http://textures.minecraft.net/texture/5131de8e951fdd7b9a3d239d7cc3aa3e8655a336b999b9edbb4fb329cbd87");
		urls.add("http://textures.minecraft.net/texture/a4efb34417d95faa94f25769a21676a022d263346c8553eb5525658b34269");
		urls.add("http://textures.minecraft.net/texture/915f7c313bca9c2f958e68ab14ab393867d67503affff8f20cb13fbe917fd31");
		urls.add("http://textures.minecraft.net/texture/1c3cec68769fe9c971291edb7ef96a4e3b60462cfd5fb5baa1cbb3a71513e7b");

		int i = 0;
		for (PlayableMap map : PlayableMap.getReadyMaps()) {
			setItem(new ItemBuilder(Material.PAPER).setName(playerData.getLanguage().getMessage(MsgEnum.VOTE_MAP_NAME).replaceAll("%MAP_NAME%", map.getName())).setLore(playerData.getLanguage().getMessage(MsgEnum.VOTE_MAP_LORE).replaceAll("%VOTE_COUNT%", String.valueOf(SheepWarsPlugin.getWorldManager().getVoteCount(map)))).toItemStack(), i);
			slotMap.put(i, map);
			i++;
		}
		
		setItem(new ItemBuilder(Material.SKULL_ITEM).setSkullTexture(RandomUtils.getRandom(urls)).setName(playerData.getLanguage().getMessage(MsgEnum.VOTE_MAP_NAME).replaceAll("%MAP_NAME%", playerData.getLanguage().getMessage(MsgEnum.RANDOM_MAP_NAME))).setLore(playerData.getLanguage().getMessage(MsgEnum.VOTE_RANDOM_MAP_LORE).replaceAll("%VOTE_COUNT%", String.valueOf(SheepWarsPlugin.getWorldManager().getVoteCount(null)))).toItemStack(), i);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_OPEN, 2.0f, 0.0f);
	}

	@Override
	public void onClose() {
		slotMap.clear();
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_CLOSE, 2.0f, 0.0f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			if (slotMap.containsKey(event.getSlot())) {
				this.playerData.setVotedMap(slotMap.get(event.getSlot()));
			} else {
				this.playerData.setVotedMap(null);
			}
			Sounds.CLICK.playSound((Player) event.getWhoClicked(), 1f, 0.5f);
			event.getWhoClicked().closeInventory();
		}
		event.setCancelled(true);
	}
}
