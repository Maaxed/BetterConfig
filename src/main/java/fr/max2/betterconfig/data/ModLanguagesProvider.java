package fr.max2.betterconfig.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import fr.max2.betterconfig.BetterConfig;
import fr.max2.betterconfig.client.gui.component.widget.CycleOptionButton;
import fr.max2.betterconfig.client.util.GuiTexts;
import fr.max2.betterconfig.config.impl.value.ForgeConfigList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguagesProvider implements DataProvider
{
	/** The list of supported languages */
	private final List<PartialLanguageProvider> languages = new ArrayList<>();
	
	protected ModLanguagesProvider(DataGenerator gen, String modId, String... locales)
	{
		for (String locale : locales)
		{
			this.languages.add(new PartialLanguageProvider(gen, modId, locale));
		}
	}
	
	public ModLanguagesProvider(DataGenerator gen)
	{
		this(gen, BetterConfig.MODID, "en_us", "fr_fr");
	}
	
	protected void addTranslations()
    {
		add(CycleOptionButton.NO_OPTION_KEY, "NONE", "AUCUN");
		add(CycleOptionButton.TRUE_OPTION_KEY, "ON", "Oui");
		add(CycleOptionButton.FALSE_OPTION_KEY, "OFF", "Non");
		add(GuiTexts.CANCEL_CONFIG_KEY, "Cancel changes", "Annuler les modifications");
		
		add(GuiTexts.DEFAULT_VALUE_KEY, "Default: %s", "Par défaut : %s");
		add(GuiTexts.SEARCH_BAR_KEY, "Search", "Rechercher");
		add(GuiTexts.ADD_ELEMENT_KEY, "Add", "Ajouter");
		add(GuiTexts.ADD_FIRST_TOOLTIP_KEY, "Add a new element at the start of the list", "Ajouter un nouvel élément au début de la liste");
		add(GuiTexts.ADD_LAST_TOOLTIP_KEY, "Add a new element at the end of the list", "Ajouter un nouvel élément à la fin de la liste");
		add(GuiTexts.REMOVE_TOOLTIP_KEY, "Remove this element from the list", "Retirer cet élément de la liste");
		add(GuiTexts.UNDO_TOOLTIP_KEY, "Undo", "Annuler le changement");
		add(GuiTexts.RESET_TOOLTIP_KEY, "Reset to default", "Réinitialiserà la valeur par défaut");
		add(ForgeConfigList.LIST_ELEMENT_LABEL_KEY, "%s[%d]", "%s[%d]");
		
		// Narration
		add(GuiTexts.SECTION_TITLE_SHOWN, "%s section", "Section %s");
		add(GuiTexts.SECTION_TITLE_COLLAPSED, "%s collapsed section", "Section repliée %s");
		add(GuiTexts.SECTION_USAGE_FOCUSED, "Press Enter to show or hide", "Appuyez sur Entrée pour afficher ou cacher");
		add(GuiTexts.SECTION_USAGE_HOVERED, "Left click to show or hide", "Faites un clic gauche pour afficher ou cacher");
    }
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		this.addTranslations();
		for (LanguageProvider language : this.languages)
		{
			language.run(cache);
		}
	}
	
	/** Add the translations of the given key for all languages */
	protected void add(String key, String... values)
	{
		Preconditions.checkArgument(this.languages.size() == values.length, "The number of provided values should be equal to the number of languages");
		for (int i = 0; i < this.languages.size(); i++)
		{
			this.languages.get(i).add(key, values[i]);
		}
	}
	
	@Override
	public String getName()
	{
		return BetterConfig.MODID + " Languages";
	}

	private static class PartialLanguageProvider extends LanguageProvider
	{
		public PartialLanguageProvider(DataGenerator gen, String modid, String locale)
		{
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations()
		{ }
	}
}
