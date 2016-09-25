package net.nexustools.chesty.network;

import cpw.mods.fml.client.FMLClientHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.support.IronChestSupport;

public class PacketChestyIronChestSupport extends PacketChestyBase {

	@Override
	public int getID() {
		return PacketHandler.PACKET_IRON_CHEST_SUPPORT;
	}

	@Override
	public void readData(EntityPlayer player, DataInputStream data) throws IOException {
		//TODO: This might cause problems if the packet can be received after entities are received.
		Chesty.ironChestSupportForcedEnabled = data.readBoolean();
		if(Chesty.ironChestSupportEnabled && !Chesty.ironChestSupportForcedEnabled) {
			Chesty.getLogger().info("This server does not have IronChest support enabled.");
		}
		if(Chesty.ironChestSupportForcedEnabled && !Chesty.ironChestSupportEnabled) {
			Chesty.getLogger().info("Server has IronChest support enabled. Forcing enable.");
			IronChestSupport.init();
			if(IronChestSupport.ironChestEntries == null) {
				FMLClientHandler.instance().haltGame("IronChestSupport failed to load. Support is required to connect to this server.", new Throwable("IronChestSupport failed to load."));
			}
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeBoolean(Chesty.ironChestExists && Chesty.ironChestSupportEnabled);
	}
}
