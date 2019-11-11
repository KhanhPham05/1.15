package cofh.test.renderer.entity;

import cofh.test.entity.projectile.PrismarineArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrismarineArrowRenderer extends ArrowRenderer<PrismarineArrowEntity> {

    public static final ResourceLocation RES_UNDERWATER_ARROW = new ResourceLocation("cofh_test:textures/entity/projectiles/prismarine_arrow_entity.png");

    public PrismarineArrowRenderer(EntityRendererManager manager) {

        super(manager);
    }

    protected ResourceLocation getEntityTexture(PrismarineArrowEntity entity) {

        return RES_UNDERWATER_ARROW;
    }

}