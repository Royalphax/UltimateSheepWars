package fr.royalpha.sheepwars.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class RandomUtils {
	
	private RandomUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static final Random random;

	static {
		random = new Random(System.nanoTime());
	}

	public static Vector getRandomVector() {
		final double x = RandomUtils.random.nextDouble() * 2.0 - 1.0;
		final double y = RandomUtils.random.nextDouble() * 2.0 - 1.0;
		final double z = RandomUtils.random.nextDouble() * 2.0 - 1.0;
		return new Vector(x, y, z).normalize();
	}

	public static Vector getRandomCircleVector() {
		final double rnd = RandomUtils.random.nextDouble() * 2.0 * 3.141592653589793;
		final double x = Math.cos(rnd);
		final double z = Math.sin(rnd);
		return new Vector(x, 0.0, z);
	}

	public static Material getRandomMaterial(final Material[] materials) {
		Material mat = Material.AIR;
		while (mat == Material.AIR)
		{
			mat = materials[RandomUtils.random.nextInt(materials.length)];
		}
		return mat;
	}

	public static double getRandomAngle() {
		return RandomUtils.random.nextDouble() * 2.0 * Math.PI;
	}
	
	public static double getRandomAngle(Double limitInDegrees) {
		Double output = 100.0;
		while (output > Math.toRadians(limitInDegrees))
			output = RandomUtils.random.nextDouble() * 2.0 * Math.PI;
		return output;
	}

	public static Player getRandomPlayer() {
		if (Bukkit.getOnlinePlayers().isEmpty())
			return null;
		
		List<Player> rplayer = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			rplayer.add(p);
		}
		int alea = new Random().nextInt(rplayer.size());
		Player rplayer2 = rplayer.get(alea);
		return rplayer2;
	}
	
	public static Boolean getRandomByPercent(Integer percent)
	{
		Integer rdm = new Random().nextInt(101);
		if (rdm < percent) {
			return true;
		} else {
			return false;
		}
	}
	
	public static <T> T getRandom(List<T> list) {
		int rdm = random.nextInt(list.size());
		return list.get(rdm);
	}
	
	@SafeVarargs
	public static <T> T getRandom(T... list) {
		int rdm = random.nextInt(list.length);
		return list[rdm];
	}

	public static Color getRandomColor() {
		Random r = new Random();
		int i = r.nextInt(17) + 1;
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}
}