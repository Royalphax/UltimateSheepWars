package fr.asynchronous.sheepwars.v1_13_R2.entity;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.World;

public class InstantFireworksEntity extends EntityFireworks {

	public final boolean isInstantExplodingFirework;
	
	public InstantFireworksEntity(World world) {
		super(world);
		this.isInstantExplodingFirework = false;
	}

	public InstantFireworksEntity(World world, ItemStack itemstack) {
		super(world);
		this.isInstantExplodingFirework = true;
		a(0.25F, 0.25F);

		if ((itemstack != null) && (itemstack.hasTag())) {
			datawatcher.watch(8, itemstack);
		}
	}

	@Override
	public void t_() {
		if (this.isInstantExplodingFirework) {
			world.broadcastEntityEffect(this, (byte) 17);
			die();
		} else {
			super.t_();
		}
	}
}
