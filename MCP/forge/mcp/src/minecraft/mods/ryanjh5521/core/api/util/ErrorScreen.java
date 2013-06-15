package mods.ryanjh5521.core.api.util;

import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.Side;

public class ErrorScreen {
	private static class GuiErrorScreen extends GuiScreen {
		public String[] message;
		
	    public GuiErrorScreen(String[] message) {
	    	this.message = message;
	    }

	    /**
	     * Draws the screen and all the components in it.
	     */
	    @Override
		public void drawScreen(int par1, int par2, float par3)
	    {
	        this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
	        
	        int spacing = fontRenderer.FONT_HEIGHT + fontRenderer.FONT_HEIGHT/2;
	        
	        int y = height/2 - message.length*spacing/2;
	        for(String line : message) {
	        	drawCenteredString(fontRenderer, line, width/2, y, 0xFFFFFF);
	        	y += spacing;
	        }
	        super.drawScreen(par1, par2, par3);
	    }

	    /**
	     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	     */
	    @Override
		protected void keyTyped(char par1, int par2) {}
	}
	
	public static void displayFatalError(final String... msg) {
		if(FMLRelauncher.side().equals("SERVER")) {
			
			// only logging is shown in the dedicated server GUI
			Logger l = Logger.getLogger("Minecraft");
			l.log(Level.SEVERE, "");
			for(String line : msg)
				l.log(Level.SEVERE, line);
			
			System.err.println();
			for(String line : msg)
				System.err.println(line);
			
			while(true) {
				try {
					Thread.sleep(10000);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
		
		TickRegistry.registerTickHandler(new ITickHandler() {
			
			@Override
			public EnumSet<TickType> ticks() {
				return EnumSet.of(TickType.CLIENT);
			}
			
			@Override
			public void tickStart(EnumSet<TickType> type, Object... tickData) {
				if(!(Minecraft.getMinecraft().currentScreen instanceof GuiErrorScreen)) {
					GuiErrorScreen ges = new GuiErrorScreen(msg);
					Minecraft.getMinecraft().displayGuiScreen(ges);
				}
			}
			
			@Override
			public void tickEnd(EnumSet<TickType> type, Object... tickData) {
				
			}
			
			@Override
			public String getLabel() {
				return "error screen";
			}
		}, Side.CLIENT);
	}
}
