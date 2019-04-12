package fr.asynchronous.sheepwars.core.version;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;

public class VersionManager {

	private MinecraftVersion version;
	private Constructor<? extends AAnvilGUI> anvilGUIConstructor;
	private ATitleUtils ATitleUtils;
	private IBoosterDisplayer IBoosterDisplayer;
	private ICustomEntityType ICustomEntityType;
	private INMSUtils INMSUtils;
	private IParticleSpawner IParticleSpawner;
	private ISheepSpawner ISheepSpawner;
	private IWorldUtils IWorldUtils;

	public VersionManager(MinecraftVersion version) throws ReflectiveOperationException {
		this.version = version;
		load();
	}

	@SuppressWarnings("unchecked")
	private void load() throws ReflectiveOperationException {
		this.ATitleUtils = loadModule("TitleUtils");
		this.ICustomEntityType = loadModule("CustomEntityType$GlobalMethods");
		this.INMSUtils = loadModule("NMSUtils");
		if (this.version.equals(MinecraftVersion.v1_8_R3)) {
			this.IParticleSpawner = loadModule("ParticleSpawner");
			this.IBoosterDisplayer = loadModule("BoosterDisplayer");
		} else {
			this.IParticleSpawner = loadModule("ParticleSpawner", MinecraftVersion.v1_9_R1);
			this.IBoosterDisplayer = loadModule("BoosterDisplayer", MinecraftVersion.v1_9_R1);
		}
		this.ISheepSpawner = loadModule("SheepSpawner");
		this.IWorldUtils = loadModule("util.WorldUtils");
		this.anvilGUIConstructor = (Constructor<AAnvilGUI>) ReflectionUtils.getConstructor(Class.forName(SheepWarsPlugin.PACKAGE + "." + version + ".AnvilGUI"), Player.class, SheepWarsPlugin.class, AAnvilGUI.AnvilClickEventHandler.class, String.class, String[].class);
		this.anvilGUIConstructor.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	private <T> T loadModule(String name) throws ReflectiveOperationException {
		return (T) loadModule(name, this.version);
	}

	@SuppressWarnings("unchecked")
	private <T> T loadModule(String name, MinecraftVersion version) throws ReflectiveOperationException {
		return (T) ReflectionUtils.instantiateObject(Class.forName(SheepWarsPlugin.PACKAGE + "." + version.toString() + "." + name));
	}

	public AAnvilGUI newAnvilGUI(final Player player, final SheepWarsPlugin plugin, final AAnvilGUI.AnvilClickEventHandler handler, final String itemName, final String... itemLore) {
		try {
			return anvilGUIConstructor.newInstance(player, plugin, handler, itemName, itemLore);
		} catch (ReflectiveOperationException ex) {
			new ExceptionManager(ex).register(true);
			return null;
		}
	}

	public ATitleUtils getTitleUtils() {
		return this.ATitleUtils;
	}

	public IBoosterDisplayer getBoosterDisplayer() {
		return this.IBoosterDisplayer;
	}

	public ICustomEntityType getCustomEntities() {
		return this.ICustomEntityType;
	}

	public INMSUtils getNMSUtils() {
		return this.INMSUtils;
	}

	public IParticleSpawner getParticleFactory() {
		return this.IParticleSpawner;
	}

	public ISheepSpawner getSheepFactory() {
		return this.ISheepSpawner;
	}

	public IWorldUtils getWorldUtils() {
		return this.IWorldUtils;
	}

	public MinecraftVersion getVersion() {
		return this.version;
	}
}