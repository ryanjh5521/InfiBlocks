package mods.ryanjh5521.core;

import mods.ryanjh5521.core.api.FMLModInfo;
import mods.ryanjh5521.core.api.porting.SidedProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(version=ImmibisCore.VERSION, modid=ImmibisCore.MODID, name=ImmibisCore.NAME)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
@FMLModInfo(
		url="http://www.minecraftforum.net/topic/1001131-",
		description="",
		authors="ryanjh5521"
		)
public class ICNonCoreMod {
	@cpw.mods.fml.common.SidedProxy(
			clientSide = "mods.ryanjh5521.core.porting.ClientProxy142",
			serverSide = "mods.ryanjh5521.core.porting.ServerProxy142")
	public static SidedProxy sidedProxy;
	
	public boolean disabled = false;
	
	public ICNonCoreMod() {
		if(ImmibisCore.instance != null)
			disabled = true;
		else {
			ImmibisCore.instance = new ImmibisCore();
		}
	}
	
	@PreInit
	public void preinit(FMLPreInitializationEvent evt) {
		if(!disabled) {
			ImmibisCore.sidedProxy = sidedProxy;
			ImmibisCore.instance.preInit(evt);
		}
	}
	
	@Init
	public void init(FMLInitializationEvent evt) {
		if(!disabled)
			ImmibisCore.instance.init(evt);
	}
	
	@PostInit
	public void postinit(FMLPostInitializationEvent evt) {
		if(!disabled)
			ImmibisCore.instance.postInit(evt);
	}
}
