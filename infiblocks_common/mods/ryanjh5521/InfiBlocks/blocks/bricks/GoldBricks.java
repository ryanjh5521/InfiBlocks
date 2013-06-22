package mods.ryanjh5521.InfiBlocks.blocks.bricks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class GoldBricks extends Block {

        public GoldBricks (int id, int texture, Material material) {
                super(id, material);
        }
        
        @Override
        public void registerIcons(IconRegister iconRegister) {
                this.blockIcon = iconRegister.registerIcon("InfiBlocks:brickGold");
        }

}