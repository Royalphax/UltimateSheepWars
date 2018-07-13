package fr.asynchronous.sheepwars.core.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class BoosterWoolTask extends BukkitRunnable {

	public final GameTask currentTask;
	
	public final int boosterInterval;
	public final int boosterLifeTime;
	private int maxWait = 0;
	private int time = 0;
	private Block magicBlock;
	private Location magicBlockLocation;
	private Material lastSavedMaterial;
	private List<DyeColor> colors = new ArrayList<>();
	
	public BoosterWoolTask(GameTask currentTask) {
		this.currentTask = currentTask;
		this.boosterInterval = ConfigManager.getInt(Field.BOOSTER_INTERVAL);
		this.boosterLifeTime = ConfigManager.getInt(Field.BOOSTER_LIFE_TIME);
		this.maxWait = this.boosterInterval + this.boosterLifeTime;
		this.time = this.maxWait - this.boosterInterval;
		for (BoosterManager boost : BoosterManager.getAvailableBoosters())
			this.colors.add(boost.getDisplayColor().getColor());
		Message.broadcast(MsgEnum.BOOSTERS_MESSAGE);
	}

	public void run() {
		this.currentTask.setBoosterCountdown(this.time);
		if (this.time <= this.boosterLifeTime) {
			if (this.time == this.boosterLifeTime) {
				this.magicBlockLocation = ConfigManager.getRdmLocationFromList(Field.BOOSTERS);
				this.magicBlock = this.magicBlockLocation.getBlock();
				this.lastSavedMaterial = this.magicBlock.getType();
				this.magicBlock.setType(Material.WOOL);
				Sounds.playSoundAll(null, Sounds.LEVEL_UP, 1f, 1f);
			}
			if (magicBlock.getType() == Material.WOOL) {
				Wool wool = (Wool) magicBlock.getState().getData();
				wool.setColor(RandomUtils.getRandom(this.colors));
				magicBlock.getState().setData(wool);
				magicBlock.getState().update();
				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.REDSTONE, this.magicBlockLocation, 1.0f, 1.0f, 1.0f, 20, 1.0f);
			} else {
				this.time = 0;
			}
		}
		if (this.time <= 0 || !GameState.isStep(GameState.INGAME)) {
			magicBlock.setType(this.lastSavedMaterial);
			this.time = this.maxWait;
			if (!GameState.isStep(GameState.INGAME))
				this.cancel();
		}
		this.time--;
	}
}
