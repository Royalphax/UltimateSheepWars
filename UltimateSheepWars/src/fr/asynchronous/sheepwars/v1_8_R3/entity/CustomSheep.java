package fr.asynchronous.sheepwars.v1_8_R3.entity;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;
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

	private SheepWarsSheep sheep;
	private Player player;
	private boolean ground = false;
	private boolean upComingCollision = false;
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
	public CustomSheep(net.minecraft.server.v1_8_R3.World world, Player player, SheepWarsSheep sheep, Plugin plugin) {
		this(world, player, plugin);

		this.sheep = sheep;
		this.ticks = sheep.getDuration() <= 0 ? Long.MAX_VALUE : sheep.getDuration() * 20;

		setColor(EnumColor.valueOf(sheep.getColor().toString()));
		sheep.onSpawn(player, getBukkitSheep(), plugin);

		if (sheep.hasAbility(SheepAbility.FIRE_PROOF))
			this.fireProof = true;
		if (sheep.hasAbility(SheepAbility.SEEK_PLAYERS)) {
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(this.goalSelector, new UnsafeList<>());
				bField.set(this.targetSelector, new UnsafeList<>());
				cField.set(this.goalSelector, new UnsafeList<>());
				cField.set(this.targetSelector, new UnsafeList<>());
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
		if (sheep.hasAbility(SheepAbility.RIDEABLE)) {
			Boat boat = (Boat) getBukkitEntity().getWorld().spawnEntity(getBukkitEntity().getLocation(), EntityType.BOAT);
			boat.setPassenger(player);
			getBukkitSheep().setPassenger(boat);
			// CraftPlayer craftPlayer = (CraftPlayer) player;
			// ((EntityPlayer) craftPlayer.getHandle()).mount(this);
		}
	}

	private static final int RADIUS = 2;
	private static final int SPEED_DIVIDER = 20;
	private static double relativeX = 0.0;
	private static double relativeY = 0.0;
	private static double relativeZ = 0.0;

	@Override
	public void move(double d0, double d1, double d2) {
		if (this.sheep != null) {
			final Location from = new Location(this.getBukkitEntity().getWorld(), this.locX, this.locY, this.locZ);
			final Location to = from.clone().add(this.motX, this.motY, this.motZ);
			if (!this.ground && !this.sheep.isFriendly()) {
				final Vector dir = to.subtract(from).toVector();
				Vector copy = dir.clone();
				boolean noclip = true;
				for (double i = 0; i <= 1; i += 0.2) {
					copy.multiply(i);
					final Location loc = from.clone().add(copy);
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FIREWORKS_SPARK, from, 0.0F, 0.0F, 0.0F, 1, 0.0F);
					final Location frontLoc = loc.clone().add(copy.clone().normalize().multiply(RADIUS));
					if (!this.upComingCollision && frontLoc.getBlock().getType() != Material.AIR) {
						this.upComingCollision = true;
						relativeX = d0 / (double) SPEED_DIVIDER;
						relativeY = d1 / (double) SPEED_DIVIDER;
						relativeZ = d2 / (double) SPEED_DIVIDER;
					}
					if (loc.getBlock().getType() != Material.AIR) {
						noclip = false;
						break;
					}
					copy = dir.clone();
				}
				this.noclip = noclip;
			}
			if (this.sheep.hasAbility(SheepAbility.EAT_BLOCKS) && this.upComingCollision) {
				if (!this.ground)
					this.ground = true;
				d0 = relativeX;
				d1 = relativeY;
				d2 = relativeZ;
			}
		}
		super.move(d0, d1, d2);
	}

	@Override
	public void g(double d0, double d1, double d2) {
		// Do nothing
	}

	@Override
	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.onGround && sheep.hasAbility(SheepAbility.RIDEABLE)) {
			if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
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
				// Do nothing
			} catch (SecurityException localSecurityException) {
				// Do nothing
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

	@Override
	public void bL() {
		try {
			if (this.sheep != null) {
				if ((this.onGround || this.inWater || this.sheep.isFriendly()) && !this.ground) {
					this.ground = true;
					if (this.sheep.hasAbility(SheepAbility.DISABLE_SLIDE)) {
						this.motX = 0;
						this.motY = 0;
						this.motZ = 0;
					}
				}
				if (this.ground) {
					if (!this.isDead && (this.ticks <= 0 || !isAlive() || this.sheep.onTicking(this.player, this.ticks, getBukkitSheep(), this.plugin))) {
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
					SheepWarsSheep.giveSheep(killer, this.sheep);
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
		SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX, this.locY, this.locZ, power, breakBlocks, fire);
	}
}
