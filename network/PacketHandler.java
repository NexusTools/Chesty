package net.nexustools.chesty.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHandler implements IPacketHandler {
	public static final String PACKET_CHANNEL_NAME = "NX|Chesty";
	public static final int PACKET_OPEN_CHEST = 0;
	public static final int PACKET_IRON_CHEST_SUPPORT = 1;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			switch(in.readByte()) {
				case PACKET_OPEN_CHEST:
					PacketChestyOpen chestyOpen = new PacketChestyOpen();
					chestyOpen.readData((EntityPlayer)player, in);
					break;
				case PACKET_IRON_CHEST_SUPPORT:
					PacketChestyIronChestSupport chestyIronChestSupport = new PacketChestyIronChestSupport();
					chestyIronChestSupport.readData((EntityPlayer)player, in);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
