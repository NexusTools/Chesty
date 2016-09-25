package net.nexustools.chesty.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.nexustools.chesty.Chesty;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			switch(in.readByte()) {
				case Chesty.PACKET_OPEN_CHEST:
					PacketChestyOpen chestyOpen = new PacketChestyOpen();
					chestyOpen.readData((EntityPlayer)player, in);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
