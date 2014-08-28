package com.gameminers.powerwolves.entity.render;

import org.lwjgl.opengl.GL11;

import com.gameminers.powerwolves.PowerWolvesMod;
import com.gameminers.powerwolves.entity.EntityPowerWolf;
import com.gameminers.powerwolves.enums.SpecialWolfType;
import com.gameminers.powerwolves.enums.WolfType;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class RenderPowerWolf extends RenderWolf {

	private static final ResourceLocation k9Tex = new ResourceLocation("powerwolves", "textures/entity/k9.png");
	private static final ResourceLocation wolfCollarTextures = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
	
	private final ModelBiped player = new ModelBiped();
	
	public RenderPowerWolf(ModelBase mainModel, ModelBase overlayModel, float shadowSize) {
		super(mainModel, overlayModel, shadowSize);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity ent) {
		EntityPowerWolf wolf = ((EntityPowerWolf)ent);
		WolfType t = wolf.getType();
		if (wolf.getSpecialType() == SpecialWolfType.K9) {
			return k9Tex;
		}
        return t == null ? PowerWolvesMod.wolfResources.get(WolfType.ARCTIC_WOLF) : PowerWolvesMod.wolfResources.get(t);
    }
	
	@Override
	public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_,
			double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
	
	@Override
	protected void renderEquippedItems(EntityLivingBase ent, float partialTicks) {
        super.renderEquippedItems(ent, partialTicks);
        EntityPowerWolf wolf = (EntityPowerWolf)ent;
        WolfType type = wolf.getType();
        Block mushroom = null;
        if (type == WolfType.BROWN_MUSHROLF) {
        	mushroom = Blocks.brown_mushroom;
        } else if (type == WolfType.RED_MUSHROLF) {
        	mushroom = Blocks.red_mushroom;
        }
        if (!ent.isChild() && mushroom != null) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPushMatrix();
            ((ModelWolf)mainModel).wolfBody.postRender(0.0625F);
            GL11.glScalef(0.5F, -0.5F, 0.5F);
            GL11.glTranslatef(0f, 0f, 0.9f);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ModelRenderer wolfMane = ObfuscationReflectionHelper.getPrivateValue(ModelWolf.class, ((ModelWolf)mainModel), 7);
            wolfMane.postRender(0.0625F);
            GL11.glScalef(0.5F, -0.5F, 0.5F);
            GL11.glTranslatef(0f, 0f, 1.0f);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ((ModelWolf)mainModel).wolfHeadMain.postRender(0.0625F);
            GL11.glScalef(0.5F, -0.5F, 0.5F);
            GL11.glTranslatef(0f, 0.9f, 0f);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }
	
	@Override
	protected int shouldRenderPass(EntityLivingBase ent, int pass, float partialTicks) {
		EntityPowerWolf wolf = (EntityPowerWolf)ent;
        if (pass == 0 && wolf.getWolfShaking()) {
            float brightness = wolf.getBrightness(partialTicks) * wolf.getShadingWhileShaking(partialTicks);
            bindTexture(getEntityTexture(ent));
            GL11.glColor3f(brightness, brightness, brightness);
            return 1;
        } else if (pass == 1 && wolf.isTamed()) {
        	ItemStack collar = wolf.getCollar();
        	if (collar == null) return -1;
        	int color;
        	NBTTagCompound nbt = collar.getTagCompound();
        	if (nbt != null && nbt.hasKey("Color")) {
        		color = nbt.getInteger("Color");
        	} else {
        		color = 0xFFFFFF;
        	}
        	int red = color >> 16 & 255;
            int green = color >> 8 & 255;
            int blue = color & 255;
            bindTexture(wolfCollarTextures);
            float s = 1.001f;
            GL11.glScalef(s, s, s);
            GL11.glColor3f(red/255f, green/255f, blue/255f);
            return collar.hasEffect(0) ? 15 : 1;
        } else if (pass == 2) {
        	return 0;
        } else if (pass == 3) {
        	return 0;
        } else {
            return -1;
        }
    }
}