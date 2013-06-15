package mods.ryanjh5521.microblocks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import mods.ryanjh5521.core.BlockMetaPair;
import mods.ryanjh5521.core.ImmibisCore;
import mods.ryanjh5521.core.api.APILocator;
import mods.ryanjh5521.core.api.IIDCallback;
import mods.ryanjh5521.core.api.multipart.util.BlockMultipartBase;
import mods.ryanjh5521.core.api.net.IPacket;
import mods.ryanjh5521.core.api.net.IPacketMap;
import mods.ryanjh5521.core.api.porting.SidedProxy;
import mods.ryanjh5521.microblocks.api.EnumPartClass;
import mods.ryanjh5521.microblocks.api.IMicroblockCoverSystem;
import mods.ryanjh5521.microblocks.api.IMicroblockSupporterTile;
import mods.ryanjh5521.microblocks.api.IMicroblockSystem;
import mods.ryanjh5521.microblocks.api.Part;
import mods.ryanjh5521.microblocks.api.PartType;
import mods.ryanjh5521.microblocks.coremod.BridgeClass1;
import mods.ryanjh5521.microblocks.coremod.CoreModOptions;
import mods.ryanjh5521.microblocks.coremod.OptionsFile.ItemListOption.ItemID;
import mods.ryanjh5521.microblocks.recipes.RecipeCombineSeveral;
import mods.ryanjh5521.microblocks.recipes.RecipeCombineTwo;
import mods.ryanjh5521.microblocks.recipes.RecipeHollowCover;
import mods.ryanjh5521.microblocks.recipes.RecipeHorizontalCut;
import mods.ryanjh5521.microblocks.recipes.RecipeUnHollowCover;
import mods.ryanjh5521.microblocks.recipes.RecipeVerticalCut;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class MicroblockSystem implements IMicroblockSystem {
	
	/*
	 * Part IDs are bitfields.
	 * 
	 * Bits 20-31 are block ID. (12 bits)
	 * If block ID is nonzero, part is an auto-detected or third-party one.
	 * 		10-19 are ID-specific. (10 bits)
	 * 		bit 9 is 1 for parts added by the system, 0 for other parts.
	 * 		bits 5-8 are reserved.
	 * 		bits 3-4 are part type.
	 * 		bits 0-2 are part size. 
	 * 
	 * If block ID is zero, part is one that was manually added in postinit.
	 * 		bits 5-19 are manually chosen ID.
	 * 		bits 3-4 are part type.
	 * 		bits 0-2 are part size.
	 * 
	 * All vanilla-based parts are manually added in postinit.
	 */
	public final static HashMap<Integer, PartType<?>> parts = new HashMap<Integer, PartType<?>>();
	
	public static BlockMultipartBase microblockContainerBlock;
	public static ItemSaw itemSaw;
	
	public static final String CHANNEL = "ryanjh5521Micro";
	
	public static final byte PKT_S2C_MICROBLOCK_CONTAINER_DESCRIPTION = 0;
	public static final byte PKT_S2C_MICROBLOCK_DESCRIPTION_WITH_WRAPPING = 1;
	public static final byte PKT_C2S_MICROBLOCK_PLACE = 2;
	
	
	public static MicroblockSystem instance;
	
	public static ArrayList<Integer> neiPartIDs = new ArrayList<Integer>();
	public static int neiMaxDamage = 0;
	
	public void postinit() {
		registerManualParts(1, Block.stone);
		//registerManualParts(2, Block.grass);
		registerManualParts(3, Block.dirt);
		registerManualParts(4, Block.cobblestone);
		registerManualParts(5, Block.planks, 0);
		registerManualParts(6, Block.planks, 1);
		registerManualParts(7, Block.planks, 2);
		registerManualParts(8, Block.planks, 3);
		registerManualParts(9, Block.bedrock);
		registerManualParts(10, Block.sand);
		registerManualParts(11, Block.gravel);
		registerManualParts(12, Block.oreGold);
		registerManualParts(13, Block.oreIron);
		registerManualParts(14, Block.oreCoal);
		registerManualParts(15, Block.wood, 0);
		registerManualParts(16, Block.wood, 1);
		registerManualParts(17, Block.wood, 2);
		registerManualParts(18, Block.wood, 3);
		//registerManualParts(19, Block.leaves, 0);
		//registerManualParts(20, Block.leaves, 1);
		//registerManualParts(21, Block.leaves, 2);
		//registerManualParts(22, Block.leaves, 3);
		registerManualParts(23, Block.sponge);
		registerManualParts(24, Block.glass);
		registerManualParts(25, Block.oreLapis);
		registerManualParts(26, Block.blockLapis);
		registerManualParts(27, Block.dispenser);
		registerManualParts(28, Block.sandStone);
		registerManualParts(29, Block.music);
		registerManualParts(30, Block.pistonStickyBase);
		registerManualParts(31, Block.pistonBase);
		registerManualParts(32, Block.cloth, 0);
		registerManualParts(33, Block.cloth, 1);
		registerManualParts(34, Block.cloth, 2);
		registerManualParts(35, Block.cloth, 3);
		registerManualParts(36, Block.cloth, 4);
		registerManualParts(37, Block.cloth, 5);
		registerManualParts(38, Block.cloth, 6);
		registerManualParts(39, Block.cloth, 7);
		registerManualParts(40, Block.cloth, 8);
		registerManualParts(41, Block.cloth, 9);
		registerManualParts(42, Block.cloth, 10);
		registerManualParts(43, Block.cloth, 11);
		registerManualParts(44, Block.cloth, 12);
		registerManualParts(45, Block.cloth, 13);
		registerManualParts(46, Block.cloth, 14);
		registerManualParts(47, Block.cloth, 15);
		registerManualParts(48, Block.blockGold);
		registerManualParts(49, Block.blockIron);
		registerManualParts(50, Block.brick);
		registerManualParts(51, Block.tnt);
		registerManualParts(52, Block.bookShelf);
		registerManualParts(53, Block.cobblestoneMossy);
		registerManualParts(54, Block.obsidian);
		registerManualParts(55, Block.mobSpawner);
		registerManualParts(56, Block.oreDiamond);
		registerManualParts(57, Block.blockDiamond);
		registerManualParts(58, Block.workbench);
		registerManualParts(59, Block.furnaceIdle);
		registerManualParts(60, Block.oreRedstone);
		registerManualParts(61, Block.blockSnow);
		registerManualParts(62, Block.blockClay);
		registerManualParts(63, Block.jukebox);
		registerManualParts(64, Block.pumpkin);
		registerManualParts(65, Block.netherrack);
		registerManualParts(66, Block.slowSand);
		registerManualParts(67, Block.glowStone);
		registerManualParts(68, Block.pumpkinLantern);
		registerManualParts(69, Block.stoneBrick);
		registerManualParts(70, Block.melon);
		registerManualParts(71, Block.mycelium);
		registerManualParts(72, Block.netherBrick);
		registerManualParts(73, Block.whiteStone, 0);
		registerManualParts(74, Block.whiteStone, 1);
		registerManualParts(75, Block.oreEmerald);
		registerManualParts(76, Block.blockEmerald);
		registerManualParts(77, Block.commandBlock);
		registerManualParts(78, Block.sandStone, 1);
		registerManualParts(79, Block.sandStone, 2);
		registerManualParts(80, Block.redstoneLampIdle);
		registerManualParts(81, Block.stoneBrick, 1);
		registerManualParts(82, Block.stoneBrick, 2);
		registerManualParts(83, Block.stoneBrick, 3);
		registerManualParts(84, Block.blockRedstone);
		registerManualParts(85, Block.oreNetherQuartz);
		registerManualParts(86, Block.blockNetherQuartz, 0);
		registerManualParts(87, Block.blockNetherQuartz, 1);
		registerManualParts(88, Block.blockNetherQuartz, 2);
		registerManualParts(89, Block.dropper);
		registerManualParts(90, Block.stoneDoubleSlab, 0, Block.stoneSingleSlab, 0);
		//registerManualParts(91, Block.stoneDoubleSlab, 1, Block.stoneSingleSlab, 1);
		//registerManualParts(92, Block.stoneDoubleSlab, 3, Block.stoneSingleSlab, 3);
		//registerManualParts(93, Block.stoneDoubleSlab, 4, Block.stoneSingleSlab, 4);
		//registerManualParts(94, Block.stoneDoubleSlab, 5, Block.stoneSingleSlab, 5);
		//registerManualParts(95, Block.stoneDoubleSlab, 6, Block.stoneSingleSlab, 6);
		//registerManualParts(96, Block.stoneDoubleSlab, 7, Block.stoneSingleSlab, 7);
		//registerManualParts(97, Block.woodDoubleSlab, 0, Block.woodSingleSlab, 0);
		//registerManualParts(98, Block.woodDoubleSlab, 1, Block.woodSingleSlab, 1);
		//registerManualParts(99, Block.woodDoubleSlab, 2, Block.woodSingleSlab, 2);
		//registerManualParts(100, Block.woodDoubleSlab, 3, Block.woodSingleSlab, 3);
		
		
		if(CoreModOptions.autoDetectCuttableBlocks) {
			CoreModOptions.manualCuttableBlocks.clear();
			autoDetectParts();
			CoreModOptions.autoDetectCuttableBlocks = false;
			CoreModOptions.save();
			
		} else {
			for(ItemID id : CoreModOptions.manualCuttableBlocks) {
				if(id.id >= Block.blocksList.length || id.id < 0 || Block.blocksList[id.id] == null)
					FMLRelaunchLog.log("ryanjh5521Microblocks", Level.WARNING, "cuttableBlock "+id.id+":"+id.meta+" specifies block ID "+id.id+" which is not a registered block");
				else
					addCuttableBlock(Block.blocksList[id.id], id.meta);
			}
		}
	}
	
	private boolean isSanelyTexturedNonVanillaBlock(ItemStack is) {
		if(is.itemID >= Block.blocksList.length)
			return false;
		try {
			Block b = Block.blocksList[is.itemID];
			if(b == null || b.blockID != is.itemID)
				return false;
			
			if(b.blockID <= Block.anvil.blockID || !b.getClass().getName().contains(".") || b.getClass().getName().startsWith("net.minecraft."))
				return false; // vanilla block
			
			String nameKey = is.getItem().getUnlocalizedName(is);
			if(nameKey == null || nameKey.equals("") || nameKey.equals("item."))
				return false;
			
			return true;
		} catch(Throwable e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	private void autoDetectParts() {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		for(Item i : Item.itemsList) {
			if(i != null) {
				try {
					i.getSubItems(i.itemID, null, itemList);
				} catch(NoSuchMethodError e) {
				}
			}
		}
		
		for(ItemStack is : itemList) {
			int meta = is.getItemDamage();
			if(meta > 1023 || meta < 0 || !isSanelyTexturedNonVanillaBlock(is)) {
				continue;
			}
			
			Block b = Block.blocksList[is.itemID];
			if(!b.isOpaqueCube()) {
				continue;
			}
			
			try {
				addCuttableBlock(b, meta);
				CoreModOptions.manualCuttableBlocks.add(new ItemID(b.blockID, meta));
			} catch(PartIDInUseException e) {
			}
		}
	}
	
	public void preinit() {
		BridgeClass1.isMinecraftLoaded = true;
		
	}
	
	public void init() {
		
		APILocator.getIDAllocator().requestBlock(ImmibisCore.instance, "blockMultipart", new IIDCallback() {
			@Override
			public void register(int id) {
				microblockContainerBlock = new BlockMicroblockContainer(id, Material.rock);
				microblockContainerBlock.setUnlocalizedName("ryanjh5521.microblock.container");
				LanguageRegistry.addName(microblockContainerBlock, "Microblock Container");
				GameRegistry.registerBlock(microblockContainerBlock, ItemMicroblock.class, "MicroblockContainer");
				
				if(!SidedProxy.instance.isDedicatedServer())
					MinecraftForgeClient.registerItemRenderer(microblockContainerBlock.blockID, new MicroblockItemRenderer());
			}
		});
		APILocator.getIDAllocator().requestItem(ImmibisCore.instance, "itemSaw", new IIDCallback() {
			@Override
			public void register(int id) {
				itemSaw = new ItemSaw(id - 256);
			}
		});
		APILocator.getIDAllocator().addRecipes(new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unchecked")
				List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
				
				recipes.add(new RecipeHollowCover());
				recipes.add(new RecipeUnHollowCover());
				recipes.add(new RecipeVerticalCut());
				recipes.add(new RecipeHorizontalCut());
				recipes.add(new RecipeCombineTwo());
				recipes.add(new RecipeCombineSeveral());
				
				GameRegistry.addRecipe(new ItemStack(itemSaw), new Object[] {
					"III",
					"DDI",
					'I', Item.ingotIron,
					'D', Item.diamond
				});
			}
		});
		
		APILocator.getNetManager().listen(new IPacketMap() {
			@Override
			public String getChannel() {return CHANNEL;}

			@Override
			public IPacket createS2CPacket(byte id) {
				if(id == PKT_S2C_MICROBLOCK_CONTAINER_DESCRIPTION)
					return new PacketMicroblockContainerDescription();
				if(id == PKT_S2C_MICROBLOCK_DESCRIPTION_WITH_WRAPPING)
					return new PacketMicroblockDescriptionWithWrapping();
				return null;
			}
			
			@Override
			public IPacket createC2SPacket(byte id) {
				if(id == PKT_C2S_MICROBLOCK_PLACE)
					return new PacketMicroblockPlace();
				return null;
			}
		});
		
		GameRegistry.registerTileEntity(TileMicroblockContainer.class, "ryanjh5521.multipart");
		
		if(!SidedProxy.instance.isDedicatedServer()) {
			MinecraftForge.EVENT_BUS.register(new MicroblockPlacementHighlightHandler());
		}
	}
	
	private static void registerManualParts(int n, Block block, int blockMeta) {
		registerManualParts(n, block, blockMeta, block, blockMeta);
	}
	
	private static void registerManualParts(int n, Block block) {
		registerManualParts(n, block, 0);
	}
	
	private static class PartRegistrationType {
		public EnumPartClass clazz;
		public double size;
		public String prefix, suffix;
		public PartRegistrationType(EnumPartClass c, double s, String pr, String su)
		{
			clazz = c;
			size = s;
			prefix = pr;
			suffix = su;
		}
	}
	
	private static PartRegistrationType blockparts[] = new PartRegistrationType[] {
		new PartRegistrationType(EnumPartClass.Panel, 1.0/8.0, "", " Cover"),
		new PartRegistrationType(EnumPartClass.Panel, 2.0/8.0, "", " Panel"),
		new PartRegistrationType(EnumPartClass.Panel, 3.0/8.0, "", " Triple Cover"),
		new PartRegistrationType(EnumPartClass.Panel, 4.0/8.0, "", " Slab"),
		new PartRegistrationType(EnumPartClass.Panel, 5.0/8.0, "", " Cover Slab"),
		new PartRegistrationType(EnumPartClass.Panel, 6.0/8.0, "", " Triple Panel"),
		new PartRegistrationType(EnumPartClass.Panel, 7.0/8.0, "", " Anticover"),
		null,
		new PartRegistrationType(EnumPartClass.Strip, 1.0/8.0, "", " Cover Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 2.0/8.0, "", " Panel Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 3.0/8.0, "", " Triple Cover Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 4.0/8.0, "", " Slab Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 5.0/8.0, "", " Cover Slab Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 6.0/8.0, "", " Triple Panel Strip"),
		new PartRegistrationType(EnumPartClass.Strip, 7.0/8.0, "", " Anticover Strip"),
		null,
		new PartRegistrationType(EnumPartClass.Corner, 1.0/8.0, "", " Cover Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 2.0/8.0, "", " Panel Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 3.0/8.0, "", " Triple Cover Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 4.0/8.0, "", " Slab Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 5.0/8.0, "", " Cover Slab Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 6.0/8.0, "", " Triple Panel Corner"),
		new PartRegistrationType(EnumPartClass.Corner, 7.0/8.0, "", " Anticover Corner"),
		null,
		new PartRegistrationType(EnumPartClass.HollowPanel, 1.0/8.0, "Hollow ", " Cover"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 2.0/8.0, "Hollow ", " Panel"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 3.0/8.0, "Hollow ", " Triple Cover"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 4.0/8.0, "Hollow ", " Slab"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 5.0/8.0, "Hollow ", " Cover Slab"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 6.0/8.0, "Hollow ", " Triple Panel"),
		new PartRegistrationType(EnumPartClass.HollowPanel, 7.0/8.0, "Hollow ", " Anticover"),
		null,
	};
	
	private static void registerManualParts(int n, Block block, int meta, Block craftingBlock, int craftingMeta) {
		registerParts(n*64, block, meta, craftingBlock, craftingMeta, false);
	}
	
	private static String getItemDisplayName(int itemID, int meta) {
		String nameKey = Item.itemsList[itemID].getUnlocalizedName(new ItemStack(itemID, 1, meta))+".name";
		String name = StringTranslate.getInstance().translateKey(nameKey);
		
		if(name.equals(nameKey) || name.equals("")) {
			name = LanguageRegistry.instance().getStringLocalization(nameKey);
			if(name == null || name.equals(nameKey) || name.equals("")) {
				name = LanguageRegistry.instance().getStringLocalization(nameKey, "en_US");
				if(name == null || name.equals(nameKey) || name.equals("")) {
					try {
						name = Item.itemsList[itemID].getItemDisplayName(new ItemStack(itemID, 1, meta));
					} catch(Throwable t) {
						name = null;
					}
					
					if(name == null || name.equals(nameKey) || name.equals("")) {
						return null;
					}
				}
			}
		}
		return name;
	}
	
	private static void registerParts(int partIDBase, Block block, int meta, Block craftingBlock, int craftingMeta, boolean ignoreNameCheck) {
		assert(blockparts.length == 32);
		
		String name = getItemDisplayName(craftingBlock.blockID, craftingMeta);
		if(name == null) {
			if(ignoreNameCheck)
				name = "Unknown";
			else
				return;
		}
		
		for(int k = 0; k < 7; k++)
		{
			// making hollow covers
			RecipeHollowCover.addMap(partIDBase + k, partIDBase + k + 24);
			// reverting hollow covers
			RecipeUnHollowCover.addMap(partIDBase + k + 24, partIDBase + k);
			
			// cutting panels into strips
			RecipeHorizontalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase + k), ItemMicroblock.getStackWithPartID(partIDBase + k + 8, 2));
			
			// cutting strips into corners
			RecipeHorizontalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase + k + 8), ItemMicroblock.getStackWithPartID(partIDBase + k + 16, 2));
			
			// combining corners into strips
			RecipeCombineTwo.addMap(partIDBase + k + 16, partIDBase + k + 8);
			
			// combining strips into panels
			RecipeCombineTwo.addMap(partIDBase + k + 8, partIDBase + k);
		}
		
		// combining multiple panels
		RecipeCombineSeveral.addMap(partIDBase, new ItemStack(craftingBlock, 1, craftingMeta));
		
		// combining multiple hollow panels
		RecipeCombineSeveral.addMap(partIDBase + 24, new ItemStack(craftingBlock, 1, craftingMeta));
		
		// cutting full blocks/slabs/panels
		RecipeVerticalCut.addMap(new BlockMetaPair(craftingBlock.blockID, craftingMeta), ItemMicroblock.getStackWithPartID(partIDBase+3, 2));
		RecipeVerticalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase+3), ItemMicroblock.getStackWithPartID(partIDBase+1, 2));
		RecipeVerticalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase+1), ItemMicroblock.getStackWithPartID(partIDBase+0, 2));
		
		// cutting hollow slabs/panels
		RecipeVerticalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase+27), ItemMicroblock.getStackWithPartID(partIDBase+25, 2));
		RecipeVerticalCut.addMap(new BlockMetaPair(microblockContainerBlock.blockID, partIDBase+25), ItemMicroblock.getStackWithPartID(partIDBase+24, 2));
		
		for(int k = 0; k < blockparts.length; k++)
			if(blockparts[k] != null)
			{
				String unlocalizedName = "ryanjh5521.microblocks."+(partIDBase+k);
				String localizedName = blockparts[k].prefix+name+blockparts[k].suffix;
				LanguageRegistry.instance().addStringLocalization(unlocalizedName+".name", localizedName);
				
				PartType<Part> type = new DefaultPartType(partIDBase+k,
					blockparts[k].clazz,
					blockparts[k].size,
					unlocalizedName,
					block,
					meta
				);
				RegisterPartType(partIDBase+k, type);
			}
	}
	
	public static void RegisterPartType(int id, PartType<?> type) {
		if(parts.containsKey(id))
			throw new PartIDInUseException(id, parts.get(id), type);
		parts.put(id, type);
		neiPartIDs.add(id);
	}

	@Override
	public IMicroblockCoverSystem createMicroblockCoverSystem(IMicroblockSupporterTile tile) {
		return new MicroblockCoverSystem(tile);
	}

	@Override
	public void addCuttableBlock(Block block, int meta) {
		if(block.blockID < 1 || block.blockID > 4095)
			throw new IllegalArgumentException("BlockID must be between 1 and 4095 inclusive");
		if(meta < 0 || meta > 1023)
			throw new IllegalArgumentException("meta must be between 0 and 1023 inclusive");
		registerParts(((block.blockID & 4095) << 20) | ((meta & 1023) << 10), block, meta, block, meta, true);
	}

	@Override
	public PartType<?> getPartTypeByID(int id) {
		return parts.get(id);
	}

	@Override
	public Block getMicroblockContainerBlock() {
		return microblockContainerBlock;
	}

	@Override
	public ItemStack partTypeIDToItemStack(int id, int stackSize) throws IllegalArgumentException {
		if(!parts.containsKey(id))
			throw new IllegalArgumentException("No part with ID "+id+" (hex: "+Integer.toHexString(id)+")");
		return ItemMicroblock.getStackWithPartID(id, stackSize);
	}

	@Override
	public int itemStackToPartID(ItemStack stack) throws NullPointerException, IllegalArgumentException {
		if(stack.itemID != microblockContainerBlock.blockID)
			throw new IllegalArgumentException("Not a stack of microblocks");
		return ItemMicroblock.getPartTypeID(stack);
	}
}
