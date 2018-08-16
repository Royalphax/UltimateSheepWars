package fr.asynchronous.sheepwars.core.manager;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import fr.asynchronous.sheepwars.core.util.ReflectionUtils;
import fr.asynchronous.sheepwars.core.version.AAnvilGUI;
import fr.asynchronous.sheepwars.core.version.ATitleUtils;
import fr.asynchronous.sheepwars.core.version.IBoosterDisplayer;
import fr.asynchronous.sheepwars.core.version.ICustomEntityType;
import fr.asynchronous.sheepwars.core.version.IEventHelper;
import fr.asynchronous.sheepwars.core.version.INMSUtils;
import fr.asynchronous.sheepwars.core.version.IParticleSpawner;
import fr.asynchronous.sheepwars.core.version.ISheepSpawner;
import fr.asynchronous.sheepwars.core.version.IWorldUtils;

public class VersionManager {
	
    private MinecraftVersion version;
    private Constructor<? extends AAnvilGUI> anvilGUIConstructor;
    private ATitleUtils ATitleUtils;
    private IBoosterDisplayer IBoosterDisplayer;
    private ICustomEntityType ICustomEntityType;
    private IEventHelper IEventHelper;
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
    	this.IBoosterDisplayer = loadModule("BoosterDisplayer");
    	this.ICustomEntityType = loadModule("CustomEntityType$GlobalMethods");
    	this.IEventHelper = loadModule("EventHelper");
    	this.INMSUtils = loadModule("NMSUtils");
    	this.IParticleSpawner = loadModule("ParticleSpawner");
    	this.ISheepSpawner = loadModule("SheepSpawner");
        this.IWorldUtils = loadModule("util.WorldUtils");
    	this.anvilGUIConstructor = (Constructor<AAnvilGUI>) ReflectionUtils.getConstructor(Class.forName(UltimateSheepWarsPlugin.PACKAGE + "." + version + ".AnvilGUI") , Player.class, UltimateSheepWarsPlugin.class, AAnvilGUI.AnvilClickEventHandler.class, String.class, String[].class);
        this.anvilGUIConstructor.setAccessible(true);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name) throws ReflectiveOperationException{
        return (T) ReflectionUtils.instantiateObject(Class.forName(UltimateSheepWarsPlugin.PACKAGE + "." + version.toString() + "." + name));
    }

    public AAnvilGUI newAnvilGUI(final Player player, final UltimateSheepWarsPlugin plugin, final AAnvilGUI.AnvilClickEventHandler handler, final String itemName, final String... itemLore){
        try {
            return anvilGUIConstructor.newInstance(player, plugin, handler, itemName, itemLore);
        } catch (ReflectiveOperationException ex) {
            new ExceptionManager(ex).register(true);
            return null;
        }
    }
    
    public ATitleUtils getTitleUtils()
    {
    	return this.ATitleUtils;
    }
    
    public IBoosterDisplayer getBoosterDisplayer()
    {
    	return this.IBoosterDisplayer;
    }
    
    public ICustomEntityType getCustomEntities()
    {
    	return this.ICustomEntityType;
    }
    
    public IEventHelper getEventHelper()
    {
    	return this.IEventHelper;
    }
    
    public INMSUtils getNMSUtils()
    {
    	return this.INMSUtils;
    }
    
    public IParticleSpawner getParticleFactory()
    {
    	return this.IParticleSpawner;
    }
    
    public ISheepSpawner getSheepFactory()
    {
    	return this.ISheepSpawner;
    }
    
    public IWorldUtils getWorldUtils()
    {
    	return this.IWorldUtils;
    }
    
    public MinecraftVersion getVersion()
    {
    	return this.version;
    }
}