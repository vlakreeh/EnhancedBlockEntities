package foundationgames.enhancedblockentities.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderChestBlockEntity.class)
public abstract class EnderChestBlockEntityMixin extends BlockEntity {
    @Shadow
    @Final
    private ChestLidAnimator lidAnimator;
    private int rebuildScheduler = 0;

    protected EnderChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Inject(method = "clientTick", at = @At("RETURN"))
    private static void clientTickHook(World world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity, CallbackInfo ci) {
        @SuppressWarnings("all")
        EnderChestBlockEntityMixin blockEntityMixin = (EnderChestBlockEntityMixin) (Object) blockEntity;

        if(blockEntityMixin.rebuildScheduler > 0) {
            blockEntityMixin.rebuildScheduler--;
            if(blockEntityMixin.rebuildScheduler <= 0) blockEntityMixin.rebuildChunk();
        }

        ChestLidAnimatorMixinExt lidAccessorExt = (ChestLidAnimatorMixinExt) blockEntityMixin.lidAnimator;

        float progress = lidAccessorExt.getProgress();
        float lastProgress = lidAccessorExt.getLastProgress();

        boolean sameProgress = progress == lastProgress;

        if (sameProgress) return;

        float progressDelta = progress - lastProgress;

        if (progressDelta > 0 && lastProgress == 0) {
            blockEntityMixin.rebuildChunk();
        } else if (progressDelta < 0 && progress == 0) {
            blockEntityMixin.rebuildScheduler = 1;
        }
    }

    private void rebuildChunk() {
        MinecraftClient.getInstance().worldRenderer.updateBlock(world, pos, getCachedState(), getCachedState(), 1);
    }
}
