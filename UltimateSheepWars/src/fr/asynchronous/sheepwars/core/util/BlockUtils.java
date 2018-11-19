package fr.asynchronous.sheepwars.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class BlockUtils {
	
	private BlockUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static HashSet<Byte> blockPassSet = new HashSet<Byte>();

	public static HashSet<Byte> blockAirFoliageSet = new HashSet<Byte>();

	public static HashSet<Byte> fullSolid = new HashSet<Byte>();

	public static HashSet<Byte> blockUseSet = new HashSet<Byte>();

	@SuppressWarnings("deprecation")
	public static boolean solid(Block block) {
		if (block == null)
			return false;
		return solid(block.getTypeId());
	}

	public static boolean solid(int block) {
		return solid((byte) block);
	}

	public static boolean solid(byte block) {
		if (blockPassSet.isEmpty()) {
			blockPassSet.add(Byte.valueOf((byte) 0));
			blockPassSet.add(Byte.valueOf((byte) 6));
			blockPassSet.add(Byte.valueOf((byte) 8));
			blockPassSet.add(Byte.valueOf((byte) 9));
			blockPassSet.add(Byte.valueOf((byte) 10));
			blockPassSet.add(Byte.valueOf((byte) 11));
			blockPassSet.add(Byte.valueOf((byte) 26));
			blockPassSet.add(Byte.valueOf((byte) 27));
			blockPassSet.add(Byte.valueOf((byte) 28));
			blockPassSet.add(Byte.valueOf((byte) 30));
			blockPassSet.add(Byte.valueOf((byte) 31));
			blockPassSet.add(Byte.valueOf((byte) 32));
			blockPassSet.add(Byte.valueOf((byte) 37));
			blockPassSet.add(Byte.valueOf((byte) 38));
			blockPassSet.add(Byte.valueOf((byte) 39));
			blockPassSet.add(Byte.valueOf((byte) 40));
			blockPassSet.add(Byte.valueOf((byte) 50));
			blockPassSet.add(Byte.valueOf((byte) 51));
			blockPassSet.add(Byte.valueOf((byte) 55));
			blockPassSet.add(Byte.valueOf((byte) 59));
			blockPassSet.add(Byte.valueOf((byte) 63));
			blockPassSet.add(Byte.valueOf((byte) 64));
			blockPassSet.add(Byte.valueOf((byte) 65));
			blockPassSet.add(Byte.valueOf((byte) 66));
			blockPassSet.add(Byte.valueOf((byte) 68));
			blockPassSet.add(Byte.valueOf((byte) 69));
			blockPassSet.add(Byte.valueOf((byte) 70));
			blockPassSet.add(Byte.valueOf((byte) 71));
			blockPassSet.add(Byte.valueOf((byte) 72));
			blockPassSet.add(Byte.valueOf((byte) 75));
			blockPassSet.add(Byte.valueOf((byte) 76));
			blockPassSet.add(Byte.valueOf((byte) 77));
			blockPassSet.add(Byte.valueOf((byte) 78));
			blockPassSet.add(Byte.valueOf((byte) 83));
			blockPassSet.add(Byte.valueOf((byte) 90));
			blockPassSet.add(Byte.valueOf((byte) 92));
			blockPassSet.add(Byte.valueOf((byte) 93));
			blockPassSet.add(Byte.valueOf((byte) 94));
			blockPassSet.add(Byte.valueOf((byte) 96));
			blockPassSet.add(Byte.valueOf((byte) 101));
			blockPassSet.add(Byte.valueOf((byte) 102));
			blockPassSet.add(Byte.valueOf((byte) 104));
			blockPassSet.add(Byte.valueOf((byte) 105));
			blockPassSet.add(Byte.valueOf((byte) 106));
			blockPassSet.add(Byte.valueOf((byte) 107));
			blockPassSet.add(Byte.valueOf((byte) 111));
			blockPassSet.add(Byte.valueOf((byte) 115));
			blockPassSet.add(Byte.valueOf((byte) 116));
			blockPassSet.add(Byte.valueOf((byte) 117));
			blockPassSet.add(Byte.valueOf((byte) 118));
			blockPassSet.add(Byte.valueOf((byte) 119));
			blockPassSet.add(Byte.valueOf((byte) 120));
			blockPassSet.add(Byte.valueOf((byte) -85));
		}

		return !blockPassSet.contains(Byte.valueOf(block));
	}

	@SuppressWarnings("deprecation")
	public static boolean airFoliage(Block block) {
		if (block == null)
			return false;
		return airFoliage(block.getTypeId());
	}

	public static boolean airFoliage(int block) {
		return airFoliage((byte) block);
	}

	public static boolean airFoliage(byte block) {
		if (blockAirFoliageSet.isEmpty()) {
			blockAirFoliageSet.add(Byte.valueOf((byte) 0));
			blockAirFoliageSet.add(Byte.valueOf((byte) 6));
			blockAirFoliageSet.add(Byte.valueOf((byte) 31));
			blockAirFoliageSet.add(Byte.valueOf((byte) 32));
			blockAirFoliageSet.add(Byte.valueOf((byte) 37));
			blockAirFoliageSet.add(Byte.valueOf((byte) 38));
			blockAirFoliageSet.add(Byte.valueOf((byte) 39));
			blockAirFoliageSet.add(Byte.valueOf((byte) 40));
			blockAirFoliageSet.add(Byte.valueOf((byte) 51));
			blockAirFoliageSet.add(Byte.valueOf((byte) 59));
			blockAirFoliageSet.add(Byte.valueOf((byte) 104));
			blockAirFoliageSet.add(Byte.valueOf((byte) 105));
			blockAirFoliageSet.add(Byte.valueOf((byte) 115));
			blockAirFoliageSet.add(Byte.valueOf((byte) -115));
			blockAirFoliageSet.add(Byte.valueOf((byte) -114));
		}

		return blockAirFoliageSet.contains(Byte.valueOf(block));
	}

	@SuppressWarnings("deprecation")
	public static boolean fullSolid(Block block) {
		if (block == null) {
			return false;
		}
		return fullSolid(block.getTypeId());
	}

	public static boolean fullSolid(int block) {
		return fullSolid((byte) block);
	}

	public static boolean fullSolid(byte block) {
		if (fullSolid.isEmpty()) {
			fullSolid.add(Byte.valueOf((byte) 1));
			fullSolid.add(Byte.valueOf((byte) 2));
			fullSolid.add(Byte.valueOf((byte) 3));
			fullSolid.add(Byte.valueOf((byte) 4));
			fullSolid.add(Byte.valueOf((byte) 5));
			fullSolid.add(Byte.valueOf((byte) 7));
			fullSolid.add(Byte.valueOf((byte) 12));
			fullSolid.add(Byte.valueOf((byte) 13));
			fullSolid.add(Byte.valueOf((byte) 14));
			fullSolid.add(Byte.valueOf((byte) 15));
			fullSolid.add(Byte.valueOf((byte) 16));
			fullSolid.add(Byte.valueOf((byte) 17));
			fullSolid.add(Byte.valueOf((byte) 19));
			fullSolid.add(Byte.valueOf((byte) 20));
			fullSolid.add(Byte.valueOf((byte) 21));
			fullSolid.add(Byte.valueOf((byte) 22));
			fullSolid.add(Byte.valueOf((byte) 23));
			fullSolid.add(Byte.valueOf((byte) 24));
			fullSolid.add(Byte.valueOf((byte) 25));
			fullSolid.add(Byte.valueOf((byte) 29));
			fullSolid.add(Byte.valueOf((byte) 33));
			fullSolid.add(Byte.valueOf((byte) 35));
			fullSolid.add(Byte.valueOf((byte) 41));
			fullSolid.add(Byte.valueOf((byte) 42));
			fullSolid.add(Byte.valueOf((byte) 43));
			fullSolid.add(Byte.valueOf((byte) 44));
			fullSolid.add(Byte.valueOf((byte) 45));
			fullSolid.add(Byte.valueOf((byte) 46));
			fullSolid.add(Byte.valueOf((byte) 47));
			fullSolid.add(Byte.valueOf((byte) 48));
			fullSolid.add(Byte.valueOf((byte) 49));
			fullSolid.add(Byte.valueOf((byte) 56));
			fullSolid.add(Byte.valueOf((byte) 57));
			fullSolid.add(Byte.valueOf((byte) 58));
			fullSolid.add(Byte.valueOf((byte) 60));
			fullSolid.add(Byte.valueOf((byte) 61));
			fullSolid.add(Byte.valueOf((byte) 62));
			fullSolid.add(Byte.valueOf((byte) 73));
			fullSolid.add(Byte.valueOf((byte) 74));
			fullSolid.add(Byte.valueOf((byte) 79));
			fullSolid.add(Byte.valueOf((byte) 80));
			fullSolid.add(Byte.valueOf((byte) 82));
			fullSolid.add(Byte.valueOf((byte) 84));
			fullSolid.add(Byte.valueOf((byte) 86));
			fullSolid.add(Byte.valueOf((byte) 87));
			fullSolid.add(Byte.valueOf((byte) 88));
			fullSolid.add(Byte.valueOf((byte) 89));
			fullSolid.add(Byte.valueOf((byte) 91));
			fullSolid.add(Byte.valueOf((byte) 95));
			fullSolid.add(Byte.valueOf((byte) 97));
			fullSolid.add(Byte.valueOf((byte) 98));
			fullSolid.add(Byte.valueOf((byte) 99));
			fullSolid.add(Byte.valueOf((byte) 100));
			fullSolid.add(Byte.valueOf((byte) 103));
			fullSolid.add(Byte.valueOf((byte) 110));
			fullSolid.add(Byte.valueOf((byte) 112));
			fullSolid.add(Byte.valueOf((byte) 121));
			fullSolid.add(Byte.valueOf((byte) 123));
			fullSolid.add(Byte.valueOf((byte) 124));
			fullSolid.add(Byte.valueOf((byte) 125));
			fullSolid.add(Byte.valueOf((byte) 126));
			fullSolid.add(Byte.valueOf((byte) -127));
			fullSolid.add(Byte.valueOf((byte) -123));
			fullSolid.add(Byte.valueOf((byte) -119));
			fullSolid.add(Byte.valueOf((byte) -118));
			fullSolid.add(Byte.valueOf((byte) -104));
			fullSolid.add(Byte.valueOf((byte) -103));
			fullSolid.add(Byte.valueOf((byte) -101));
			fullSolid.add(Byte.valueOf((byte) -98));
		}

		return fullSolid.contains(Byte.valueOf(block));
	}

	@SuppressWarnings("deprecation")
	public static boolean usable(Block block) {
		if (block == null) {
			return false;
		}
		return usable(block.getTypeId());
	}

	public static boolean usable(int block) {
		return usable((byte) block);
	}

	public static boolean usable(byte block) {
		if (blockUseSet.isEmpty()) {
			blockUseSet.add(Byte.valueOf((byte) 23));
			blockUseSet.add(Byte.valueOf((byte) 26));
			blockUseSet.add(Byte.valueOf((byte) 33));
			blockUseSet.add(Byte.valueOf((byte) 47));
			blockUseSet.add(Byte.valueOf((byte) 54));
			blockUseSet.add(Byte.valueOf((byte) 58));
			blockUseSet.add(Byte.valueOf((byte) 61));
			blockUseSet.add(Byte.valueOf((byte) 62));
			blockUseSet.add(Byte.valueOf((byte) 64));
			blockUseSet.add(Byte.valueOf((byte) 69));
			blockUseSet.add(Byte.valueOf((byte) 71));
			blockUseSet.add(Byte.valueOf((byte) 77));
			blockUseSet.add(Byte.valueOf((byte) 93));
			blockUseSet.add(Byte.valueOf((byte) 94));
			blockUseSet.add(Byte.valueOf((byte) 96));
			blockUseSet.add(Byte.valueOf((byte) 107));
			blockUseSet.add(Byte.valueOf((byte) 116));
			blockUseSet.add(Byte.valueOf((byte) 117));
			blockUseSet.add(Byte.valueOf((byte) -126));
			blockUseSet.add(Byte.valueOf((byte) -111));
			blockUseSet.add(Byte.valueOf((byte) -110));
			blockUseSet.add(Byte.valueOf((byte) -102));
			blockUseSet.add(Byte.valueOf((byte) -98));
		}

		return blockUseSet.contains(Byte.valueOf(block));
	}

	@SuppressWarnings("deprecation")
	public static boolean isBlock(ItemStack item) {
		if (item == null) {
			return false;
		}
		return (item.getTypeId() > 0) && (item.getTypeId() < 256);
	}

	public static final Block getTargetBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR) {
				continue;
			}
			break;
		}
		return lastBlock;
	}

	public static final int getViewField(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		Integer output = 0;
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (isThroughable(lastBlock)) {
				output++;
				continue;
			}
			break;
		}
		return (output > range ? range : output);
	}

	public static final boolean isThroughable(Block block) {
		Material type = block.getType();
		if (type.isOccluding() || type == Material.GLASS)
			return false;
		if (type.name().endsWith("FENCE") || type == Material.COBBLE_WALL || type == Material.STAINED_GLASS_PANE || type == Material.THIN_GLASS) {
			if (block.getRelative(BlockFace.EAST).getType() != Material.AIR && block.getRelative(BlockFace.WEST).getType() != Material.AIR || block.getRelative(BlockFace.NORTH).getType() != Material.AIR && block.getRelative(BlockFace.SOUTH).getType() != Material.AIR) {
				return false;
			} else {
				return true;
			}
		}
		if (type.name().contains("DOOR") || type == Material.AIR) {
			return true;
		}
		return false;
	}

	public static final ArrayList<Block> getTargetBlocks(Player player, int range) {
		ArrayList<Block> output = new ArrayList<>();
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR) {
				output.add(lastBlock);
				continue;
			}
			break;
		}
		return output;
	}

	public static Block getHighest(World world, int x, int z) {
		return getHighest(world, x, z, null);
	}

	public static Block getHighest(World world, int x, int z, HashSet<Material> ignore) {
		Block block = world.getHighestBlockAt(x, z);

		while ((airFoliage(block)) || (block.getType() == Material.LEAVES) || ((ignore != null) && (ignore.contains(block.getType())))) {
			block = block.getRelative(BlockFace.DOWN);
		}

		return block.getRelative(BlockFace.UP);
	}

	public static boolean isOnAir(Location location, int x) {
		int y = location.getBlockY();
		for (int i = y; i >= (y - x);) {
			Location loc = location.clone();
			loc.setY(i);
			if (fullSolid(loc.getBlock()))
				;
			return false;
		}
		return true;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean self, boolean edges) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		List<BlockFace> faces = new ArrayList<>();
		faces.add(BlockFace.UP);
		faces.add(BlockFace.DOWN);
		faces.add(BlockFace.NORTH);
		faces.add(BlockFace.EAST);
		faces.add(BlockFace.WEST);
		faces.add(BlockFace.SOUTH);
		if (self)
			blocks.add(block);
		if (edges) {
			faces.add(BlockFace.NORTH_EAST);
			faces.add(BlockFace.NORTH_WEST);
			faces.add(BlockFace.SOUTH_EAST);
			faces.add(BlockFace.SOUTH_WEST);
			final Block up = block.getRelative(BlockFace.UP);
			blocks.add(up.getRelative(BlockFace.NORTH));
			blocks.add(up.getRelative(BlockFace.SOUTH));
			blocks.add(up.getRelative(BlockFace.WEST));
			blocks.add(up.getRelative(BlockFace.EAST));
			blocks.add(up.getRelative(BlockFace.NORTH_EAST));
			blocks.add(up.getRelative(BlockFace.NORTH_WEST));
			blocks.add(up.getRelative(BlockFace.SOUTH_EAST));
			blocks.add(up.getRelative(BlockFace.SOUTH_WEST));
			final Block down = block.getRelative(BlockFace.DOWN);
			blocks.add(down.getRelative(BlockFace.NORTH));
			blocks.add(down.getRelative(BlockFace.SOUTH));
			blocks.add(down.getRelative(BlockFace.WEST));
			blocks.add(down.getRelative(BlockFace.EAST));
			blocks.add(down.getRelative(BlockFace.NORTH_EAST));
			blocks.add(down.getRelative(BlockFace.NORTH_WEST));
			blocks.add(down.getRelative(BlockFace.SOUTH_EAST));
			blocks.add(down.getRelative(BlockFace.SOUTH_WEST));
		}
		for (BlockFace face : faces)
			blocks.add(block.getRelative(face));

		return blocks;
	}

	public static boolean isVisible(Block block) {
		for (Block other : getSurrounding(block, false, false)) {
			if (!other.getType().isOccluding()) {
				return true;
			}
		}

		return false;
	}
}