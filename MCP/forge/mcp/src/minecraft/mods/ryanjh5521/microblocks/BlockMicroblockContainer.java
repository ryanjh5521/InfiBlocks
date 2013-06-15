package mods.ryanjh5521.microblocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.ryanjh5521.core.api.multipart.util.BlockMultipartBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMicroblockContainer extends BlockMultipartBase {
	protected BlockMicroblockContainer(int id, Material mat) {
		super(id, mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileMicroblockContainer();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
	}
}
