package mods.ryanjh5521.InfiBlocks.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class Sheetrock extends Block
{
	private static final String[] Types = new String[] {"sheetrock_blue", "sheetrock_purple", "sheetrock_cyan", "sheetrock_yellow", "sheetrock_green", "sheetrock_orange", "sheetrock_white", "sheetrock_red"};
	private Icon[] textures;

	public Sheetrock(int blockID)
	{
		super(blockID, Material.cloth);
		setHardness(1.0F);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		textures = new Icon[Types.length];

		for (int i = 0; i < Types.length; ++i) {
			textures[i] = iconRegister.registerIcon("InfiBlocks:"+Types[i]);
		}
	}

	@Override
	public Icon getIcon(int side, int meta)
	{
		if (meta < 0 || meta >= textures.length) {
			meta = 0;
		}

		return textures[meta];
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
		for (int i = 0; i < Types.length; ++i) {
			list.add(new ItemStack(blockID, 1, i));
		}
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
}
