package net.nexustools.chesty.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.client.gui.GuiChesty;
import net.nexustools.chesty.client.renderer.entity.RenderChesty;
import net.nexustools.chesty.entity.passive.EntityChesty;

public class ClientProxy extends Proxy {

	@Override
	public void loadRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChesty.class, new RenderChesty());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(Chesty.getLastInteract() != null) {
			switch(ID) {
				case 0:
					return new GuiChesty(player.inventory, Chesty.getLastInteract());
			}
		}
		return null;
	}
}
