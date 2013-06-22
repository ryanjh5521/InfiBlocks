package mods.ryanjh5521.core.api.multipart;


public interface IMultipartTile extends IPartContainer {
	/**
	 * Returns an ICoverSystem object, or null if this tile does not support a cover system
	 * @see mods.ryanjh5521.core.api.multipart.ICoverSystem 
	 */
	public ICoverSystem getCoverSystem();
}
