package mods.ryanjh5521.microblocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.ryanjh5521.core.api.porting.SidedProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSaw extends Item {

	protected ItemSaw(int i) {
		super(i);
		maxStackSize = 1;
		setUnlocalizedName("ryanjh5521.microblocks.saw");
		SidedProxy.instance.addLocalization("item.ryanjh5521.microblocks.saw.name", "Hacksaw");
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("ryanjh5521/microblocks:saw");
	}
	
	@Override
	public boolean hasContainerItem() {
		return true;
	}
	
	@Override
	public Item getContainerItem() {
		return this;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack is) {
		return false;
	}
	
}
