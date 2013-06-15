package mods.ryanjh5521.microblocks;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mods.ryanjh5521.core.api.multipart.util.BlockMultipartBase;
import mods.ryanjh5521.core.api.util.Dir;
import mods.ryanjh5521.microblocks.api.EnumPartClass;
import mods.ryanjh5521.microblocks.api.EnumPosition;
import mods.ryanjh5521.microblocks.api.IMicroblockCoverSystem;
import mods.ryanjh5521.microblocks.api.IMicroblockSupporterTile;
import mods.ryanjh5521.microblocks.api.Part;
import mods.ryanjh5521.microblocks.api.PartType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MicroblockCoverSystem implements IMicroblockCoverSystem {
	
	ArrayList<Part> parts = new ArrayList<Part>();
	public BlockMultipartBase wrappedBlock;
	
	// TODO: better API for hole size of hollow covers
	public double hollow_edge_size;
	
	private TileEntity te;
	private IMicroblockSupporterTile te2;
	
	public MicroblockCoverSystem(IMicroblockSupporterTile te, double hes) {
		this.te = (TileEntity)te;
		this.te2 = te;
		hollow_edge_size = hes;
	}
	
	public MicroblockCoverSystem(IMicroblockSupporterTile te) {
		this(te, 0.25);
	}
	
	

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList l = new NBTTagList();
		for(Part p : parts)
			l.appendTag(p.writeToNBT());
		tag.setTag("ICMP", l); // ICMP = ryanjh5521 Core Multi-Part
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		parts.clear();
		NBTTagList l = tag.getTagList("ICMP");
		if(l == null)
			return;
		for(int k = 0; k < l.tagCount(); k++) {
			Part p = Part.readFromNBT(l.tagAt(k));
			if(p != null)
				parts.add(p);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(Vec3 src, Vec3 dst) {
		int k = 0;
		int x = te.xCoord, y = te.yCoord, z = te.zCoord;
		src = src.addVector(-x, -y, -z);
		dst = dst.addVector(-x, -y, -z);
		double best = dst.squareDistanceTo(src) + 1;
		Part hit = null;
		MovingObjectPosition hitInfo = null;
		int subHit = -1;
		for(Part p : parts)
		{
			AxisAlignedBB aabb = p.getBoundingBoxFromPool();
			MovingObjectPosition rt = aabb.calculateIntercept(src, dst);
			if(rt != null)
			{
				double rtdist = rt.hitVec.squareDistanceTo(src);
				if(rtdist < best)
				{
					hitInfo = rt;
					best = rtdist;
					hit = p;
					subHit = -1 - k;
				}
			}
			++k;
		}
		
		if(hit == null)
			return null;

		MovingObjectPosition pos = new MovingObjectPosition(x, y, z, hitInfo.sideHit, hitInfo.hitVec.addVector(x, y, z));
		pos.subHit = subHit;
		return pos;
	}

	@Override
	public boolean addPart(Part part) {
		if(!canPlace(part.type, part.pos))
			return false;
		parts.add(part);
		return true;
	}
	
	public boolean canPlaceCentre(double size) {
		AxisAlignedBB aabb = Part.getBoundingBoxFromPool(EnumPosition.Centre, size);
		for(Part p : parts)
		{
			if(p.getBoundingBoxFromPool().intersectsWith(aabb))
				return false;
		}
		return true;
	}

	public boolean canPlace(PartType<?> type, EnumPosition pos) {
		for(Part p : parts)
		{
			if(p.pos == pos)
				return false;
			if(p.pos.clazz == pos.clazz)
				continue;
			if(p.getBoundingBoxFromPool().intersectsWith(Part.getBoundingBoxFromPool(pos, type.getSize())))
				return false;
		}
		return te == null || !te2.isPlacementBlockedByTile(type, pos);
	}

	@Override
	public void getCollidingBoundingBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list) {
		for(Part p : parts) {
			AxisAlignedBB bb = p.getBoundingBoxFromPool().getOffsetBoundingBox(te.xCoord, te.yCoord, te.zCoord);
			if(mask.intersectsWith(bb))
				list.add(bb);
		}
	}

	/**
	 * Returns true if tubes/cables/etc that use the centre of the block
	 * can connect through the specified side - ie if it is not blocked by a non-hollow cover.
	 */
	@Override
	public boolean isSideOpen(int side) {
		return !isPositionOccupied(EnumPosition.getFacePosition(side), true, false);
	}

	public boolean isSideOpen(int myx, int myy, int myz, int x, int y, int z) {
		if(x < myx) return isSideOpen(Dir.NX);
		if(x > myx) return isSideOpen(Dir.PX);
		if(y < myy) return isSideOpen(Dir.NY);
		if(y > myy) return isSideOpen(Dir.PY);
		if(z < myz) return isSideOpen(Dir.NZ);
		if(z > myz) return isSideOpen(Dir.PZ);
		throw new IllegalArgumentException("no direction given (start = end)");
	}
	
	@Override
	public byte[] writeDescriptionBytes() {
		ByteArrayDataOutput o = ByteStreams.newDataOutput(6 + parts.size()*3);
		try {
			writeDescriptionPacket(o);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return o.toByteArray();
	}
	
	@Override
	public void readDescriptionBytes(byte[] data, int start) {
		try {
			readDescriptionPacket(ByteStreams.newDataInput(data, start));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void writeDescriptionPacket(DataOutput data) throws IOException {
		data.writeShort(wrappedBlock == null ? 0 : wrappedBlock.blockID);
		data.writeShort(parts.size());
		//data.writeShort(partiallyDamagedPart);
		//data.writeInt(te.xCoord);
		//data.writeInt(te.yCoord);
		//data.writeInt(te.zCoord);
		for(Part pt : parts) {
			data.writeByte(pt.pos.ordinal());
			data.writeInt(pt.type.getID());
			pt.writeExtraData(data);
		}
	}
	
	@Override
	public void readDescriptionPacket(DataInput data) throws IOException {
		int wrappedID = data.readShort();
		wrappedBlock = (wrappedID == 0 ? null : (BlockMultipartBase)Block.blocksList[wrappedID]);
		int ncovers = data.readShort();
		//partiallyDamagedPart = data.readShort();
		//int x = data.readInt();
		//int y = data.readInt();
		//int z = data.readInt();
		//if(x != te.xCoord || y != te.yCoord || z != te.zCoord)
		//	throw new IOException("Coordinates don't match");
		parts.clear();
		for(int k = 0; k < ncovers; k++)
		{
			EnumPosition pos = EnumPosition.values()[data.readByte()];
			int type = data.readInt();
			parts.add(MicroblockSystem.parts.get(type).createPart(pos, data));
		}
		
		te.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
	}
	/* $endif$ */

	public void copyPartsTo(MicroblockCoverSystem other) {
		for(Part p : parts)
			other.addPart(p);
	}

	/**
	 * Removes the part at the given coordinates.
	 * Returns the items dropped, or null (if no items should be dropped)
	 */
	@Override
	public List<ItemStack> removePartByPlayer(EntityPlayer ply, int part) {
		
		if(MicroblockSystem.microblockContainerBlock == null)
			return null;
		
		if(part >= parts.size())
			return null;
		
		Part p = parts.remove(part);
		if(p == null)
			return null;
		
		ItemStack drop = p.type.getDroppedStack(p, ply);
		
		if(parts.size() == 0 && te.getClass() == TileMicroblockContainer.class)
			te.worldObj.setBlock(te.xCoord, te.yCoord, te.zCoord, 0, 0, 3);
		else {
			te.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
			te.worldObj.notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, te.getBlockType().blockID);
		}
		
		return Collections.singletonList(drop);
	}
	
	/**
	 * Returns true if there is a part in the specified position.
	 */
	@Override
	public boolean isPositionOccupied(EnumPosition pos) {
		for(Part p : parts)
			if(p.pos == pos)
				return true;
		return false;
	}
	
	/**
	 * Returns true if there is a part in the specified position.
	 * If ignoreHollowPanels is true, hollow panels will be ignored.
	 */
	public boolean isPositionOccupied(EnumPosition pos, boolean ignoreHollowPanels, boolean includeTile) {
		if(includeTile && te != null && te2.isPositionOccupiedByTile(pos))
			return true;
		for(Part p : parts)
			if(p.pos == pos && (!ignoreHollowPanels || p.type.getPartClass() != EnumPartClass.HollowPanel))
				return true;
		return false;
	}

	/**
	 * Checks if an edge is unused (for example, if wire connections can connect around corners through it)
	 * Returns the same result if face1 and face2 are swapped.
	 * 
	 * @param face1 One face bordering the edge to check.
	 * @param face2 The other face bordering the edge to check.
	 * @return True if the edge is unused.
	 */
	@Override
	public boolean isEdgeOpen(int face1, int face2) {
		return !isPositionOccupied(EnumPosition.getEdgePosition(face1, face2)) && isSideOpen(face1) && isSideOpen(face2);
	}

	@Override
	public EnumPosition getPartPosition(int part) {
		if(part >= parts.size())
			return null;
		return parts.get(part).pos;
	}

	@Override
	public AxisAlignedBB getPartAABBFromPool(int part) {
		if(part >= parts.size())
			return null;
		return parts.get(part).getBoundingBoxFromPool();
	}

	@Override
	public float getPlayerRelativePartHardness(EntityPlayer ply, int part) {
		if(part >= parts.size())
			return 0;
		
		return parts.get(part).type.getPlayerRelativeHardness(parts.get(part), ply);
	}

	@Override
	public ItemStack pickPart(MovingObjectPosition rayTrace, int part) {
		if(part >= parts.size())
			return null;
		return parts.get(part).type.getPickItem(parts.get(part));
	}

	@Override
	public boolean isSolidOnSide(ForgeDirection side) {
		switch(side.ordinal()) {
		case Dir.NX: return isPositionOccupied(EnumPosition.FaceNX, true, true);
		case Dir.PX: return isPositionOccupied(EnumPosition.FacePX, true, true);
		case Dir.NY: return isPositionOccupied(EnumPosition.FaceNY, true, true);
		case Dir.PY: return isPositionOccupied(EnumPosition.FacePY, true, true);
		case Dir.NZ: return isPositionOccupied(EnumPosition.FaceNZ, true, true);
		case Dir.PZ: return isPositionOccupied(EnumPosition.FacePZ, true, true);
		}
		return false;
	}
	
	/*@SideOnly(Side.CLIENT)
	private static void renderAABB(RenderBlocks render, int x, int y, int z, Icon[] textures, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Block block) {
		/*GameSettings gs = Minecraft.getMinecraft().gameSettings;
		boolean wasAO = gs.ambientOcclusion;
		gs.ambientOcclusion = false;
		
		render.setRenderMinMax(minX, minY, minZ, maxX, maxY, maxZ);
		render.renderStandardBlock(MicroblockSystem.microblockContainerBlock, x, y, z);
		
		gs.ambientOcclusion = wasAO;/
		
		RenderUtilsIC.setBrightness(render.blockAccess, x, y, z);
		Tessellator.instance.setColorOpaque(255, 255, 255);
		
		if(render.overrideBlockTexture != null) {
			Icon t = render.overrideBlockTexture;
			textures = new Icon[] {t, t, t, t, t, t};
		}
		
		MicroblockRenderer.renderAABB(Tessellator.instance, AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ).offset(x, y, z), textures, render, x, y, z, block);
	}*/

	@SideOnly(Side.CLIENT)
	private void renderPartWorld(Part p, RenderBlocks render, int x, int y, int z) {
		boolean[] dontRenderSides = new boolean[6];
		
		for(Part p2 : parts) {
			if(!p2.type.isOpaque())
				continue;
			
			switch(p2.pos) {
			case FaceNX: dontRenderSides[Dir.NX] = true; break;
			case FaceNY: dontRenderSides[Dir.NY] = true; break;
			case FaceNZ: dontRenderSides[Dir.NZ] = true; break;
			case FacePX: dontRenderSides[Dir.PX] = true; break;
			case FacePY: dontRenderSides[Dir.PY] = true; break;
			case FacePZ: dontRenderSides[Dir.PZ] = true; break;
			default: break;
			}
		}
		switch(p.pos) {
		case FaceNX: dontRenderSides[Dir.NX] = false; break;
		case FaceNY: dontRenderSides[Dir.NY] = false; break;
		case FaceNZ: dontRenderSides[Dir.NZ] = false; break;
		case FacePX: dontRenderSides[Dir.PX] = false; break;
		case FacePY: dontRenderSides[Dir.PY] = false; break;
		case FacePZ: dontRenderSides[Dir.PZ] = false; break;
		default: break;
		}
		
		p.type.renderPartWorld(render, p, x, y, z, dontRenderSides);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(RenderBlocks render) {
		for(Part p : parts)
			renderPartWorld(p, render, te.xCoord, te.yCoord, te.zCoord);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderPart(RenderBlocks render, int part) {
		if(part >= parts.size())
			return;
		
		Part p = parts.get(part);
		renderPartWorld(p, render, te.xCoord, te.yCoord, te.zCoord);
	}
	
	@Override
	public void convertToContainerBlock() {
		if(parts.size() == 0 || MicroblockSystem.microblockContainerBlock == null)
			te.worldObj.setBlock(te.xCoord, te.yCoord, te.zCoord, 0, 0, 2);
		else
		{
			te.worldObj.setBlock(te.xCoord, te.yCoord, te.zCoord, MicroblockSystem.microblockContainerBlock.blockID, 0, 2);
			IMicroblockCoverSystem c2 = ((TileMicroblockContainer)te.worldObj.getBlockTileEntity(te.xCoord, te.yCoord, te.zCoord)).getCoverSystem();
			for(Part p : parts)
				c2.addPart(p);
		}
	}

	@Override
	public Collection<Part> getAllParts() {
		return Collections.unmodifiableList(parts);
	}
}