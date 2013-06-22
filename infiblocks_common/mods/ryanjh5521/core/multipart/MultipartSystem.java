package mods.ryanjh5521.core.multipart;

import mods.ryanjh5521.core.api.porting.SidedProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class MultipartSystem {
	public static int multipartRenderType;
	
	public static void init() {
		multipartRenderType = RenderingRegistry.getNextAvailableRenderId();
		
		SidedProxy.instance.createSidedObject("mods.ryanjh5521.core.multipart.ClientProxy", null);
	}
}
