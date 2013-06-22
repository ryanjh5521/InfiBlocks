package mods.ryanjh5521.core;

import java.util.logging.Logger;

import mods.ryanjh5521.cobaltite.impl.CobaltiteGUISystem;
import mods.ryanjh5521.core.api.APILocator;
import mods.ryanjh5521.core.api.net.IPacket;
import mods.ryanjh5521.core.api.net.IPacketMap;
import mods.ryanjh5521.core.api.porting.SidedProxy;
import mods.ryanjh5521.core.api.traits.IInventoryTrait;
import mods.ryanjh5521.core.api.traits.ITrait;
import mods.ryanjh5521.core.impl.*;
import mods.ryanjh5521.core.multipart.MultipartSystem;
import mods.ryanjh5521.core.multipart.PacketMultipartDigStart;
import mods.ryanjh5521.core.net.FragmentSequence;
import mods.ryanjh5521.core.net.PacketButtonPress;
import mods.ryanjh5521.core.net.PacketFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

public class ImmibisCore implements IPacketMap {
	
	public static final String VERSION = "55.1.3";
	public static final String MODID = "ryanjh5521Core";
	public static final String NAME = "ryanjh5521 Core";

	// 0 unused
	// 1 unused
	public static final int PACKET_TYPE_C2S_MULTIPART_DIG_START = 2;
	// 3 unused
	// 4 unused
	public static final int PACKET_TYPE_FRAGMENT = 5;
	public static final int PACKET_TYPE_C2S_BUTTON_PRESS = 6;
	


	public static final String CHANNEL = "ryanjh5521Core";
	public static SidedProxy sidedProxy;

	public static IDAllocator idAllocator = new IDAllocator();
	public static NetworkingManager networkingManager = new NetworkingManager();
	public static LocalizationManager localizationManager = new LocalizationManager();
	
	public static Logger LOGGER;
	static {
		FMLRelaunchLog.makeLog(MODID);
		LOGGER = Logger.getLogger(MODID);
	}
	
	public static java.util.Timer TIMER = new java.util.Timer("ryanjh5521 Core background task", true);

	public void preInit(FMLPreInitializationEvent evt) {
		
		SidedProxy.instance = sidedProxy;
		
		APILocator.getNetManager().listen(this);

		FragmentSequence.init();
		MainThreadTaskQueue.init();
		CobaltiteGUISystem.init();
	}

	public void init(FMLInitializationEvent evt) {
		
		MultipartSystem.init();
		
		idAllocator.allocate(new IIDSet() {
			@Override
			public int getIDFor(String name, IDType type, int _default) {
				switch(type) {
				case TerrainBlock:
					Property property = Config.config.getTerrainBlock(Configuration.CATEGORY_BLOCK, name, _default, null);
					if(!property.wasRead())
						Config.save();
					return property.getInt(_default);
				case Block:
					property = Config.config.getBlock(name+".id", _default);
					if(!property.wasRead())
						Config.save();
					return property.getInt(_default);
				case Item:
					property = Config.config.get(Configuration.CATEGORY_ITEM, name, _default);
					if(!property.wasRead())
						Config.save();
					return property.getInt(_default);
				}
				return 0;
			}
		});
	}

	public void postInit(FMLPostInitializationEvent evt) {
		
	}

	public static ImmibisCore instance;

	public ImmibisCore() {
		instance = this;
	}



	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		if(a == null && b == null)
			return true;
		if(a == null || b == null)
			return false;
		if(a.itemID != b.itemID)
			return false;
		if(a.getHasSubtypes() && a.getItemDamage() != b.getItemDamage())
			return false;
		if(a.stackTagCompound == null && b.stackTagCompound == null)
			return true;
		if(a.stackTagCompound == null || b.stackTagCompound == null)
			return false;
		return a.stackTagCompound.equals(b.stackTagCompound);
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}

	@Override
	public IPacket createS2CPacket(byte id) {
		if(id == PACKET_TYPE_FRAGMENT)
			return new PacketFragment();
		return null;
	}

	@Override
	public IPacket createC2SPacket(byte id) {
		if(id == PACKET_TYPE_C2S_BUTTON_PRESS)
			return new PacketButtonPress(0);
		if(id == PACKET_TYPE_C2S_MULTIPART_DIG_START)
			return new PacketMultipartDigStart();
		if(id == PACKET_TYPE_FRAGMENT)
			return new PacketFragment();
		return null;
	}

	static {
		((RelaunchClassLoader)ImmibisCore.class.getClassLoader()).registerTransformer(MultiInterfaceClassTransformer.class.getName());
		((RelaunchClassLoader)ImmibisCore.class.getClassLoader()).registerTransformer(TraitTransformer.class.getName());
		
		ITrait.knownInterfaces.put(IInventoryTrait.class, InventoryTraitImpl.class);
	}

	private static ILogAgent getClientLogAgent() {
		return Minecraft.getMinecraft().getLogAgent();
	}
	public static ILogAgent getLogAgent() {
		MinecraftServer sv = MinecraftServer.getServer();
		if(sv == null)
			return getClientLogAgent();
		return sv.getLogAgent();
	}

}
