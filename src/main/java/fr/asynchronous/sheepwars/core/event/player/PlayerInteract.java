package fr.asynchronous.sheepwars.core.event.player;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.gui.GuiManager;
import fr.asynchronous.sheepwars.core.gui.guis.VoteMapInventory;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.sheep.IntergalacticSheep;
import fr.asynchronous.sheepwars.core.util.MathUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerInteract extends UltimateSheepWarsEventListener {

	public PlayerInteract(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final PlayerData data = PlayerData.getPlayerData(player);

		if (event.getAction() == null || event.getPlayer() == null)
			return;

		if ((!GameState.isStep(GameState.INGAME) || data.isSpectator()) && !Permissions.USW_BUILDER.hasPermission(player))
			event.setCancelled(true);

		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().hasItemMeta() && !data.isSpectator()) {
			final ItemStack item = event.getItem();
			final Material mat = item.getType();

			if (GameState.isStep(GameState.INGAME) && mat.toString().contains("WOOL")) {
				
				SheepWarsSheep sheep = SheepWarsSheep.getCorrespondingSheep(item, player);
				int maxIntergalactic = ConfigManager.getInt(Field.MAX_INTERGALACTIC_SHEEPS);
				boolean isIntergalactic = (sheep.equals(new IntergalacticSheep()));
				if (sheep != null && !data.getTeam().isBlocked() && !player.isInsideVehicle() && (!isIntergalactic || maxIntergalactic <= 0 || isIntergalactic && IntergalacticSheep.IN_USE < maxIntergalactic)) {
					ItemStack newItem = item.clone();
					final int amount = item.getAmount() - 1;
					if (amount <= 0) {
						newItem = new ItemStack(Material.AIR);
					} else {
						newItem.setAmount(amount);
					}
					setItemInHand(newItem, player);
					boolean launch = sheep.throwSheep(player, this.plugin);
					if (launch) {
						data.increaseSheepThrown(1);
					} else {
						newItem.setAmount(amount + 1);
						setItemInHand(newItem, player);
					}
					player.updateInventory();
				} else {
					Sounds.playSound(player, Sounds.VILLAGER_NO, 1f, 1f);
					SheepWarsPlugin.getVersionManager().getTitleUtils().actionBarPacket(player, data.getLanguage().getMessage(Messages.PLAYER_CANT_LAUNCH_SHEEP));
				}
				event.setCancelled(true);

			} else if (GameState.isStep(GameState.WAITING)) {

				if (mat.equals(ConfigManager.getItemStack(Field.RETURN_TO_HUB_ITEM).getType())) {

					player.chat("/hub");
					event.setCancelled(true);

				} else if (mat.equals(ConfigManager.getItemStack(Field.VOTING_ITEM).getType())) {

					if (plugin.getWaitingTask() == null || plugin.getWaitingTask().getRemainingSeconds() > 10) {
						GuiManager.openGui(plugin, player, data.getLanguage().getMessage(Messages.VOTE_INVENTORY_NAME), new VoteMapInventory());
					} else {
						player.sendMessage(data.getLanguage().getMessage(Messages.VOTE_CLOSED));
					}
					event.setCancelled(true);

				} else if (mat.equals(ConfigManager.getItemStack(Field.PARTICLES_ON_ITEM).getType()) || mat.equals(ConfigManager.getItemStack(Field.PARTICLES_OFF_ITEM).getType())) {

					if (data.getAllowedParticles()) {
						data.setAllowParticles(false);
					} else {
						data.setAllowParticles(true);
						SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SPELL_INSTANT, player.getLocation().add(0, 1, 0), 1f, 0.5f, 1f, 10, 0.0f);
					}

					data.getLanguage().equipPlayer(player);

					Sounds.playSound(player, player.getLocation(), Sounds.NOTE_STICKS, 1f, 1f);
					event.setCancelled(true);

				} else if (Utils.areSimilar(item, SheepWarsTeam.RED.getIcon(player)) || Utils.areSimilar(item, SheepWarsTeam.BLUE.getIcon(player))) {
					for (SheepWarsTeam team : Arrays.asList(SheepWarsTeam.RED, SheepWarsTeam.BLUE)) {
						if (Utils.areSimilar(item, team.getIcon(player))) {
							final String displayName = team.getDisplayName(player);
							final SheepWarsTeam playerTeam = data.getTeam();
							if (playerTeam == team) {
								Message.sendMessage(player, Messages.ALREADY_IN_THIS_TEAM);
								break;
							}
							if (!Permissions.USW_BYPASS_TEAMS.hasPermission(player) && !Contributor.isImportant(player) && Bukkit.getOnlinePlayers().size() > 1 && team.getOnlinePlayers().size() >= MathUtils.ceil((Bukkit.getOnlinePlayers().size() / 2))) {
								Message.sendMessage(player, Messages.CANT_JOIN_FULL_TEAM);
								break;
							}
							data.setTeam(team);
							player.sendMessage(data.getLanguage().getMessage(Messages.TEAM_JOIN_MESSAGE).replaceAll("%TEAM%", team.getColor() + displayName));
							Sounds.playSound(player, player.getLocation(), Sounds.CLICK, 1f, 1f);
							break;
						}
					}
					player.updateInventory();
					event.setCancelled(true);
				}
			}

			// Le choix de Kit doit pouvoir s'effectuer meme si le jeu a commencé juste avant de lancer la partie.
			if (mat.equals(ConfigManager.getItemStack(Field.KIT_ITEM).getType())) {

				GuiManager.openKitsInventory(player, this.plugin);
				event.setCancelled(true);

			}
		}
	}

	public void setItemInHand(final ItemStack item, final Player player) {
		SheepWarsPlugin.getVersionManager().getNMSUtils().setItemInHand(item, player);
	}
}
