package mods.ryanjh5521.microblocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.ryanjh5521.core.ImmibisCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// A microblock description packet that also contains another arbitrary packet
public class PacketMicroblockDescriptionWithWrapping extends PacketMicroblockContainerDescription {
	
	public Packet wrappedPacket;
	
	@Override
	public byte getID() {
		return MicroblockSystem.PKT_S2C_MICROBLOCK_DESCRIPTION_WITH_WRAPPING;
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException {
		super.write(out);
		
		if(wrappedPacket == null)
			out.writeByte(0);
		else {
			out.writeByte(wrappedPacket.getPacketId());
			wrappedPacket.writePacketData(out);
		}
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		super.read(in);
		
		int packetID = in.readByte() & 255;
		if(packetID != 0) {
			wrappedPacket = Packet.getNewPacket(ImmibisCore.getLogAgent(), packetID);
			wrappedPacket.readPacketData(in);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onReceived(EntityPlayer source) {
		super.onReceived(source);
		
		if(wrappedPacket != null) {
			wrappedPacket.processPacket(Minecraft.getMinecraft().thePlayer.sendQueue);
		}
	}
}
