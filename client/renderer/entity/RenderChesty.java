package net.nexustools.chesty.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;

import net.nexustools.chesty.client.model.ModelChesty;

@SideOnly(Side.CLIENT)
public class RenderChesty extends RenderLiving {

	public RenderChesty() {
		super(new ModelChesty(), 0.5f);
	}
}
