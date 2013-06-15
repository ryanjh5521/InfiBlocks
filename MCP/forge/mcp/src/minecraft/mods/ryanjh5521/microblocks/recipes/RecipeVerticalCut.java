package mods.ryanjh5521.microblocks.recipes;


import java.util.HashMap;

import mods.ryanjh5521.core.BlockMetaPair;
import mods.ryanjh5521.microblocks.ItemMicroblock;
import mods.ryanjh5521.microblocks.MicroblockSystem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeVerticalCut implements IRecipe {
	private static HashMap<BlockMetaPair, ItemStack> damageMap = new HashMap<BlockMetaPair, ItemStack>();
	
	public static void addMap(BlockMetaPair a, ItemStack b)
	{
		damageMap.put(a, b);
	}

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		return getCraftingResult(inventorycrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		int n = 0;
		for(int k = 0; k < inventorycrafting.getSizeInventory(); k++) {
			if(inventorycrafting.getStackInSlot(k) != null)
				n++;
		}
		if(n != 2)
			return null;
		
		
		ItemStack is;
        for (int i = 0; i <= 2; i++)
            for (int j = 0; j <= 1; j++)
            {
            	is = check(inventorycrafting, i, j);
                if(is != null) return is;
            }
        return null;
	}
	
	private ItemStack check(InventoryCrafting i, int xo, int yo) {
		ItemStack saw = i.getStackInRowAndColumn(xo, yo);
		if(saw == null || saw.itemID != MicroblockSystem.itemSaw.itemID)
			return null;
		ItemStack block = i.getStackInRowAndColumn(xo, yo+1);
		if(block == null)
			return null;
		int inputMeta =
			block.itemID == MicroblockSystem.microblockContainerBlock.blockID
				? ItemMicroblock.getPartTypeID(block)
				: block.getItemDamage();
		return ItemStack.copyItemStack(damageMap.get(new BlockMetaPair(block.itemID, inputMeta)));
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(MicroblockSystem.microblockContainerBlock, 1, 0);
	}
	
	
}
