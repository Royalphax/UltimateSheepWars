package fr.royalpha.sheepwars.api.util;

import java.lang.reflect.Field;
import java.util.*;

import fr.royalpha.sheepwars.core.legacy.LegacyBanner;
import fr.royalpha.sheepwars.core.legacy.LegacyItem;
import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class ItemBuilder {
	private ItemStack is;

	public ItemBuilder() {
		this(Material.STONE, 1);
	}
	
	public ItemBuilder(Material m) {
		this(m, 1);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;
	}
	
	public ItemBuilder(ItemBuilder origin) {
		this(origin.is);
	}
	
	public ItemBuilder(Material m, int amount) {
		is = new ItemStack(m, amount);
	}

	public ItemBuilder(Material m, int amount, byte durability) {
		is = new ItemStack(m, amount, durability);
	}

	public ItemBuilder(LegacyItem item) {
		is = item.getItemStack();
	}
	
	public ItemBuilder setData(byte data) {
		is = new MaterialData(is.getType(), data).toItemStack(is.getAmount());
		return this;
	}

	public ItemBuilder setDurability(short dur) {
		is.setDurability(dur);
		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
		is.addUnsafeEnchantment(ench, level);
		return this;
	}

	public ItemBuilder removeEnchantment(Enchantment ench) {
		is.removeEnchantment(ench);
		return this;
	}

	public ItemBuilder setSkullOwner(String owner) {
		try {
			if (is.getDurability() != SkullType.PLAYER.ordinal())
				is = new LegacyItem(LegacyMaterial.PLAYER_SKULL_ITEM, is.getAmount(), (short) SkullType.PLAYER.ordinal()).getItemStack();
			SkullMeta im = (SkullMeta) is.getItemMeta();
			im.setOwner(owner);
			is.setItemMeta(im);
		} catch (ClassCastException expected) {
			// Do nothing
		}
		return this;
	}
	
	public ItemBuilder setSkullTexture(String url) {
        ItemStack head = new LegacyItem(LegacyMaterial.PLAYER_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()).getItemStack();
        //ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        if(url.isEmpty())return this;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        is = head;
        return this;
    }

	public ItemBuilder setIllegallyGlow(boolean bool) {
		ItemStack newItem = SheepWarsPlugin.getVersionManager().getNMSUtils().setIllegallyGlowing(is, bool);
		is = newItem;
		return this;
	}

    public ItemBuilder addIllegallyGlow() {
		ItemStack newItem = SheepWarsPlugin.getVersionManager().getNMSUtils().setIllegallyGlowing(is, true);
		is = newItem;
		return this;
	}
	
	public ItemBuilder removeIllegallyGlow() {
		ItemStack newItem = SheepWarsPlugin.getVersionManager().getNMSUtils().setIllegallyGlowing(is, false);
		is = newItem;
		return this;
	}

	public ItemBuilder addEnchant(Enchantment ench, int level) {
		ItemMeta im = is.getItemMeta();
		im.addEnchant(ench, level, true);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
		is.addEnchantments(enchantments);
		return this;
	}

	public ItemBuilder setUnbreakable() {
		this.is.setItemMeta(SheepWarsPlugin.getVersionManager().getNMSUtils().setUnbreakable(this.is.getItemMeta(), true));
		return this;
	}
	
	public ItemBuilder hideAttributes() {
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setLore(String... lore) {
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		ItemMeta im = is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder removeLoreLine(String line) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (!lore.contains(line))
			return this;
		lore.remove(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder removeLoreLine(int index) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (index < 0 || index > lore.size())
			return this;
		lore.remove(index);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	
	public ItemBuilder addLoreLine(String... lines) {
		for (int i = 0; i < lines.length; i++)
			addLoreLine(lines[i]);
		return this;
	}
	
	public ItemBuilder addLoreLine(List<String> lines) {
		for (String str : lines)
			addLoreLine(str);
		return this;
	}

	public ItemBuilder addLoreLine(String line) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addLoreLine(String line, int pos) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.set(pos, line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	@Deprecated
	public ItemBuilder setColor(DyeColor color) {
		final Material type = this.is.getType();
		if (type.toString().contains("BANNER")) {
			this.is = LegacyBanner.setColor(this.is, color);
		} else {
			if (type.toString().contains("INK")) {
				this.is.setDurability((short) color.getDyeData());
			} else if (type.toString().contains("WOOL")) {
				this.is.setDurability((short) color.getWoolData());
			} else if (type.toString().contains("STAINED_GLASS_PANE")) {
				this.is.setDurability((short) color.getDyeData());
			} else if (type.toString().contains("STAINED_GLASS")) {
				this.is.setDurability((short) color.getDyeData());
			}
		}
        return this;
	}

	/**public ItemBuilder setDyeColor(DyeColor color) {
		MaterialData blockData = is.getData();
		if (!(blockData instanceof Dye))
		    return this;
		Dye dyeData = (Dye) blockData;
		dyeData.setColor(color);
		is.setData(dyeData);
		return this;
	}

	public ItemBuilder setWoolColor(DyeColor color) {
		MaterialData blockData = is.getData();
		if (!(blockData instanceof Wool))
			return this;
		Wool woolData = (Wool) blockData;
		woolData.setColor(color);
		is.setData(woolData);
		return this;
	}
	
	public ItemBuilder setBannerColor(DyeColor color) {
		if (is.getType() != Material.BANNER)
		    return this;
		BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
		bannerMeta.setBaseColor(color);
		is.setItemMeta(bannerMeta);
		return this;
	}**/

	public ItemBuilder setLeatherArmorColor(Color color) {
		try {
			LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
			im.setColor(color);
			is.setItemMeta(im);
		} catch (ClassCastException expected) {
			// Do nothing
		}
		return this;
	}

	public ItemStack toItemStack() {
		return is;
	}
	
	public static String translateText(String input, String patternInput) {
		char[] pattern = patternInput.toCharArray();
        char[] chars = input.toCharArray();
        StringBuilder colorBuilder = new StringBuilder();
        int start = 0;
        @SuppressWarnings("unused")
		Character c;
        if (chars[0] == '&' && (c = chars[1]).toString().matches("[0-9a-fk-o]")) {
            start = 2;
        }
        int patternPos = 0;
        int i = start;
        while (i < chars.length) {
            colorBuilder.append('&').append(pattern[patternPos % pattern.length]);
            if (start != 0) {
                colorBuilder.append('&').append(chars[1]);
            }
            colorBuilder.append(chars[i]);
            if (!Character.isWhitespace(chars[i])) {
                ++patternPos;
            }
            ++i;
        }
        return ChatColor.translateAlternateColorCodes('&', colorBuilder.toString());
    }
}
