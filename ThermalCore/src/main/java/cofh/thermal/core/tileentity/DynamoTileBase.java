package cofh.thermal.core.tileentity;

import cofh.lib.tileentity.TileCoFH;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import static cofh.lib.util.constants.Constants.FACING_ALL;
import static cofh.lib.util.constants.NBTTags.*;

public abstract class DynamoTileBase extends ThermalTileBase implements ITickableTileEntity {

    protected static final int BASE_PROCESS_TICK = 40;

    protected Direction facing;

    protected int fuel;
    protected int fuelMax;
    protected int coolant;
    protected int coolantMax;

    protected int processTick = getBaseProcessTick();

    public DynamoTileBase(TileEntityType<?> tileEntityTypeIn) {

        super(tileEntityTypeIn);
    }

    protected int getBaseProcessTick() {

        return BASE_PROCESS_TICK;
    }

    @Override
    public TileCoFH worldContext(BlockState state, IBlockReader world) {

        facing = state.get(FACING_ALL);

        return this;
    }

    @Override
    public void updateContainingBlockInfo() {

        super.updateContainingBlockInfo();
        updateFacing();
    }

    @Override
    public void neighborChanged(Block blockIn, BlockPos fromPos) {

        super.neighborChanged(blockIn, fromPos);

        // TODO: Handle caching of neighbor caps.
    }

    @Override
    public void tick() {

        boolean curActive = isActive;

        if (isActive) {
            processTick();
            if (canProcessFinish()) {
                processFinish();
                if (!redstoneControl.getState() || !canProcessStart()) {
                    processOff();
                } else {
                    processStart();
                }
            }
        } else if (redstoneControl.getState()) {
            if (timeCheck() && canProcessStart()) {
                processStart();
                processTick();
                isActive = true;
            }
        }
        updateActiveState(curActive);
    }

    // region PROCESS
    protected abstract boolean canProcessStart();

    protected boolean canProcessFinish() {

        return fuel <= 0;
    }

    protected abstract void processStart();

    protected void processFinish() {

    }

    protected void processOff() {

        isActive = false;
        wasActive = true;
        if (world != null) {
            timeTracker.markTime(world);
        }
    }

    protected int processTick() {

        if (fuel <= 0) {
            return 0;
        }
        int energy = Math.min(fuel, processTick);
        fuel -= energy;
        transferEnergy(energy);
        return energy;
    }
    // endregion

    // region HELPERS
    protected void transferEnergy(int energy) {

        EnergyHelper.insertIntoAdjacent(this, energy, getFacing());
    }

    protected Direction getFacing() {

        if (facing == null) {
            updateFacing();
        }
        return facing;
    }

    protected void updateFacing() {

        facing = getBlockState().get(FACING_ALL);
    }

    // endregion

    // region GUI
    @Override
    public int getScaledDuration(int scale) {

        if (fuelMax <= 0 || fuel <= 0) {
            return 0;
        }
        return scale * fuel / fuelMax;
    }
    // endregion

    // region NETWORK
    @Override
    public PacketBuffer getControlPacket(PacketBuffer buffer) {

        super.getControlPacket(buffer);

        buffer.writeFluidStack(renderFluid);

        return buffer;
    }

    @Override
    public PacketBuffer getGuiPacket(PacketBuffer buffer) {

        super.getGuiPacket(buffer);

        buffer.writeFluidStack(renderFluid);
        buffer.writeInt(fuelMax);
        buffer.writeInt(fuel);

        return buffer;
    }

    @Override
    public PacketBuffer getStatePacket(PacketBuffer buffer) {

        super.getStatePacket(buffer);

        buffer.writeFluidStack(renderFluid);

        return buffer;
    }

    @Override
    public void handleControlPacket(PacketBuffer buffer) {

        super.handleControlPacket(buffer);

        renderFluid = buffer.readFluidStack();
    }

    @Override
    public void handleGuiPacket(PacketBuffer buffer) {

        super.handleGuiPacket(buffer);

        renderFluid = buffer.readFluidStack();
        fuelMax = buffer.readInt();
        fuel = buffer.readInt();
    }

    @Override
    public void handleStatePacket(PacketBuffer buffer) {

        super.handleStatePacket(buffer);

        renderFluid = buffer.readFluidStack();
    }
    // endregion

    // region NBT
    @Override
    public void read(CompoundNBT nbt) {

        super.read(nbt);

        fuelMax = nbt.getInt(TAG_FUEL_MAX);
        fuel = nbt.getInt(TAG_FUEL);
        coolantMax = nbt.getInt(TAG_COOLANT_MAX);
        coolant = nbt.getInt(TAG_COOLANT);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {

        super.write(nbt);

        nbt.putInt(TAG_FUEL_MAX, fuelMax);
        nbt.putInt(TAG_FUEL, fuel);
        nbt.putInt(TAG_COOLANT_MAX, coolantMax);
        nbt.putInt(TAG_COOLANT, coolant);

        return nbt;
    }
    // endregion

    // region AUGMENTS
    protected float processMod = 1.0F;

    @Override
    protected void resetAttributes() {

        super.resetAttributes();
        processMod = 1.0F;
    }

    @Override
    protected void setAttributesFromAugment(CompoundNBT augmentData) {

        super.setAttributesFromAugment(augmentData);
        processMod += getAttributeMod(augmentData, TAG_AUGMENT_POWER_MOD);
    }

    @Override
    protected void finalizeAttributes() {

        super.finalizeAttributes();
        processTick = Math.round(getBaseProcessTick() * processMod);
    }
    // endregion
}
