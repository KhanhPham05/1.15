package cofh.cofh_archery.entity.projectile;

import cofh.lib.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import static cofh.cofh_archery.init.ModReferences.SLIME_ARROW_ENTITY;
import static cofh.cofh_archery.init.ModReferences.SLIME_ARROW_ITEM;
import static cofh.lib.util.constants.Tags.TAG_ARROW_DATA;

public class SlimeArrowEntity extends AbstractArrowEntity {

    private static float DAMAGE = 0.5F;
    private static float MAX_VELOCITY = 3.0F;
    private static float MIN_VELOCITY = 0.5F;

    public static int bounces = 4;
    public static int knockback = 4;
    public static int knockbackFactor = 2;

    private int curBounces = 0;
    private int maxBounces = bounces;

    public SlimeArrowEntity(EntityType<? extends SlimeArrowEntity> entityIn, World worldIn) {

        super(entityIn, worldIn);
        this.damage = DAMAGE;
        this.knockbackStrength = knockback;
    }

    public SlimeArrowEntity(World worldIn, LivingEntity shooter) {

        super(SLIME_ARROW_ENTITY, shooter, worldIn);
        this.damage = DAMAGE;
        this.knockbackStrength = knockback;
    }

    public SlimeArrowEntity(World worldIn, double x, double y, double z) {

        super(SLIME_ARROW_ENTITY, x, y, z, worldIn);
        this.damage = DAMAGE;
        this.knockbackStrength = knockback;
    }

    @Override
    protected ItemStack getArrowStack() {

        return new ItemStack(SLIME_ARROW_ITEM);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {

        if (raytraceResultIn.getType() != RayTraceResult.Type.MISS) {
            this.setHitSound(SoundEvents.BLOCK_SLIME_BLOCK_HIT);

            if (raytraceResultIn.getType() == RayTraceResult.Type.ENTITY) {
                this.func_213868_a((EntityRayTraceResult) raytraceResultIn);
            } else if (raytraceResultIn.getType() == RayTraceResult.Type.BLOCK) {
                Vec3d motion = getMotion();
                if (motion.lengthSquared() < MIN_VELOCITY || isInWater() || curBounces >= maxBounces) {
                    super.onHit(raytraceResultIn);
                    return;
                }
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceResultIn;
                switch (blockraytraceresult.getFace()) {
                    case DOWN:
                    case UP:
                        this.setMotion(motion.x, motion.y * -1, motion.z);
                        break;
                    case NORTH:
                    case SOUTH:
                        this.setMotion(motion.x, motion.y, motion.z * -1);
                        break;
                    case WEST:
                    case EAST:
                        this.setMotion(motion.x * -1, motion.y, motion.z);
                        break;
                }
                float f = MathHelper.sqrt(horizontalMag(motion));
                this.rotationYaw = (float) (MathHelper.atan2(motion.x, motion.z) * (double) (180F / (float) Math.PI));
                this.rotationPitch = (float) (MathHelper.atan2(motion.y, f) * (double) (180F / (float) Math.PI));
                this.prevRotationYaw = this.rotationYaw;
                this.prevRotationPitch = this.rotationPitch;
                ++curBounces;
                --knockbackStrength;
            }
        }
    }

    @Override
    public void setFire(int seconds) {

    }

    @Override
    public void setIsCritical(boolean critical) {

    }

    @Override
    public void setKnockbackStrength(int knockbackStrengthIn) {

        super.setKnockbackStrength(knockback + knockbackStrengthIn * knockbackFactor);
        this.maxBounces = this.knockbackStrength;
    }

    @Override
    public void setPierceLevel(byte level) {

    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {

        super.shoot(x, y, z, Math.min(velocity, MAX_VELOCITY), inaccuracy);
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.inGround || this.func_203047_q()) {
            if (Utils.isClientWorld(world)) {
                Vec3d vec3d = this.getMotion();
                double d1 = vec3d.x;
                double d2 = vec3d.y;
                double d0 = vec3d.z;
                this.world.addParticle(ParticleTypes.ITEM_SLIME, this.posX + d1 * 0.25D, this.posY + d2 * 0.25D, this.posZ + d0 * 0.25D, -d1, -d2 + 0.2D, -d0);
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {

        super.writeAdditional(compound);
        compound.putInt(TAG_ARROW_DATA, curBounces);

    }

    @Override
    public void readAdditional(CompoundNBT compound) {

        super.readAdditional(compound);
        curBounces = compound.getInt(TAG_ARROW_DATA);
    }

    @Override
    public IPacket<?> createSpawnPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
