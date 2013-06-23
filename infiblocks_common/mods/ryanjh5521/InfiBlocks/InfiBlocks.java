package mods.ryanjh5521.InfiBlocks;

import mods.ryanjh5521.InfiBlocks.common.CommonProxy;
import mods.ryanjh5521.InfiBlocks.util.BlockUtils;
import mods.ryanjh5521.InfiBlocks.util.ConfigUtils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="InfiBlocks", name="InfiBlocks", version="0.00.001")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class InfiBlocks {

        // The instance of your mod that Forge uses.
        @Instance("InfiBlocks")
        public static InfiBlocks instance;
        // Says where the client and server 'proxy' code is loaded.
        @SidedProxy(clientSide="mods.ryanjh5521.InfiBlocks.client.ClientProxy", serverSide="mods.ryanjh5521.InfiBlocks.common.CommonProxy")
        public static CommonProxy proxy;
        
        @PreInit
        public void preInit(FMLPreInitializationEvent event) {
            ConfigUtils.init(event.getSuggestedConfigurationFile());
            BlockUtils.initBlocks();
            
            
    }
        
        @Init
        public void load(FMLInitializationEvent event) {
            BlockUtils.registerBlocks();
            BlockUtils.addRecipes();
            BlockUtils.addNames();
            
            
        }
        
        @PostInit
        public void postInit(FMLPostInitializationEvent event) {

        }
}