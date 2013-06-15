package mods.ryanjh5521.microblocks.coremod;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import mods.ryanjh5521.microblocks.coremod.OptionsFile.BooleanOption;
import mods.ryanjh5521.microblocks.coremod.OptionsFile.ItemAndMetaListOption;
import mods.ryanjh5521.microblocks.coremod.OptionsFile.StringListOption;
import mods.ryanjh5521.microblocks.coremod.OptionsFile.ItemListOption.ItemID;

import com.google.common.collect.ImmutableList;

public class CoreModOptions {
	public static boolean autoDetectCuttableBlocks;
	public static List<ItemID> manualCuttableBlocks;
	public static boolean enableNEI;
	
	public static final ImmutableList<String> DEFAULT_BLOCK_CLASSES = ImmutableList.of(
		"buildcraft.transport.BlockGenericPipe",
		"ic2.core.block.wiring.BlockCable",
		"appeng.common.base.AppEngMultiBlock",
		"powercrystals.minefactoryreloaded.block.BlockRedstoneCable",
		"micdoodle8.mods.galacticraft.core.blocks.GCCoreBlockOxygenPipe",
		"thermalexpansion.block.conduit.BlockConduit"
	);
	public static final ImmutableList<String> DEFAULT_TILE_CLASSES = ImmutableList.of(
		"buildcraft.transport.TileGenericPipe",
		"ic2.core.block.wiring.TileEntityCable",
		"appeng.me.tile.TileCable",
		"appeng.me.tile.TileDarkCable",
		"appeng.me.tile.TileInputCable",
		"appeng.me.tile.TileOutputCable",
		"appeng.me.tile.TileLevelEmitter",
		"appeng.me.tile.TileStorageBus",
		"appeng.me.tile.TileColorlessCable",
		"powercrystals.minefactoryreloaded.tile.TileRedstoneCable",
		"micdoodle8.mods.galacticraft.core.tile.GCCoreTileEntityOxygenPipe",
		"thermalexpansion.block.conduit.TileConduitEnergy",
		"thermalexpansion.block.conduit.TileConduitLiquid"
	);
	
	private static boolean loaded;
	private static File configFile;
	private static File configDir;
	
	private static OptionsFile of;
	
	private static void updateConfig() throws IOException {
		File oldConfigFile = new File(configDir, "ryanjh5521-coremod.cfg");
		if(oldConfigFile.exists()) {
			Properties props = new Properties();
			
			try {
				FileReader fr = new FileReader(oldConfigFile);
				try {
					props.load(fr);
				} finally {
					fr.close();
				}
			} catch(IOException ex) {
				throw new RuntimeException(ex);
			}
			
			StringListOption blockClasses = new StringListOption("blockClass");
			StringListOption tileClasses = new StringListOption("tileEntityClass");
			BooleanOption autoDetect = new BooleanOption("autoDetectCuttableBlocks");
			
			OptionsFile of = new OptionsFile();
			of.addOption(blockClasses);
			of.addOption(tileClasses);
			of.addOption(autoDetect);
			
			blockClasses.addValues(Arrays.asList(props.getProperty("microblockTransformer.blockClasses").split(";")));
			tileClasses.addValues(Arrays.asList(props.getProperty("microblockTransformer.tileEntityClasses").split(";")));
			autoDetect.set(props.getProperty("autoDetectCuttableBlocks", "false").equals("true"));
			
			of.write(configFile);
			
			oldConfigFile.delete();
		}
	}
	
	public static void load(File minecraftDir) {
		if(loaded)
			return;
		
		loaded = true;
		
		configDir = new File(minecraftDir, "config");
		if(!configDir.exists() && !configDir.mkdirs())
			throw new RuntimeException("Couldn't create directory: "+configDir);
		configFile = new File(configDir, "ryanjh5521-microblocks.cfg");
		
		try {
			updateConfig();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		StringListOption blockClasses = new StringListOption("blockClass", DEFAULT_BLOCK_CLASSES);
		StringListOption tileClasses = new StringListOption("tileEntityClass", DEFAULT_TILE_CLASSES);
		BooleanOption autoDetect = new BooleanOption("autoDetectCuttableBlocks", false);
		ItemAndMetaListOption cuttableBlocks = new ItemAndMetaListOption("cuttableBlock");
		BooleanOption enableNEIOption = new BooleanOption("enableNEI", true);
		
		of = new OptionsFile();
		of.addOption(blockClasses);
		of.addOption(tileClasses);
		of.addOption(autoDetect);
		of.addOption(cuttableBlocks);
		of.addOption(enableNEIOption);
		
		if(configFile.exists()) {
			try {
				of.read(configFile);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		manualCuttableBlocks = cuttableBlocks.get();
		autoDetectCuttableBlocks = autoDetect.get();
		enableNEI = enableNEIOption.get();
		MicroblockSupporterTransformer.blockClasses.addAll(blockClasses.get());
		MicroblockSupporterTransformer.tileClasses.addAll(tileClasses.get());
		
		save();
	}
	
	public static void save() {
		of.<ItemAndMetaListOption>getOption("cuttableBlock").set(manualCuttableBlocks);
		of.<BooleanOption>getOption("autoDetectCuttableBlocks").set(autoDetectCuttableBlocks);
		if(OptionsFile.DEBUG)
			System.out.println("[ryanjh5521's Microblocks DEBUG] Saving config file");
		try {
			of.write(configFile);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
