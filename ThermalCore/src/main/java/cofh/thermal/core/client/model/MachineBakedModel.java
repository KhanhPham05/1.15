package cofh.thermal.core.client.model;

import cofh.lib.util.control.IReconfigurable;
import cofh.thermal.core.client.gui.ThermalTextures;
import cofh.thermal.core.tileentity.MachineTileReconfigurable;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cofh.lib.util.control.IReconfigurable.SideConfig.SIDE_NONE;

public class MachineBakedModel extends BakedModelWrapper<IBakedModel> implements IDynamicBakedModel {

    IReconfigurable.SideConfig[] DEFAULT_SIDES = new IReconfigurable.SideConfig[]{SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};

    private static final double LOWER = -0.004;
    private static final double UPPER = 1.004;

    public MachineBakedModel(IBakedModel originalModel) {

        super(originalModel);
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, side, rand));

        IReconfigurable.SideConfig[] sides = extraData.getData(MachineTileReconfigurable.SIDES);
        if (sides == null || sides.length < 6) {
            sides = DEFAULT_SIDES;
        }
        TextureAtlasSprite[] textures = getTextures(sides);
        // Down
        quads.add(createQuad(v(LOWER, LOWER, LOWER), v(UPPER, LOWER, LOWER), v(UPPER, LOWER, UPPER), v(LOWER, LOWER, UPPER), textures[0]));
        // Up
        quads.add(createQuad(v(LOWER, UPPER, LOWER), v(LOWER, UPPER, UPPER), v(UPPER, UPPER, UPPER), v(UPPER, UPPER, LOWER), textures[1]));
        // North
        quads.add(createQuad(v(UPPER, UPPER, LOWER), v(UPPER, LOWER, LOWER), v(LOWER, LOWER, LOWER), v(LOWER, UPPER, LOWER), textures[2]));
        // South
        quads.add(createQuad(v(LOWER, UPPER, UPPER), v(LOWER, LOWER, UPPER), v(UPPER, LOWER, UPPER), v(UPPER, UPPER, UPPER), textures[3]));
        // West
        quads.add(createQuad(v(LOWER, UPPER, LOWER), v(LOWER, LOWER, LOWER), v(LOWER, LOWER, UPPER), v(LOWER, UPPER, UPPER), textures[4]));
        // East
        quads.add(createQuad(v(UPPER, UPPER, UPPER), v(UPPER, LOWER, UPPER), v(UPPER, LOWER, LOWER), v(UPPER, UPPER, LOWER), textures[5]));

        return quads;
    }

    // region HELPERS
    private TextureAtlasSprite[] getTextures(IReconfigurable.SideConfig[] sides) {

        TextureAtlasSprite[] ret = new TextureAtlasSprite[sides.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = getTexture(sides[i]);
        }
        return ret;
    }

    private TextureAtlasSprite getTexture(IReconfigurable.SideConfig side) {

        switch (side) {
            case SIDE_INPUT:
                return ThermalTextures.MACHINE_CONFIG_INPUT;
            case SIDE_OUTPUT:
                return ThermalTextures.MACHINE_CONFIG_OUTPUT;
            case SIDE_BOTH:
                return ThermalTextures.MACHINE_CONFIG_BOTH;
            case SIDE_ACCESSIBLE:
                return ThermalTextures.MACHINE_CONFIG_ACCESSIBLE;
            default:
                return ThermalTextures.MACHINE_CONFIG_NONE;
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {

        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getFacingFromVector(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, 1.0f, 1.0f, 1.0f);
        builder.setApplyDiffuseLighting(true);
        return builder.build();
    }

    private void putVertex(BakedQuadBuilder builder, Vec3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {

        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
        for (int j = 0; j < elements.size(); j++) {
            VertexFormatElement e = elements.get(j);
            switch (e.getUsage()) {
                case POSITION:
                    builder.put(j, (float) x, (float) y, (float) z, 1.0f);
                    break;
                case COLOR:
                    builder.put(j, r, g, b, 1.0f);
                    break;
                case UV:
                    switch (e.getIndex()) {
                        case 0:
                            float iu = sprite.getInterpolatedU(u);
                            float iv = sprite.getInterpolatedV(v);
                            builder.put(j, iu, iv);
                            break;
                        case 2:
                            builder.put(j, 0f, 1f);
                            break;
                        default:
                            builder.put(j);
                            break;
                    }
                    break;
                case NORMAL:
                    builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                default:
                    builder.put(j);
                    break;
            }
        }
    }

    private static Vec3d v(double x, double y, double z) {

        return new Vec3d(x, y, z);
    }
    // endregion
}
