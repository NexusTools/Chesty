package net.nexustools.chesty.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.nexustools.chesty.entity.passive.EntityChesty;

@SideOnly(Side.CLIENT)
public class ModelChesty extends ModelBase {

	public final ModelRenderer chestyBottom;
	public final ModelRenderer chestyLid;
	public final ModelRenderer chestyLock;
	public final ModelRenderer chestyLegFrontLeft;
	public final ModelRenderer chestyLegFrontRight;
	public final ModelRenderer chestyLegBackLeft;
	public final ModelRenderer chestyLegBackRight;

	public ModelChesty() {
		textureWidth = 64;
		textureHeight = 64;

		chestyBottom = new ModelRenderer(this, 0, 19);
		chestyBottom.addBox(-7F, -5F, -7F, 14, 10, 14);
		chestyBottom.setRotationPoint(0F, 13F, 0F);
		chestyBottom.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyBottom, 0F, 0F, 0F);
		chestyLid = new ModelRenderer(this, 0, 0);
		chestyLid.addBox(-7F, -3.99F, -14F, 14, 4, 14);
		chestyLid.setRotationPoint(0F, 8F, 7F);
		chestyLid.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLid, 0F, 0F, 0F);
		chestyLock = new ModelRenderer(this, 0, 0);
		chestyLock.addBox(-1F, -2F, -15F, 2, 4, 1);
		chestyLock.setRotationPoint(0F, 9F, 7F);
		chestyLock.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLock, 0F, 0F, 0F);
		chestyLegFrontLeft = new ModelRenderer(this, 0, 43);
		chestyLegFrontLeft.addBox(0F, 0F, -1.5F, 3, 7, 3);
		chestyLegFrontLeft.setRotationPoint(-6F, 16.5F, -4F);
		chestyLegFrontLeft.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLegFrontLeft, 0F, 0F, 0.3490659F);
		chestyLegFrontRight = new ModelRenderer(this, 0, 43);
		chestyLegFrontRight.addBox(-3F, 0F, -1.5F, 3, 7, 3);
		chestyLegFrontRight.setRotationPoint(6F, 16.5F, -5F);
		chestyLegFrontRight.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLegFrontRight, 0F, 0F, -0.3490659F);
		chestyLegBackLeft = new ModelRenderer(this, 0, 43);
		chestyLegBackLeft.addBox(0F, 0F, -1.5F, 3, 7, 3);
		chestyLegBackLeft.setRotationPoint(-6F, 16.5F, 4F);
		chestyLegBackLeft.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLegBackLeft, 0F, 0F, 0.3490659F);
		chestyLegBackRight = new ModelRenderer(this, 0, 43);
		chestyLegBackRight.addBox(-3F, 0F, -1.5F, 3, 7, 3);
		chestyLegBackRight.setRotationPoint(6F, 16.5F, 4F);
		chestyLegBackRight.setTextureSize(textureWidth, textureHeight);
		setRotation(chestyLegBackRight, 0F, 0F, -0.3490659F);
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
		setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
		chestyBottom.render(par7);
		chestyLid.render(par7);
		chestyLock.render(par7);
		chestyLegFrontLeft.render(par7);
		chestyLegFrontRight.render(par7);
		chestyLegBackLeft.render(par7);
		chestyLegBackRight.render(par7);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        this.chestyLegFrontLeft.rotateAngleX = MathHelper.cos(par1 * 0.9992F) * 1.4F * par2;
        this.chestyLegFrontRight.rotateAngleX = MathHelper.cos(par1 * 0.9992F + (float)Math.PI) * 1.4F * par2;
        this.chestyLegBackLeft.rotateAngleX = MathHelper.cos(par1 * 0.9992F + (float)Math.PI) * 1.4F * par2;
        this.chestyLegBackRight.rotateAngleX = MathHelper.cos(par1 * 0.9992F) * 1.4F * par2;
		
		EntityChesty chesty = (EntityChesty)par7Entity;
		chestyLid.rotateAngleX = -(chesty.lidAngle * (float)Math.PI / 2.0F); //TODO: find out how to smooth the animation more?
		chestyLock.rotateAngleX = chestyLid.rotateAngleX;
	}
}
