package mods.ryanjh5521.InfiBlocks.util;

import java.io.File;
import java.util.logging.Level;

import mods.ryanjh5521.InfiBlocks.InfiBlocks;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public class ConfigUtils {

	public static Configuration config;

	//Block IDS
	public static int DiamondBrickID;
	public static int GoldBrickID;
	public static int IronBrickID;
	public static int CobbleBrickID;
	
	public static void init(File configFile)
	{
		config = new Configuration(configFile);

		try
		{
			config.load();
			//
			CobbleBrickID = config.getBlock("Cobblestone Bricks", 520, null).getInt();
			DiamondBrickID = config.getBlock("Diamond Bricks", 521, null).getInt();
			GoldBrickID = config.getBlock("Gold Bricks", 522, null).getInt();
            IronBrickID = config.getBlock("Iron Bricks", 1923, null).getInt();
			
			FMLCommonHandler.instance().getFMLLogger().log(Level.INFO, "[InfiBlocks] Generated Configuration File!");
		}
		catch (Exception e)
		{
			FMLLog.log(Level.SEVERE, e, "InfiBlocks was unable to load/create its config");
		}
		finally
		{
			if (config.hasChanged()) {
				config.save();
			}
		}
	}
}
