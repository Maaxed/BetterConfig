package fr.max2.betterconfig.client.gui.better.widget;

import java.util.Objects;

import fr.max2.betterconfig.client.gui.component.widget.Button;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigUnknown;
import fr.max2.betterconfig.util.property.IListener;
import net.minecraft.network.chat.Component;

/** The widget for properties of unknown type */
public class UnknownOptionWidget extends Button
{
	private final ConfigPrimitive<?> property;
	private final IListener<Object> propertyListener;

	public UnknownOptionWidget(ConfigPrimitive<?> property)
	{
		super(Component.literal(Objects.toString(property.getValue())), NO_OVERLAY);
		this.addClass("better:unknown");
		this.setActive(false);

		this.property = property;
		this.propertyListener = newVal -> this.setMessage(Component.literal(Objects.toString(newVal)));
		this.property.onChanged(this.propertyListener);
	}

	public UnknownOptionWidget(ConfigUnknown property)
	{
		super(Component.literal(Objects.toString(property.getValue())), NO_OVERLAY);
		this.addClass("better:unknown");
		this.setActive(false);

		this.property = null;
		this.propertyListener = null;
	}

	@Override
	public void invalidate()
	{
		if (this.property != null && this.propertyListener != null)
			this.property.removeOnChangedListener(this.propertyListener);
	}
}
