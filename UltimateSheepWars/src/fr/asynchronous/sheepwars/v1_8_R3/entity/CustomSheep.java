package fr.asynchronous.sheepwars.v1_8_R3.entity;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.util.BlockUtils;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EnumColor;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class CustomSheep extends EntitySheep {
	private fr.asynchronous.sheepwars.core.handler.Sheeps sheep;
	private Player player;
	private net.minecraft.server.v1_8_R3.World world;
	private boolean explosion = true;
	private boolean ground;
	private long defaultTicks;
	private long ticks;
	private boolean drop;
	private int noclipDistance;
	private UltimateSheepWarsPlugin plugin;

	public CustomSheep(net.minecraft.server.v1_8_R3.World world) {
		super(world);
	}

	public CustomSheep(net.minecraft.server.v1_8_R3.World world, Player player, UltimateSheepWarsPlugin plugin) {
		super(world);
		this.player = player;
		this.plugin = plugin;
		this.world = ((CraftWorld) player.getWorld()).getHandle();
	}

	@SuppressWarnings("deprecation")
	public void convertColor() {
		sheep.getColor().getWoolData();
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	public CustomSheep(net.minecraft.server.v1_8_R3.World world, Player player,
			fr.asynchronous.sheepwars.core.handler.Sheeps sheep, UltimateSheepWarsPlugin plugin) {
		this(world, player, plugin);
		getNavigation();
		a(0.9F, 1.3F);

		this.sheep = sheep;
		this.ticks = (sheep.getDuration() == -1 ? Long.MAX_VALUE : sheep.getDuration() * 20);
		this.defaultTicks = this.ticks;
		this.ground = (!sheep.isOnGround());
		this.drop = sheep.isDrop();
		this.noclip = !sheep.isFriendly();
		this.noclipDistance = BlockUtils.getViewField(player, 6);

		setColor((EnumColor.fromColorIndex(sheep.getColor().getWoolData())));
		if (sheep != null) {
			sheep.getAction().onSpawn(player, getBukkitSheep(), plugin);
		}
		if ((sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.INTERGALACTIC)
				|| (sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.LIGHTNING)) {
			this.fireProof = true;
		} else if (sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.SEEKER) {
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(this.goalSelector, new UnsafeList());
				bField.set(this.targetSelector, new UnsafeList());
				cField.set(this.goalSelector, new UnsafeList());
				cField.set(this.targetSelector, new UnsafeList());
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.getNavigation();
			this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.5D, false));
			this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			// this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this,
			// true));
			this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
			// getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.5D);
		}
	}
	
	@Override
	public void move(double d0, double d1, double d2) {
		if (this.noclip && this.player.getLocation().distance(getBukkitEntity().getLocation()) > noclipDistance) {
			this.noclip = false;
		}
		super.move(d0, d1, d2);
	}

	public void g(double d0, double d1, double d2) {
	}

	public void g(float sideMot, float forMot) {
		if (this.sheep != null && this.onGround && this.sheep == fr.asynchronous.sheepwars.core.handler.Sheeps.REMOTE) {
			if (this.passenger == null || !(this.passenger instanceof EntityHuman)
					|| this.sheep != fr.asynchronous.sheepwars.core.handler.Sheeps.REMOTE) {
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
				Location location = getBukkitEntity().getLocation();
				if (!this.ground) {
					this.ground = (this.sheep.isFriendly() || this.onGround || this.inWater);
				} else {
					if (((this.sheep.isFriendly()) || (this.ticks <= this.defaultTicks - 20L))
							&& ((this.ticks == 0L) || (this.sheep.getAction().onTicking(this.player, this.ticks,
									getBukkitSheep(), this.plugin)) || (!isAlive()))) {
						boolean death = true;
						if (isAlive()) {
							die();
							death = false;
						}
						this.sheep.getAction().onFinish(this.player, getBukkitSheep(), death, this.plugin);
						return;
					}
					--this.ticks;
				}
				if (!this.onGround && this.ticksLived < 100 && !sheep.isFriendly()) {
					this.plugin.versionManager.getParticleFactory().playParticles(Particles.FIREWORKS_SPARK,
							location.add(0, 0.5, 0), 0.0F, 0.0F, 0.0F, 1, 0.0F);
				}
				this.explosion = !this.explosion;
			}
		} catch (Exception ex) {
			return;
		} finally {
			super.bL();
		}
		super.bL();
	}

	public void dropDeathLoot() {
		if (this.drop) {
			this.drop = false;
			if (getBukkitEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(plugin, getBukkitSheep().getKiller()).increaseSheepKilled(1);
					fr.asynchronous.sheepwars.core.handler.Sheeps.giveSheep(getBukkitSheep().getKiller(), this.sheep, this.plugin);
				}
			} else if (getBukkitEntity().getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
				if (getBukkitSheep().getKiller() instanceof Player) {
					PlayerData.getPlayerData(plugin, getBukkitSheep().getKiller()).increaseSheepKilled(1);
					getBukkitSheep().getWorld().dropItem(getBukkitSheep().getLocation(),
							this.sheep.getIcon(getBukkitSheep().getKiller()));
				}
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
		this.drop = false;
		getBukkitEntity().remove();
		this.plugin.versionManager.getWorldUtils().createExplosion(this.player, getBukkitSheep().getWorld(), this.locX,
				this.locY, this.locZ, power, breakBlocks, fire);
	}
}
