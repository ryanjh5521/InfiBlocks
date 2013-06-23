package mods.ryanjh5521.InfiBlocks.blocks.bricks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;

public class IronBricks extends Block {

    public IronBricks(int par1)
    {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
        
        @Override
        public void registerIcons(IconRegister iconRegister) {
                this.blockIcon = iconRegister.registerIcon("InfiBlocks:brickIron");
        }

}