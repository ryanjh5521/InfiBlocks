package mods.ryanjh5521.core;


import java.io.File;
import java.util.HashSet;

import mods.ryanjh5521.core.api.porting.SidedProxy;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class Config {
	private static File configFile;
	public static Configuration config;
	private static boolean autoAssign;
	
	static {
		File configFile;
		{
			File configDir = new File(SidedProxy.instance.getMinecraftDir(), "config");
			configDir.mkdir();
			configFile = new File(configDir, "ryanjh5521.cfg");
		}
		config = new Configuration(configFile);
		config.load();
		
		autoAssign = getBoolean("autoAssign", true);
		
		config.get(Configuration.CATEGORY_GENERAL, "autoAssign", false).set("false");
		config.save();
	}
	
	public static boolean getBoolean(String name, boolean def)
	{
		boolean result = config.get(Configuration.CATEGORY_GENERAL, name, def).getBoolean(def);
		config.save();
		return result;
	}
	
	private static HashSet<String> forceAllowReassign = new HashSet<String>();
	
	public static void save() {
		if(config.hasChanged())
			config.save();
	}
	
	public static int getItemID(String name, int def)
	{
		Property prop = config.get(Configuration.CATEGORY_ITEM, name, def);
		
		int id = prop.getInt();
		if(Item.itemsList[id] != null || id == 0)
		{
			if(!autoAssign && id != 0)
				throw new RuntimeException("Item "+name+" conflicts with "+Item.itemsList[id]+" ");
			id = -1;
			for(int k = Item.itemsList.length - 1; k >= 500; k--)
				if(Item.itemsList[k] == null)
				{
					id = k;
					break;
				}
			if(id == -1)
				throw new RuntimeException("You're out of item IDs! Oh no! Remove some mods! (And congratulations... there are OVER THIRTY THOUSAND of them!)");
			prop.set(String.valueOf(id));
			config.save();
		}
		return id;
	}
	
	public static int getItemID(String name) {
		return getItemID(name, 0); // always auto-assign
	}
	
	public static String getString(String name, String def, String category, String comment) {
		boolean save = !config.hasCategory(category) || config.getCategory(category).get(name) == null;
		Property prop = config.get(category, name, def);
		save |= (prop.comment == null && comment != null) || !prop.comment.equals(comment);
		prop.comment = comment;
		if(save)
			config.save();
		return prop.getString();
	}

	public static int getInt(String name, int def)
	{
		return config.get(Configuration.CATEGORY_GENERAL, name, def).getInt(def);
	}
	
	public static int getInt(String name, int def, String comment)
	{
		return getInt(name, def, Configuration.CATEGORY_GENERAL, comment);
	}
	
	public static int getInt(String name, int def, String category, String comment)
	{
		boolean save = !config.hasCategory(category) || config.getCategory(category).get(name) == null;
		Property prop = config.get(category, name, def);
		save |= (prop.comment == null && comment != null) || !prop.comment.equals(comment);
		prop.comment = comment;
		if(save)
			config.save();
		return prop.getInt(def);
	}
}
