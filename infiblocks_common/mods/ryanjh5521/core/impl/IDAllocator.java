package mods.ryanjh5521.core.impl;


import java.util.ArrayList;
import java.util.List;

import mods.ryanjh5521.core.Config;
import mods.ryanjh5521.core.api.IIDAllocator;
import mods.ryanjh5521.core.api.IIDCallback;
import mods.ryanjh5521.core.api.IRecipeSaveHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;
import cpw.mods.fml.common.Loader;

public class IDAllocator implements IIDAllocator {
	
	private boolean canRequest = true;
	
	private static class Request {
		public Object mod;
		public String name;
		public IIDCallback callback;
		public int min, max, _default;
		public IDType type;
		
		public int id = -1;
	}
	
	private List<Request> requests = new ArrayList<Request>();
	
	private static class SavedRecipes<S,D> {
		private S initialState, finalState;
		private D diff;
		private final IRecipeSaveHandler<S,D> handler;
		private boolean saved = false;
		
		public SavedRecipes(IRecipeSaveHandler<S,D> handler) {
			this.handler = handler;
		}
		
		public void saveInitial() {
			initialState = handler.save();
		}
		public void saveFinal() {
			finalState = handler.save();
			diff = handler.diff(finalState, initialState);
			saved = true;
		}
		public void load() {
			handler.apply(diff);
		}
		public boolean isSaved() {
			return saved;
		}
		
		public static <S,D> SavedRecipes<S,D> create(IRecipeSaveHandler<S,D> handler) {
			return new SavedRecipes<S,D>(handler);
		}
	}
	
	private List<SavedRecipes<?,?>> savedRecipes = new ArrayList<SavedRecipes<?,?>>();
	private List<Runnable> recipeAdders = new ArrayList<Runnable>();

	public void allocate(IIDSet idSet) {
		canRequest = false;
		
		for(SavedRecipes<?,?> r : savedRecipes) {
			if(r.isSaved()) {
				r.load();
			}
		}
		for(Request r : requests) {
			if(r.id != -1) {
				//r.callback.unregister(r.id);
				//r.id = -1;
				throw new AssertionError("already registered: "+r.name);
			}
		}
		
		for(Request r : requests) {
			r.id = idSet.getIDFor(r.name, r.type, r._default);
			if(r.id != -1 && (r.id < 1 || r.id >= r.max))
				throw new AssertionError("Failed to get ID for "+r.name+" from "+idSet);
			
			if(!Loader.isModLoaded("IDResolver"))
			{
				Item item = Item.itemsList[r.id];
				Block block = r.id < Block.blocksList.length ? Block.blocksList[r.id] : null;
				if(item != null || block != null)
					throw new RuntimeException("Slot "+r.id+" already occupied by "+item+"/"+block+" when adding "+r.name);
			}
			
			if(r.id != -1) {
				r.callback.register(r.id);
				if(!Loader.isModLoaded("IDResolver")) {
					switch(r.type) {
					case TerrainBlock:
					case Block:
						if(Block.blocksList[r.id] == null)
							throw new AssertionError(r.callback+" failed to register block "+r.name);
						break;
					case Item:
						if(Item.itemsList[r.id] == null)
							throw new AssertionError(r.callback+" failed to register item "+r.name);
						break;
					}
				}
			}
		}
		
		for(SavedRecipes<?,?> r : savedRecipes)
			if(!r.isSaved())
				r.saveInitial();
		
		for(Runnable r : recipeAdders)
			r.run();
		
		for(SavedRecipes<?,?> r : savedRecipes)
			if(!r.isSaved())
				r.saveFinal();
		
	}

	private Request request(Object mod, String name, IIDCallback callback, int min, int max, boolean allowDefault, IDType type) {
		if(!canRequest)
			throw new IllegalStateException("Too late to request IDs, use pre-init");
		
		Request r = new Request();
		r.mod = mod;
		r.name = name;
		r.callback = callback;
		r.min = min;
		r.max = max;
		r.type = type;
		r._default = allowDefault ? min + (Math.abs(r.name.hashCode()) % (max - min)) : max - 1;
		requests.add(r);
		return r;
	}

	@Override
	public void addRecipes(Runnable callback) {
		if(!canRequest)
			throw new IllegalStateException("Too late to register recipe callbacks, use pre-init");
		recipeAdders.add(callback);
	}

	@Override
	public void registerRecipeSaveHandler(IRecipeSaveHandler<?,?> handler) {
		if(!canRequest)
			throw new IllegalStateException("Too late to register recipe save handlers, use pre-init");
		savedRecipes.add(SavedRecipes.create(handler));
	}

	@Override
	public void requestItem(Object mod, String name, IIDCallback callback) {
		Request r = request(mod, name, callback, 4096, 32000, true, IDType.Item);
		if(Config.config.getCategory(Configuration.CATEGORY_ITEM).containsKey(r.name))
			Config.config.get(Configuration.CATEGORY_ITEM, r.name, r._default).getInt(r._default);
		
		Config.save();
	}

	@Override
	public void requestTerrainBlock(Object mod, String name, IIDCallback callback) {
		Request r = request(mod, name, callback, 128, 256, false, IDType.TerrainBlock);
		if(Config.config.getCategory(Configuration.CATEGORY_BLOCK).containsKey(r.name))
			Config.config.getTerrainBlock(Configuration.CATEGORY_BLOCK, r.name, r._default, null);
		
		Config.save();
	}

	@Override
	public void requestBlock(Object mod, String name, IIDCallback callback) {
		Request r = request(mod, name, callback, 256, 4096, true, IDType.Block);
		ConfigCategory cat = Config.config.getCategory(Configuration.CATEGORY_BLOCK);
		
		if(cat.containsKey(r.name))
			cat.put(r.name+".id", new Property(r.name+".id", cat.remove(r.name).getString(), Type.INTEGER));
		
		if(cat.containsKey(r.name+".id"))
			Config.config.getBlock(r.name+".id", r._default);
		
		Config.save();
	}

}
