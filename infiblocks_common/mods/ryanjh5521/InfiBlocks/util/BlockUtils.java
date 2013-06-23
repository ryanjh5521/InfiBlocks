package mods.ryanjh5521.InfiBlocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import com.google.common.base.Optional;

import mods.ryanjh5521.InfiBlocks.api.Blocks;
import mods.ryanjh5521.InfiBlocks.blocks.bricks.DiamondBricks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockUtils {
    public static void initBlocks()
    {
        // Block declaration
        Blocks.DiamondBricks = Optional.of((new DiamondBricks(ConfigUtils.DiamondBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickDiamond"));
        Blocks.GoldBricks = Optional.of((new DiamondBricks(ConfigUtils.GoldBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickGold"));
        Blocks.IronBricks = Optional.of((new DiamondBricks(ConfigUtils.IronBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickCobblestone"));
        Blocks.CobblestoneBricks = Optional.of((new DiamondBricks(ConfigUtils.CobbleBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickIron"));
    
    }

    public static void registerBlocks()
    {
        // Add block registration
        GameRegistry.registerBlock(Blocks.DiamondBricks.get(), "brickDiamond");
        GameRegistry.registerBlock(Blocks.CobblestoneBricks.get(), "brickCobblestone");
        GameRegistry.registerBlock(Blocks.IronBricks.get(), "brickIron");
        GameRegistry.registerBlock(Blocks.GoldBricks.get(), "brickGold");
    }
    
    
}