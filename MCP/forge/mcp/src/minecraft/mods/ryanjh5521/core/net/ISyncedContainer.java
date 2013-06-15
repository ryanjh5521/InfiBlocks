package mods.ryanjh5521.core.net;

import mods.ryanjh5521.core.api.net.IPacket;

/**
 * Implement this on your container class to receive GUI packets.
 * (send them from OneTwoFiveNetworking.sendContainerUpdate)
 */
public interface ISyncedContainer {
	/** You will want to check the class of the packet before using it */
	public void onReceivePacket(IPacket packet);
}
