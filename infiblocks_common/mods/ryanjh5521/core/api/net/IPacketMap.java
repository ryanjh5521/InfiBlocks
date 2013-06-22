package mods.ryanjh5521.core.api.net;

public interface IPacketMap {
	public String getChannel();
	public IPacket createS2CPacket(byte id);
	public IPacket createC2SPacket(byte id);
}
