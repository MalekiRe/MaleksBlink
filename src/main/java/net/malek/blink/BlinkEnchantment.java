package net.malek.blink;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class BlinkEnchantment extends Enchantment {
    protected BlinkEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values());
    }
    @Override
    public int getMinPower(int level) {
        return 1;
    }
    @Override
    public int getMaxLevel() {
        return 5;
    }
}
