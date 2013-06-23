package mods.ryanjh5521.InfiBlocks.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.ryanjh5521.InfiBlocks.util.*;
public class Recipes {
    
    public static void addRecipes()
    {
        ItemStack diamondStack = new ItemStack(Block.blockDiamond, 1);
        ItemStack goldStack = new ItemStack(Block.blockGold, 1);
        ItemStack ironStack = new ItemStack(Block.blockIron, 1);
        ItemStack cobblestoneStack = new ItemStack(Block.cobblestone, 1);

        
        // Recipes
        GameRegistry.addRecipe(new ItemStack(brickGold), "xx", "xx", 
                'x', goldStack);
        
        GameRegistry.addRecipe(new ItemStack(brickIron), "xx", "xx", 
                'x', ironStack);
        
        GameRegistry.addRecipe(new ItemStack(brickDiamond), "xx", "xx", 
                'x', diamondStack);
        
        GameRegistry.addRecipe(new ItemStack(brickCobblestone), "xx", "xx", 
                'x', cobblestoneStack);
    }

}
