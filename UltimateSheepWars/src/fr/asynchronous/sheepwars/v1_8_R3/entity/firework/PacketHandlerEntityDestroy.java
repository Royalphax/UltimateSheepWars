
package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

import java.lang.reflect.Field;

public class PacketHandlerEntityDestroy extends AbstractPacketHandler {

	private int[]           entityIds;
	private static String   mcVersion;
	private static Class<?> packetClass;
	private static Field    fieldEntityIds;

	static {
		try {
			PacketHandlerEntityDestroy.mcVersion = org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
			PacketHandlerEntityDestroy.packetClass = Class.forName("net.minecraft.server." + PacketHandlerEntityDestroy.mcVersion + ".PacketPlayOutEntityDestroy");
			PacketHandlerEntityDestroy.fieldEntityIds = PacketHandlerEntityDestroy.packetClass.getDeclaredField("a");
			PacketHandlerEntityDestroy.fieldEntityIds.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PacketHandlerEntityDestroy(int... entityIds) {
		this.entityIds = entityIds;
	}

	/**
	 * Build
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Object build() {
		try {
			Object packet = PacketHandlerEntityDestroy.packetClass.newInstance();

			PacketHandlerEntityDestroy.fieldEntityIds.set(packet, this.entityIds);

			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}