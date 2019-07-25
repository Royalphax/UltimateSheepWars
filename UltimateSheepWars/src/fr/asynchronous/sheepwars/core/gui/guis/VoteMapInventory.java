package fr.asynchronous.sheepwars.core.gui.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import net.md_5.bungee.api.ChatColor;

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
			setItem(new ItemBuilder(Material.PAPER).setName(playerData.getLanguage().getMessage(Messages.VOTE_MAP_NAME).replaceAll("%MAP_NAME%", map.getDisplayName())).setLore(playerData.getLanguage().getMessage(Messages.VOTE_MAP_LORE).replaceAll("%VOTE_COUNT%", String.valueOf(SheepWarsPlugin.getWorldManager().getVoteCount(map)))).toItemStack(), i);
			slotMap.put(i, map);
			i++;
		}
		
		setItem(new ItemBuilder(Material.SKULL_ITEM).setSkullTexture(RandomUtils.getRandom(urls)).setName(playerData.getLanguage().getMessage(Messages.VOTE_MAP_NAME).replaceAll("%MAP_NAME%", playerData.getLanguage().getMessage(Messages.RANDOM_MAP_NAME))).setLore(playerData.getLanguage().getMessage(Messages.VOTE_RANDOM_MAP_LORE).replaceAll("%VOTE_COUNT%", String.valueOf(SheepWarsPlugin.getWorldManager().getVoteCount(null)))).toItemStack(), i);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHICKEN_EGG_POP, 2.0f, 2.0f);
	}

	@Override
	public void onClose() {
		slotMap.clear();
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			PlayableMap map = null;
			if (slotMap.containsKey(event.getSlot())) {
				map = slotMap.get(event.getSlot());
			}
			Sounds.DIG_WOOD.playSound((Player) event.getWhoClicked(), 1f, 1.5f);
			event.getWhoClicked().closeInventory();
			if ((this.playerData.getVotedMap() == null && map != null) || (this.playerData.getVotedMap() != null && map == null) || (this.playerData.getVotedMap() != map)) {
				this.playerData.setVotedMap(map);
				for (Player online : Bukkit.getOnlinePlayers()) {
					SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(online, Message.getMessage(online, Messages.VOTE_ACTION_BAR_BROADCAST).replaceAll("%PLAYER%", this.player.getName()).replaceAll("%MAP_NAME%", ChatColor.stripColor(map == null ? Message.getMessage(online, Messages.RANDOM_MAP_NAME) : map.getDisplayName())));
				}
				Message.sendMessage(this.player, Messages.VOTE_SUCCESS, Arrays.asList("%MAP_NAME%", "%VOTE_COUNT%"), Arrays.asList(ChatColor.stripColor(map == null ? Message.getMessage(this.player, Messages.RANDOM_MAP_NAME) : map.getDisplayName()), String.valueOf(SheepWarsPlugin.getWorldManager().getVoteCount(map))));
			}
		}
		event.setCancelled(true);
	}
}
