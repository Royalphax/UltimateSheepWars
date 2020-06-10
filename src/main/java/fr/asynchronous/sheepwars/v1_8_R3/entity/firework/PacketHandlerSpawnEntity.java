package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

import java.lang.reflect.Field;

public class PacketHandlerSpawnEntity extends AbstractPacketHandler {

	private int             entityId;
	private double          locX;
	private double          locY;
	private double          locZ;
	private int             velX;
	private int             velY;
	private int             velZ;
	private float           yaw;
	private float           pitch;
	private int             entityTypeId;
	private int             data;
	private static String   mcVersion;
	private static Class<?> packetClass;
	private static Field    fieldEntityId, fieldLocX, fieldLocY, fieldLocZ, fieldVelX, fieldVelY, fieldVelZ, fieldPitch,
			fieldYaw, fieldEntityTypeId, fieldData;

	static {
		try {
			PacketHandlerSpawnEntity.mcVersion = org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
			PacketHandlerSpawnEntity.packetClass = Class.forName("net.minecraft.server." + PacketHandlerSpawnEntity.mcVersion + ".PacketPlayOutSpawnEntity");
			PacketHandlerSpawnEntity.fieldEntityId = PacketHandlerSpawnEntity.packetClass.getDeclaredField("a");
			PacketHandlerSpawnEntity.fieldEntityId.setAccessible(true);
			PacketHandlerSpawnEntity.fieldLocX = PacketHandlerSpawnEntity.packetClass.getDeclaredField("b");
			PacketHandlerSpawnEntity.fieldLocX.setAccessible(true);
			PacketHandlerSpawnEntity.fieldLocY = PacketHandlerSpawnEntity.packetClass.getDeclaredField("c");
			PacketHandlerSpawnEntity.fieldLocY.setAccessible(true);
			PacketHandlerSpawnEntity.fieldLocZ = PacketHandlerSpawnEntity.packetClass.getDeclaredField("d");
			PacketHandlerSpawnEntity.fieldLocZ.setAccessible(true);
			PacketHandlerSpawnEntity.fieldVelX = PacketHandlerSpawnEntity.packetClass.getDeclaredField("e");
			PacketHandlerSpawnEntity.fieldVelX.setAccessible(true);
			PacketHandlerSpawnEntity.fieldVelY = PacketHandlerSpawnEntity.packetClass.getDeclaredField("f");
			PacketHandlerSpawnEntity.fieldVelY.setAccessible(true);
			PacketHandlerSpawnEntity.fieldVelZ = PacketHandlerSpawnEntity.packetClass.getDeclaredField("g");
			PacketHandlerSpawnEntity.fieldVelZ.setAccessible(true);
			PacketHandlerSpawnEntity.fieldPitch = PacketHandlerSpawnEntity.packetClass.getDeclaredField("h");
			PacketHandlerSpawnEntity.fieldPitch.setAccessible(true);
			PacketHandlerSpawnEntity.fieldYaw = PacketHandlerSpawnEntity.packetClass.getDeclaredField("i");
			PacketHandlerSpawnEntity.fieldYaw.setAccessible(true);
			PacketHandlerSpawnEntity.fieldEntityTypeId = PacketHandlerSpawnEntity.packetClass.getDeclaredField("j");
			PacketHandlerSpawnEntity.fieldEntityTypeId.setAccessible(true);
			PacketHandlerSpawnEntity.fieldData = PacketHandlerSpawnEntity.packetClass.getDeclaredField("k");
			PacketHandlerSpawnEntity.fieldData.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PacketHandlerSpawnEntity(int entityId, int entityTypeId, double x, double y, double z, float yaw, float pitch, int velocityX, int velocityY, int velocityZ, int data) {
		this.entityId = entityId;
		this.locX = x;
		this.locY = y;
		this.locZ = z;
		this.velX = velocityX;
		this.velY = velocityY;
		this.velZ = velocityZ;
		this.pitch = pitch;
		this.yaw = yaw;
		this.entityTypeId = entityTypeId;
		this.data = data;
	}

	/**
	 * Build
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Object build() {
		try {
			Object packet = PacketHandlerSpawnEntity.packetClass.newInstance();

			PacketHandlerSpawnEntity.fieldEntityId.set(packet, this.entityId);
			PacketHandlerSpawnEntity.fieldLocX.set(packet, this.floor(this.locX));
			PacketHandlerSpawnEntity.fieldLocY.set(packet, this.floor(this.locY));
			PacketHandlerSpawnEntity.fieldLocZ.set(packet, this.floor(this.locZ));

			double d1 = this.velX;
			double d2 = this.velY;
			double d3 = this.velZ;
			double d4 = 3.9D;
			if (d1 < -d4)
				d1 = -d4;
			if (d2 < -d4)
				d2 = -d4;
			if (d3 < -d4)
				d3 = -d4;
			if (d1 > d4)
				d1 = d4;
			if (d2 > d4)
				d2 = d4;
			if (d3 > d4)
				d3 = d4;
			PacketHandlerSpawnEntity.fieldVelX.set(packet, ((int) (d1 * 8000.0D)));
			PacketHandlerSpawnEntity.fieldVelY.set(packet, ((int) (d2 * 8000.0D)));
			PacketHandlerSpawnEntity.fieldVelZ.set(packet, ((int) (d3 * 8000.0D)));

			PacketHandlerSpawnEntity.fieldPitch.set(packet, this.d(this.pitch));
			PacketHandlerSpawnEntity.fieldYaw.set(packet, this.d(this.yaw));
			PacketHandlerSpawnEntity.fieldEntityTypeId.set(packet, this.entityTypeId);
			PacketHandlerSpawnEntity.fieldData.set(packet, this.data);

			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/***/
	private final int floor(double a) {
		int b = (int) (a * 32.0D);
		return ((a < b) ? (b - 1) : (b));
	}

	/***/
	private final int d(float a) {
		int b = (int) (a * 256.0f / 360.0f);
		return ((a < b) ? (b - 1) : (b));
	}
}
