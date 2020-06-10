package fr.asynchronous.sheepwars.core.legacy;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class LegacyItem {

    private LegacyMaterial mat;
    private DyeColor color;
    private int amount;
    private short data;

    public LegacyItem(LegacyMaterial mat, int amount, short data) {
        this(mat, null, amount, data);
    }

    public LegacyItem(LegacyMaterial mat, int amount, DyeColor color) {
        this(mat, color, amount, (short) color.ordinal());
    }

    public LegacyItem(LegacyMaterial mat, DyeColor color) {
        this(mat, color, 1, (short) color.ordinal());
    }

    public LegacyItem(LegacyMaterial mat, DyeColor color, int amount, short data) {
        this.mat = mat;
        this.color = color;
        this.amount = amount;
        this.data = data;
    }

    public ItemStack getItemStack() {
        if (SheepWarsPlugin.getVersionManager().getVersion().newerThan(MinecraftVersion.v1_12_R1)) {
            //Bukkit.broadcastMessage("no boy");
            return new ItemStack(color == null ? mat.getMaterial() : mat.getColoredMaterial(color), amount);
        } else {
            //Bukkit.broadcastMessage("yes boy");
            return new ItemStack(mat.getMaterial(), amount, data);
        }
    }
}
