package fr.royalpha.sheepwars.core.handler;

import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.particle.EnchantEffect;
import fr.royalpha.sheepwars.core.particle.FlameCircleEffect;
import fr.royalpha.sheepwars.core.particle.FlameRingsEffect;
import fr.royalpha.sheepwars.core.particle.FlameSpiralEffect;
import fr.royalpha.sheepwars.core.particle.ShadowWalkEffect;
import fr.royalpha.sheepwars.core.particle.WitchSpiralEffect;
import fr.royalpha.sheepwars.core.task.ParticleTask;

public enum Particles {
	EXPLOSION_NORMAL("explode", 0, true),
	EXPLOSION_LARGE("largeexplode", 1, true),
	EXPLOSION_HUGE("hugeexplosion", 2, true),
	FIREWORKS_SPARK("fireworksSpark", 3, false),
	WATER_BUBBLE("bubble", 4, false),
	WATER_SPLASH("splash", 5, false),
	WATER_WAKE("wake", 6, false),
	SUSPENDED("suspended", 7, false),
	SUSPENDED_DEPTH("depthsuspend", 8, false),
	CRIT("crit", 9, false),
	CRIT_MAGIC("magicCrit", 10, false),
	SMOKE_NORMAL("smoke", 11, false),
	SMOKE_LARGE("largesmoke", 12, false),
	SPELL("spell", 13, false),
	SPELL_INSTANT("instantSpell", 14, false),
	SPELL_MOB("mobSpell", 15, false),
	SPELL_MOB_AMBIENT("mobSpellAmbient", 16, false),
	SPELL_WITCH("witchMagic", 17, false),
	DRIP_WATER("dripWater", 18, false),
	DRIP_LAVA("dripLava", 19, false),
	VILLAGER_ANGRY("angryVillager", 20, false),
	VILLAGER_HAPPY("happyVillager", 21, false),
	TOWN_AURA("townaura", 22, false),
	NOTE("note", 23, false),
	PORTAL("portal", 24, false),
	ENCHANTMENT_TABLE("enchantmenttable", 25, false),
	FLAME("flame", 26, false),
	LAVA("lava", 27, false),
	FOOTSTEP("footstep", 28, false),
	CLOUD("cloud", 29, false),
	REDSTONE("reddust", 30, false),
	SNOWBALL("snowballpoof", 31, false),
	SNOW_SHOVEL("snowshovel", 32, false),
	SLIME("slime", 33, false),
	HEART("heart", 34, false),
	BARRIER("barrier", 35, false),
	ITEM_CRACK("iconcrack_", 36, false, 2),
	BLOCK_CRACK("blockcrack_", 37, false, 1),
	BLOCK_DUST("blockdust_", 38, false, 1),
	WATER_DROP("droplet", 39, false),
	ITEM_TAKE("take", 40, false),
	MOB_APPEARANCE("mobappearance", 41, true),
	DRAGON_BREATH("dragonbreath", 42, false, MinecraftVersion.v1_9_R1, PORTAL),
	END_ROD("endrod", 43, false, MinecraftVersion.v1_9_R1, FIREWORKS_SPARK),
	DAMAGE_INDICATOR("damageindicator", 44, false, MinecraftVersion.v1_9_R1, TOWN_AURA),
	SWEEP_ATTACK("sweepattack", 45, false, MinecraftVersion.v1_9_R1, CRIT_MAGIC);

	private final String name;
	private final int id;
	private final boolean bool;
	private final int option;
	private final MinecraftVersion supportedVersion;
	private final Particles alternative;
	private static final Map<Integer, Particles> particlesId;
	private static final Map<String, Particles> particlesName;
	private static final MinecraftVersion version;

	static {
		version = SheepWarsPlugin.getVersionManager().getVersion();
		particlesId = Maps.newHashMap();
		particlesName = Maps.newHashMap();
		for (Particles localEnumParticle : values()) {
			particlesId.put(Integer.valueOf(localEnumParticle.getId()), localEnumParticle);
			particlesName.put(localEnumParticle.getName(), localEnumParticle);
		}
	}

	private Particles(String paramString, int paramInt1, boolean paramBoolean, int paramInt2, MinecraftVersion version, Particles alternative) {
		this.supportedVersion = version;
		this.alternative = alternative;
		this.name = paramString;
		this.id = paramInt1;
		this.bool = paramBoolean;
		this.option = paramInt2;
	}

	private Particles(String paramString, int paramInt, boolean paramBoolean, MinecraftVersion version, Particles alternative) {
		this(paramString, paramInt, paramBoolean, 0, version, alternative);
	}

	private Particles(String paramString, int paramInt, boolean paramBoolean, int paramInt2) {
		this(paramString, paramInt, paramBoolean, paramInt2, MinecraftVersion.v1_8_R3, null);
	}

	private Particles(String paramString, int paramInt, boolean paramBoolean) {
		this(paramString, paramInt, paramBoolean, 0, MinecraftVersion.v1_8_R3, null);
	}

	public static Set<String> getParticles() {
		return particlesName.keySet();
	}

	public String getName() {
		return this.name;
	}
	
	public String getString() {
		return (version.newerOrEqualTo(this.supportedVersion) ? this.toString() : (this.alternative == null ? TOWN_AURA.toString() : this.alternative.toString()));
	}

	public int getId() {
		return (version.newerOrEqualTo(this.supportedVersion) ? this.id : (this.alternative == null ? TOWN_AURA.getId() : this.alternative.getId()));
	}

	public int getOptions() {
		return this.option;
	}

	public MinecraftVersion getVersion() {
		return this.supportedVersion;
	}

	public Particles getAlternative() {
		return this.alternative;
	}

	public boolean getBoolean() {
		return this.bool;
	}

	public static Particles getEnumById(int paramInt) {
		return (Particles) particlesId.get(Integer.valueOf(paramInt));
	}

	public static Particles getEnumByString(String paramString) {
		return (Particles) particlesName.get(paramString);
	}

	public static enum ParticleEffect {

		FLAME_RINGS((ParticleEffectType) new FlameRingsEffect(), 0),
		FLAME_CIRCLE((ParticleEffectType) new FlameCircleEffect(), 0),
		FLAME_SPIRAL((ParticleEffectType) new FlameSpiralEffect(), 0),
		WITCH_SPIRAL((ParticleEffectType) new WitchSpiralEffect(), 0),
		ENCHANT((ParticleEffectType) new EnchantEffect(), 5),
		SHADOW_WALK((ParticleEffectType) new ShadowWalkEffect(), 8);

		private ParticleEffectType action;
		private int ticks;

		private ParticleEffect(ParticleEffectType action, int ticks) {
			this.action = action;
			this.ticks = ticks;
		}

		public ParticleEffectType getAction() {
			return this.action;
		}

		public int getTicks() {
			return this.ticks;
		}

		public interface ParticleEffectType {
			void update(Player player, Boolean moving);
		}

		public static void equipEffect(Player player, SheepWarsPlugin plugin) {
			if (Contributor.isContributor(player))
				new ParticleTask(Contributor.getContributor(player).getEffect(), player, plugin);
		}
		
		public static void equipEffect(Player player, ParticleEffect effect, SheepWarsPlugin plugin) {
			new ParticleTask(effect, player, plugin);
		}
	}
}