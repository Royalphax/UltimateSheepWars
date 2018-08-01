package fr.asynchronous.sheepwars.v1_8_R3.entity;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsAPI;
import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EnumColor;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class CustomSheep extends EntitySheep {

	private SheepManager sheep;
	private Player player;
	private net.minecraft.server.v1_8_R3.World world;
	private boolean ground = false;
	private boolean isDead = false;
	private long ticks;
	private Plugin plugin;

	public CustomSheep(net.minecraft.server.v1_8_R3.World world) {
		super(world);
	}

	public CustomSheep(net.minecraft.server.v1_8_R3.World world, Player player, Plugin plugin) {
		super(world);
		this.player = player;
		this.plugin = plugin;
		this.world = ((CraftWorld) player.getWorld()).getHandle();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public CustomSheep(net.minecraft.server.v1_8_R3.World world, Player player, SheepManager sheep, Plugin plugin) {
		this(world, player, plugin);

		this.sheep = sheep;
		this.ticks = sheep.getDuration() <= 0 ? Long.MAX_VALUE : sheep.getDuration() * 20;

		setColor(EnumColor.valueOf(sheep.getColor().toString()));
		sheep.onSpawn(player, getBukkitSheep(), plugin);

		if (sheep.getAbilities().contains(SheepAbility.FIRE_PROOF))
			this.fireProof = true;
		if (sheep.getAbilities().contains(SheepAbility.SEEK_PLAYERS)) {
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(this.goalSelector, Sets.newLinkedHashSet());
				bField.set(this.targetSelector, Sets.newLinkedHashSet());
				cField.set(this.goalSelector, Sets.newLinkedHashSet());
				cField.set(this.targetSelector, Sets.newLinkedHashSet());
			} catch (Exception e) {
				new ExceptionManager(e).register(true);
			}
			this.getNavigation();
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
			this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
		}
	}

	@Override
	public void move(double d0, double d1, double d2) {
		if (this.getBukkitEntity().hasMetadata(UltimateSheepWarsAPI.SHEEPWARS_SHEEP_METADATA) && !this.ground) {
			Location from = new Location(this.getBukkitEntity().getWorld(), this.locX, this.locY, this.locZ);
			Location to = from.clone().add(this.motX, this.motY, this.motZ);
			
			Vector dir = to.subtract(from).toVector();
			Vector copy = dir.clone();
			boolean noclip = true;
			for (double i = 0; i <= 1; i += 0.2) {
			    copy.multiply(i);
			    Location loc = from.clone().add(copy);
			    UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FIREWORKS_SPARK, from, 0.0F, 0.0F, 0.0F, 1, 0.0F);
			    if (loc.getBlock().getType() != Material.AIR)
			    	noclip = false;
			    copy = dir.clone();
			}
			this.noclip = noclip;
		}
		super.move(d0, d1, d2);
	}

	@Override
	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.sheep.getAbilities().contains(SheepAbility.RIDEABLE) && this.onGround && this.passenger != null) {
			final Entity passenger = this.passenger;
			if (passenger == null || !(passenger instanceof EntityHuman)) {
				super.g(sideMot, forMot);
				this.S = 1.0f;
				this.aK = 0.02f;
				return;
			}
			this.lastYaw = (this.yaw = this.passenger.yaw);
			this.pitch = (this.passenger.pitch * 0.5F);
			setYawPitch(this.yaw, this.pitch);
			this.aI = (this.aG = this.yaw);
			sideMot = ((EntityLiving) this.passenger).aZ * 0.15F;
			forMot = ((EntityLiving) this.passenger).ba * 0.15F;
			if (forMot <= 0.0F) {
				forMot *= 0.15F;
			}

			Field jump = null;
			try {
				jump = EntityLiving.class.getDeclaredField("aY");
			} catch (NoSuchFieldException localNoSuchFieldException) {
			} catch (SecurityException localSecurityException) {
			}

			jump.setAccessible(true);

			if ((jump != null) && (this.onGround)) {
				try {
					if (jump.getBoolean(this.passenger)) {
						double jumpHeight = 0.5D;

						this.motY = jumpHeight;
					}
				} catch (IllegalAccessException localIllegalAccessException) {
				}

			}

			this.S = 1.0F;

			this.aK = (bh() * 0.1F);
			if (!this.world.isClientSide) {
				k(0.35F);
				super.g(sideMot, forMot);
			}

			this.ay = this.az;
			double d0 = this.locX - this.lastX;
			double d1 = this.locZ - this.lastZ;
			float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
			if (f4 > 1.0F) {
				f4 = 1.0F;
			}

			this.az += (f4 - this.az) * 0.4F;
			this.aA += this.az;
		}
		super.g(sideMot, forMot);
	}

	public void bL() {
		try {
			if (this.sheep != null) {
				if ((this.onGround || this.inWater || this.sheep.isFriendly()) && !this.ground)
					this.ground = true;
				if (this.ground && !this.isDead && (this.ticks <= 0 || !isAlive() || this.sheep.onTicking(this.player, this.ticks, getBukkitSheep(), this.plugin))) {
					this.isDead = true;
					boolean death = true;
					if (this.passenger != null)
						this.passenger.getBukkitEntity().eject();
					if (isAlive()) {
						die();
						death = false;
					}
					this.sheep.onFinish(this.player, getBukkitSheep(), death, this.plugin);
					if (death)
						this.dropDeathLoot();
					return;
				}
				this.ticks--;
			}
		} catch (Exception ex) {
			return;
		} finally {
			super.bL();
		}
	}

	public void dropDeathLoot() {
		if (this.sheep.isDropAllowed()) {
			final Player killer = getBukkitSheep().getKiller();
			final DamageCause damageCause = getBukkitEntity().getLastDamageCause().getCause();
			if (damageCause == DamageCause.ENTITY_ATTACK) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(killer).increaseSheepKilled(1);
					SheepManager.giveSheep(killer, this.sheep);
				}
			} else {
				Location location = getBukkitEntity().getLocation();
				location.getWorld().dropItemNaturally(location, this.sheep.getIcon(killer));
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public org.bukkit.entity.Sheep getBukkitSheep() {
		return (org.bukkit.entity.Sheep) getBukkitEntity();
	}

	public void explode(float power) {
		explode(power, true, false);
	}

	public void explode(float power, boolean fire) {
		explode(power, true, fire);
	}

	public void explode(float power, boolean breakBlocks, boolean fire) {
		getBukkitEntity().remove();
		UltimateSheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX, this.locY, this.locZ, power, breakBlocks, fire);
	}
}
