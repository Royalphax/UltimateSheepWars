package fr.asynchronous.sheepwars.v1_9_R2.entity.firework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Optional;

public class jDataWatcherItem<T> implements iNmsObject{
	
	private static Class<?> clazz;
	private static Constructor<?> constructor;
	private static Method method_flag_set;
	
	static{
		try{
			jDataWatcherItem.clazz=ProtocolUtils.getMinecraftClass("DataWatcher$Item");
			
			jDataWatcherItem.constructor=jDataWatcherItem.clazz.getDeclaredConstructor(new Class[]{ProtocolUtils.getMinecraftClass("DataWatcherObject"), Object.class});
			jDataWatcherItem.constructor.setAccessible(true);
			
			jDataWatcherItem.method_flag_set=jDataWatcherItem.clazz.getDeclaredMethod("a", new Class[]{boolean.class});
			jDataWatcherItem.method_flag_set.setAccessible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private final jDataWatcherObject<T> watcherobject;
	private T value;
	private boolean flag;
	
	public jDataWatcherItem(jDataWatcherObject<T> watcherobject, T value){
		this.watcherobject=watcherobject;
		this.value=value;
		this.flag=true;
	}
	
	/**
	 * WatcherObject
	 */
	//Get
	public jDataWatcherObject<T> getWatcherObject(){
		return this.watcherobject;
	}
	
	/**
	 * Value
	 */
	//Set
	public void setValue(T value){
		this.value=value;
	}
	//Get
	public T getValue(){
		return this.value;
	}
	
	/**
	 * Flag
	 */
	//Get
	public boolean getFlag(){
		return this.flag;
	}
	//Set
	public void setFlag(boolean flag){
		this.flag=flag;
	}
	
	/**
	 * Build
	 */
	@Override
	public Object build()throws Exception{
		Object val=null;
		
		if(this.value instanceof ItemStack)val=Optional.of(ProtocolUtils.refl_itemAsNMSCopy((ItemStack)this.value));
		else val=this.value;
		
		Object item=jDataWatcherItem.constructor.newInstance(this.watcherobject.build(), val);
		jDataWatcherItem.method_flag_set.invoke(item, this.flag);
		
		return item;
	}
}