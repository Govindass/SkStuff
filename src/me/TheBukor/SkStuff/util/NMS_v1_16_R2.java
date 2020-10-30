package me.TheBukor.SkStuff.util;


import net.minecraft.server.v1_16_R2.EntityInsentient;
import net.minecraft.server.v1_16_R2.PathfinderGoal;
import net.minecraft.server.v1_16_R2.PathfinderGoalSelector;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.LinkedHashSet;

public class NMS_v1_16_R2 implements NMSInterface {

	@Override
	public void clearPathfinderGoals(Entity entity) {
		Mob e = (Mob) entity;
		Bukkit.getMobGoals().removeAllGoals(e);

	}

	@Override
	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).setGoalTarget(null);
			if (isTargetSelector) {
				((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).targetSelector)).removeIf(goal -> ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass);
			} else {
				((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).goalSelector)).removeIf(goal -> ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass);
			}
		}
	}

	@Override
	public void addPathfinderGoal(Object entity, int priority, Object goal, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient && goal instanceof PathfinderGoal) {
			if (isTargetSelector)
				((EntityInsentient) entity).targetSelector.a(priority, (PathfinderGoal) goal);
			else
				((EntityInsentient) entity).goalSelector.a(priority, (PathfinderGoal) goal);
		}
	}


	@Override
	public boolean getNoClip(Entity entity) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.noclip;
	}

	@Override
	public void setNoClip(Entity entity, boolean noclip) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.noclip = noclip;
	}

	@Override
	public boolean getFireProof(Entity entity) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.isFireProof();
	}

	@Override
	public void setFireProof(Entity entity, boolean fireProof) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		ReflectionUtils.setField("fireProof", nmsEntity.getClass(), nmsEntity, fireProof);
	}
	public float getEntityStepLength(Entity entity) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.G;
	}

	@Override
	public void setEntityStepLength(Entity entity, float length) {
		net.minecraft.server.v1_16_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.G = length;
	}
}