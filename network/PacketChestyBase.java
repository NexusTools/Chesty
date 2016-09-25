package net.nexustools.chesty.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public abstract class PacketChestyBase {

	protected String channel = PacketHandler.PACKET_CHANNEL_NAME;

	public abstract int getID();

	public Packet getPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeByte(getID());
			writeData(data);
		} catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;
		return packet;
	}

	public abstract void readData(EntityPlayer player, DataInputStream data) throws IOException;

	public abstract void writeData(DataOutputStream data) throws IOException;
}
