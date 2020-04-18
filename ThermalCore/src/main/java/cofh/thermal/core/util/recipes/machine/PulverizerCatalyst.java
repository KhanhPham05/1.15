package cofh.thermal.core.util.recipes.machine;

import cofh.thermal.core.common.ThermalRecipeTypes;
import cofh.thermal.core.util.recipes.ThermalCatalyst;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static cofh.thermal.core.ThermalCore.RECIPE_SERIALIZERS;
import static cofh.thermal.core.common.ThermalReferences.ID_CATALYST_PULVERIZER;

public class PulverizerCatalyst extends ThermalCatalyst {

    public PulverizerCatalyst(ResourceLocation recipeId, Ingredient ingredient, float primaryMod, float secondaryMod, float energyMod, float minChance, float useChance) {

        super(recipeId, ingredient, primaryMod, secondaryMod, energyMod, minChance, useChance);
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {

        return RECIPE_SERIALIZERS.get(ID_CATALYST_PULVERIZER);
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {

        return ThermalRecipeTypes.CATALYST_PULVERIZER;
    }

    //    @Nonnull
    //    @Override
    //    public String getGroup() {
    //
    //        return MACHINE_PULVERIZER_BLOCK.getTranslationKey();
    //    }
    //
    //    @Nonnull
    //    @Override
    //    public ItemStack getIcon() {
    //
    //        return new ItemStack(MACHINE_PULVERIZER_BLOCK);
    //    }

}
