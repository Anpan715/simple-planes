package xyz.przemyk.simpleplanes.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;

public class HelicopterRenderer extends AbstractPlaneRenderer<PlaneEntity> {

    protected final HelicopterModel planeModel = new HelicopterModel();

    public HelicopterRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        propellerModel = new HelicopterPropellerModel();
        shadowSize = 0.6F;
    }

//    @Override
//    protected void renderEngine(PlaneEntity planeEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
//        matrixStackIn.translate(0, -0.8, 0.65);
//        super.renderEngine(planeEntity, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//    }

    @Override
    protected EntityModel<PlaneEntity> getModel() {
        return planeModel;
    }

//    @Override
//    public ResourceLocation getEntityTexture(PlaneEntity entity) {
//        //        if (entity.isPowered()) {
//        //            return new ResourceLocation(SimplePlanesMod.MODID, "textures/entity/plane/furnace_powered/"+entity.getMaterial().name+".png");
//        //        }
//        return new ResourceLocation(SimplePlanesMod.MODID, "textures/entity/plane/furnace/" + entity.getMaterial().name + ".png");
//    }
}
