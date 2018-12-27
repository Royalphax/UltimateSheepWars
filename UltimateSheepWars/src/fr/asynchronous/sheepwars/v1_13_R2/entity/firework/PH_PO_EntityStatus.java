package fr.asynchronous.sheepwars.v1_13_R2.entity.firework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class PH_PO_EntityStatus extends aPacketHandler {

	public int entityId;
	public byte statusId;

	private static Class<?> clazz;
	private static Constructor<?> constructor;
	private static Field field_entityId;
	private static Field field_statusId;

	static {
		try {
			PH_PO_EntityStatus.clazz = ProtocolUtils.getMinecraftClass("PacketPlayOutEntityStatus");
			PH_PO_EntityStatus.constructor = PH_PO_EntityStatus.clazz.getDeclaredConstructor();
			PH_PO_EntityStatus.constructor.setAccessible(true);

			PH_PO_EntityStatus.field_entityId = PH_PO_EntityStatus.clazz.getDeclaredField("a");
			PH_PO_EntityStatus.field_entityId.setAccessible(true);

			PH_PO_EntityStatus.field_statusId = PH_PO_EntityStatus.clazz.getDeclaredField("b");
			PH_PO_EntityStatus.field_statusId.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PH_PO_EntityStatus(int entityId, byte statusId) {
		this.entityId = entityId;
		this.statusId = statusId;
	}

	/**
	 * Build
	 */
	@Override
	public Object build() throws Exception {
		Object packet = PH_PO_EntityStatus.constructor.newInstance();

		PH_PO_EntityStatus.field_entityId.set(packet, this.entityId);
		PH_PO_EntityStatus.field_statusId.set(packet, this.statusId);

		return packet;
	}
}