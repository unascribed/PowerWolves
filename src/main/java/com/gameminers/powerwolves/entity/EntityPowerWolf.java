package com.gameminers.powerwolves.entity;

import gminers.kitchensink.Strings;

import java.util.ArrayList;
import java.util.List;

import com.gameminers.powerwolves.PowerWolvesMod;
import com.gameminers.powerwolves.enums.SpecialWolfType;
import com.gameminers.powerwolves.enums.WolfType;
import com.gameminers.powerwolves.gui.GuiWolfInventory;
import com.gameminers.powerwolves.inventory.ContainerWolf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

public class EntityPowerWolf extends EntityWolf {

	public static final int ENTITY_ID = 0;
	private WolfType type = WolfType.ARCTIC_WOLF;
	public AnimalChest inventory = new AnimalChest("PowerWolfInventory", 3);
	
	public EntityPowerWolf(World w) {
		super(w);
	}

	@Override
	public void setLocationAndAngles(double x, double y,
			double z, float yaw, float pitch) {
		super.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(24, 0);
		dataWatcher.addObject(25, new ItemStack(PowerWolvesMod.COLLAR));
		dataWatcher.updateObject(25, null);
	}

	protected String getLivingSound() {
		if (isAngry()) {
			return getAngrySound();
		}
		if (rand.nextInt(3) == 0) {
			if (this.isTamed() && dataWatcher.getWatchableObjectFloat(18) < 10.0F) {
				return getWhineSound();
			} else {
				return getPantingSound();
			}
		}
		return getIdleSound();
	}

	protected String getAngrySound() {
		SpecialWolfType special = getSpecialType();
		if (special == SpecialWolfType.SHIZUNE) return null;
		return "mob.wolf.growl";
	}

	protected String getWhineSound() {
		SpecialWolfType special = getSpecialType();
		if (special == SpecialWolfType.SHIZUNE) return null;
		return "mob.wolf.whine";
	}

	protected String getPantingSound() {
		return "mob.wolf.panting";
	}

	protected String getIdleSound() {
		SpecialWolfType special = getSpecialType();
		if (special == SpecialWolfType.SHIZUNE) return null;
		return "mob.wolf.bark";
	}

	@Override
	protected String getHurtSound() {
		SpecialWolfType special = getSpecialType();
		if (special == SpecialWolfType.SHIZUNE) return "game.neutral.hurt";
		return "mob.wolf.hurt";
	}

	@Override
	protected String getDeathSound() {
		SpecialWolfType special = getSpecialType();
		if (special == SpecialWolfType.SHIZUNE) return "game.neutral.die";
		return "mob.wolf.death";
	}
	
	@Override
	protected String getSplashSound() {
		return "game.neutral.swim.splash";
	}
	
	@Override
	protected String getSwimSound() {
		return "game.neutral.swim";
	}

	@Override
	protected void updateAITick() {
		super.updateAITick();
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		if (!worldObj.isRemote && hasCollar() && hasCustomNameTag()) {
			EntityLivingBase owner = getOwner();
			if (owner instanceof EntityPlayer) {
				((EntityPlayer)owner).addChatMessage(source.func_151519_b(this));
			}
		}
	}

	public WolfType getType() {
		if (dataWatcher.getWatchableObjectInt(24) != type.ordinal()) {
			type = WolfType.values()[dataWatcher.getWatchableObjectInt(24)];
		}
		return type;
	}

	public void setType(WolfType type) {
		this.type = type;
		this.dataWatcher.updateObject(24, type.ordinal());
	}

	public ItemStack getCollar() {
		return dataWatcher.getWatchableObjectItemStack(25);
	}

	public void setCollar(ItemStack collar) {
		dataWatcher.updateObject(25, collar);
		inventory.setInventorySlotContents(0, collar);
	}

	public boolean hasCollar() {
		return getCollar() != null;
	}
	
	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		List<WolfType> types = new ArrayList<WolfType>();
		int x = (int)posX;
		int y = (int)posY;
		int z = (int)posZ;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
		System.out.println("Spawning wolf in biome "+biome.biomeName);
		wolves: for (WolfType w : WolfType.values()) {
			if (w.getBiomes().length == 0) {
				types.add(w);
				continue;
			}
			for (BiomeGenBase b : w.getBiomes()) {
				if (b == biome) {
					types.add(w);
					continue wolves;
				}
			}
		}
		String[] names = new String[types.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = types.get(i).getFriendlyName();
		}
		System.out.println("Candidate types: "+Strings.formatList(names));
		setType(types.get(getRNG().nextInt(types.size())));
		System.out.println("Chose type "+getType().getFriendlyName());
		return null;
	}

	@Override
	public boolean hasCustomNameTag() {
		if (!isTamed()) return false;
		ItemStack collar = getCollar();
		return collar == null ? false : collar.hasDisplayName();
	}

	@Override
	public String getCustomNameTag() {
		ItemStack collar = getCollar();
		return collar == null ? "" : collar.getDisplayName();
	}

	@Override
	public void setCustomNameTag(String name) {
		ItemStack collar = getCollar();
		if (collar == null) {
			// you're out of luck, buddy
		} else {
			collar.setStackDisplayName(name);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
	}
	
	public SpecialWolfType getSpecialType() {
		if (hasCustomNameTag()) {
			WolfType type = getType();
			for (SpecialWolfType swt : SpecialWolfType.values()) {
				if (!swt.getApplicableBreeds().contains(type)) continue;
				if (getCustomNameTag().equals(swt.getRequiredNameTag())) return swt;
			}
		}
		return null;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.hasKey("WolfType", 8)) {
			setType(WolfType.valueOf(tag.getString("WolfType").toUpperCase().replace(" ", "_")));
		} else if (tag.hasKey("WolfType", 1)) { 
			setType(WolfType.values()[tag.getByte("WolfType")]);
		} else {
			setType(WolfType.ARCTIC_WOLF);
		}
		if (tag.hasKey("CollarItem")) {
			setCollar(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("CollarItem")));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setByte("WolfType", (byte)type.ordinal());
		ItemStack collar = getCollar();
		if (collar != null) {
			tag.setTag("CollarItem", collar.writeToNBT(new NBTTagCompound()));
		} else {
			tag.removeTag("CollarItem");
		}
		tag.removeTag("CustomName");
	}

	public void openGUI(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
        	if (isTamed() && getOwner() == player) {
        		player.openGui(PowerWolvesMod.inst, getEntityId(), worldObj, (int)posX, (int)posY, (int)posZ);
        	} else {
        		playSoundSafely(getAngrySound(), getSoundVolume(), getSoundPitch());
        	}
        }
    }
	
	private void playSoundSafely(String sound, float volume, float pitch) {
		if (sound != null) playSound(sound, volume, pitch);
	}

	public String getCommandSenderName() {
        return hasCustomNameTag() ? this.getCustomNameTag() : I18n.format("entity.powerwolves.wolf.name");
    }
	
	@Override
	public boolean interact(EntityPlayer p) {
		ItemStack itemstack = p.inventory.getCurrentItem();
		if (p.isSneaking()) {
			if (isTamed() && p == getOwner()) {
				openGUI(p);
			}
			return itemstack != null && itemstack.getItem() == Items.name_tag;
		}
		if (itemstack != null && itemstack.getItem() == Items.name_tag) {
			super.interact(p);
			return true;
		} else {
			return super.interact(p);
		}
	}

	public void spawnTransmutationParticles() {
		for (int i = 0; i < 100; ++i) {
			double d2 = this.rand.nextGaussian() * 0.02D;
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle("witchMagic", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
		}
	}

	@Override
	public EntityPowerWolf createChild(EntityAgeable m8) {
		EntityPowerWolf mate = (EntityPowerWolf)m8;
		EntityPowerWolf child = new EntityPowerWolf(worldObj);
		String owner = func_152113_b();
		if (owner != null && owner.trim().length() > 0) {
			child.func_152115_b(owner);
			child.setTamed(true);
		}
		if (getRNG().nextBoolean()) {
			child.setType(getType());
		} else {
			child.setType(mate.getType());
		}
		return child;
	}
}
