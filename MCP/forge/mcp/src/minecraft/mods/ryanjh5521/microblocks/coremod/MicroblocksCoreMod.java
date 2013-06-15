package mods.ryanjh5521.microblocks.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class MicroblocksCoreMod implements IFMLLoadingPlugin {

	public final static boolean MCP = MicroblocksCoreMod.class.getClassLoader().getResource("mods/ryanjh5521/microblocks/MicroblockSystem.class").toString().endsWith("/bin/mods/ryanjh5521/microblocks/MicroblockSystem.class");
	
	// if true, the mod will pretend not to exist, and will prevent all non-API classes from loading.
	public final static boolean TEST_DISABLED = MCP && false;
	
	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		if(TEST_DISABLED)
			return new String[] {
				"mods.ryanjh5521.microblocks.coremod.MicroblockSupporterTransformer",
				"mods.ryanjh5521.microblocks.coremod.DisableMicroblocksTransformer"
			};
		else
			return new String[] {
				"mods.ryanjh5521.microblocks.coremod.MicroblockSupporterTransformer"
			};
	}

	@Override
	public String getModContainerClass() {
		if(TEST_DISABLED)
			return null;
		else
			return "mods.ryanjh5521.microblocks.coremod.MicroblocksModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		if(!TEST_DISABLED)
			CoreModOptions.load((java.io.File)data.get("mcLocation"));
	}

}
