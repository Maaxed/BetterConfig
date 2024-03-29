package fr.max2.betterconfig.client.gui.component.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.CycleFocusState;
import fr.max2.betterconfig.client.gui.component.EventState;
import fr.max2.betterconfig.client.gui.component.UnitComponent;
import fr.max2.betterconfig.client.gui.layout.Rectangle;
import fr.max2.betterconfig.client.gui.style.PropertyIdentifier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class WidgetComponent<W extends AbstractWidget> extends UnitComponent
{
	public static final PropertyIdentifier<Boolean> ACTIVE = new PropertyIdentifier<>(new ResourceLocation(BetterConfig.MODID, "active"));

	public final W widget;

	public WidgetComponent(String type, W widget)
	{
		super(type);
		this.widget = widget;
		this.registerProperty(ACTIVE, () -> this.widget.active);
	}

	// Layout

	protected void setPos(int x, int y)
	{
		this.widget.setPosition(x, y);
	}

	protected void setSize(int w, int h)
	{
		this.widget.setWidth(w);
		this.widget.setHeight(h);
	}

	private void updatePosition()
	{
		Rectangle rect = this.getRect();
		this.setPos(rect.x, rect.y);
	}

	@Override
	protected void setRelativeRect(Rectangle rect)
	{
		super.setRelativeRect(rect);
		this.setSize(rect.size.width, rect.size.height);
	}

	// Rendering

	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.updatePosition();
		this.widget.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	// Mouse Handling

	@Override
	protected void onMouseMoved(double mouseX, double mouseY)
	{
		this.updatePosition();
		this.widget.mouseMoved(mouseX, mouseY);
	}

	@Override
	protected void onMouseClicked(double mouseX, double mouseY, int button, EventState state)
	{
		this.updatePosition();
		if (state.isConsumed())
		{
			if (this.widget.isFocused())
			{
				// Ensure this widget is not focused
				this.widget.changeFocus(true);
			}
		}
		else if (this.widget.mouseClicked(mouseX, mouseY, button))
		{
			state.consume();
		}
	}

	@Override
	protected void onMouseReleased(double mouseX, double mouseY, int button, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.widget.mouseReleased(mouseX, mouseY, button))
			state.consume();
	}

	@Override
	protected void onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.widget.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			state.consume();
	}

	@Override
	protected void onMouseScrolled(double mouseX, double mouseY, double delta, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.widget.mouseScrolled(mouseX, mouseY, delta))
			state.consume();
	}

	// Input handling

	@Override
	protected void onKeyPressed(int keyCode, int scanCode, int modifiers, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.hasFocus() && this.widget.keyPressed(keyCode, scanCode, modifiers))
			state.consume();
	}

	@Override
	protected void onKeyReleased(int keyCode, int scanCode, int modifiers, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.hasFocus() && this.widget.keyReleased(keyCode, scanCode, modifiers))
			state.consume();
	}

	@Override
	protected void onCharTyped(char codePoint, int modifiers, EventState state)
	{
		this.updatePosition();
		if (!state.isConsumed() && this.hasFocus() && this.widget.charTyped(codePoint, modifiers))
			state.consume();
	}

	@Override
	protected void onCycleFocus(boolean forward, CycleFocusState state)
	{
		this.updatePosition();
		if (state.isConsumed())
		{
			// If something is already focused
			if (this.widget.isFocused())
			{
				// Ensure this widget is not focused
				this.widget.changeFocus(forward);
			}
		}
		else if (state.isPropagated())
		{
			// Ensure focused
			if (this.widget.isFocused())
			{
				state.consume();
			}
			else if (this.widget.changeFocus(forward))
			{
				state.consume();
			}
		}
		else if (this.widget.isFocused())
		{
			// Try unfocus and propagate
			if (this.widget.changeFocus(forward))
			{
				state.consume();
			}
			else
			{
				state.propagate();
			}
		}

		if (this.widget.isFocused())
			this.layoutManager.setAreaOfInterest(this.relativeRect);
	}

	// Narration

	@Override
	public NarrationPriority narrationPriority()
	{
		return this.widget.narrationPriority();
	}

	@Override
	public boolean isActive()
	{
		return super.isActive() && this.widget.isActive();
	}

	@Override
	public boolean hasFocus()
	{
		return this.widget.isFocused();
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationOutput)
	{
		this.widget.updateNarration(narrationOutput);
		super.updateNarration(narrationOutput);
	}

}
