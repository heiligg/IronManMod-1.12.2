package com.example.ironmanmod;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class EnergyHUD {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.player == null) return;

        int totalEnergy = 0;
        int maxEnergy = 0;

        // Loop through armor and calculate energy
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack stack = mc.player.getItemStackFromSlot(slot);
                if (stack.getItem() instanceof IronManArmor) {
                    IronManArmor armor = (IronManArmor) stack.getItem();
                    totalEnergy += armor.getEnergy(stack);   // pass ItemStack
                    maxEnergy += armor.getMaxEnergy();
                }
            }
        }

        if (maxEnergy == 0) return; // no armor, nothing to draw

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        int barWidth = 100;
        int barHeight = 10;
        int x = width / 2 - barWidth / 2;
        int y = height - 50;

        float percent = (float) totalEnergy / maxEnergy;
        int filledWidth = (int) (barWidth * percent);

        // Draw background
        mc.ingameGUI.drawRect(x, y, x + barWidth, y + barHeight, new Color(0, 0, 0, 150).getRGB());
        // Draw energy bar
        mc.ingameGUI.drawRect(x, y, x + filledWidth, y + barHeight, new Color(255, 0, 0, 200).getRGB());

        // Draw energy text
        mc.fontRenderer.drawString("Energy: " + totalEnergy + "/" + maxEnergy, x, y - 10, 0xFFFFFF);
    }
}
