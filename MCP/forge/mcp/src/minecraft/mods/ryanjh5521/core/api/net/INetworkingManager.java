package mods.ryanjh5521.core.api.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;

public interface INetworkingManager {
	public Packet wrap(IPacket packet);
	public void sendToServer(IPacket packet);
	public void sendToClient(IPacket packet, EntityPlayer target);
	
	public void sendToServer(ISimplePacket packet);
	public void sendToClient(ISimplePacket packet, EntityPlayerMP target);

	public void listen(IPacketMap handler);
}
