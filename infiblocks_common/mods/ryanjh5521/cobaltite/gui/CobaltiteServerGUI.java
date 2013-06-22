package mods.ryanjh5521.cobaltite.gui;

import mods.ryanjh5521.cobaltite.impl.CobaltiteGUISystem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CobaltiteServerGUI extends CobaltiteGUI {
	/**
	 * Returns the client GUI class name.
	 * The client GUI class must extend CobaltiteClientGUI,
	 * and must have a nullary constructor or a constructor with a single
	 * NBTTagCompound argument.
	 */
	protected abstract String getClientGUIClass();
	
	/**
	 * Returns an NBT compound tag which is passed to the client GUI constructor.
	 */
	protected NBTTagCompound getClientConstructionData() {
		return new NBTTagCompound();
	}
	
	
	
	public static void openGUI(EntityPlayerMP player, CobaltiteServerGUI gui) {
		CobaltiteGUISystem.openGUI(player, gui, gui.getClientGUIClass(), gui.getClientConstructionData());
	}
}
