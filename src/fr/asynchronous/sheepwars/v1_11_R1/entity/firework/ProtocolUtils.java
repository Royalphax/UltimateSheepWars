package fr.asynchronous.sheepwars.v1_11_R1.entity.firework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProtocolUtils {
	
	public static final String version=org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
	private static Method refl_item_asNMSCopy;
	private static Method refl_player_getHandle;
	private static Field refl_player_playerConnection;
	private static Method refl_player_sendPacket;
	
	static{
		try{
			ProtocolUtils.refl_item_asNMSCopy=ProtocolUtils.getCraftbukkitClass("inventory.CraftItemStack")
					.getDeclaredMethod("asNMSCopy", new Class[]{ItemStack.class});
			ProtocolUtils.refl_item_asNMSCopy.setAccessible(true);
			
			Class<?> class_EntityPlayer=ProtocolUtils.getMinecraftClass("EntityPlayer");
			Class<?> class_CraftPlayer=ProtocolUtils.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> class_PlayerConnection=ProtocolUtils.getMinecraftClass("PlayerConnection");
			
			ProtocolUtils.refl_player_getHandle=class_CraftPlayer.getDeclaredMethod("getHandle");
			ProtocolUtils.refl_player_playerConnection=class_EntityPlayer.getDeclaredField("playerConnection");
			ProtocolUtils.refl_player_sendPacket=class_PlayerConnection.getDeclaredMethod("sendPacket", new Class[]{ProtocolUtils.getMinecraftClass("Packet")});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * GetMinecraftClass
	 */
	public static Class<?> getMinecraftClass(String classname)throws ClassNotFoundException{
		return Class.forName("net.minecraft.server."+ProtocolUtils.version+"."+classname);
	}
	
	/**
	 * GetCraftbukkitClass
	 */
	public static Class<?> getCraftbukkitClass(String classname)throws ClassNotFoundException{
		return Class.forName("org.bukkit.craftbukkit."+ProtocolUtils.version+"."+classname);
	}
	
	/**
	 * Reflection
	 */
	//Field_Get
	public static Object refl_fieldGet(String fieldname, String classname)throws Exception{
		Field field=ProtocolUtils.getMinecraftClass(classname).getDeclaredField(fieldname);
		field.setAccessible(true);
		return field.get(null);
	}
	public static Object refl_fieldGet0(String fieldname, String classname){
		try{
			return ProtocolUtils.refl_fieldGet(fieldname, classname);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	//Item_asNMSCopy
	public static Object refl_itemAsNMSCopy(ItemStack item)throws Exception{
		return ProtocolUtils.refl_item_asNMSCopy.invoke(null, item);
	}
	public static Object refl_itemAsNMSCopy0(ItemStack item){
		try{
			return ProtocolUtils.refl_itemAsNMSCopy(item);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	//SendPacket
	public static void refl_sendPacket(Player player, Object packet)throws Exception{
		ProtocolUtils.refl_player_sendPacket.invoke(ProtocolUtils.refl_player_playerConnection.get(ProtocolUtils.refl_player_getHandle.invoke(player)), packet);
	}
	public static boolean refl_sendPacket0(Player player, Object packet){
		try{
			ProtocolUtils.refl_sendPacket(player, packet);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}