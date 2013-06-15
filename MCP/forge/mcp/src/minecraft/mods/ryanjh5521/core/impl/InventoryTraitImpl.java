package mods.ryanjh5521.core.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import mods.ryanjh5521.core.BasicInventory;
import mods.ryanjh5521.core.TileCombined;
import mods.ryanjh5521.core.api.traits.IInventoryTraitUser;
import mods.ryanjh5521.core.api.traits.IInventoryTrait;
import mods.ryanjh5521.core.api.traits.TraitClass;
import mods.ryanjh5521.core.api.traits.TraitMethod;

@TraitClass(interfaces={ISidedInventory.class})
public class InventoryTraitImpl implements IInventoryTrait {
	
	private BasicInventory inv;
	private IInventoryTraitUser tile;
	private String invName;
	
	public InventoryTraitImpl(Object tile) {
		try {
			this.tile = (IInventoryTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IPortableSidedInventory.", e);
		}
		
		if(!(tile instanceof TileCombined))
			throw new RuntimeException("Tile '"+tile+"' must extend TileCombined.");
		
		inv = new BasicInventory(this.tile.getInventorySize());
		
		invName = tile.getClass().getSimpleName().substring(0, 15);
	}

	@TraitMethod @Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return tile.getAccessibleSlots(var1);
	}
	
	@TraitMethod @Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return tile.canInsert(i, j, itemstack);
	}

	@TraitMethod @Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return tile.canExtract(i, j, itemstack);
	}
	
	@TraitMethod @Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return tile.canInsert(i, itemstack);
	}
	
	@TraitMethod @Override public int getSizeInventory() {return inv.contents.length;}
	@TraitMethod @Override public ItemStack getStackInSlot(int i) {return inv.contents[i];}
	@TraitMethod @Override public ItemStack decrStackSize(int i, int j) {return inv.decrStackSize(i, j);}
	@TraitMethod @Override public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack is = getStackInSlot(i);
		setInventorySlotContents(i, null);
		return is;
	}
	@TraitMethod @Override public void setInventorySlotContents(int i, ItemStack itemstack) {inv.contents[i] = itemstack;}
	@TraitMethod @Override public String getInvName() {return invName;}
	@TraitMethod @Override public boolean isInvNameLocalized() {return true;}
	@TraitMethod @Override public int getInventoryStackLimit() {return 64;}
	@TraitMethod @Override public void onInventoryChanged() {}
	@TraitMethod @Override public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		TileEntity te = (TileEntity)tile;
		return !te.isInvalid() && te.worldObj == entityplayer.worldObj && entityplayer.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5) <= 64;
	}
	@TraitMethod @Override public void openChest() {}
	@TraitMethod @Override public void closeChest() {}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		inv.readFromNBT(tag.getTagList("Items"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setTag("Items", inv.writeToNBT());
	}
	
	
	
	
	
	protected void dropStack(int slot, List<ItemStack> drops) {
		if(inv.contents[slot] != null)
		{
			if(inv.contents[slot].stackSize > 0)
				drops.add(inv.contents[slot]);
			
			inv.contents[slot] = null;
		}
	}
	
	// method from TileCombined
	@TraitMethod
	public List<ItemStack> getInventoryDrops() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(int k = 0; k < inv.contents.length; k++)
			dropStack(k, list);
		return list;
	}
}
