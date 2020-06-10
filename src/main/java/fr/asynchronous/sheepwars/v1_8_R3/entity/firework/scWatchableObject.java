
package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class scWatchableObject {

	private final int             a;
	private final int             b;
	private Object                c;
	private boolean               d;
	private static String         mcVersion;
	private static Class<?>       nmsClass;
	private static Constructor<?> constructor;
	private static Method         methodA;

	static {
		try {
			scWatchableObject.mcVersion = org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
			scWatchableObject.nmsClass = Class.forName("net.minecraft.server." + scWatchableObject.mcVersion + ".DataWatcher$WatchableObject");
			scWatchableObject.constructor = scWatchableObject.nmsClass.getDeclaredConstructor(new Class[] { int.class, int.class, Object.class });
			scWatchableObject.methodA = scWatchableObject.nmsClass.getDeclaredMethod("a", new Class[] { boolean.class });
			scWatchableObject.methodA.setAccessible(true);
		} catch (Exception a) {
			a.printStackTrace();
		}
	}

	public scWatchableObject(int i, int j, Object object) {
		this.b = j;
		this.c = object;
		this.a = i;
		this.d = true;
	}

	public int a() {
		return this.b;
	}

	public void a(Object object) {
		this.c = object;
	}

	public Object b() {
		return this.c;
	}

	public int c() {
		return this.a;
	}

	public boolean d() {
		return this.d;
	}

	public void a(boolean flag) {
		this.d = flag;
	}

	public Object toNMSObject() {
		try {
			Object packet = scWatchableObject.constructor.newInstance(this.a, this.b, this.c);
			scWatchableObject.methodA.invoke(packet, this.d);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}