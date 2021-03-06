package cwgfarplaneview.world.terrain.volumetric;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import javax.annotation.Nullable;

import cubicchunks.regionlib.impl.EntryLocation3D;
import cubicchunks.regionlib.impl.save.SaveSection3D;
import cwgfarplaneview.util.DiskDataUtil;
import cwgfarplaneview.util.NBTUtil;
import cwgfarplaneview.util.TerrainConfig;
import cwgfarplaneview.world.terrain.IncorrectTerrainDataException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class TerrainPoint3DProviderDiskData extends TerrainPoint3DProvider {
	private SaveSection3D cubeIO;
	private ExtendedBlockStorage cachedStorage = new ExtendedBlockStorage(0, true);;
	private final TerrainPoint3DProviderLayered fallBackProvider;

	public TerrainPoint3DProviderDiskData(WorldServer worldIn, TerrainPoint3DProviderLayered fallBackProviderIn) {
		super(worldIn);
		fallBackProvider = fallBackProviderIn;
		cubeIO = DiskDataUtil.createCubeIO(worldIn);
	}

	@Override
	public TerrainCube getTerrainCubeAt(@Nullable TerrainCube cube, int tcX, int tcY, int tcZ) throws IncorrectTerrainDataException {
		if (cube == null)
			cube = new TerrainCube(world, tcX, tcY, tcZ);
		int cx0 = tcX << TerrainConfig.CUBE_SIZE_BIT_MESH + TerrainConfig.MESH_SIZE_BIT_CHUNKS;
		int cy0 = tcY << TerrainConfig.CUBE_SIZE_BIT_MESH + TerrainConfig.MESH_SIZE_BIT_CHUNKS;
		int cz0 = tcZ << TerrainConfig.CUBE_SIZE_BIT_MESH + TerrainConfig.MESH_SIZE_BIT_CHUNKS;
		for (int ix = cx0; ix < cx0 + 16; ix++) {
			for (int iy = cy0; iy < cy0 + 16; iy++) {
				for (int iz = cz0; iz < cz0 + 16; iz++) {
					EntryLocation3D ebsKey = new EntryLocation3D(ix, iy, iz);
					Optional<ByteBuffer> buf;
					try {
						buf = cubeIO.load(ebsKey, true);
						if (!buf.isPresent()) {
							fallBackProvider.getGenerator(ix, iy, iz).getPointOf(cube, ix, iy, iz);
							continue;
						}
						NBTTagCompound nbt = CompressedStreamTools.readCompressed(new ByteArrayInputStream(buf.get().array()))
								.getCompoundTag("Level");
						readBlocks(nbt, world);
					} catch (IOException e) {
						e.printStackTrace();
						fallBackProvider.getGenerator(ix, iy, iz).getPointOf(cube, ix, iy, iz);
						continue;
					}
					this.getPointOf(cube, ix, iy, iz);
				}
			}
		}
		return cube;
	}
	
	@Override
	protected void reset(int cubeX, int cubeY, int cubeZ) {	}
		
	@Override
	protected IBlockState getBlockStateAt(int x, int y, int z) {
		return cachedStorage.get(x & 15, y & 15, z & 15);
	}
	
	@Override
	protected int getBlockLightAt(int localX, int localY, int localZ) {
		return cachedStorage.getBlockLight(localX & 15, localY & 15, localZ & 15);
	}

	@Override
	protected int getSkyLightAt(int localX, int localY, int localZ) {
		if ((localY & 15) == 15)
			return 15;
		return cachedStorage.getSkyLight(localX & 15, (localY & 15) + 1, localZ & 15);
	}

	private void readBlocks(NBTTagCompound nbt, World world) {
		if (nbt.hasKey("Sections")) {
			NBTTagList sectionList = nbt.getTagList("Sections", 10);
			NBTUtil.readEBS(cachedStorage, sectionList.getCompoundTagAt(0));
		}
		else {
			NBTUtil.resetEBS(cachedStorage);
		}
	}

}
