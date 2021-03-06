package cwgfarplaneview.world.terrain.flat;

import cwgfarplaneview.world.terrain.IncorrectTerrainDataException;
import io.github.opencubicchunks.cubicchunks.api.util.XZAddressable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.GameData;

public class TerrainPoint implements XZAddressable {
	public int chunkX;
	public int chunkZ;
	public int blockY;
	public IBlockState blockState;
	public Biome biome;

	public TerrainPoint(int x, int z, int height, int blockStateIDIn, int biomeId) throws IncorrectTerrainDataException {
		this(x, z, height, GameData.getBlockStateIDMap().getByValue(blockStateIDIn), Biome.getBiome(biomeId));
	}

	public TerrainPoint(int x, int z, int height, IBlockState blockStateIn, Biome biomeIn) throws IncorrectTerrainDataException {
		chunkX = x;
		chunkZ = z;
		blockY = height;
		blockState = blockStateIn;
		biome = biomeIn;
		if (blockState == null)
			throw new IncorrectTerrainDataException("Blockstate is NULL");
		if (blockState == Blocks.AIR.getDefaultState())
			throw new IncorrectTerrainDataException("Blockstate should not be AIR!");
		if (biome == null)
			throw new IncorrectTerrainDataException("Biome is NULL");
	}

	@Override
	public int getX() {
		return chunkX;
	}

	@Override
	public int getZ() {
		return chunkZ;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", chunkX);
		nbt.setInteger("z", chunkZ);
		nbt.setInteger("y", blockY);
		nbt.setInteger("blockstate", GameData.getBlockStateIDMap().get(blockState));
		nbt.setInteger("biome", Biome.getIdForBiome(biome));
		return nbt;
	}

	public static TerrainPoint fromNBT(NBTTagCompound nbt) throws IncorrectTerrainDataException {
		int x = nbt.getInteger("x");
		int z = nbt.getInteger("z");
		int y = nbt.getInteger("y");
		int bstate = nbt.getInteger("blockstate");
		int biome = nbt.getInteger("biome");
		return new TerrainPoint(x, z, y, bstate, biome);
	}

	@Override
	public String toString() {
		return "TerrainPoint[chunkX:" + chunkX + ",chunkZ:" + chunkZ + ",height:" + blockY + "]";
	}
}
