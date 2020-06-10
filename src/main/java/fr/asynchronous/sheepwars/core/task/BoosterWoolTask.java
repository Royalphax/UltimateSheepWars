package fr.asynchronous.sheepwars.core.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fr.asynchronous.sheepwars.core.legacy.LegacyMaterial;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class BoosterWoolTask extends BukkitRunnable {

	public static final String BOOSTER_METADATA = "sheepwars_booster_block";
	
	public final GameTask currentTask;
	
	public final int boosterInterval;
	public final int boosterLifeTime;
	private int maxWait = 0;
	private int time = 0;
	private Block magicBlock;
	private Location magicBlockLocation;
	private Material lastSavedMaterial;
	private Boolean firstTime = true;
	private List<DyeColor> colors = new ArrayList<>();
	
	public BoosterWoolTask(GameTask currentTask) {
		this.currentTask = currentTask;
		this.boosterInterval = ConfigManager.getInt(Field.BOOSTER_INTERVAL);
		this.boosterLifeTime = ConfigManager.getInt(Field.BOOSTER_LIFE_TIME);
		this.maxWait = this.boosterInterval + this.boosterLifeTime;
		this.time = this.boosterInterval - 1;
		for (SheepWarsBooster boost : SheepWarsBooster.getAvailableBoosters())
			this.colors.add(boost.getWoolColor());
		for (Location boosters : SheepWarsPlugin.getWorldManager().getVotedMap().getBoosterSpawns().getBukkitLocations())
			boosters.getBlock().setMetadata(BOOSTER_METADATA, new FixedMetadataValue(this.currentTask.plugin, true));
	}

	public void run() {
		this.currentTask.setBoosterCountdown(this.time);
		if (this.time <= 0 || this.time > this.boosterInterval) {
			if (this.time == 0) {
				this.time = this.maxWait;
				if (this.firstTime) {
					this.firstTime = false;
					Message.broadcast(Messages.BOOSTERS_MESSAGE);
				}
				this.magicBlockLocation = RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getBoosterSpawns().getBukkitLocations());
				this.magicBlock = this.magicBlockLocation.getBlock();
				this.lastSavedMaterial = this.magicBlock.getType();
				this.magicBlock.setType(LegacyMaterial.WOOL.getColoredMaterial(DyeColor.WHITE));
				Sounds.playSoundAll(null, Sounds.LEVEL_UP, 1f, 1f);
			}
			if (magicBlock.getType().toString().contains("WOOL")) {
				DyeColor rdmColor = RandomUtils.getRandom(this.colors);
				Material mat = LegacyMaterial.WOOL.getColoredMaterial(rdmColor);
				this.magicBlock.setType(mat);
				if (mat.toString().equals("WOOL")) {
					try {
						Method method = this.magicBlock.getClass().getDeclaredMethod("setData", byte.class);
						method.invoke(this.magicBlock, (byte) rdmColor.getWoolData());
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						ExceptionManager.register(e, true);
					}
				}
				//this.magicBlock.setData(rdmColor.getWoolData());
				SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.REDSTONE, this.magicBlockLocation, 1.0f, 1.0f, 1.0f, 20, 1.0f);
			}
		}
		if (this.time == this.boosterInterval || !GameState.isStep(GameState.INGAME)) {
			if (magicBlock != null && this.lastSavedMaterial != null)
				magicBlock.setType(this.lastSavedMaterial);
			if (!GameState.isStep(GameState.INGAME))
				this.cancel();
		}
		this.time--;
	}
}
