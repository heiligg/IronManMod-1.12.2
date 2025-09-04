package com.example.ironmanmod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.UUID;
import java.util.HashMap;

public class ArmorTickHandler {

    private static HashMap<UUID, Boolean> flightToggles = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        boolean fullSuit = true;

        // Check if player has full suit
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (!(stack.getItem() instanceof IronManArmor)) {
                    fullSuit = false;
                    break;
                }
            }
        }

        // Recharge armor pieces
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof IronManArmor) {
                ((IronManArmor) stack.getItem()).recharge(stack);
            }
        }

        if (!fullSuit) {
            flightToggles.put(player.getUniqueID(), false);
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            return;
        }

        // Get toggle state
        boolean flying = flightToggles.getOrDefault(player.getUniqueID(), false);
        player.capabilities.allowFlying = flying;

        if (flying) {
            // Vertical boost
            player.motionY += 0.05;

            // Horizontal boost if CTRL is held
            if (player.isSneaking()) {
                double yaw = Math.toRadians(player.rotationYaw);
                double boost = 0.15;
                player.motionX += -Math.sin(yaw) * boost;
                player.motionZ += Math.cos(yaw) * boost;
            }

            // Drain energy from chest
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (chest.getItem() instanceof IronManArmor) {
                IronManArmor armor = (IronManArmor) chest.getItem();
                armor.consumeEnergy(chest, 1);
                if (armor.getEnergy(chest) <= 0) {
                    flightToggles.put(player.getUniqueID(), false);
                    player.capabilities.isFlying = false;
                }
            }
        }

        if (player.onGround) {
            flightToggles.put(player.getUniqueID(), false);
        }
    }

    public static void toggleFlight(EntityPlayer player) {
        boolean flying = flightToggles.getOrDefault(player.getUniqueID(), false);
        flightToggles.put(player.getUniqueID(), !flying);
    }
}
