package mods.ryanjh5521.core.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import mods.ryanjh5521.core.api.net.INetworkingManager;
import mods.ryanjh5521.core.api.net.IPacket;
import mods.ryanjh5521.core.api.net.IPacketMap;
import mods.ryanjh5521.core.api.net.ISimplePacket;
import mods.ryanjh5521.core.api.porting.SidedProxy;
import mods.ryanjh5521.core.net.PacketFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class NetworkingManager implements INetworkingManager {

	// Sends a packet to a player on a channel.
	// If player is null, packet is sent to the server.
	
	private static final AtomicInteger nextFragmentSequenceID = new AtomicInteger(0);
	
	private static final int MAX_PACKET_SIZE = 32767;
	private static final int FRAGMENT_SIZE = 32500;
	
	private void send250(Packet250CustomPayload p250, EntityPlayer target) {
		if(p250.data.length <= MAX_PACKET_SIZE) {
			if(target == null)
				PacketDispatcher.sendPacketToServer(p250);
			else if(target instanceof Player)
				PacketDispatcher.sendPacketToPlayer(p250, (Player)target);
			else
				System.err.println("[ryanjh5521 Core] Can't send packet (channel "+p250.channel+") to non-player "+target);
			return;
		}
		int seqID = nextFragmentSequenceID.incrementAndGet();
		
		int numFragments = (p250.data.length + FRAGMENT_SIZE - 1) / FRAGMENT_SIZE;
		int start = 0;
		
		//System.out.println("Splitting "+p250.data.length+"-size packet into "+numFragments+" fragments");
		
		for(int k = 0; k < numFragments; k++) {
			int fragmentLen = Math.min(FRAGMENT_SIZE, p250.data.length - start);
			
			PacketFragment fragment = new PacketFragment();
			fragment.fragmentIndex = k;
			fragment.numFragments = numFragments;
			fragment.senderSeqID = seqID;
			fragment.channel = p250.channel;
			fragment.data = new byte[fragmentLen];
			
			System.arraycopy(p250.data, start, fragment.data, 0, fragmentLen);
			start += fragmentLen;
			
			send250(wrap(fragment), target);
		}
	}
	
	// Always closes "in"
	private static void onReceivePacket(String channel, DataInputStream in, EntityPlayer source, IPacketMap mod) {
		
		try {
			byte id = in.readByte();
			IPacket packet = source == null ? mod.createS2CPacket(id) : mod.createC2SPacket(id);
			if(packet == null) {
				System.err.println("[ryanjh5521 Core] Received packet with invalid ID "+id+" (on channel "+channel+", mod "+mod+")");
				return;
			}
			packet.read(in);
			packet.onReceived(source);
		} catch(IOException e) {
			ModLoader.getLogger().log(Level.SEVERE, "While trying to receive packet on channel " + channel, e);
		} finally {
			try {
				in.close();
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static void onReceivePacket(String channel, DataInputStream in, EntityPlayer source) {
		IPacketMap mod = channels.get(channel);
		if(mod == null) {
			System.err.println("Received fragmented packet on unknown channel '"+channel+"' from "+(source == null ? "server" : "client '"+source.username+"'"));
			try {
				in.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		onReceivePacket(channel, in, source, mod);
	}
	
	static Map<String, IPacketMap> channels = new HashMap<String, IPacketMap>();

	@Override
	public Packet250CustomPayload wrap(IPacket packet) {
		Packet250CustomPayload p = new Packet250CustomPayload();
		p.channel = packet.getChannel();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		try {
			out.writeByte(packet.getID());
			packet.write(out);
			
			p.data = bytes.toByteArray();
			p.length = p.data.length;
			
			return p;
			
		} catch(IOException e) {
			ModLoader.getLogger().log(Level.SEVERE, "While trying to send packet of type "+packet.getClass().getName(), e);
			return null;
		} finally {
			try {
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendToServer(IPacket packet) {
		if(SidedProxy.instance.isDedicatedServer())
			throw new IllegalStateException("can't send packets to the server on the server.");
		send250(wrap(packet), null);
	}

	@Override
	public void sendToClient(IPacket packet, EntityPlayer target) {
		send250(wrap(packet), target);
	}

	@Override
	public void listen(final IPacketMap handler) {
		channels.put(handler.getChannel(), handler);
		NetworkRegistry.instance().registerChannel(new IPacketHandler() {
			@Override
			public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
				onReceivePacket(packet.channel, new DataInputStream(new ByteArrayInputStream(packet.data)), null, handler);
			}
		}, handler.getChannel(), Side.CLIENT);
		NetworkRegistry.instance().registerChannel(new IPacketHandler() {
			@Override
			public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
				onReceivePacket(packet.channel, new DataInputStream(new ByteArrayInputStream(packet.data)), (EntityPlayer)player, handler);
			}
		}, handler.getChannel(), Side.SERVER);
	}
	
	
	
	
	/* *************** SIMPLE PACKETS ***************** */
	
	private String SIMPLE_PACKET_CHANNEL = "ryanjh5521";
	
	private class SimplePacketTypeMap {
		Map<Integer, Constructor<? extends ISimplePacket>> recvMap = new HashMap<Integer, Constructor<? extends ISimplePacket>>();
		Map<Class<? extends ISimplePacket>, Integer> sendMap = new HashMap<Class<? extends ISimplePacket>, Integer>();
		int nextSendID = 2;
		
		{
			try {
				recvMap.put(1, PacketRegisterSimplePacket.class.getConstructor());
				sendMap.put(PacketRegisterSimplePacket.class, 1);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private WeakHashMap<INetworkManager, SimplePacketTypeMap> simplePacketTypeMap = new WeakHashMap<INetworkManager, SimplePacketTypeMap>();
	
	private SimplePacketTypeMap getSPTM(INetworkManager manager) {
		SimplePacketTypeMap rv = simplePacketTypeMap.get(manager);
		if(rv == null)
			simplePacketTypeMap.put(manager, rv = new SimplePacketTypeMap());
		return rv;
	}
	
	
	private class PacketRegisterSimplePacket implements ISimplePacket {
		// Associates an ID with a class
		int id;
		String clazz;
		
		@Override
		public void read(DataInputStream in) throws IOException {
			id = in.readInt();
			clazz = in.readUTF();
		}
		
		@Override
		public void write(DataOutputStream out) throws IOException {
			out.writeInt(id);
			out.writeUTF(clazz);
		}
		
		@Override 	
		public void onReceived(EntityPlayer source, INetworkManager connection) {
			Constructor<? extends ISimplePacket> constructor;
			try {
				constructor = Class.forName(clazz).asSubclass(ISimplePacket.class).getConstructor();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			getSPTM(connection).recvMap.put(id, constructor);
		}
	}
	
	private ISimplePacket unwrapSimplePacket(INetworkManager manager, Packet250CustomPayload packet) {
		try {
			SimplePacketTypeMap types = getSPTM(manager);
			
			ISimplePacket p;
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
			try {
				short type = in.readShort();
				
				Constructor<? extends ISimplePacket> constructor = types.recvMap.get(type);
				if(constructor == null) {
					return null;
				}
				
				p = constructor.newInstance();
				p.read(in);
				
			} finally {
				in.close();
			}
			
			return p;
			
		} catch(Exception e) {
			ModLoader.getLogger().log(Level.SEVERE, "While trying to receive packet on channel " + packet.channel, e);
			return null;
		}
	}
	
	{
		NetworkRegistry.instance().registerChannel(new IPacketHandler() {
			@Override
			public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
				ISimplePacket p = unwrapSimplePacket(manager, packet);
				if(p != null)
					p.onReceived((EntityPlayer)player, manager);
			}
		}, SIMPLE_PACKET_CHANNEL, Side.SERVER);
		NetworkRegistry.instance().registerChannel(new IPacketHandler() {
			@Override
			public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
				ISimplePacket p = unwrapSimplePacket(manager, packet);
				if(p != null)
					p.onReceived(null, manager);
			}
		}, SIMPLE_PACKET_CHANNEL, Side.CLIENT);
	}

	@Override
	public void sendToServer(ISimplePacket packet) {
		Packet wrapped = wrap(packet, Minecraft.getMinecraft().thePlayer.sendQueue.getNetManager());
		if(wrapped != null)
			PacketDispatcher.sendPacketToServer(wrapped);
	}

	@Override
	public void sendToClient(ISimplePacket packet, EntityPlayerMP target) {
		Packet wrapped = wrap(packet, target.playerNetServerHandler.netManager);
		if(wrapped != null)
			PacketDispatcher.sendPacketToPlayer(wrapped, (Player)target);
	}

	private Packet wrap(ISimplePacket packet, INetworkManager conn) {
		SimplePacketTypeMap sptm = getSPTM(conn);
		Integer id = sptm.sendMap.get(packet.getClass());
		if(id == null) {
			if(packet instanceof PacketRegisterSimplePacket)
				throw new AssertionError();
			
			sptm.sendMap.put(packet.getClass(), id = sptm.nextSendID++);
			
			PacketRegisterSimplePacket rp = new PacketRegisterSimplePacket();
			rp.id = id;
			rp.clazz = packet.getClass().getName();
			conn.addToSendQueue(wrap(rp, conn));
		}
		
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bytes);
			out.writeShort(id);
			packet.write(out);
			out.close();
			
			Packet250CustomPayload p = new Packet250CustomPayload();
			p.channel = SIMPLE_PACKET_CHANNEL;
			p.data = bytes.toByteArray();
			p.length = p.data.length;
			return p;
		
		} catch(IOException e) {
			ModLoader.getLogger().log(Level.SEVERE, "While trying to send packet of type "+packet.getClass().getName(), e);
			return null;
		}
	}
	
	
}
