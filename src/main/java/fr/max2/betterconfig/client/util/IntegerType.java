package fr.max2.betterconfig.client.util;

import java.util.function.Function;

public final class IntegerType<N extends Number> implements INumberType<N>
{
	public static final IntegerType<Byte> BYTE = new IntegerType<>(Byte::parseByte, Number::byteValue);
	public static final IntegerType<Short> SHORT = new IntegerType<>(Short::parseShort, Number::shortValue);
	public static final IntegerType<Integer> INTEGER = new IntegerType<>(Integer::parseInt, Number::intValue);
	public static final IntegerType<Long> LONG = new IntegerType<>(Long::parseLong, Number::longValue);
	
	/** The function to parse the number */
	private final Function<String, N> parser;
	/** The function to convert a number into the represented integer */
	private final Function<Number, N> converter;

	private IntegerType(Function<String, N> parser, Function<Number, N> converter)
	{
		this.parser = parser;
		this.converter = converter;
	}

	@Override
	public N parse(String value) throws NumberFormatException
	{
		return this.parser.apply(value);
	}

	@Override
	public N applyOperation(N value, Operator op, Increment inc)
	{
		long left = switch (inc)
		{
			case HIGH -> 10;
			case NORMAL, LOW -> 1;
		};
		
		return this.converter.apply(value.longValue() + op.getMultiplier() * left);
	}
	
}
