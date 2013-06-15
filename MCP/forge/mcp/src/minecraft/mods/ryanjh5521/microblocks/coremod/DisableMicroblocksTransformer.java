package mods.ryanjh5521.microblocks.coremod;

import cpw.mods.fml.relauncher.IClassTransformer;

public class DisableMicroblocksTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String originalName, String name, byte[] bytes) {
		if(name.startsWith("mods.ryanjh5521.microblocks.api"))
			return bytes;
		if(name.startsWith("mods.ryanjh5521.microblocks.coremod"))
			return bytes;
		if(name.equals("mods.ryanjh5521.microblocks.MicroblocksNonCoreMod"))
			return bytes;
		if(name.startsWith("mods.ryanjh5521.microblocks"))
			return null;
		return bytes;
	}

}
