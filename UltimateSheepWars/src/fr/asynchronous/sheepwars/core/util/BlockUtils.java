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
	
	public final static Block getTargetBlock(Player player, int range) {
        BlockIterator bi= new BlockIterator(player, range);
        Block lastBlock = bi.next();
        while (bi.hasNext()) {
            lastBlock = bi.next();
            if (lastBlock.getType() == Material.AIR)
                continue;
            break;
        }
        return lastBlock;
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