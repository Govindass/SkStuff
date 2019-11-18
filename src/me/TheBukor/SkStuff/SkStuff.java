package me.TheBukor.SkStuff;

import java.io.IOException;

import javax.annotation.Nullable;

import me.TheBukor.SkStuff.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;



import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.TheBukor.SkStuff.effects.EffClearPathGoals;
import me.TheBukor.SkStuff.effects.EffGZipFile;
import me.TheBukor.SkStuff.effects.EffMakeJump;
import me.TheBukor.SkStuff.effects.EffRemovePathGoal;
import me.TheBukor.SkStuff.effects.EffResourceSound;
import me.TheBukor.SkStuff.effects.EffSetPathGoal;
import me.TheBukor.SkStuff.effects.EffShowEntityEffect;
import me.TheBukor.SkStuff.expressions.ExprClickedInventory;

import me.TheBukor.SkStuff.expressions.ExprFireProof;
import me.TheBukor.SkStuff.expressions.ExprGlideState;
import me.TheBukor.SkStuff.expressions.ExprInventoryOwner;



import me.TheBukor.SkStuff.expressions.ExprNoClip;
import me.TheBukor.SkStuff.expressions.ExprNoGravityState;

import me.TheBukor.SkStuff.expressions.ExprTimespanToNumber;
import me.TheBukor.SkStuff.expressions.ExprToLowerCase;
import me.TheBukor.SkStuff.expressions.ExprToUpperCase;
import me.TheBukor.SkStuff.expressions.ExprWordsToUpperCase;

public class SkStuff extends JavaPlugin {
	private int condAmount = 0;
	private int effAmount = 0;
	private int evtAmount = 0;
	private int exprAmount = 0;
	private int typeAmount = 0;

	private static NMSInterface nmsMethods;

	@SuppressWarnings("rawtypes")
	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("Skript") != null && Skript.isAcceptRegistrations()) {
			Skript.registerAddon(this);
			getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully enabled!");
			getLogger().info("Registering general non version specific stuff...");
			Skript.registerEffect(EffShowEntityEffect.class, "(display|play|show) entity effect (0¦firework[s] explo(de|sion)|1¦hurt|2¦[[iron] golem] (give|offer) (rose|poppy)|3¦[sheep] eat grass|4¦wolf shake) at %entity%");
			Skript.registerExpression(ExprToUpperCase.class, String.class, ExpressionType.SIMPLE, "%string% [converted] to [all] (cap[ital]s|upper[ ]case)", "convert %string% to [all] (cap[ital]s|upper[ ]case)", "capitalize [all] [char[acter]s (of|in)] %string%");
			Skript.registerExpression(ExprToLowerCase.class, String.class, ExpressionType.SIMPLE, "%string% [converted] to [all] lower[ ]case", "convert %string% to [all] lower[ ]case", "un[( |-)]capitalize [all] [char[acter]s (of|in)] %string%");
			Skript.registerExpression(ExprWordsToUpperCase.class, String.class, ExpressionType.SIMPLE, "(first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% [converted] to (cap[ital]s|upper[ ]case) (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])", "convert (first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% to (cap[ital]s|upper[ ]case) (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])", "capitalize (first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])");
			Skript.registerExpression(ExprTimespanToNumber.class, Number.class, ExpressionType.SIMPLE, "%timespan% [converted] [in]to (0¦ticks|1¦sec[ond]s|2¦min[ute]s|3¦hours|4¦days)");
			Skript.registerExpression(ExprClickedInventory.class, Inventory.class, ExpressionType.SIMPLE, "[skstuff] clicked inventory");
			Skript.registerExpression(ExprInventoryOwner.class, Object.class, ExpressionType.PROPERTY, "[inventory] (owner|holder) of %inventory%", "%inventory%'s [inventory] (owner|holder)");
			effAmount += 1;
			exprAmount += 6;
			if (Skript.isRunningMinecraft(1, 9)) {
				Skript.registerEffect(EffResourceSound.class, "play [raw] [([resource[ ]]pack)] sound %string% (for|to) %players% at %location% [[with] volume %-number%[[(,| and)] pitch %-number%]]", "play [raw] [([resource[ ]]pack)] sound %string% for %players% at %location% [[with] pitch %-number%[[(,| and)] volume %-number%]]");
				Skript.registerEvent("Elytra glide toggle", SimpleEvent.class, EntityToggleGlideEvent.class, "[entity] elytra (fl(y|ight)|glid(e|ing)) toggl(e|ing)", "[entity] toggle elytra (fl(y|ight)|glid(e|ing))");
				Skript.registerExpression(ExprGlideState.class, Boolean.class, ExpressionType.PROPERTY, "elytra (fl(y|ight)|glid(e|ing)) state of %livingentity%", "%livingentity%'s elytra (fl(y|ight)|glid(e|ing)) state");
				//Skript.registerExpression(ExprPlayerChargeTime.class, Float.class, ExpressionType.PROPERTY, "blah of %player%", "%player%'s blah");
				//Increase exprAmount when I uncomment this
				EventValues.registerEventValue(EntityToggleGlideEvent.class, Entity.class, new Getter<Entity, EntityToggleGlideEvent>() {
					@Override
					@Nullable
					public Entity get(EntityToggleGlideEvent e) {
						return e.getEntity();
					}
				}, 0);
				effAmount += 1;
				evtAmount += 1;
				exprAmount += 1;
				if (Skript.isRunningMinecraft(1, 10)) {
					Skript.registerExpression(ExprNoGravityState.class, Boolean.class, ExpressionType.PROPERTY, "no gravity (state|mode) of %entities%", "%entities%'s no gravity (state|mode)");
					exprAmount += 1;
				}
			}
			if (setupNMSVersion()) {
				getLogger().info("Trying to register version specific stuff...");
				Skript.registerEffect(EffClearPathGoals.class, "(clear|delete) [all] pathfind[er] goals (of|from) %livingentities%");
				Skript.registerEffect(EffRemovePathGoal.class, "remove pathfind[er] goal (0¦(avoid|run away from) entit(y|ies)|1¦break door[s]|2¦breed|3¦eat grass|4¦(flee from the sun|seek shad(e|ow))|5¦float (in[side]|on) water|6¦follow (owner|tamer)|7¦follow (adult|parent)[s]|8¦(fight back|react to|target) (damager|attacker)|9¦o(c|z)elot jump on blocks|10¦leap at target|11¦look at entit(y|ies)|12¦melee attack entit(y|ies)|13¦move to[wards] target|14¦target nearest entity|15¦o(c|z)elot attack [chicken[s]]|16¦open door[s]|17¦(panic|flee)|18¦look around randomly|19¦(walk around randomly|wander)|20¦sit|21¦[creeper] (explode|inflate|swell)|22¦squid (swim|wander)|23¦shoot fireball[s]|24¦[silverfish] hide (in[side]|on) block[s]|25¦(wake other silverfish[es]|[silverfish] call (help|reinforcement|other [hidden] silverfish[es]))|26¦[enderm(a|e)n] pick[[ ]up] block[s]|27¦[enderm(a|e)n] place block[s]|28¦[enderman] attack player (staring|looking) [at eye[s]]|29¦ghast move to[wards] target|30¦ghast (idle move[ment]|wander|random fl(ight|y[ing]))|31¦(tempt to|follow players (holding|with)) [a[n]] item|32¦target [random] entity (if|when) (not tamed|untamed)|33¦guardian attack [entity]|34¦[z[ombie[ ]]pig[man]] attack [player[s]] (if|when) angry|35¦[z[ombie[ ]]pig[man]] (react to|fight back|target) (attacker|damager) (if|when) angry|36¦[rabbit] eat carrot crops|37¦[killer] rabbit [melee] attack|38¦slime [random] jump|39¦slime change (direction|facing) randomly|40¦slime (idle move[ment]|wander)|41¦follow [entity]|42¦bow shoot) from %livingentities%");
				// Note to self: whenever adding a new pathfinder goal, increase the expression index for 'entities' in EffSetPathGoal 
				Skript.registerEffect(EffSetPathGoal.class, "add pathfind[er] goal [[with] priority %-integer%] (0¦(avoid|run away from) %*entitydatas%[, radius %-number%[, speed %-number%[, speed (if|when) (close|near) %-number%]]]|1¦break door[s]|2¦breed[,[move[ment]] speed %-number%]|3¦eat grass|4¦(flee from the sun|seek shad(e|ow))[, [move[ment]] speed %-number%]|5¦(float (in[side]|on) water|swim)|6¦follow (owner|tamer)[, speed %-number%[, min[imum] distance %-number%[, max[imum] distance %-number%]]]|7¦follow (adult|parent)[s][, [move[ment]] speed %-number%]|8¦(fight back|react to|target) (damager|attacker) [[of] type] %*entitydatas%[, call ([for] help|reinforcement) %-boolean%]|9¦o(c|z)elot jump on blocks[, [move[ment]] speed %-number%]|10¦leap at target[, [leap] height %-number%]|11¦look at %*entitydatas%[, (radius|max[imum] distance) %-number%]|12¦melee attack %*entitydatas%[, [move[ment]] speed %-number%[, (memorize|do('nt| not) forget) target [for [a] long[er] time] %-boolean%]]|13¦move to[wards] target[, [move[ment]] speed %-number%[, (radius|max[imum] distance) %-number%]]|14¦target nearest [entity [of] type] %*entitydatas%[, check sight %-boolean%]|15¦o(c|z)elot attack|16¦open door[s]|17¦(panic|flee)[, [move[ment]] speed %-number%]|18¦look around randomly|19¦(walk around randomly|wander)[, [move[ment]] speed %-number%[, min[imum] [of] %-timespan% between mov(e[ment][s]|ing)]]|20¦sit|21¦[creeper] (explode|inflate|swell)|22¦squid (swim around|wander)|23¦shoot fireball[s]|24¦[silverfish] hide (in[side]|on) block[s]|25¦((call|summon|wake) [other] [hidden] silverfish[es])|26¦[enderman] pick[[ ]up] block[s]|27¦[enderman] place block[s]|28¦[enderman] attack player (staring|looking) at [their] eye[s]]|29¦ghast move to[wards] target|30¦ghast (idle move[ment]|wander|random fl(ight|y[ing]))|31¦(tempt to|follow players (holding|with)) %-itemstack%[, [move[ment]] speed %number%[, scared of player movement %-boolean%]]|32¦target [random] %*entitydatas% (if|when) (not |un)tamed|33¦guardian attack [entities]|34¦[z[ombie[ ]]pig[man]] attack [player[s]] (if|when) angry|35¦[z[ombie[ ]]pig[man]] (react to|fight back|target) (attacker|damager) (if|when) angry|36¦[rabbit] eat carrot crops|37¦[killer] rabbit [melee] attack|38¦slime [random] jump|39¦slime change (direction|facing) randomly|40¦slime (idle move[ment]|wander)|41¦follow %*entitydatas%[, radius %-number%[, speed %-number%[, [custom[ ]]name[d] %-string%]]]|42¦bow shoot[, [move[ment]] speed %-number%[, unk[nown] param[eter] %-number%[, follow range %-number%]]])) to %livingentities%");
				Skript.registerEffect(EffMakeJump.class, "make %livingentities% jump", "force %livingentities% to jump");
				Skript.registerEffect(EffGZipFile.class, "create [a] gzip[ped] file [at] [path] %string%");
				Skript.registerExpression(ExprNoClip.class, Boolean.class, ExpressionType.PROPERTY, "no[( |-)]clip (state|mode) of %entities%", "%entities%'s no[( |-)]clip (state|mode)");
				Skript.registerExpression(ExprFireProof.class, Boolean.class, ExpressionType.PROPERTY, "fire[ ]proof (state|mode) of %entities%", "%entities%'s fire[ ]proof (state|mode)");

				effAmount += 5;
				exprAmount += 12;
				// 13 with the ender blocks expression
				typeAmount += 2;
			}
				effAmount += 1;
				evtAmount += 1;
				exprAmount += 1;
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
				getLogger().info("Hooked into Metrics! Woohoo!!");
			} catch (IOException ex) {
				getLogger().warning("Sorry, I've failed to hook SkStuff into Metrics. I'm really sorry.");
				getLogger().warning("Here's an error for you: " + ex.getMessage());
			}
			getLogger().info("Everything ready! Loaded a total of " + condAmount + " conditions, " + effAmount + " effects, " + evtAmount + " events, " + exprAmount + " expressions and " + typeAmount + " types!");
		} else {
			getLogger().info("Unable to find Skript or Skript isn't accepting registrations, disabling SkStuff...");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	private boolean setupNMSVersion() {
		String version = ReflectionUtils.getVersion();

		if (version.equals("v1_13_R2.")) {
			nmsMethods = new NMS_v1_13_R2();
			getLogger().info("It looks like you're running 1.13.2!");
		} else if (version.equals("v1_14_R1.")) {
			nmsMethods = new NMS_v1_14_R1();
			getLogger().info("It looks like you're running 1.14.2!");
		} else {
			getLogger().warning("It looks like you're running an unsupported server version, some features will not be available :(");
		}
		return nmsMethods != null;
	}

	public static NMSInterface getNMSMethods() {
		return nmsMethods;
	}

	public void onDisable() {
		getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully disabled.");
	}
}