package com.gameminers.powerwolves.item;

import java.util.List;

import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class ItemCollar extends Item {
	private IIcon collarBase;
	private IIcon collarStud;
	private IIcon collarStudDiamond;
	public ItemCollar() {
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("collar");
	}
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}
	@Override
	public int getColorFromItemStack(ItemStack is, int pass) {
		if (pass == 1) return 0xFFFFFF;
		if (is.hasTagCompound()) {
			if (is.getTagCompound().hasKey("Color")) {
				return is.getTagCompound().getInteger("Color");
			}
		}
		return 0xFFFFFF;
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		return "item."+(meta == 0 ? "collar" : "diamond_collar");
	}
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		int meta = stack.getItemDamage();
		return pass == 0 ? collarBase : meta == 0 ? collarStud : collarStudDiamond;
	}
	@Override
	public void registerIcons(IIconRegister registry) {
		collarBase = registry.registerIcon("powerwolves:collar");
		collarStud = registry.registerIcon("powerwolves:collar_stud");
		collarStudDiamond = registry.registerIcon("powerwolves:collar_stud_diamond");
	}
	public boolean hasColor(ItemStack is) {
		if (is.hasTagCompound()) {
			if (is.getTagCompound().hasKey("Color")) {
				return true;
			}
		}
		return false;
	}
	public void setColor(ItemStack p_82813_1_, int p_82813_2_) {
        NBTTagCompound nbttagcompound = p_82813_1_.getTagCompound();

        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
            p_82813_1_.setTagCompound(nbttagcompound);
        }

        nbttagcompound.setInteger("Color", p_82813_2_);
    }
	@Override
	public void addInformation(ItemStack stack, EntityPlayer p, List list, boolean advanced) {
		super.addInformation(stack, p, list, advanced);
		if (advanced) {
			list.add("Color: #"+Integer.toHexString(getColorFromItemStack(stack, 0)).toUpperCase());
		}
	}
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	@Override
	public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_,
			List p_150895_3_) {
		p_150895_3_.add(new ItemStack(p_150895_1_, 1, 0));
		p_150895_3_.add(new ItemStack(p_150895_1_, 1, 1));
	}
}

