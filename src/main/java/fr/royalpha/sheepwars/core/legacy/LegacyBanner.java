package fr.royalpha.sheepwars.core.legacy;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;

public class LegacyBanner {

    public static ItemStack setColor(ItemStack stack, DyeColor color) {
        if (Material.getMaterial("BANNER") != null) {
            BannerMeta bannerMeta = (BannerMeta) stack.getItemMeta();
            bannerMeta.setBaseColor(color);
            if (color == DyeColor.RED || color == DyeColor.BLUE) {
                final DyeColor fadedColor = (color == DyeColor.RED ? DyeColor.PINK : DyeColor.LIGHT_BLUE);
                bannerMeta.setPatterns(Arrays.asList(new Pattern(fadedColor, PatternType.DIAGONAL_RIGHT),
                        new Pattern(color, PatternType.SQUARE_BOTTOM_RIGHT),
                        new Pattern(fadedColor, PatternType.SQUARE_TOP_LEFT),
                        new Pattern(fadedColor, PatternType.GRADIENT_UP),
                        new Pattern(color, PatternType.GRADIENT)));
            }
            stack.setItemMeta(bannerMeta);
        } else {
            Material mat = Material.getMaterial(color.toString().toUpperCase() + "_BANNER");
            if (mat != null) {
                stack.setType(mat);
            } else {
                SheepWarsPlugin.getInstance().getLogger().warning("Banner color not found ! If you see this, please contact the developer.");
                stack.setType(Material.getMaterial("WHITE_BANNER"));
            }
        }
        return stack;
    }
}
