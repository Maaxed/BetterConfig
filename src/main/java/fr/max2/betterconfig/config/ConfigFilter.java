package fr.max2.betterconfig.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigFilter
{
	public static final ConfigFilter ALL = new ConfigFilter(Collections.emptyList());
	
	private final List<String> words;
	
	private ConfigFilter(List<String> words)
	{
		this.words = words;
	}
	
	public ConfigFilter()
	{
		this(new ArrayList<>());
	}

	public void setFilter(String filter)
	{
		this.words.clear();
		if (filter == null)
			return;
		
		for (String word : filter.split("[ .,]"))
		{
			if (!word.isEmpty())
			{
				this.words.add(word.toLowerCase());
			}
		}
	}
	
	public boolean matches(ConfigProperty<?> property)
	{
		for (String word : this.words)
		{
			if (!propertyMatchesWord(property, word))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean matches(ConfigTable table)
	{
		for (String word : this.words)
		{
			if (!tableMatchesWord(table, word))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean propertyMatchesWord(ConfigProperty<?> property, String word)
	{
		String comment = property.getCommentString(); 
		return comment != null && comment.toLowerCase().contains(word)
		    || property.getDisplayName().getString().toLowerCase().contains(word)
		    || property.getPath().stream().anyMatch(p -> p.toLowerCase().contains(word));
	}
	
	private static boolean tableMatchesWord(ConfigTable table, String word)
	{
		String comment = table.getCommentString(); 
		return comment != null && comment.toLowerCase().contains(word)
		    || table.getPath().stream().anyMatch(p -> p.toLowerCase().contains(word));
	}
}
