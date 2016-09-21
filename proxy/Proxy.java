package net.nexustools.chesty.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.inventory.ContainerChesty;

/**
 *
 * @author Steve4448
 */
public class Proxy implements IGuiHandler {

	public Minecraft getClientInstance() {
		return FMLClientHandler.instance().getClient();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		System.out.println(FMLCommonHandler.instance().getSide().isClient());
		if(Chesty.getLastInteractRemote() != null) {
			switch(ID) {
				case 0:
					return new ContainerChesty(player.inventory, Chesty.getLastInteractRemote());
			}
		}
		return null;
	}
	
	public void loadRenderers() {}
}
