package com.gameminers.powerwolves;

import java.util.ArrayList;

import com.gameminers.powerwolves.item.ItemCollar;

import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesCollarDyes implements IRecipe {

    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        ItemStack itemstack = null;
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);

            if (itemstack1 != null) {
                if (itemstack1.getItem() == PowerWolvesMod.COLLAR) {
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.dye) {
                        return false;
                    }
                    arraylist.add(itemstack1);
                }
            }
        }

        return itemstack != null && !arraylist.isEmpty();
    }

    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        ItemStack itemstack = null;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemCollar collar = null;
        int k;
        int l;
        float f;
        float f1;
        int l1;

        for (k = 0; k < p_77572_1_.getSizeInventory(); ++k) {
            ItemStack itemstack1 = p_77572_1_.getStackInSlot(k);

            if (itemstack1 != null) {
                if (itemstack1.getItem() == PowerWolvesMod.COLLAR) {
                    collar = (ItemCollar)itemstack1.getItem();

                    itemstack = itemstack1.copy();
                    itemstack.stackSize = 1;

                    if (collar.hasColor(itemstack1)) {
                        l = collar.getColorFromItemStack(itemstack, 0);
                        f = (float)(l >> 16 & 255) / 255.0F;
                        f1 = (float)(l >> 8 & 255) / 255.0F;
                        float f2 = (float)(l & 255) / 255.0F;
                        i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int)((float)aint[0] + f * 255.0F);
                        aint[1] = (int)((float)aint[1] + f1 * 255.0F);
                        aint[2] = (int)((float)aint[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (itemstack1.getItem() != Items.dye) {
                        return null;
                    }

                    float[] afloat = EntitySheep.fleeceColorTable[BlockColored.func_150032_b(itemstack1.getItemDamage())];
                    int j1 = (int)(afloat[0] * 255.0F);
                    int k1 = (int)(afloat[1] * 255.0F);
                    l1 = (int)(afloat[2] * 255.0F);
                    i += Math.max(j1, Math.max(k1, l1));
                    aint[0] += j1;
                    aint[1] += k1;
                    aint[2] += l1;
                    ++j;
                }
            }
        }

        if (collar == null) {
            return null;
        } else {
            k = aint[0] / j;
            int i1 = aint[1] / j;
            l = aint[2] / j;
            f = (float)i / (float)j;
            f1 = (float)Math.max(k, Math.max(i1, l));
            k = (int)((float)k * f / f1);
            i1 = (int)((float)i1 * f / f1);
            l = (int)((float)l * f / f1);
            l1 = (k << 8) + i1;
            l1 = (l1 << 8) + l;
            collar.setColor(itemstack, l1);
            return itemstack;
        }
    }

    public int getRecipeSize() {
        return 10;
    }

    public ItemStack getRecipeOutput() {
        return null;
    }
}