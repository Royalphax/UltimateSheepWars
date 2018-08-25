package fr.asynchronous.sheepwars.v1_12_R1.entity.firework;

import org.bukkit.inventory.ItemStack;

public class jDataWatcherObject<T> implements iNmsObject {

	public static final jDataWatcherObject<Byte> entity_ay = new jDataWatcherObject<>("Z", "Entity");
	public static final jDataWatcherObject<Integer> entity_az = new jDataWatcherObject<>("aA", "Entity");
	public static final jDataWatcherObject<String> entity_aA = new jDataWatcherObject<>("aB", "Entity");
	public static final jDataWatcherObject<Boolean> entity_aB = new jDataWatcherObject<>("aC", "Entity");
	public static final jDataWatcherObject<Boolean> entity_aC = new jDataWatcherObject<>("aD", "Entity");
	public static final jDataWatcherObject<Boolean> entity_aD = new jDataWatcherObject<>("aE", "Entity");
	public static final jDataWatcherObject<ItemStack> entityfireworks_FIREWORK_ITEM = new jDataWatcherObject<>("FIREWORK_ITEM", "EntityFireworks");

	private final Object nms;

	private jDataWatcherObject(String fieldname, String classname) {
		this.nms = ProtocolUtils.refl_fieldGet0(fieldname, classname);
	}

	/**
	 * Build
	 */
	@Override
	public Object build() {
		return this.nms;
	}
}
