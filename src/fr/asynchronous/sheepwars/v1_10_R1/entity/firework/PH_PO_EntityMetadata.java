package fr.asynchronous.sheepwars.v1_10_R1.entity.firework;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class PH_PO_EntityMetadata extends aPacketHandler{
	
	public int entityId;
	public List<jDataWatcherItem<?>> metadata;
	
	private static Class<?> clazz;
	private static Field field_entityid;
	private static Field field_metadata;
	
	static{
		try{
			PH_PO_EntityMetadata.clazz=ProtocolUtils.getMinecraftClass("PacketPlayOutEntityMetadata");
			
			PH_PO_EntityMetadata.field_entityid=PH_PO_EntityMetadata.clazz.getDeclaredField("a");
			PH_PO_EntityMetadata.field_entityid.setAccessible(true);
			
			PH_PO_EntityMetadata.field_metadata=PH_PO_EntityMetadata.clazz.getDeclaredField("b");
			PH_PO_EntityMetadata.field_metadata.setAccessible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PH_PO_EntityMetadata(int entityId, List<jDataWatcherItem<?>> metadata){
		this.entityId=entityId;
		this.metadata=metadata;
	}
	
	/**
	 * Build
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Object build()throws Exception{
		Object packet=PH_PO_EntityMetadata.clazz.newInstance();
		
		PH_PO_EntityMetadata.field_entityid.set(packet, this.entityId);
		
		List list=new LinkedList<>();
		for(jDataWatcherItem<?> item:this.metadata)list.add(item.build());
		PH_PO_EntityMetadata.field_metadata.set(packet, list);
		
		return packet;
	}
}