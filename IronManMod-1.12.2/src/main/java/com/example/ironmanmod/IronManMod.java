package com.example.ironmanmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = IronManMod.MODID, name = IronManMod.NAME, version = IronManMod.VERSION)
public class IronManMod {
    public static final String MODID = "ironmanmod";
    public static final String NAME = "Iron Man Mod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println(NAME + " is loaded!");
    }
}
