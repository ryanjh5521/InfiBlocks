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

@Mod(version=ryanjh5521Core.VERSION, modid=ryanjh5521Core.MODID, name=ryanjh5521Core.NAME, dependencies="before:IC2")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
@FMLModInfo(
		url="http://www.minecraftforum.net/topic/1001131-110-ryanjh5521s-mods-smp/",
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
		if(ryanjh5521Core.instance != null)
			disabled = true;
		else {
			ryanjh5521Core.instance = new ryanjh5521Core();
		}
	}
	
	@PreInit
	public void preinit(FMLPreInitializationEvent evt) {
		if(!disabled) {
			ryanjh5521Core.sidedProxy = sidedProxy;
			ryanjh5521Core.instance.preInit(evt);
		}
	}
	
	@Init
	public void init(FMLInitializationEvent evt) {
		if(!disabled)
			ryanjh5521Core.instance.init(evt);
	}
	
	@PostInit
	public void postinit(FMLPostInitializationEvent evt) {
		if(!disabled)
			ryanjh5521Core.instance.postInit(evt);
	}
}
