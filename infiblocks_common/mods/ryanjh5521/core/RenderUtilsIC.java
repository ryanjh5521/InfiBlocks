package mods.ryanjh5521.core;
import mods.ryanjh5521.core.api.util.TextureSlice;
import mods.ryanjh5521.core.api.util.TextureStitchedNonSquare;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtilsIC {
	public static void setBrightness(IBlockAccess w, int x, int y, int z) {
		Tessellator.instance.setBrightness(w.getLightBrightnessForSkyBlocks(x, y, z, 0));
	}
	public static void setBrightnessDirect(IBlockAccess w, int x, int y, int z) {
		int i = w.getLightBrightnessForSkyBlocks(x, y, z, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i & 0xFFFF, i >> 16);
	}
	public static void setFullBrightness() {
		Tessellator.instance.setBrightness(0x00F000F0);
	}
	public static void renderCube(double x, double y, double z, double cx, double cy, double cz, int tex_top, int tex_bottom, int tex_side, double du, double dv) {
		Tessellator t = Tessellator.instance;
		float u, v;
		
		u = (tex_bottom & 15) / 16.0f; v = (tex_bottom / 16) / 16.0f;
		
		t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertexWithUV(x   , y, z   , u, v);
		t.addVertexWithUV(x+cx, y, z   , u+du, v);
		t.addVertexWithUV(x+cx, y, z+cz, u+du, v+dv);
		t.addVertexWithUV(x   , y, z+cz, u, v+dv);
		
		u = (tex_side & 15) / 16.0f; v = (tex_side / 16) / 16.0f;
		
		t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertexWithUV(x+cx, y+cy, z, u, v);
		t.addVertexWithUV(x+cx, y   , z, u, v+dv);
		t.addVertexWithUV(x   , y   , z, u+du, v+dv);
		t.addVertexWithUV(x   , y+cy, z, u+du, v);
		
		t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertexWithUV(x+cx, y+cy, z+cz, u, v);
		t.addVertexWithUV(x   , y+cy, z+cz, u+du, v);
		t.addVertexWithUV(x   , y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y   , z+cz, u, v+dv);
		
		t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x, y+cy, z   , u, v);
		t.addVertexWithUV(x, y   , z   , u, v+dv);
		t.addVertexWithUV(x, y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x, y+cy, z+cz, u+du, v);
		
		t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x+cx, y+cy, z   , u, v);
		t.addVertexWithUV(x+cx, y+cy, z+cz, u+du, v);
		t.addVertexWithUV(x+cx, y   , z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y   , z   , u, v+dv);
		
		u = (tex_top & 15) / 16.0f; v = (tex_top / 16) / 16.0f;
		
		t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertexWithUV(x   , y+cy, z   , u, v);
		t.addVertexWithUV(x   , y+cy, z+cz, u, v+dv);
		t.addVertexWithUV(x+cx, y+cy, z+cz, u+du, v+dv);
		t.addVertexWithUV(x+cx, y+cy, z   , u+du, v);
	}
	
	public static Icon[] loadIconArray(IconRegister reg, String prefix, int num) {
		Icon[] rv = new Icon[num];
		for(int k = 0; k < num; k++)
			rv[k] = loadIcon(reg, prefix + k);
		return rv;
	}
	
	public static Icon loadIcon(IconRegister reg, String name) {
		if(reg instanceof TextureMap)
			((TextureMap) reg).setTextureEntry(name, name.contains(">") ? new TextureSlice(name) : new TextureStitchedNonSquare(name));
		return reg.registerIcon(name);
	}
	
	
}