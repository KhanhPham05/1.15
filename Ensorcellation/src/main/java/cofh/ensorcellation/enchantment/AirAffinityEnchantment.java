package cofh.ensorcellation.enchantment;

import cofh.core.enchantment.EnchantmentCoFH;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class AirAffinityEnchantment extends EnchantmentCoFH {

    public AirAffinityEnchantment() {

        super(Rarity.RARE, EnchantmentType.ARMOR_HEAD, new EquipmentSlotType[]{EquipmentSlotType.HEAD});
    }

    @Override
    public int getMinEnchantability(int level) {

        return 1;
    }

    @Override
    protected int maxDelegate(int level) {

        return getMinEnchantability(level) + 40;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return enable && (type != null && type.canEnchantItem(stack.getItem()) || supportsEnchantment(stack));
    }

}
