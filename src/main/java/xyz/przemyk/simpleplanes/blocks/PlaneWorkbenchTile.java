package xyz.przemyk.simpleplanes.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xyz.przemyk.simpleplanes.setup.SimplePlanesBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaneWorkbenchTile extends BlockEntity {

    public ItemStackHandler itemStackHandler = new ItemStackHandler(2);
    public LazyOptional<ItemStackHandler> itemStackHandlerLazyOptional = LazyOptional.of(() -> itemStackHandler);
    public DataSlot selectedRecipe = DataSlot.standalone();

    public PlaneWorkbenchTile(BlockPos blockPos, BlockState blockState) {
        super(SimplePlanesBlocks.PLANE_WORKBENCH_TILE.get(), blockPos, blockState);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("input", itemStackHandler.serializeNBT());
        compoundTag.putInt("selected_recipe", selectedRecipe.get());
        return super.save(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        itemStackHandler.deserializeNBT(compoundTag.getCompound("input"));
        selectedRecipe.set(compoundTag.getInt("selected_recipe"));
        super.load(compoundTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemStackHandlerLazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemStackHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
}