package fr.asynchronous.sheepwars.v1_8_R3.entity.firework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class AbstractPacketHandler {

	private static String mcVersion;
	private static Method methodSendPacket, methodGetHandle;
	private static Field  fieldPlayerConnection;
	static {
		try {
			AbstractPacketHandler.mcVersion = org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];

			Class<?> classEntityPlayer = Class.forName("net.minecraft.server." + AbstractPacketHandler.mcVersion + ".EntityPlayer"),
					classCraftPlayer = Class.forName("org.bukkit.craftbukkit." + AbstractPacketHandler.mcVersion + ".entity.CraftPlayer"),
					classPlayerConnection = Class.forName("net.minecraft.server." + AbstractPacketHandler.mcVersion + ".PlayerConnection");

			AbstractPacketHandler.methodGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
			AbstractPacketHandler.fieldPlayerConnection = classEntityPlayer.getDeclaredField("playerConnection");
			AbstractPacketHandler.methodSendPacket = classPlayerConnection.getDeclaredMethod("sendPacket", new Class[] { Class.forName("net.minecraft.server." + AbstractPacketHandler.mcVersion + ".Packet") });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build
	 */
	public abstract Object build();

	/**
	 * Send
	 */
	public final void send(Player... players) {
		if (players.length <= 0)
			return;

		Object packet = this.build();
		for (Player player : players)
			if (player != null)
				AbstractPacketHandler.sendPacket(player, packet);
	}

	public final void send(List<Player> players) {
		this.send(players.toArray(new Player[players.size()]));
	}

	public static boolean sendPacket(Player player, Object packet) {
		try {
			AbstractPacketHandler.methodSendPacket.invoke(AbstractPacketHandler.fieldPlayerConnection.get(AbstractPacketHandler.methodGetHandle.invoke(player)), packet);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
