package me.TheBukor.SkStuff.pathfinders;

import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityCreature;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.PathfinderGoal;

import java.util.List;

public class PathfinderGoalFollow_v1_15_R1 extends PathfinderGoal {
	private EntityCreature follower;
	private EntityLiving followed;
	private Class<?> followedClass;
	private float radius;
	private double speed;
	private boolean isByName;
	private String customName;

	public PathfinderGoalFollow_v1_15_R1(EntityCreature follower, Class<?> followedClass, float radius, double speed, boolean isByName, String customName) {
		this.follower = follower;
		this.followedClass = followedClass;
		this.radius = radius;
		this.speed = speed;
		this.isByName = isByName;
		this.customName = customName;
	}

	// a() is shouldExecute()
	@SuppressWarnings("unchecked")
	@Override
	public boolean a() {
		if (followed == null) {
			List<?> list = follower.world.a((Class<? extends Entity>) followedClass, follower.getBoundingBox().grow(radius, 4.0D, radius));
			if (list.isEmpty()) {
				return false;
			}
			if (isByName) {
				for (Object entity : list) {
					if (((EntityLiving) entity).getCustomName().equals(customName)) {
						followed = (EntityLiving) entity;
						return true;
					}
				}
			} else {
				followed = (EntityLiving) list.get(0);
				return true;
			}
		}
		return true;
	}

	// b() is shouldContinueExecuting()
	@Override
	public boolean b() {
		if (followed.dead) {
			followed = null;
			return false;
		} else if (followed.h(follower) < 9.0D || followed.h(follower) > Math.pow(radius, 2)) {  // h() = distanceSquaredFrom()
			return false; // if 3 blocks away or not in radius, stop moving.
			//Maybe I'll add a teleport feature later.
		} else if (isByName) {
			if (!followed.getCustomName().equals(customName)) {
				followed = null;
				return false;
			}
		}
		//return follower.getNavigation().n(); // n() means hasNoPath()
		return true;
		// n() is now protected void and I'm not sure how to access it, so I'm just returning a boolean, this probably will cause some issues with this pathfinder goal, if you know a better solution, let me know. - Govindas.
	}

		// c() is execute()
		@Override
		public void c () {
			follower.getNavigation().a(followed, speed); // a() means moveTo()
		}


	}
