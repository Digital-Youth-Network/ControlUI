package com.dyn.control.gui.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;
import com.dyn.control.command.Command;
import com.dyn.control.command.param.Parameter;
import com.dyn.control.gui.GuiServerCommand;
import com.dyn.control.gui.IGuiComponent;

public class EnterIPAddress implements IGuiComponent {
	private static final String allowedCharacters = ".0123456789";
	private GuiServerCommand gui;
	private FontRenderer fontRenderer;

	public EnterIPAddress(GuiServerCommand gui, FontRenderer fontRenderer, Command command, Parameter parameter) {
		this.gui = gui;
		this.fontRenderer = fontRenderer;

		this.command = command;
		this.parameter = parameter;

		this.inputText = "";
	}

	public int getWidth() {
		return 100;
	}

	public int getHeight() {
		return this.fontRenderer.FONT_HEIGHT + 1;
	}

	public String getTitle() {
		return this.command.getName();
	}

	public String getHelp() {
		return this.command.getHelp();
	}

	public void keyTyped(char c, int i) {
		if (i == 28) {
			select();
		} else if (this.inputText != null) {
			if ((i == 14) && (this.inputText.length() > 0)) {
				this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
			}
			if ((allowedCharacters.indexOf(c) >= 0) && (this.inputText.length() < 10)) {
				this.inputText += c;
			}
		}
	}

	public void mouseMove(int x, int y) {
	}

	public void mouseClick(int x, int y) {
	}

	public void select() {
		boolean inputOk = this.parameter.setInput(this.inputText);

		if (inputOk) {
			this.gui.selectCommand(this.command);
		}
	}

	private Command command;

	private Parameter parameter;

	private String inputText;

	public void renderComponent(int x, int y) {
		int colour = 14737632;
		String prompt = this.parameter.getLabel() + ":";
		int pWidth = this.fontRenderer.getStringWidth(prompt);
		this.fontRenderer.drawString(prompt, x, y + 1, colour);
		this.fontRenderer.drawString(this.inputText, x + pWidth + 5, y + 1, colour);

		if (this.gui.updateCounter / 6 % 2 == 0) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(2896);
			Minecraft.getMinecraft().renderEngine.bindTexture(this.gui.icons);

			int width = this.fontRenderer.getStringWidth(this.inputText);
			this.gui.drawTexturedModalRect(x + pWidth + 5 + width, y + 1, 0, 6, 3, 8);
		}
	}

	public void reset() {
		this.command.clearInput();
	}
}
