package mods.ryanjh5521.microblocks.coremod;


import java.util.Arrays;

import mods.ryanjh5521.microblocks.ModProperties;

import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class MicroblocksModContainer extends DummyModContainer {

	public MicroblocksModContainer() {
		super(new ModMetadata());
		ModMetadata md = getMetadata();
		
		md.modId = ModProperties.MODID;
		md.name = ModProperties.MOD_NAME;
		md.version = ModProperties.MOD_VERSION;
		md.credits = "";
		md.authorList = Arrays.asList("ryanjh5521");
		md.description = "";
		md.url = "http://www.minecraftforum.net/topic/1001131-110-ryanjh5521s-mods-smp/";
		md.updateUrl = "";
		md.screenshots = new String[0];
		md.logoFile = "";
	}
	
	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		return VersionParser.parseRange("[1.5.2]");
	}
	
	private LoadController controller;
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		this.controller = controller;
		return true;
	}
	
	@Subscribe
	public void construct(FMLConstructionEvent evt) {
		
	}
	
	@Subscribe
	public void preinit(FMLPreInitializationEvent evt) {
		BridgeClass1.isMinecraftLoaded = true;
		try {
			BridgeClass2.preinit(evt);
		} catch(Throwable t) {
			controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
		}
	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {
		try {
			BridgeClass2.init(evt);
		} catch(Throwable t) {
			controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
		}
	}
	
	@Subscribe
	public void postinit(FMLPostInitializationEvent evt) {
		try {
			BridgeClass2.postinit(evt);
		} catch(Throwable t) {
			controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
		}
	}
}
