package mods.ryanjh5521.core;


import java.util.ArrayList;
import java.util.List;

import mods.ryanjh5521.core.api.APILocator;
import mods.ryanjh5521.core.api.net.IPacket;
import mods.ryanjh5521.core.api.porting.PortableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

public abstract class TileCombined extends PortableTileEntity {
	
	public int redstone_output = 0;
	
	public List<ItemStack> getInventoryDrops() {
		return new ArrayList<ItemStack>();
	}

	public void onBlockNeighbourChange() {}
	public boolean onBlockActivated(EntityPlayer player) {return false;}
	public void onBlockRemoval() {}
	
	public void notifyNeighbouringBlocks() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
	}
	
	public void resendDescriptionPacket() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		IPacket p = getDescriptionPacket2();
		if(p == null)
			return null;
		Packet p2 = APILocator.getNetManager().wrap(p);
		p2.isChunkDataPacket = true;
		return p2;
	}
	
	public IPacket getDescriptionPacket2() {
		return null;
	}
	
	// look = The closest axis to the direction the player is looking towards
	public void onPlaced(EntityLiving player, int look) {}
	
	
	private void notifyComparator(int x, int y, int z) {
		if(worldObj.blockExists(x, y, z)) {
			int id = worldObj.getBlockId(x, y, z);
			if(id == Block.redstoneComparatorActive.blockID || id == Block.redstoneComparatorIdle.blockID)
				worldObj.notifyBlockOfNeighborChange(x, y, z, getBlockType().blockID);
		}
	}
	public void notifyComparators() {
		notifyComparator(xCoord-1, yCoord, zCoord);
        notifyComparator(xCoord+1, yCoord, zCoord);
        notifyComparator(xCoord, yCoord, zCoord-1);
        notifyComparator(xCoord, yCoord, zCoord+1);
        notifyComparator(xCoord-2, yCoord, zCoord);
        notifyComparator(xCoord+2, yCoord, zCoord);
        notifyComparator(xCoord, yCoord, zCoord-2);
        notifyComparator(xCoord, yCoord, zCoord+2);
	}
}
