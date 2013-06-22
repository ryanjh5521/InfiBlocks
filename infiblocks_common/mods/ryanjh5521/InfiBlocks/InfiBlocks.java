package mods.ryanjh5521.InfiBlocks;

import net.minecraft.block.Block;
import mods.ryanjh5521.InfiBlocks.blocks.bricks.CobblestoneBricks;
import mods.ryanjh5521.InfiBlocks.blocks.bricks.DiamondBricks;
import mods.ryanjh5521.InfiBlocks.blocks.bricks.GoldBricks;
import mods.ryanjh5521.InfiBlocks.blocks.bricks.IronBricks;
import mods.ryanjh5521.InfiBlocks.common.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid="InfiBlocks", name="InfiBlocks", version="0.00.001")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class InfiBlocks {

        // The instance of your mod that Forge uses.
        @Instance("InfiBlocks")
        public static InfiBlocks instance;
        //Bricks
        public final static Block brickGold = new GoldBricks(500, 1, Material.anvil)
        .setHardness(35.0F).setUnlocalizedName("brickGold").setCreativeTab(CreativeTabs.tabBlock);
        
        public final static Block brickDiamond = new DiamondBricks(501, 1, Material.anvil)
        .setHardness(35.0F).setUnlocalizedName("brickDiamond").setCreativeTab(CreativeTabs.tabBlock);
        
        public final static Block brickCobblestone = new CobblestoneBricks(502, 1, Material.anvil)
        .setHardness(35.0F).setUnlocalizedName("brickCobblestone").setCreativeTab(CreativeTabs.tabBlock);
        
        public final static Block brickIron = new IronBricks(503, 1, Material.anvil)
        .setHardness(35.0F).setUnlocalizedName("brickIron").setCreativeTab(CreativeTabs.tabBlock);
        // Says where the client and server 'proxy' code is loaded.
        @SidedProxy(clientSide="mods.ryanjh5521.InfiBlocks.client.ClientProxy", serverSide="mods.ryanjh5521.InfiBlocks.common.CommonProxy")
        public static CommonProxy proxy;
        
        @PreInit
        public void preInit(FMLPreInitializationEvent event) {
       
        }
        
        @Init
        public void load(FMLInitializationEvent event) {
        	LanguageRegistry.addName(brickGold, "GoldBricks");
            MinecraftForge.setBlockHarvestLevel(brickGold, "pickaxe", 1);
            GameRegistry.registerBlock(brickGold, "brickGold");
            
            LanguageRegistry.addName(brickDiamond, "DiamondBricks");
            MinecraftForge.setBlockHarvestLevel(brickDiamond, "pickaxe", 3);
            GameRegistry.registerBlock(brickDiamond, "brickDiamond");
            
            LanguageRegistry.addName(brickCobblestone, "CobblestoneBricks");
            MinecraftForge.setBlockHarvestLevel(brickGold, "pickaxe", 0);
            GameRegistry.registerBlock(brickCobblestone, "brickCobblestone");
            
            LanguageRegistry.addName(brickIron, "IronBricks");
            MinecraftForge.setBlockHarvestLevel(brickIron, "pickaxe", 2);
            GameRegistry.registerBlock(brickIron, "brickIron");
        }
        
        @PostInit
        public void postInit(FMLPostInitializationEvent event) {

        }
}