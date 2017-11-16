package fr.asynchronous.sheepwars.v1_11_R1.entity.firework;

import java.lang.reflect.Field;

public class PH_PO_EntityDestroy extends aPacketHandler{
	
	public int[] entityIds;
	
	private static Class<?> clazz;
	private static Field field_entityIds;
	
	static{
		try{
			PH_PO_EntityDestroy.clazz=ProtocolUtils.getMinecraftClass("PacketPlayOutEntityDestroy");
			
			PH_PO_EntityDestroy.field_entityIds=PH_PO_EntityDestroy.clazz.getDeclaredField("a");
			PH_PO_EntityDestroy.field_entityIds.setAccessible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PH_PO_EntityDestroy(int...entityIds){
		this.entityIds=entityIds;
	}
	
	/**
	 * Build
	 */
	@Override
	public Object build()throws Exception{
		Object packet=PH_PO_EntityDestroy.clazz.newInstance();
		
		PH_PO_EntityDestroy.field_entityIds.set(packet, this.entityIds);
		
		return packet;
	}
}