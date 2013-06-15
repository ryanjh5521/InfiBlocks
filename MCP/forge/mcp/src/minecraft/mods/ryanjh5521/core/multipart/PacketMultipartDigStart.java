package mods.ryanjh5521.core.multipart;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.ryanjh5521.core.ImmibisCore;
import mods.ryanjh5521.core.api.multipart.util.BlockMultipartBase;
import mods.ryanjh5521.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;

public class PacketMultipartDigStart implements IPacket {
	
	public int x, y, z, part;
	public boolean isCSPart;

	public PacketMultipartDigStart(PartCoordinates coord) {
		this.x = coord.x;
		this.y = coord.y;
		this.z = coord.z;
		this.part = coord.part;
		this.isCSPart = coord.isCoverSystemPart;
	}
	
	public PacketMultipartDigStart() {
		
	}

	@Override
	public byte getID() {
		return ImmibisCore.PACKET_TYPE_C2S_MULTIPART_DIG_START;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		part = in.readInt();
		isCSPart = in.readBoolean();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeInt(part);
		out.writeBoolean(isCSPart);
	}

	@Override
	public void onReceived(EntityPlayer source) {
		if(source != null) {
			//System.out.println("received PMDS: "+source.username+" "+x+" "+y+" "+z+" "+part+" "+isCSPart);
			BlockMultipartBase.setBreakingPart(source, new PartCoordinates(x, y, z, part, isCSPart));
		}
	}
	
	@Override
	public String getChannel() {
		return ImmibisCore.CHANNEL;
	}

}
