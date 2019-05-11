package cwgfarplaneview.world.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public interface TerrainPointProvider {

	TerrainPoint getTerrainPointAt(int meshX, int meshZ) throws IncorrectTerrainDataException;
}
