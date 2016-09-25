package net.nexustools.chesty.network;

import cpw.mods.fml.client.FMLClientHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.nexustools.chesty.Chesty;
import net.nexustools.chesty.client.gui.GuiChesty;
import net.nexustools.chesty.entity.passive.EntityChesty;

public class PacketChestyOpen extends PacketChestyBase {

	public int chestyId;
	public int openWindowId;

	public PacketChestyOpen() {
	}

	public PacketChestyOpen(int chestyId, int openWindowId) {
		this.chestyId = chestyId;
		this.openWindowId = openWindowId;
	}

	@Override
	public int getID() {
		return PacketHandler.PACKET_OPEN_CHEST;
	}

	@Override
	public void readData(EntityPlayer player, DataInputStream data) throws IOException {
		chestyId = data.readInt();
		openWindowId = data.readInt();
		Entity e = player.worldObj.getEntityByID(chestyId);
		if(e == null || !(e instanceof EntityChesty)) {
			Chesty.getLogger().log(Level.WARNING, "Opening Chesty Packet but entity isn't valid.");
		}
		FMLClientHandler.instance().showGuiScreen(new GuiChesty(player.inventory, (EntityChesty)e));
		player.openContainer.windowId = openWindowId;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(chestyId);
		data.writeInt(openWindowId);
	}

}
