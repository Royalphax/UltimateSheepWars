package fr.asynchronous.sheepwars.core.legacy;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public enum LegacyMaterial {

    FIREWORK("FIREWORK", "FIREWORK_ROCKET"),
    WOODEN_SWORD("WOOD_SWORD", "WOODEN_SWORD"),
    WOOL("WOOL", "%COLOR%_WOOL"),
    STAINED_GLASS("STAINED_GLASS", "%COLOR%_STAINED_GLASS"),
    STAINED_GLASS_PANE("STAINED_GLASS_PANE", "%COLOR%_STAINED_GLASS_PANE"),
    PLAYER_SKULL_ITEM("SKULL_ITEM", "PLAYER_HEAD");

    private String[] alternatives;
    LegacyMaterial(String... alternatives) {
        this.alternatives = alternatives;
    }

    public Material getMaterial() {
        for (String str : alternatives) {
            if (str.contains("%COLOR%")) {
                str = str.replaceAll("%COLOR%", "WHITE");
                SheepWarsPlugin.getInstance().getLogger().warning("Colored material get using non coloring method ! If you see this, please contact the developer");
            }
            Material mat = Material.getMaterial(str);
            if (mat != null) {
                return mat;
            }
        }
        return Material.STONE;
    }

    public Material getColoredMaterial(DyeColor color) {
        for (String str : alternatives) {
            Material mat = Material.getMaterial(str.replaceAll("%COLOR%", color.toString().toUpperCase()));
            if (mat != null) {
                return mat;
            }
        }
        return Material.STONE;
    }


}
