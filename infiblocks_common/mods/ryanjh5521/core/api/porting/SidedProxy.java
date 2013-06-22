package mods.ryanjh5521.core.api.porting;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class SidedProxy {
	public static SidedProxy instance;
	
	public abstract File getMinecraftDir();
	@Deprecated
	public abstract void addLocalization(String key, String value);
	@Deprecated
	public abstract void sendChat(String msg, EntityPlayer player);
	public abstract double getPlayerReach(EntityPlayer ply);
	public abstract EntityPlayer getThePlayer();
	public abstract int getUniqueBlockModelID(String renderClass, boolean b);
	public abstract boolean isOp(String player);
	@Deprecated
	public abstract void preloadTexture(String texfile);
	public abstract void registerTileEntity(Class<? extends TileEntity> clazz, String id, String rclass);
	public abstract boolean isWorldCurrent(World w);
	public abstract void registerItemRenderer(int itemID, String renderClassName);
	
	public abstract boolean isDedicatedServer();
	
	public abstract Object createSidedObject(String clientClass, String serverClass);
}
