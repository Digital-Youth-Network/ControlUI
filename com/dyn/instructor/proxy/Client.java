package com.dyn.instructor.proxy;

import org.lwjgl.input.Keyboard;

import com.dyn.instructor.gui.Home;
import com.rabbit.gui.GuiFoundation;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

public class Client implements Proxy {

	private KeyBinding teacherKey;

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Render GUI when on call from client
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

		if ((Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
			return;
		}
		if (this.teacherKey.getIsKeyPressed()) {
			// this is broken because of computercraft... for now we will keep
			// it in our hands until this gets sorted out
			// if(getOpLevel(Minecraft.getMinecraft().thePlayer.getGameProfile())>0)
			GuiFoundation.proxy.display(new Home());
		}
	}

	@Override
	public void init() {
		FMLCommonHandler.instance().bus().register(this);

		this.teacherKey = new KeyBinding("key.toggle.teacherui", Keyboard.KEY_K, "key.categories.toggle");

		ClientRegistry.registerKeyBinding(this.teacherKey);
	}

}