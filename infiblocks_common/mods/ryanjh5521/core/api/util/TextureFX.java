package mods.ryanjh5521.core.api.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.client.texturepacks.ITexturePack;

@SideOnly(Side.CLIENT)
public class TextureFX extends TextureStitched {

	protected TextureFX(String name, int width, int height) {
		super(name);
		this.width = width;
		this.height = height;
	}
	
	protected int width, height;
	protected byte[] imageData;
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void onSetup() {
		imageData = new byte[width * height * 4];
		
		String texName = getIconName();
		
		// second arg unused?
		// clamp/repeat mode and interpolation mode ignored since we don't render directly from this texture
		dynamicTexture = new Texture(texName, 0, width, height, 1, GL11.GL_CLAMP, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, null);
	}
	
	public void onTick() {
	}
	
	// "fake" texture which is updated every tick then copied to the main texture sheet
	private Texture dynamicTexture;
	
	
	@Override
	public final void updateAnimation() {
		if(this.textureList.size() > 2) {
			
			// if a texture pack has an animation (with 3+ frames) it overrides the procedural one
			super.updateAnimation();
			
		} else {
			
			if(dynamicTexture == null) {
				onSetup();
			}
			
			this.onTick();
			
			// copy the texture into dynamicTexture's buffer, then to the texture sheet
			ByteBuffer intermediate = dynamicTexture.getTextureData();
			intermediate.position(0);
			intermediate.put(imageData);
			
			this.textureSheet.copyFrom(this.originX, this.originY, this.dynamicTexture, this.rotated);
		}
	}
	
	@Override
	public boolean loadTexture(TextureManager manager, ITexturePack texturepack, String name, String fileName, BufferedImage image, ArrayList textures) {
		if(image != null) {
			width = image.getWidth();
			height = image.getHeight();
		}
		onSetup();
		textures.add(dynamicTexture);
		textures.add(dynamicTexture);
		return true;
	}

}
