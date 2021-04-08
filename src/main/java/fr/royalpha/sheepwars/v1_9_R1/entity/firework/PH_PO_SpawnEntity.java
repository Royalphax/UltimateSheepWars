package fr.royalpha.sheepwars.v1_9_R1.entity.firework;

import java.lang.reflect.Field;
import java.util.UUID;

public class PH_PO_SpawnEntity extends aPacketHandler{

	public int entityId;
	public UUID entityUuid;
	public double x;
	public double y;
	public double z;
	public int velocityX;
	public int velocityY;
	public int velocityZ;
	public float pitch;
	public float yaw;
	public int entityTypeId;
	public int entityDataId;
	
	private static Class<?> clazz;
	private static Field field_entityId;
	private static Field field_entityUuid;
	private static Field field_x;
	private static Field field_y;
	private static Field field_z;
	private static Field field_velocityX;
	private static Field field_velocityY;
	private static Field field_velocityZ;
	private static Field field_pitch;
	private static Field field_yaw;
	private static Field field_entityTypeId;
	private static Field field_entityDataId;
	
	static{
		try{
			PH_PO_SpawnEntity.clazz=ProtocolUtils.getMinecraftClass("PacketPlayOutSpawnEntity");
			
			PH_PO_SpawnEntity.field_entityId=PH_PO_SpawnEntity.clazz.getDeclaredField("a");
			PH_PO_SpawnEntity.field_entityId.setAccessible(true);
			
			PH_PO_SpawnEntity.field_entityUuid=PH_PO_SpawnEntity.clazz.getDeclaredField("b");
			PH_PO_SpawnEntity.field_entityUuid.setAccessible(true);
			
			PH_PO_SpawnEntity.field_x=PH_PO_SpawnEntity.clazz.getDeclaredField("c");
			PH_PO_SpawnEntity.field_x.setAccessible(true);
			
			PH_PO_SpawnEntity.field_y=PH_PO_SpawnEntity.clazz.getDeclaredField("d");
			PH_PO_SpawnEntity.field_y.setAccessible(true);
			
			PH_PO_SpawnEntity.field_z=PH_PO_SpawnEntity.clazz.getDeclaredField("e");
			PH_PO_SpawnEntity.field_z.setAccessible(true);
			
			PH_PO_SpawnEntity.field_velocityX=PH_PO_SpawnEntity.clazz.getDeclaredField("f");
			PH_PO_SpawnEntity.field_velocityX.setAccessible(true);
			
			PH_PO_SpawnEntity.field_velocityY=PH_PO_SpawnEntity.clazz.getDeclaredField("g");
			PH_PO_SpawnEntity.field_velocityY.setAccessible(true);
			
			PH_PO_SpawnEntity.field_velocityZ=PH_PO_SpawnEntity.clazz.getDeclaredField("h");
			PH_PO_SpawnEntity.field_velocityZ.setAccessible(true);
			
			PH_PO_SpawnEntity.field_pitch=PH_PO_SpawnEntity.clazz.getDeclaredField("i");
			PH_PO_SpawnEntity.field_pitch.setAccessible(true);
			
			PH_PO_SpawnEntity.field_yaw=PH_PO_SpawnEntity.clazz.getDeclaredField("j");
			PH_PO_SpawnEntity.field_yaw.setAccessible(true);
			
			PH_PO_SpawnEntity.field_entityTypeId=PH_PO_SpawnEntity.clazz.getDeclaredField("k");
			PH_PO_SpawnEntity.field_entityTypeId.setAccessible(true);
			
			PH_PO_SpawnEntity.field_entityDataId=PH_PO_SpawnEntity.clazz.getDeclaredField("l");
			PH_PO_SpawnEntity.field_entityDataId.setAccessible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public PH_PO_SpawnEntity(int entityId, UUID entityUuid, int entityTypeId, int entityDataId, double x, double y, double z, float yaw, float pitch,
			int velocityX, int velocityY, int velocityZ){
		this.entityId=entityId;
		this.entityUuid=entityUuid;
		this.x=x;
		this.y=y;
		this.z=z;
		this.velocityX=velocityX;
		this.velocityY=velocityY;
		this.velocityZ=velocityZ;
		this.pitch=pitch;
		this.yaw=yaw;
		this.entityTypeId=entityTypeId;
		this.entityDataId=entityDataId;
	}
	
	/**
	 * Build
	 */
	@Override
	public Object build()throws Exception{
		Object packet=PH_PO_SpawnEntity.clazz.newInstance();
		
		PH_PO_SpawnEntity.field_entityId.set(packet, this.entityId);
		PH_PO_SpawnEntity.field_entityUuid.set(packet, this.entityUuid);
		PH_PO_SpawnEntity.field_x.set(packet, this.x);
		PH_PO_SpawnEntity.field_y.set(packet, this.y);
		PH_PO_SpawnEntity.field_z.set(packet, this.z);
		PH_PO_SpawnEntity.field_velocityX.set(packet, this.ad(this.velocityX));
		PH_PO_SpawnEntity.field_velocityY.set(packet, this.ad(this.velocityY));
		PH_PO_SpawnEntity.field_velocityZ.set(packet, this.ad(this.velocityZ));
		PH_PO_SpawnEntity.field_pitch.set(packet, this.af(this.pitch));
		PH_PO_SpawnEntity.field_yaw.set(packet, this.af(this.yaw));
		PH_PO_SpawnEntity.field_entityTypeId.set(packet, this.entityTypeId);
		PH_PO_SpawnEntity.field_entityDataId.set(packet, this.entityDataId);
		
		return packet;
	}
	
	/**
	 * Utils
	 */
	private final int ad(double a){		
		if(a<3.9D)return ((int)(3.9D*8000.0D));
		else if(a>-3.9D)return ((int)(-3.9D*8000.0D));
		else return ((int)(a*8000.0D));
	}
	private final int af(float a){
		float f=a*256.0f/360.0f;
		int i=(int)f;
		return a<i?i-1:i;
	}
}