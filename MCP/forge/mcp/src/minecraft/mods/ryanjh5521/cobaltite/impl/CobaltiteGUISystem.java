package mods.ryanjh5521.cobaltite.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import mods.ryanjh5521.cobaltite.gui.CobaltiteClientGUI;
import mods.ryanjh5521.cobaltite.gui.CobaltiteServerGUI;
import mods.ryanjh5521.core.api.APILocator;
import mods.ryanjh5521.core.api.net.ISimplePacket;

public class CobaltiteGUISystem {
	
	
	private CobaltiteGUISystem() {}
	private static CobaltiteGUISystem instance = new CobaltiteGUISystem();

	public static void init() {
		
	}

	public static void openGUI(EntityPlayerMP player, CobaltiteServerGUI gui, String clientClass, NBTTagCompound constructionData) {
		
		player.incrementWindowID();
        player.closeInventory();
        int windowId = player.currentWindowId;
        
        player.openContainer = new ContainerWrapperServer(gui);
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters(player);
		
		PacketOpenGUI p = new PacketOpenGUI();
		p.clazz = clientClass;
		p.constructionData = constructionData;
		APILocator.getNetManager().sendToClient(p, player);
	}
	
	
	
	
	public static class PacketOpenGUI implements ISimplePacket {

		public String clazz;
		public NBTTagCompound constructionData;
		public int windowID;
		
		@Override
		public void read(DataInputStream in) throws IOException {
			clazz = in.readUTF();
			constructionData = (NBTTagCompound)NBTBase.readNamedTag(in);
			windowID = in.readInt();
		}

		@Override
		public void write(DataOutputStream out) throws IOException {
			out.writeUTF(clazz);
			NBTBase.writeNamedTag(constructionData, out);
			out.writeInt(windowID);
		}

		@Override
		public void onReceived(EntityPlayer source, INetworkManager connection) {
			if(source == null)
				onReceivedOnClient();
		}
		
		private void onReceivedOnClient() {
			CobaltiteClientGUI gui;
			try {
				Class<? extends CobaltiteClientGUI> guiClass = Class.forName(clazz).asSubclass(CobaltiteClientGUI.class);
				
				try {
					gui = guiClass.getConstructor().newInstance();
				} catch(NoSuchMethodException e) {
					gui = guiClass.getConstructor(NBTTagCompound.class).newInstance(constructionData);
				}
				
			} catch (Exception e) {
				throw new RuntimeException("Invalid GUI class: "+clazz+", do you have an outdated mod?", e);
			}
			
			ContainerWrapperClient container = new ContainerWrapperClient(gui);
			GuiWrapperClient screen = new GuiWrapperClient(gui, container);
			
			Minecraft.getMinecraft().displayGuiScreen(screen);
			Minecraft.getMinecraft().thePlayer.openContainer.windowId = windowID;
		}
	}

}
