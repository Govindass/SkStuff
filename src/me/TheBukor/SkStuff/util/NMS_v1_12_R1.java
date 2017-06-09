package me.TheBukor.SkStuff.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagByte;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagFloat;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagLong;
import net.minecraft.server.v1_12_R1.NBTTagShort;
import net.minecraft.server.v1_12_R1.NBTTagString;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.TileEntity;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.EntityLiving;

public class NMS_v1_12_R1 implements NMSInterface {

	@Override
	public Object getNBTTag(Object compound, String tag) {
		if (compound instanceof NBTTagCompound) {
			return ((NBTTagCompound) compound).get(tag);
		}
		return null;
	}

	@Override
	public void setNBTTag(Object compound, String tag, Object toSet) {
		if (compound instanceof NBTTagCompound && (toSet instanceof NBTBase || toSet instanceof Number || toSet instanceof String)) {
			NBTBase converted = null;
			if (toSet instanceof Number) {
				converted = convertToNBT((Number) toSet);
			} else if (toSet instanceof String) {
				converted = convertToNBT((String) toSet);
			} else { //Already an NBTBase
				converted = (NBTBase) toSet; //No need to convert anything
			}
			((NBTTagCompound) compound).set(tag, converted);
		}
	}

	@Override
	public void removeNBTTag(Object compound, String tag) {
		if (compound instanceof NBTTagCompound) {
			((NBTTagCompound) compound).remove(tag);
		}
	}

	@Override
	public byte getTypeId(Object nbtBase) {
		if (nbtBase instanceof NBTBase) {
			return ((NBTBase) nbtBase).getTypeId();
		}
		return 0;
	}

	@Override
	public Object getNBTTagValue(Object compound, String tag, byte typeId) {
		if (compound instanceof NBTTagCompound) {
			switch (typeId) {
				case 1:
					return ((NBTTagCompound) compound).getByte(tag);
				case 2:
					return ((NBTTagCompound) compound).getShort(tag);
				case 3:
					return ((NBTTagCompound) compound).getInt(tag);
				case 4:
					return ((NBTTagCompound) compound).getLong(tag);
				case 5:
					return ((NBTTagCompound) compound).getFloat(tag);
				case 6:
					return ((NBTTagCompound) compound).getDouble(tag);
				case 7: //Byte array, only used in chunk files. Also doesn't have support for the MojangsonParser.
					break;
				case 8:
					return ((NBTTagCompound) compound).getString(tag);
				case 9:
					int i;
					NBTTagList list = null;
					for (i = 1; i <= 11; i++) { //To get a list I need to know the type of the tags it contains inside,
						//since I can't predict what type the list will have, I just loop all of the IDs until I find a non-empty list.
						list = ((NBTTagCompound) compound).getList(tag, i); //Try to get the list with the ID "loop-number".
						if (!list.isEmpty()) { //If list is not empty.
							break; //Stop loop.
						}
					}
					return list; //May be null
				case 10:
					return ((NBTTagCompound) compound).getCompound(tag);
				case 11: //Integer array, this one is only used on the chunk files (and maybe schematic files?).
					return ((NBTTagCompound) compound).getIntArray(tag);
				default: //This should never happen, but it's better to have this just in case it spills errors everywhere.
					break;
			}
		}
		return null;
	}

	@Override
	public void addToCompound(Object compound, Object toAdd) {
		if (compound instanceof NBTTagCompound && toAdd instanceof NBTTagCompound) {
			((NBTTagCompound) compound).a((NBTTagCompound) toAdd);
		}
	}

	@Override
	public void removeFromCompound(Object compound, String ... toRemove) {
		if (compound instanceof NBTTagCompound) {
			for (String s : toRemove) {
				((NBTTagCompound) compound).remove(s);
			}
		}
	}

	@Override
	public NBTTagCompound parseRawNBT(String rawNBT) {
		NBTTagCompound parsedNBT = null;
		try {
			parsedNBT = MojangsonParser.parse(rawNBT);
		} catch (MojangsonParseException ex) {
			Skript.warning("Error when parsing NBT - " + ex.getMessage());
			return null;
		}
		return parsedNBT;
	}

	@Override
	public Object[] getContents(Object nbtList) {
		if (nbtList instanceof NBTTagList) {
			Object[] contents = new Object[((NBTTagList) nbtList).size()];
			for (int i = 0; i < ((NBTTagList) nbtList).size(); i++) {
				if (getIndex(nbtList, i) != null) {
					contents[i] = getIndex(nbtList, i);
				}
			}
			return contents;
		}
		return null;
	}

	@Override
	public void addToList(Object nbtList, Object toAdd) {
		if (nbtList instanceof NBTTagList && toAdd instanceof NBTBase) {
			((NBTTagList) nbtList).add((NBTBase) toAdd);
		}
	}

	@Override
	public void removeFromList(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			((NBTTagList) nbtList).remove(index);
		}
	}

	@Override
	public void setIndex(Object nbtList, int index, Object toSet) {
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			if (toSet instanceof NBTBase) {
				((NBTTagList) nbtList).a(index, (NBTBase) toSet);
			} else if (toSet instanceof Number) {
				((NBTTagList) nbtList).a(index, convertToNBT((Number) toSet));
			} else if (toSet instanceof String) {
				((NBTTagList) nbtList).a(index, convertToNBT((String) toSet));
			}
		}
	}

	@Override
	public Object getIndex(Object nbtList, int index) {
		if (nbtList instanceof NBTTagList && index >= 0 && index < ((NBTTagList) nbtList).size()) {
			NBTBase value = ((NBTTagList) nbtList).h(index);
			if (value instanceof NBTTagByte) {
				return ((NBTTagByte) value).g(); //Byte stored inside a NBTNumber
			} else if (value instanceof NBTTagShort) {
				return ((NBTTagShort) value).f(); //Short inside a NBTNumber
			} else if (value instanceof NBTTagInt) {
				return ((NBTTagInt) value).e(); //Integer inside a NBTNumber
			} else if (value instanceof NBTTagLong) {
				return ((NBTTagLong) value).d(); //Long inside a NBTNumber
			} else if (value instanceof NBTTagFloat) {
				return ((NBTTagFloat) value).i(); //Float inside a NBTNumber
			} else if (value instanceof NBTTagDouble) {
				return ((NBTTagDouble) value).asDouble(); //Double inside a NBTNumber
			} else if (value instanceof NBTTagString) {
				return ((NBTTagString) value).c_(); //String inside the NBTTagString
			} else if (value instanceof NBTBase) {
				return value; //No need to convert this
			}
		}
		return null;
	}

	@Override
	public void clearPathfinderGoals(Entity entity) {
		EntityInsentient nmsEnt = (EntityInsentient) ((CraftEntity) entity).getHandle();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.goalSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
		((LinkedHashSet<?>) ReflectionUtils.getField("c", PathfinderGoalSelector.class, nmsEnt.targetSelector)).clear();
	}

	@Override
	public void removePathfinderGoal(Object entity, Class<?> goalClass, boolean isTargetSelector) {
		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).setGoalTarget(null);
			if (isTargetSelector) {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).targetSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
			} else {
				Iterator<?> goals = ((LinkedHashSet<?>) ReflectionUtils.getField("b", PathfinderGoalSelector.class, ((EntityInsentient) entity).goalSelector)).iterator();
				while (goals.hasNext()) {
					Object goal = goals.next();
					if (ReflectionUtils.getField("a", goal.getClass(), goal).getClass() == goalClass) {
						goals.remove();
					}
				}
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
	public void registerCompoundClassInfo() {
		Classes.registerClass(new ClassInfo<NBTTagCompound>(NBTTagCompound.class, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Compound").changer(new Changer<NBTTagCompound>() {

			@SuppressWarnings("unchecked")
			@Override
			@Nullable
			public Class<?>[] acceptChange(ChangeMode mode) {
				if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
					return CollectionUtils.array(String.class, NBTTagCompound.class);
				}
				return null;
			}

			@Override
			public void change(NBTTagCompound[] NBT, @Nullable Object[] delta, ChangeMode mode) {
				if (mode == ChangeMode.ADD) {
					if (delta[0] instanceof String) {
						NBTTagCompound parsedNBT = parseRawNBT((String) delta[0]);
						addToCompound(NBT[0], parsedNBT);
					} else {
						addToCompound(NBT[0], delta[0]);
					}
				} else if (mode == ChangeMode.REMOVE) {
					if (delta[0] instanceof NBTTagCompound)
						return;
					for (Object s : delta) {
						NBT[0].remove((String) s);
					}
				}
			}
		}).parser(new Parser<NBTTagCompound>() {

			@Override
			public String getVariableNamePattern() {
				return ".+";
			}

			@Override
			@Nullable
			public NBTTagCompound parse(String rawNBT, ParseContext context) {
				if (rawNBT.startsWith("nbt:{") && rawNBT.endsWith("}")) {
					NBTTagCompound NBT = parseRawNBT(rawNBT.substring(4));
					return NBT;
				}
				return null;
			}

			@Override
			public String toString(NBTTagCompound compound, int arg1) {
				return compound.toString();
			}

			@Override
			public String toVariableNameString(NBTTagCompound compound) {
				return compound.toString();
			}
		}).serializer(new Serializer<NBTTagCompound>() {

			@Override
			public Fields serialize(NBTTagCompound compound) throws NotSerializableException {
				Fields f = new Fields();
				f.putObject("asString", compound.toString());
				return f;
			}

			@Override
			public void deserialize(NBTTagCompound compound, Fields f) throws StreamCorruptedException, NotSerializableException {
				assert false;
			}

			@Override
			protected boolean canBeInstantiated() {
				return false;
			}

			@Override
			protected NBTTagCompound deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
				String raw = fields.getObject("asString", String.class);
				NBTTagCompound compound =  parseRawNBT(raw);
				if (compound == null) {
					throw new StreamCorruptedException("Unable to parse NBT from a variable: " + raw);
				}
				return compound;
			}

			@Override
			@Nullable
			public NBTTagCompound deserialize(String s) {
				NBTTagCompound compound =  parseRawNBT(s);
				return compound;
			}

			@Override
			public boolean mustSyncDeserialization() {
				return true;
			}
		}));

	}

	@Override
	public void registerNBTListClassInfo() {
		Classes.registerClass(new ClassInfo<NBTTagList>(NBTTagList.class, "nbtlist").user("nbt ?list ?(tag)?").name("NBT List").changer(new Changer<NBTTagList>() {

			@SuppressWarnings("unchecked")
			@Override
			@Nullable
			public Class<?>[] acceptChange(ChangeMode mode) {
				if (mode == ChangeMode.ADD) {
					return CollectionUtils.array(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class, NBTTagCompound.class, NBTTagList.class);
				}
				return null;
			}

			@Override
			public void change(NBTTagList[] nbtList, @Nullable Object[] delta, ChangeMode mode) {
				if (delta.length == 0)
					return;
				if (delta[0] instanceof Number) {
					addToList(nbtList[0], convertToNBT((Number) delta[0]));
				} else if (delta[0] instanceof String) {
					addToList(nbtList[0], convertToNBT((String) delta[0]));
				} else if (delta[0] instanceof NBTBase) {
					addToList(nbtList[0], delta[0]);
				}
			}
		}).parser(new Parser<NBTTagList>() {

			@Override
			public String getVariableNamePattern() {
				return ".+";
			}

			@Override
			@Nullable
			public NBTTagList parse(String listString, ParseContext context) {
				if ((listString.startsWith("nbt:[") || listString.startsWith("nbtlist:[")) && listString.endsWith("]")) {
					int substring;
					if (listString.startsWith("nbt:[")) {
						substring = 4;
					} else { // "nbtlist:[WHATEVER]"
						substring = 8;
					}
					NBTTagCompound tempNBT =  parseRawNBT("{temp:" + listString.substring(substring) + "}");
					NBTTagList parsedList = (NBTTagList) tempNBT.get("temp");
					return parsedList;
				}
				return null;
			}

			@Override
			public String toString(NBTTagList nbtList, int arg1) {
				return nbtList.toString();
			}

			@Override
			public String toVariableNameString(NBTTagList nbtList) {
				return nbtList.toString();
			}
		}).serializer(new Serializer<NBTTagList>() {

			@Override
			public Fields serialize(NBTTagList nbtList) throws NotSerializableException {
				Fields f = new Fields();
				f.putObject("asString", nbtList.toString());
				return f;
			}

			@Override
			public void deserialize(NBTTagList nbtList, Fields f) throws StreamCorruptedException, NotSerializableException {
				assert false;
			}

			@Override
			protected boolean canBeInstantiated() {
				return false;
			}

			@Override
			protected NBTTagList deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
				String s = fields.getObject("asString", String.class);
				NBTTagCompound tempNBT =  parseRawNBT("{temp:" + s + "}");
				if (tempNBT == null || !tempNBT.hasKey("temp")) {
					throw new StreamCorruptedException("Unable to parse NBT list from a variable: " + s);
				}
				NBTTagList nbtList = (NBTTagList) tempNBT.get("temp");
				return nbtList;
			}

			@Override
			@Nullable
			public NBTTagList deserialize(String s) {
				NBTTagCompound tempNBT =  parseRawNBT("{temp:" + s + "}");
				NBTTagList nbtList = (NBTTagList) tempNBT.get("temp");
				return nbtList;
			}

			@Override
			public boolean mustSyncDeserialization() {
				return true;
			}
		}));
	}

	@Override
	public NBTTagCompound getEntityNBT(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		NBTTagCompound NBT = new NBTTagCompound();
		nmsEntity.e(NBT);
		return NBT;
	}

	@Override
	public NBTTagCompound getTileNBT(Block block) {
		NBTTagCompound NBT = new NBTTagCompound();
		World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
		TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		if (tileEntity == null)
			return null;
		tileEntity.save(NBT);
		return NBT;
	}

	@Override
	public NBTTagCompound getItemNBT(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return null;
		NBTTagCompound itemNBT = CraftItemStack.asNMSCopy(itemStack).getTag();
		if (itemNBT == null)
			itemNBT = new NBTTagCompound();
		return itemNBT;
	}

	@Override
	public void setEntityNBT(Entity entity, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
			nmsEntity.f((NBTTagCompound) newCompound);
		}
	}

	@Override
	public void setTileNBT(Block block, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
			TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
			if (tileEntity == null)
				return;
			tileEntity.a((NBTTagCompound) newCompound);
			tileEntity.update();
			IBlockData tileEntType = nmsWorld.getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
			nmsWorld.notify(tileEntity.getPosition(), tileEntType, tileEntType, 3);
		}
	}

	@Override
	public ItemStack getItemWithNBT(ItemStack itemStack, Object compound) {
		net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		if (compound instanceof NBTTagCompound && itemStack != null) {
			if (itemStack.getType() == Material.AIR)
				return null;
			if (((NBTTagCompound) compound).isEmpty())
				return itemStack;
			nmsItem.setTag((NBTTagCompound) compound);
			ItemStack newItem = CraftItemStack.asBukkitCopy(nmsItem);
			return newItem;
		} else if (compound == null) {
			nmsItem.setTag(null);
			ItemStack newItem = CraftItemStack.asBukkitCopy(nmsItem);
			return newItem;
		}
		return itemStack;
	}

	@Override
	public NBTTagCompound getFileNBT(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			return null; //File doesn't exist.
		}
		NBTTagCompound fileNBT = null;
		try {
			fileNBT = NBTCompressedStreamTools.a(fis);
			fis.close();
		} catch (IOException ex) {
			if (ex instanceof EOFException) {
				; //Nothing.
			} else {
				ex.printStackTrace();
			}
		}
		return fileNBT;
	}

	@Override
	public void setFileNBT(File file, Object newCompound) {
		if (newCompound instanceof NBTTagCompound) {
			OutputStream os = null;
			try {
				os = new FileOutputStream(file);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			try {
				NBTCompressedStreamTools.a((NBTTagCompound) newCompound, os);
				os.close();
			} catch (IOException ex) {
				if (ex instanceof EOFException) {
					; //Ignore, just end of the file
				} else {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public NBTBase convertToNBT(Number number) {
		if (number instanceof Byte) {
			return new NBTTagByte((byte) number);
		} else if (number instanceof Short) {
			return new NBTTagShort((short) number);
		} else if (number instanceof Integer) {
			return new NBTTagInt((int) number);
		} else if (number instanceof Long) {
			return new NBTTagLong((long) number);
		} else if (number instanceof Float) {
			return new NBTTagFloat((float) number);
		} else if (number instanceof Double) {
			return new NBTTagDouble((double) number);
		}
		return null;
	}

	@Override
	public NBTTagString convertToNBT(String string) {
		return new NBTTagString(string);
	}

	@Override
	public String getMCId(ItemStack itemStack) {
		net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		MinecraftKey mcKey = Item.REGISTRY.b(nmsItem.getItem());
		return mcKey.toString();
	}

	@Override
	public ItemStack getItemFromMcId(String mcId) {
		MinecraftKey mcKey = new MinecraftKey(mcId);
		Item nmsItem = (Item) Item.REGISTRY.get(mcKey);
		return CraftItemStack.asNewCraftStack(nmsItem);
	}

	@Override
	public boolean getNoClip(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.noclip;
	}

	@Override
	public void setNoClip(Entity entity, boolean noclip) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.noclip = noclip;
	}

	@Override
	public boolean getFireProof(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.isFireProof();
	}

	@Override
	public void setFireProof(Entity entity, boolean fireProof) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		ReflectionUtils.setField("fireProof", nmsEntity.getClass(), nmsEntity, fireProof);
	}

	/*
	@SuppressWarnings("unchecked")
	@Override
	public ItemStack[] getEndermanBlocks(Entity enderman) {
		EntityEnderman nmsEnder = ((CraftEnderman) enderman).getHandle();
		Set<net.minecraft.server.v1_12_R1.Block> nmsBlocks = (Set<net.minecraft.server.v1_12_R1.Block>) ReflectionUtils.getField("c", EntityEnderman.class, nmsEnder);
		ItemStack[] items = new ItemStack[nmsBlocks.size()];
		int i = 0;
		for (net.minecraft.server.v1_12_R1.Block nmsBlock : nmsBlocks) {
			IBlockData nmsBlockData = nmsBlock.getBlockData();
			int dataValue = nmsBlock.toLegacyData(nmsBlockData);
			net.minecraft.server.v1_12_R1.ItemStack nmsItem = new net.minecraft.server.v1_12_R1.ItemStack(nmsBlock, 1, dataValue);
			ItemStack bukkitItem = CraftItemStack.asCraftMirror(nmsItem);
			items[i] = bukkitItem;
			i++;
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setEndermanBlocks(Entity enderman, ItemStack... blocks) {
		EntityEnderman nmsEnder = ((CraftEnderman) enderman).getHandle();
		Set<net.minecraft.server.v1_12_R1.Block> nmsBlocks = (Set<net.minecraft.server.v1_12_R1.Block>) ReflectionUtils.getField("c", EntityEnderman.class, nmsEnder);
		for (ItemStack block : blocks) {
			if (!block.getType().isBlock())
				return;
			// TODO Figure out how to get a Blocks from a Bukkit ItemStack
			// Blocks.class has a PRIVATE method to get from a MC id.
		}
	}
	*/

	@Override
	public Location getLastLocation(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		org.bukkit.World world = nmsEntity.world.getWorld();
		Location lastEntLoc = new Location(world, nmsEntity.M, nmsEntity.N, nmsEntity.O);
		return lastEntLoc;
	}

	@Override
	public float getEntityStepLength(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.P;
	}

	@Override
	public void setEntityStepLength(Entity entity, float length) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.P = length;
	}

	@Override
	public boolean getElytraGlideState(Entity entity) {
		EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
		return nmsEntity.getFlag(7);
	}

	public void setElytraGlideState(Entity entity, boolean glide) {
		EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
		nmsEntity.setFlag(7, glide);
	}

	public boolean getNoGravity(Entity entity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		return nmsEntity.isNoGravity();
	}

	public void setNoGravity(Entity entity, boolean noGravity) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		nmsEntity.setNoGravity(noGravity);
	}
}