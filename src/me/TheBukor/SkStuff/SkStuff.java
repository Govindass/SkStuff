package me.TheBukor.SkStuff;

import java.io.IOException;

import javax.annotation.Nullable;

import me.TheBukor.SkStuff.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.mcstats.Metrics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.TheBukor.SkStuff.conditions.CondSelectionContains;
import me.TheBukor.SkStuff.effects.EffClearPathGoals;
import me.TheBukor.SkStuff.effects.EffDrainLiquid;
import me.TheBukor.SkStuff.effects.EffDrawLineWE;
import me.TheBukor.SkStuff.effects.EffGZipFile;
import me.TheBukor.SkStuff.effects.EffMakeCylinder;
import me.TheBukor.SkStuff.effects.EffMakeJump;
import me.TheBukor.SkStuff.effects.EffMakePyramid;
import me.TheBukor.SkStuff.effects.EffMakeSphere;
import me.TheBukor.SkStuff.effects.EffMakeWalls;
import me.TheBukor.SkStuff.effects.EffNaturalize;
import me.TheBukor.SkStuff.effects.EffPasteSchematic;
import me.TheBukor.SkStuff.effects.EffRememberChanges;
import me.TheBukor.SkStuff.effects.EffRemovePathGoal;
import me.TheBukor.SkStuff.effects.EffReplaceBlocksWE;
import me.TheBukor.SkStuff.effects.EffResourceSound;
import me.TheBukor.SkStuff.effects.EffSetBlocksWE;
import me.TheBukor.SkStuff.effects.EffSetPathGoal;
import me.TheBukor.SkStuff.effects.EffShowEntityEffect;
import me.TheBukor.SkStuff.effects.EffSimulateSnow;
import me.TheBukor.SkStuff.effects.EffToggleVanish;
import me.TheBukor.SkStuff.effects.EffUndoRedoSession;
import me.TheBukor.SkStuff.events.EvtWorldEditChange;
import me.TheBukor.SkStuff.events.WorldEditChangeHandler;
import me.TheBukor.SkStuff.expressions.ExprChangedBlocksSession;
import me.TheBukor.SkStuff.expressions.ExprClickedInventory;
import me.TheBukor.SkStuff.expressions.ExprEditSessionLimit;
import me.TheBukor.SkStuff.expressions.ExprFileNBT;
import me.TheBukor.SkStuff.expressions.ExprFireProof;
import me.TheBukor.SkStuff.expressions.ExprFlagOfWGRegion;
import me.TheBukor.SkStuff.expressions.ExprFlagsOfWGRegion;
import me.TheBukor.SkStuff.expressions.ExprGlideState;
import me.TheBukor.SkStuff.expressions.ExprInventoryOwner;
import me.TheBukor.SkStuff.expressions.ExprItemNBT;
import me.TheBukor.SkStuff.expressions.ExprLastLocation;
import me.TheBukor.SkStuff.expressions.ExprMCIdOf;
import me.TheBukor.SkStuff.expressions.ExprMCIdToItem;
import me.TheBukor.SkStuff.expressions.ExprNBTListContents;
import me.TheBukor.SkStuff.expressions.ExprNBTListIndex;
import me.TheBukor.SkStuff.expressions.ExprNBTOf;
import me.TheBukor.SkStuff.expressions.ExprNewEditSession;
import me.TheBukor.SkStuff.expressions.ExprNoClip;
import me.TheBukor.SkStuff.expressions.ExprNoGravityState;
import me.TheBukor.SkStuff.expressions.ExprRegionsWithinLocation;
import me.TheBukor.SkStuff.expressions.ExprSchematicArea;
import me.TheBukor.SkStuff.expressions.ExprSelectionArea;
import me.TheBukor.SkStuff.expressions.ExprSelectionOfPlayer;
import me.TheBukor.SkStuff.expressions.ExprSelectionPos;
import me.TheBukor.SkStuff.expressions.ExprStepLength;
import me.TheBukor.SkStuff.expressions.ExprSuperPickaxe;
import me.TheBukor.SkStuff.expressions.ExprTagOf;
import me.TheBukor.SkStuff.expressions.ExprTimespanToNumber;
import me.TheBukor.SkStuff.expressions.ExprToLowerCase;
import me.TheBukor.SkStuff.expressions.ExprToUpperCase;
import me.TheBukor.SkStuff.expressions.ExprVanishState;
import me.TheBukor.SkStuff.expressions.ExprWGMemberOwner;
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
				Skript.registerExpression(ExprNBTOf.class, Object.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %~object%", "%~object%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBT.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOf.class, Object.class, ExpressionType.PROPERTY, "[nbt[ ]]tag %string% of [[nbt] compound] %compound%");
				Skript.registerExpression(ExprFileNBT.class, Object.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Skript.registerExpression(ExprNBTListIndex.class, Object.class, ExpressionType.PROPERTY, "[nbt[ ]list] %nbtlist% index %number%");
				Skript.registerExpression(ExprNBTListContents.class, Object.class, ExpressionType.PROPERTY, "[all] contents (of|from) [nbt[ ]list] %nbtlist%", "[nbt[ ]list] %nbtlist% contents");
				Skript.registerExpression(ExprNoClip.class, Boolean.class, ExpressionType.PROPERTY, "no[( |-)]clip (state|mode) of %entities%", "%entities%'s no[( |-)]clip (state|mode)");
				Skript.registerExpression(ExprFireProof.class, Boolean.class, ExpressionType.PROPERTY, "fire[ ]proof (state|mode) of %entities%", "%entities%'s fire[ ]proof (state|mode)");
				//Skript.registerExpression(ExprEndermanBlocks.class, ItemStack.class, ExpressionType.PROPERTY, "blocks that %entity% can (carry|hold|grab|steal)");
				Skript.registerExpression(ExprMCIdOf.class, String.class, ExpressionType.PROPERTY, "(mc|minecraft) [(string|native)] id of %itemtype%", "%itemtype%'s minecraft [(string|native)] id");
				Skript.registerExpression(ExprMCIdToItem.class, ItemStack.class, ExpressionType.SIMPLE, "item[[ ](stack|type)] (of|from) (mc|minecraft) [(string|native)] id %string%");
				Skript.registerExpression(ExprLastLocation.class, Location.class, ExpressionType.SIMPLE, "[the] (last|past|former) location of %entity%", "%entity%'s (last|past|former) location", "[the] location of %entity% (1|one) tick before", "%entity%'s location (1|one) tick before");
				Skript.registerExpression(ExprStepLength.class, Number.class, ExpressionType.PROPERTY, "[the] step length of %entity%", "%entity%'s step length");
				nmsMethods.registerCompoundClassInfo();
				nmsMethods.registerNBTListClassInfo();
				effAmount += 5;
				exprAmount += 12;
				// 13 with the ender blocks expression
				typeAmount += 2;
			}
			if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
				getLogger().info("WorldEdit found! Registering WorldEdit stuff...");
				Skript.registerCondition(CondSelectionContains.class, "[(world[ ]edit|we)] selection of %player% (contains|has) %location%", "%player%'s [(world[ ]edit|we)] selection (contains|has) %location%", "[(world[ ]edit|we)] selection of %player% does(n't| not) (contain|have) %location%", "%player%'s [(world[ ]edit|we)] selection does(n't| not) (contain|have) %location%");
				Skript.registerEffect(EffDrawLineWE.class, "(create|draw|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) line from %location% to %location% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks% [with] thick[ness] %double%");
				Skript.registerEffect(EffUndoRedoSession.class, "(0¦undo|1¦redo) (change|edit)s (of|from) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffRememberChanges.class, "make %player% (remember|be able to undo) changes (of|from) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeSphere.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) (ellipsoid|sphere) [centered] at %location% [with] radius [of] %double%,[ ]%double%(,[ ]| and )%double% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffSimulateSnow.class, "(simulate snow at|place snow over) %location% (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%", "make %location% snowy (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakePyramid.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) pyramid at %location% [with] radius [of] %integer% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffDrainLiquid.class, "(drain|remove) [all] liquid[s] at %location% (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffNaturalize.class, "naturalize ([cuboid] region|[all] blocks) (from|between) %location% (to|and) %location% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeWalls.class, "(create|make) wall[s] from %location% to %location% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffSetBlocksWE.class, "set [all] blocks (from|between) %location% (to|and) %location% to %itemstacks% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeCylinder.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) cylinder at %location% [with] radius [of] %double%,[ ]%integer%(,[ ]| and )%double% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffReplaceBlocksWE.class, "replace [all] %itemstacks% (from|between) %location% (to|and) %location% with %itemstacks% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffPasteSchematic.class, "paste schem[atic] %string% at %location% using [edit[ ]session] %editsession% (0¦|1¦(ignor(e|ing)|without|[with] no) air)");
				Skript.registerExpression(ExprEditSessionLimit.class, Integer.class, ExpressionType.PROPERTY, "[block] limit [change] of [edit[ ]session] %editsession%");
				Skript.registerExpression(ExprChangedBlocksSession.class, Integer.class, ExpressionType.PROPERTY, "number of [all] changed blocks (in|of) [edit[ ]session] %editsession%");
				Skript.registerExpression(ExprNewEditSession.class, EditSession.class, ExpressionType.PROPERTY, "[new] edit[ ]session in [world] %world% [with] [max[imum]] [block] limit [change] [of] %integer%");
				Skript.registerExpression(ExprSelectionOfPlayer.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection");
				Skript.registerExpression(ExprSelectionPos.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ](0¦1|1¦2) of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ](0¦1|1¦2)");
				Skript.registerExpression(ExprSelectionArea.class, Integer.class, ExpressionType.SIMPLE, "(0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection (0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area)");
				Skript.registerExpression(ExprSchematicArea.class, Integer.class, ExpressionType.SIMPLE, "(0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area) of schem[atic] [from] %string%");
				Skript.registerExpression(ExprSuperPickaxe.class, Boolean.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] super[ ]pick[axe] (state|mode) of %players%", "%players%'s [(world[ ]edit|we)] super[ ]pick[axe] (state|mode)");
				Classes.registerClass(new ClassInfo<EditSession>(EditSession.class, "editsession").name("Edit Session").user("edit ?sessions?"));
				try {
					Class.forName("com.sk89q.worldedit.extent.logging.AbstractLoggingExtent");
					new WorldEditChangeHandler();
					Skript.registerEvent("WorldEdit block change", SimpleEvent.class, EvtWorldEditChange.class, "world[ ]edit [block] (chang(e|ing)|edit[ing])");
					EventValues.registerEventValue(EvtWorldEditChange.class, Player.class, new Getter<Player, EvtWorldEditChange>() {
						@Override
						@Nullable
						public Player get(EvtWorldEditChange e) {
							return EvtWorldEditChange.getPlayer();
						}
					}, 0);
					EventValues.registerEventValue(EvtWorldEditChange.class, Block.class, new Getter<Block, EvtWorldEditChange>() {
						@Override
						@Nullable
						public Block get(EvtWorldEditChange e) {
							return EvtWorldEditChange.getBlock();
						}
					}, 0);
					evtAmount += 1;
				} catch (ClassNotFoundException ex) {
					Skript.error("Unable to register \"On WorldEdit block change\" event! You will need to upgrade to WorldEdit 6.x if you want to use it!");
				}
				condAmount += 1;
				effAmount += 13;
				exprAmount += 8;
				typeAmount += 1;
				final Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
				if (worldGuard != null) { //WorldGuard depends on WorldEdit
					getLogger().info("WorldGuard found! Registering WorldGuard stuff...");
					Plugin umbaska = Bukkit.getPluginManager().getPlugin("Umbaska");
					Plugin skRambled = Bukkit.getPluginManager().getPlugin("SkRambled");
					boolean registerNewTypes = (umbaska == null && skRambled == null);
					if (registerNewTypes) {
						Skript.registerExpression(ExprFlagOfWGRegion.class, String.class, ExpressionType.PROPERTY, "[(world[ ]guard|wg)] flag %wgflag% of %wgregion%");
						Skript.registerExpression(ExprFlagsOfWGRegion.class, Flag.class, ExpressionType.PROPERTY, "[(all|the)] [(world[ ]guard|wg)] flags of %wgregion%");
						Skript.registerExpression(ExprWGMemberOwner.class, OfflinePlayer.class, ExpressionType.PROPERTY, "[the] (0¦members|1¦owner[s]) of [[the] (world[ ]guard|wg) region] %wgregion%");
						Classes.registerClass(new ClassInfo<Flag>(Flag.class, "wgflag").name("WorldGuard Flag").user("((world ?guard|wg) )?flags?").defaultExpression(new EventValueExpression<Flag>(Flag.class)).parser(new Parser<Flag<?>>() {

							@Override
							@Nullable
							public Flag<?> parse(String flag, ParseContext context) {
								FlagRegistry flagRegistry = ((WorldGuardPlugin) worldGuard).getFlagRegistry();
								return DefaultFlag.fuzzyMatchFlag(flagRegistry, flag);
							}

							@Override
							public String toString(Flag<?> flag, int flags) {
								return flag.getName().toLowerCase();
							}

							@Override
							public String toVariableNameString(Flag<?> flag) {
								return flag.getName().toLowerCase();
							}

							@Override
							public String getVariableNamePattern() {
								return ".+";
							}
						}));
						Classes.registerClass(new ClassInfo<ProtectedRegion>(ProtectedRegion.class, "wgregion").name("WorldGuard Region").user("((world ?guard|wg) )?regions?").defaultExpression(new EventValueExpression<>(ProtectedRegion.class)).parser(new Parser<ProtectedRegion>() {
	
							@Override
							@Nullable
							public ProtectedRegion parse(String region, ParseContext context) {
								for (World w : Bukkit.getWorlds()) {
									if (WGBukkit.getRegionManager(w).hasRegion(region)) {
										return WGBukkit.getRegionManager(w).getRegion(region);
									}
								}
								return null;
							}
	
							@Override
							public String toString(ProtectedRegion region, int flags) {
								return region.getId();
							}
	
							@Override
							public String toVariableNameString(ProtectedRegion region) {
								return region.getId();
							}
	
							@Override
							public String getVariableNamePattern() {
								return ".+";
							}
							
						}));
						typeAmount += 2;
					} else {
						Skript.registerExpression(ExprFlagOfWGRegion.class, String.class, ExpressionType.PROPERTY, "[skstuff] [w[orld[ ]]g[uard]] flag %flag% of %protectedregion%");
						Skript.registerExpression(ExprFlagsOfWGRegion.class, Flag.class, ExpressionType.PROPERTY, "[skstuff] [all] [w[orld[ ]]g[uard]] flags of %protectedregion%");
						Skript.registerExpression(ExprWGMemberOwner.class, OfflinePlayer.class, ExpressionType.PROPERTY, "[the] [skstuff] (0¦members|1¦owner[s]) of [[the] (world[ ]guard|wg) region] %protectedregion%");
					}
					Skript.registerExpression(ExprRegionsWithinLocation.class, ProtectedRegion.class, ExpressionType.SIMPLE, "[all] [the] regions (containing|within|inside) %location%");
					exprAmount += 4;
					
					if (!Skript.classExists("com.sk89q.protection.flags.registry.FlagRegistry")) {
						getLogger().warning("Detected old unsupported version of WorldGuard, please update to WorldGuard 6.2. Flags will not fully work with your current version!!");
					}
				}
			}
			if (Bukkit.getPluginManager().getPlugin("VanishNoPacket") != null) {
				getLogger().info("VanishNoPacket was found! Registering vanishing features...");
				Skript.registerEffect(EffToggleVanish.class, "toggle vanish (state|mode) of %player% (0¦|1¦(silently|quietly))", "toggle %player%'s vanish (state|mode) (0¦|1¦(silently|quietly))");
				Skript.registerEvent("Vanish toggle event", SimpleEvent.class, VanishStatusChangeEvent.class, "[player] vanish [(state|mode)] (chang|toggl)(e|ing)");
				Skript.registerExpression(ExprVanishState.class, Boolean.class, ExpressionType.PROPERTY, "vanish (state|mode) of %player%", "%player%'s vanish (state|mode)");
				EventValues.registerEventValue(VanishStatusChangeEvent.class, Player.class, new Getter<Player, VanishStatusChangeEvent>() {
					@Override
					@Nullable
					public Player get(VanishStatusChangeEvent e) {
						return e.getPlayer();
					}
				}, 0);
				effAmount += 1;
				evtAmount += 1;
				exprAmount += 1;
			}
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
		if (version.equals("v1_7_R4.")) {
			nmsMethods = new NMS_v1_7_R4();
			getLogger().info("It looks like you're running 1.7.10!");
		} else if (version.equals("v1_8_R3.")) {
			nmsMethods = new NMS_v1_8_R3();
			getLogger().info("It looks like you're either running 1.8.4, 1.8.7, 1.8.8 or 1.8.9!");
		} else if (version.equals("v1_9_R1.")) {
			nmsMethods = new NMS_v1_9_R1();
			getLogger().info("It looks like you're either running 1.9.0 or 1.9.2!");
		} else if (version.equals("v1_9_R2.")) {
			nmsMethods = new NMS_v1_9_R2();
			getLogger().info("It looks like you're running 1.9.4!");
		} else if (version.equals("v1_10_R1.")) {
			nmsMethods = new NMS_v1_10_R1();
			getLogger().info("It looks like you're running 1.10!");
		} else if (version.equals("v1_11_R1.")) {
			nmsMethods = new NMS_v1_11_R1();
			getLogger().info("It looks like you're running 1.11!");
		} else if (version.equals("v1_12_R1.")) {
			nmsMethods = new NMS_v1_12_R1();
			getServer().getConsoleSender().sendMessage("§c[SkStuff] You are running an unofficial fork of SkStuff, by Tuke_Nuke. Some nbt stuffs may not work properly!");
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