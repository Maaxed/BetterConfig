package fr.max2.betterconfig.client.gui.component;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.style.IPropertySource;
import fr.max2.betterconfig.client.gui.style.StyleProperty;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

/**
 * Represents a component of user interface
 */
public interface IComponent extends Renderable, NarratableEntry, IPropertySource
{
	void init(IComponentParent layoutManager, IComponent parent);

	// Style

	<T> T getStyleProperty(StyleProperty<T> property);

	// Layout

	Size getPrefSize();

	Size measureLayout();

	void computeLayout(Rectangle availableRect);

	void invalidate();

	// Rendering

	/**
	 * Renders the overlay of the element if it has one
	 * @param matrixStack the transformation matrix stack
	 * @param mouseX the x coordinate of the mouse on the screen
	 * @param mouseY the y coordinate of the mouse on the screen
	 * @param partialTicks
	 */
	void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, EventState state);

	// Mouse handling

	void mouseMoved(double mouseX, double mouseY);

	void mouseClicked(double mouseX, double mouseY, int button, EventState state);

	void mouseReleased(double mouseX, double mouseY, int button, EventState state);

	void mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state);

	void mouseScrolled(double mouseX, double mouseY, double delta, EventState state);

	boolean isHovered();

	// Input handling

	void keyPressed(int keyCode, int scanCode, int modifiers, EventState state);

	void keyReleased(int keyCode, int scanCode, int modifiers, EventState state);

	void charTyped(char codePoint, int modifiers, EventState state);

	void cycleFocus(boolean forward, CycleFocusState state);

	boolean hasFocus();

	// Narration

	@Override
	void updateNarration(NarrationElementOutput narrationOutput);
}
