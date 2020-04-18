package cofh.thermal.essentials.plugins.jei.machine;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermal.core.plugins.jei.Drawables;
import cofh.thermal.core.plugins.jei.ThermalCategory;
import cofh.thermal.core.util.recipes.machine.SawmillRecipe;
import cofh.thermal.essentials.client.gui.BasicSawmillScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static cofh.thermal.essentials.init.TEssReferences.BASIC_SAWMILL_BLOCK;

public class SawmillRecipeCategory extends ThermalCategory<SawmillRecipe> {

    public SawmillRecipeCategory(IGuiHelper guiHelper, ResourceLocation uid) {

        super(guiHelper, uid);

        background = guiHelper.drawableBuilder(BasicSawmillScreen.TEXTURE, 26, 11, 124, 62)
                .addPadding(0, 0, 16, 24)
                .build();
        localizedName = StringHelper.localize(BASIC_SAWMILL_BLOCK.getTranslationKey());

        progressBackground = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
        speedBackground = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_SAW);

        progress = guiHelper.createAnimatedDrawable(Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW), 200, IDrawableAnimated.StartDirection.LEFT, false);
        speed = guiHelper.createAnimatedDrawable(Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_SAW), 400, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public Class<? extends SawmillRecipe> getRecipeClass() {

        return SawmillRecipe.class;
    }

    @Override
    public void setIngredients(SawmillRecipe recipe, IIngredients ingredients) {

        ingredients.setInputIngredients(recipe.getInputItems());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputItems());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, SawmillRecipe recipe, IIngredients ingredients) {

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        for (int i = 0; i < outputs.size(); ++i) {
            float chance = recipe.getOutputItemChances().get(i);
            if (chance > 1.0F) {
                for (ItemStack stack : outputs.get(i)) {
                    stack.setCount((int) chance);
                }
            }
        }
        IGuiItemStackGroup guiItemStacks = layout.getItemStacks();

        guiItemStacks.init(0, true, 33, 14);
        guiItemStacks.init(1, false, 96, 14);
        guiItemStacks.init(2, false, 114, 14);
        guiItemStacks.init(3, false, 96, 32);
        guiItemStacks.init(4, false, 114, 32);

        guiItemStacks.set(0, inputs.get(0));

        for (int i = 0; i < outputs.size(); ++i) {
            guiItemStacks.set(i + 1, outputs.get(i));
        }
        addDefaultItemTooltipCallback(guiItemStacks, recipe.getOutputItemChances(), 1);
    }

    @Override
    public void draw(SawmillRecipe recipe, double mouseX, double mouseY) {

        super.draw(recipe, mouseX, mouseY);

        progressBackground.draw(62, 23);
        speedBackground.draw(34, 33);

        progress.draw(62, 23);
        speed.draw(34, 33);
    }

}
