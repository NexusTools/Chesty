package net.nexustools.chesty.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.nexustools.chesty.network.PacketChestyBase;

public class Proxy {
	
	public void sendToPlayer(EntityPlayer entityplayer, PacketChestyBase packet) {
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		player.playerNetServerHandler.sendPacketToPlayer(packet.getPacket());
	}
	
	public void loadRenderers() {}
}
