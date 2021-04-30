package xyz.przemyk.simpleplanes.upgrades.booster;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xyz.przemyk.simpleplanes.MathUtil;
import xyz.przemyk.simpleplanes.SimplePlanesMod;
import xyz.przemyk.simpleplanes.entities.HelicopterEntity;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.setup.SimplePlanesItems;
import xyz.przemyk.simpleplanes.setup.SimplePlanesUpgrades;
import xyz.przemyk.simpleplanes.upgrades.Upgrade;

import static net.minecraft.item.Items.GUNPOWDER;

public class BoosterUpgrade extends Upgrade {
    
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimplePlanesMod.MODID, "textures/plane_upgrades/rocket.png");
    public static int FUEL_PER_GUNPOWDER = 20;

    public int fuel = 0;

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("fuel", fuel);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        fuel = compoundNBT.getInt("fuel");
    }

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeVarInt(fuel);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        fuel = buffer.readVarInt();
    }

    public BoosterUpgrade(PlaneEntity planeEntity) {
        super(SimplePlanesUpgrades.BOOSTER.get(), planeEntity);
    }

    @Override
    public void tick() {
        push();
    }

    @Override
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getPlayer().getItemInHand(event.getHand());
        if (fuel <= 0) {
            if (itemStack.getItem().equals(GUNPOWDER)) {
                if (!event.getPlayer().isCreative()) {
                    itemStack.shrink(1);
                }
                fuel = FUEL_PER_GUNPOWDER;
            }
        }
        push();
    }

    private void push() {
        if (fuel < 0) {
            return;
        }

        --fuel;
        updateClient();

        Vector3d m = planeEntity.getDeltaMovement();
        float pitch = 0;
        PlayerEntity player = planeEntity.getPlayer();
        if (player != null) {
            if (player.zza > 0.0F) {
                if (planeEntity.isSprinting()) {
                    pitch += 2;
                }
            } else if (player.zza < 0.0F) {
                pitch -= 2;
            }
        }
        if (planeEntity.level.random.nextInt(50) == 0) {
            planeEntity.hurt(DamageSource.ON_FIRE, 1);
        }
        if (planeEntity instanceof HelicopterEntity) {
            pitch = 0;
        }
        planeEntity.xRot += pitch;
        Vector3d motion = MathUtil.rotationToVector(planeEntity.yRot, planeEntity.xRot, 0.05);

        planeEntity.setDeltaMovement(m.add(motion));
        if (planeEntity.level.isClientSide) {
            spawnParticle(ParticleTypes.FLAME, new Vector3f(-0.6f, 0f, -1.3f));
            spawnParticle(ParticleTypes.FLAME, new Vector3f(0.6f, 0f, -1.3f));
        }
    }

    public void spawnParticle(IParticleData particleData, Vector3f relPos) {
        relPos = new Vector3f(relPos.x(), relPos.y() - 0.3f, relPos.z());
        relPos = planeEntity.transformPos(relPos);
        relPos = new Vector3f(relPos.x(), relPos.y() + 0.9f, relPos.z());
        Vector3d motion = planeEntity.getDeltaMovement();
        double speed = motion.length() / 4;
        planeEntity.level.addParticle(particleData,
                planeEntity.getX() + relPos.x(),
                planeEntity.getY() + relPos.y(),
                planeEntity.getZ() + relPos.z(),
                motion.x * speed,
                (motion.y + 1) * speed,
                motion.z * speed);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, float partialTicks) {
        IVertexBuilder ivertexbuilder = buffer.getBuffer(BoosterModel.INSTANCE.renderType(TEXTURE));
        BoosterModel.INSTANCE.renderToBuffer(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void dropItems() {
        planeEntity.spawnAtLocation(SimplePlanesItems.BOOSTER.get());
    }
}
