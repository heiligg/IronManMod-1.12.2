package com.example.ironmanmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.Color;

public class ClientProxy extends CommonProxy {

    public static KeyBinding repulsorKey;
    public static KeyBinding flyKey;

    @Override
    public void registerRenderers() {
        // Register item models
        ModelLoader.setCustomModelResourceLocation(IronManMod.ironmanHelmet, 0,
                new ModelResourceLocation("ironmanmod:ironman_helmet", "inventory"));
        ModelLoader.setCustomModelResourceLocation(IronManMod.ironmanChest, 0,
                new ModelResourceLocation("ironmanmod:ironman_chest", "inventory"));
        ModelLoader.setCustomModelResourceLocation(IronManMod.ironmanLegs, 0,
                new ModelResourceLocation("ironmanmod:ironman_legs", "inventory"));
        ModelLoader.setCustomModelResourceLocation(IronManMod.ironmanBoots, 0,
                new ModelResourceLocation("ironmanmod:ironman_boots", "inventory"));

        // Keybindings
        repulsorKey = new KeyBinding("key.ironman.repulsor", Keyboard.KEY_R, "key.categories.ironman");
        flyKey = new KeyBinding("key.ironman.fly", Keyboard.KEY_F, "key.categories.ironman");

        ClientRegistry.registerKeyBinding(repulsorKey);
        ClientRegistry.registerKeyBinding(flyKey);

        // Register handlers
        MinecraftForge.EVENT_BUS.register(new ArmorTickHandler());
        MinecraftForge.EVENT_BUS.register(new EnergyHUD());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    // Energy HUD
    public static class EnergyHUD {
        private Minecraft mc = Minecraft.getMinecraft();

        @SubscribeEvent
        public void onRenderHUD(RenderGameOverlayEvent.Post event) {
            if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
            if (mc.player == null) return;

            int totalEnergy = 0;
            int maxEnergy = 0;

            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                    ItemStack stack = mc.player.getItemStackFromSlot(slot);
                    if (stack.getItem() instanceof IronManArmor) {
                        IronManArmor armor = (IronManArmor) stack.getItem();
                        totalEnergy += armor.getEnergy(stack);
                        maxEnergy += armor.getMaxEnergy();
                    }
                }
            }

            if (maxEnergy == 0) return;

            int width = event.getResolution().getScaledWidth();
            int height = event.getResolution().getScaledHeight();
            int barWidth = 100;
            int barHeight = 10;
            int x = width / 2 - barWidth / 2;
            int y = height - 50;

            float percent = (float) totalEnergy / maxEnergy;
            int filledWidth = (int) (barWidth * percent);

            // Draw bars
            mc.ingameGUI.drawRect(x, y, x + barWidth, y + barHeight, new Color(0, 0, 0, 150).getRGB());
            mc.ingameGUI.drawRect(x, y, x + filledWidth, y + barHeight, new Color(255, 0, 0, 200).getRGB());

            // Draw energy text
            mc.fontRenderer.drawString("Energy: " + totalEnergy + "/" + maxEnergy, x, y - 10, 0xFFFFFF);
        }
    }
}
