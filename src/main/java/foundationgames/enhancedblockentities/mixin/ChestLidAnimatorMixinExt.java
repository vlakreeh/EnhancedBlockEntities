package foundationgames.enhancedblockentities.mixin;

import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestLidAnimator.class)
public interface ChestLidAnimatorMixinExt {
    @Accessor
    float getProgress();

    @Accessor
    float getLastProgress();
}
