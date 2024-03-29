package fr.max2.betterconfig.client.gui.style.operator;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class StructOperator<T> implements IStyleOperation<T>
{
	private StructDefinition<T> definition;
	private Map<String, IStyleOperation<?>> members;
	
	public StructOperator(StructDefinition<T> definition, Map<String, IStyleOperation<?>> members)
	{
		this.members = members;
	}

	@Override
	public String typeName()
	{
		return "struct";
	}

	@Override
	public T updateValue(@Nullable T prevValue, @Nullable T defaultValue)
	{
		T value = prevValue;
		if (value == null)
			value = defaultValue;
		if (value == null)
			value = this.definition.defaultValue();
		
		
		
		return value;
	}
}
