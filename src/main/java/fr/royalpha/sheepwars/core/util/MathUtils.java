package fr.royalpha.sheepwars.core.util;

import java.util.Random;
import java.util.TreeMap;

import org.bukkit.util.Vector;

public final class MathUtils {

	private MathUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static final float nanoToSec = 1.0E-9f;
	public static final float FLOAT_ROUNDING_ERROR = 1.0E-6f;
	public static final float PI = 3.1415927f;
	public static final float PI2 = 6.2831855f;
	public static final float SQRT_3 = 1.73205f;
	public static final float E = 2.7182817f;
	public static final float radiansToDegrees = 57.295776f;
	public static final float radDeg = 57.295776f;
	public static final float degreesToRadians = 0.017453292f;
	public static final float degRad = 0.017453292f;
	static final int ATAN2_DIM;
	private static final float INV_ATAN2_DIM_MINUS_1;
	public static Random random;

	static {
		ATAN2_DIM = (int) Math.sqrt(16384.0);
		INV_ATAN2_DIM_MINUS_1 = 1.0f / (MathUtils.ATAN2_DIM - 1);
		MathUtils.random = new Random();
	}

	public static final float sin(final float radians) {
		return Sin.table[(int) (radians * 2607.5945f) & 0x3FFF];
	}

	public static final float cos(final float radians) {
		return Sin.table[(int) ((radians + 1.5707964f) * 2607.5945f) & 0x3FFF];
	}

	public static final float sinDeg(final float degrees) {
		return Sin.table[(int) (degrees * 45.511112f) & 0x3FFF];
	}

	public static final float cosDeg(final float degrees) {
		return Sin.table[(int) ((degrees + 90.0f) * 45.511112f) & 0x3FFF];
	}

	public static final float atan2(float y, float x) {
		float mul;
		float add;
		if (x < 0.0f) {
			if (y < 0.0f) {
				y = -y;
				mul = 1.0f;
			} else {
				mul = -1.0f;
			}
			x = -x;
			add = -3.1415927f;
		} else {
			if (y < 0.0f) {
				y = -y;
				mul = -1.0f;
			} else {
				mul = 1.0f;
			}
			add = 0.0f;
		}

		final float invDiv = 1.0f / (((x < y) ? y : x) * MathUtils.INV_ATAN2_DIM_MINUS_1);
		if (invDiv == Float.POSITIVE_INFINITY) {
			return ((float) Math.atan2(y, x) + add) * mul;
		}

		final int xi = (int) (x * invDiv);
		final int yi = (int) (y * invDiv);
		return (Atan2.table[yi * MathUtils.ATAN2_DIM + xi] + add) * mul;
	}

	public static final int random(final int range) {
		return MathUtils.random.nextInt(range + 1);
	}

	public static final int random(final int start, final int end) {
		return start + MathUtils.random.nextInt(end - start + 1);
	}

	public static final boolean randomBoolean() {
		return MathUtils.random.nextBoolean();
	}

	public static final boolean randomBoolean(final float chance) {
		return random() < chance;
	}

	public static final float random() {
		return MathUtils.random.nextFloat();
	}

	public static final float random(final float range) {
		return MathUtils.random.nextFloat() * range;
	}

	public static final float random(final float start, final float end) {
		return start + MathUtils.random.nextFloat() * (end - start);
	}

	public static int nextPowerOfTwo(int value) {
		if (value == 0) {
			return 1;
		}
		value = (--value | value >> 1);
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	public static boolean isPowerOfTwo(final int value) {
		return value != 0 && (value & value - 1) == 0x0;
	}
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
		return true;
	}

	public static int clamp(final int value, final int min, final int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static short clamp(final short value, final short min, final short max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static float clamp(final float value, final float min, final float max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static int floor(final float x) {
		return (int) (x + 16384.0) - 16384;
	}

	public static int floorPositive(final float x) {
		return (int) x;
	}

	public static int ceil(final float x) {
		return (int) (x + 16384.999999999996) - 16384;
	}

	public static int ceilPositive(final float x) {
		return (int) (x + 0.9999999);
	}

	public static int round(final float x) {
		return (int) (x + 16384.5) - 16384;
	}

	public static int roundPositive(final float x) {
		return (int) (x + 0.5f);
	}

	public static boolean isZero(final float value) {
		return Math.abs(value) <= 1.0E-6f;
	}

	public static boolean isZero(final float value, final float tolerance) {
		return Math.abs(value) <= tolerance;
	}

	public static boolean isEqual(final float a, final float b) {
		return Math.abs(a - b) <= 1.0E-6f;
	}

	public static boolean isEqual(final float a, final float b, final float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	private static class Sin {
		static final float[] table;

		static {
			table = new float[16384];
			for (int i = 0; i < 16384; ++i) {
				Sin.table[i] = (float) Math.sin((i + 0.5f) / 16384.0f * 6.2831855f);
			}
			for (int i = 0; i < 360; i += 90) {
				Sin.table[(int) (i * 45.511112f) & 0x3FFF] = (float) Math.sin(i * 0.017453292f);
			}
		}
	}

	private static class Atan2 {
		static final float[] table;

		static {
			table = new float[16384];
			for (int i = 0; i < MathUtils.ATAN2_DIM; ++i) {
				for (int j = 0; j < MathUtils.ATAN2_DIM; ++j) {
					final float x0 = i / MathUtils.ATAN2_DIM;
					final float y0 = j / MathUtils.ATAN2_DIM;
					Atan2.table[j * MathUtils.ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
				}
			}
		}
	}

	private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

	static {
		map.put(1000, "M");
		map.put(900, "CM");
		map.put(500, "D");
		map.put(400, "CD");
		map.put(100, "C");
		map.put(90, "XC");
		map.put(50, "L");
		map.put(40, "XL");
		map.put(10, "X");
		map.put(9, "IX");
		map.put(5, "V");
		map.put(4, "IV");
		map.put(1, "I");
	}

	public final static String toRoman(int number) {
		int l = map.floorKey(number);
		if (number == l) {
			return map.get(number);
		}
		return map.get(l) + toRoman(number - l);
	}

	public static final Vector rotateAroundAxisX(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static final Vector rotateAroundAxisY(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static final Vector rotateAroundAxisZ(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
		rotateAroundAxisX(v, angleX);
		rotateAroundAxisY(v, angleY);
		rotateAroundAxisZ(v, angleZ);
		return v;
	}

	public static final double angleToXAxis(Vector vector) {
		return Math.atan2(vector.getX(), vector.getY());
	}
}