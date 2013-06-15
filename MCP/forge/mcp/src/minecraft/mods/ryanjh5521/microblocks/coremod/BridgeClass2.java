package mods.ryanjh5521.microblocks.coremod;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mods.ryanjh5521.microblocks.MicroblockSystem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BridgeClass2 {
	static {
		if(!BridgeClass1.isMinecraftLoaded)
			throw new AssertionError("can't touch this");
	}
	
	public static String getItemName(int id, int meta) {
		Item item = Item.itemsList[id];
		if(item == null)
			return "<invalid item>";
		String className = item.getClass().getName();
		if(item instanceof ItemBlock)
			className = Block.blocksList[item.itemID].getClass().getName();
		
		try {
			return item.getUnlocalizedName(new ItemStack(id, 1, meta))+", class "+className;
		} catch(Throwable t) {
			return "<error getting name: " + t + ">, class "+className;
		}
	}
	
	public static void preinit(FMLPreInitializationEvent evt) {
		MicroblockSystem.instance = new MicroblockSystem();
		MicroblockSystem.instance.preinit();
	}
	
	public static void init(FMLInitializationEvent evt) {
		MicroblockSystem.instance.init();
	}
	
	public static void postinit(FMLPostInitializationEvent evt) {
		MicroblockSystem.instance.postinit();
	}
}
