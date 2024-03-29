package fr.max2.betterconfig.client.gui.component.widget;

import java.util.List;
import java.util.function.Function;

import fr.max2.betterconfig.client.util.GuiTexts;
import net.minecraft.network.chat.Component;

/**
 * A button for cycling between several options
 * @param <V> the type of option value
 */
public class CycleOptionButton<V> extends Button
{
	/** The list of available option values */
	private final List<? extends V> acceptedValues;
	/** The function to get the text to show from the selection option */
	private final Function<? super V, Component> valueToText;
	/** The index of the selected option in the list */
	private int index;

	public CycleOptionButton(List<? extends V> acceptedValues, Function<? super V, Component> valueToText, V currentValue, OnTooltip tooltip)
	{
		super(getValueText(valueToText, currentValue), tooltip);
		this.acceptedValues = acceptedValues;
		this.valueToText = valueToText;
		this.index = acceptedValues.indexOf(currentValue);
	}

	/** Gets the currently selected option value */
	public V getCurrentValue()
	{
		return this.acceptedValues.size() == 0 ? null : this.acceptedValues.get(this.index % this.acceptedValues.size());
	}

	protected void setCurrentValue(V newValue)
	{
		this.index = this.acceptedValues.indexOf(newValue);
		this.setMessage(getValueText(this.valueToText, newValue));
	}

	/** Selects the next available option */
	public void cycleOption()
	{
		this.index++;
		if (this.index >= this.acceptedValues.size())
		{
			this.index = 0;
		}
		this.setMessage(getValueText(this.valueToText, this.getCurrentValue()));
	}

	@Override
	protected void onPress()
	{
		this.cycleOption();
		super.onPress();
	}

	/** Gets the text corresponding to the given option value using the given translation function */
	private static <V> Component getValueText(Function<? super V, Component> valueToText, V value)
	{
		return value == null ? Component.translatable(GuiTexts.NO_OPTION_KEY) : valueToText.apply(value);
	}

}
