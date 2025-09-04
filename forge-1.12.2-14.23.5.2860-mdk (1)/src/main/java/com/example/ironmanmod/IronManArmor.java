package com.example.ironmanmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IronManArmor extends ItemArmor {

    private int maxEnergy = 100;

    public IronManArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setUnlocalizedName("ironman_armor_" + slot.getName());
        this.setRegistryName("ironman_armor_" + slot.getName());
    }

    // Repulsor ability: shoot a projectile
    public void repulsorShoot(EntityPlayer player, ItemStack stack) {
        if (getEnergy(stack) >= 10) {
            World world = player.world;
            if (!world.isRemote) {
                EntitySnowball projectile = new EntitySnowball(world, player);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
                world.spawnEntity(projectile);
            }
            consumeEnergy(stack, 10);
        } else {
            if (!player.world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString("Not enough energy!"));
            }
        }
    }

    // Flight ability (called by KeyInputHandler)
    public void fly(EntityPlayer player, ItemStack stack) {
        if (getEnergy(stack) >= 5) {
            player.motionY += 0.3; // vertical boost
            player.motionX *= 1.05; // small horizontal speed boost
            player.motionZ *= 1.05;
            consumeEnergy(stack, 5);
        }
    }

    // Recharge energy over time
    public void recharge(ItemStack stack) {
        int energy = getEnergy(stack);
        if (energy < maxEnergy) {
            setEnergy(stack, energy + 1);
        }
    }

    // Consume energy safely
    public void consumeEnergy(ItemStack stack, int amount) {
        int energy = getEnergy(stack);
        energy -= amount;
        if (energy < 0) energy = 0;
        setEnergy(stack, energy);
    }

    // Get energy from ItemStack (stored in NBT)
    public int getEnergy(ItemStack stack) {
        ensureTag(stack);
        return stack.getTagCompound().getInteger("Energy");
    }

    // Set energy in ItemStack
    public void setEnergy(ItemStack stack, int energy) {
        ensureTag(stack);
        stack.getTagCompound().setInteger("Energy", energy);
    }

    // Make sure the ItemStack has an NBT tag
    private void ensureTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }
}
