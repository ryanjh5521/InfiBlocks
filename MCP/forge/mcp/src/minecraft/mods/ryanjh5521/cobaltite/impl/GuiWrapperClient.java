package mods.ryanjh5521.cobaltite.impl;

import mods.ryanjh5521.cobaltite.gui.CobaltiteClientGUI;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiWrapperClient extends GuiContainer {

	public GuiWrapperClient(CobaltiteClientGUI gui, ContainerWrapperClient container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		// TODO Auto-generated method stub

	}

}
