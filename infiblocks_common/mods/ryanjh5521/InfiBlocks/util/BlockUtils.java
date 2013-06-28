package mods.ryanjh5521.InfiBlocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import com.google.common.base.Optional;


import mods.ryanjh5521.InfiBlocks.InfiBlocks;
import mods.ryanjh5521.InfiBlocks.api.Blocks;
import mods.ryanjh5521.InfiBlocks.blocks.CobblestoneBricks;
import mods.ryanjh5521.InfiBlocks.blocks.DiamondBricks;
import mods.ryanjh5521.InfiBlocks.blocks.GoldBricks;
import mods.ryanjh5521.InfiBlocks.blocks.IronBricks;
import mods.ryanjh5521.InfiBlocks.blocks.Sheetrock;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockUtils {
    public static void initBlocks()
    {
        // Block declaration
        Blocks.DiamondBricks = Optional.of((new DiamondBricks(ConfigUtils.DiamondBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickDiamond"));
        Blocks.GoldBricks = Optional.of((new GoldBricks(ConfigUtils.GoldBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickGold"));
        Blocks.IronBricks = Optional.of((new IronBricks(ConfigUtils.IronBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickCobblestone"));
        Blocks.CobblestoneBricks = Optional.of((new CobblestoneBricks(ConfigUtils.CobbleBrickID)).setHardness(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("brickIron"));
    
    }

    public static void registerBlocks()
    {
        // Add block registration
        GameRegistry.registerBlock(Blocks.DiamondBricks.get(), "brickDiamond");
        GameRegistry.registerBlock(Blocks.CobblestoneBricks.get(), "brickCobblestone");
        GameRegistry.registerBlock(Blocks.IronBricks.get(), "brickIron");
        GameRegistry.registerBlock(Blocks.GoldBricks.get(), "brickGold");
        GameRegistry.registerBlock(Blocks.Sheetrock.get(), "blockSheetrock");
    }
    public static void addRecipes()
    {
        ItemStack diamondStack = new ItemStack(Block.blockDiamond, 1);
        ItemStack goldStack = new ItemStack(Block.blockGold, 1);
        ItemStack ironStack = new ItemStack(Block.blockIron, 1);
        ItemStack cobblestoneStack = new ItemStack(Block.cobblestone, 1);

        
        // Recipes
        GameRegistry.addRecipe(new ItemStack(Blocks.GoldBricks.get()), "xx", "xx", 
                'x', goldStack);
        
        GameRegistry.addRecipe(new ItemStack(Blocks.IronBricks.get()), "xx", "xx", 
                'x', ironStack);
        
        GameRegistry.addRecipe(new ItemStack(Blocks.DiamondBricks.get()), "xx", "xx", 
                'x', diamondStack);
        
        GameRegistry.addRecipe(new ItemStack(Blocks.CobblestoneBricks.get()), "xx", "xx", 
                'x', cobblestoneStack);
    }
    public static void addNames()
    {
        LanguageRegistry.addName(Blocks.DiamondBricks.get(), "Diamond Bricks");
        LanguageRegistry.addName(Blocks.GoldBricks.get(), "Gold Bricks");
        LanguageRegistry.addName(Blocks.CobblestoneBricks.get(), "Cobblestone Bricks");
        LanguageRegistry.addName(Blocks.IronBricks.get(), "Iron Bricks");
        LanguageRegistry.addName(Blocks.Sheetrock.get(), "Sheetrock");


    
    }
}