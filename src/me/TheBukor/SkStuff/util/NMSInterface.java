package me.TheBukor.SkStuff.util;

import java.io.File;

import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface NMSInterface {






	public void clearPathfinderGoals(Entity entity);

	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector);

	public void addPathfinderGoal(Object entity, int priority, Object goal, boolean isTargetSelector);

	public boolean getNoClip(Entity entity);

	public void setNoClip(Entity entity, boolean noclip);

	public boolean getFireProof(Entity entity);

	public void setFireProof(Entity entity, boolean fireProof);




	public boolean getElytraGlideState(Entity entity);

	public void setElytraGlideState(Entity entity, boolean glide);
	
	public boolean getNoGravity(Entity entity);
	
	public void setNoGravity(Entity entity, boolean noGravity);
}