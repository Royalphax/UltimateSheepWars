/**
 * 
 */
package fr.asynchronous.sheepwars.v1_8_R3.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.v1_8_R3.entity.EntityBossBar;

/**
 * @author Therence
 */
public class BossBar {

	private static Map<Player, EntityBossBar> playerEntityBossBar = new HashMap<>();

	private Message message;
	private float progress = 1.0f;

	public BossBar(Message message) {
		this.message = message;
	}

	public BossBar(Message message, float initialProgress) {
		this.message = message;
		this.progress = initialProgress;
	}

	public BossBar(List<Player> viewers, Message message, float initialProgress) {
		this.message = message;
		this.progress = initialProgress;
		for (Player player : viewers)
			addViewer(player);
	}

	public void setMessage(Message message) {
		this.message = message;
		for (EntityBossBar entityBossBar : playerEntityBossBar.values()) {
			entityBossBar.updateClientEntityName();
			entityBossBar.updateClientEntityLocation();
		}
	}

	public void setProgress(float progress) {
		this.progress = progress;
		for (EntityBossBar entityBossBar : playerEntityBossBar.values()) {
			entityBossBar.updateClientEntityHealth();
			entityBossBar.updateClientEntityLocation();
		}
	}

	public void addViewer(Player player) {
		if (!playerEntityBossBar.containsKey(player)) {
			EntityBossBar bossBar = new EntityBossBar(player, this);
			playerEntityBossBar.put(player, bossBar);
			bossBar.spawnClientEntity();
		}
	}
	
	public void removeViewer(Player player) {
		if (playerEntityBossBar.containsKey(player)) {
			playerEntityBossBar.get(player).destroyClientEntity();
			playerEntityBossBar.remove(player);
		}
	}
	
	public void show() {
		for (Player online : Bukkit.getOnlinePlayers())
			addViewer(online);
	}
	
	public void hide() {
		for (Player online : Bukkit.getOnlinePlayers())
			removeViewer(online);
	}

	public Message getMessage() {
		return message;
	}

	public float getProgress() {
		return progress;
	}

	public boolean isViewer(Player player) {
		return playerEntityBossBar.containsKey(player);
	}

	public static EntityBossBar getBossBarEntity(Player viewer) {
		return playerEntityBossBar.get(viewer);
	}

	public static Set<Player> getAllViewers() {
		return playerEntityBossBar.keySet();
	}

	public static Collection<EntityBossBar> getAllBossBarEntities() {
		return playerEntityBossBar.values();
	}
}
