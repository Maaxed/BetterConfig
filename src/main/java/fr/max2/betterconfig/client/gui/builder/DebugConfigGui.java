package fr.max2.betterconfig.client.gui.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.config.ConfigProperty;
import fr.max2.betterconfig.config.ConfigTable;
import fr.max2.betterconfig.config.IConfigEntryVisitor;
import fr.max2.betterconfig.config.IConfigPropertyVisitor;
import net.minecraft.client.gui.FontRenderer;

/**
 * A user interface builder that only shows the available configuration properties but doesn't allow edition
 */
public class DebugConfigGui  implements IGuiComponent
{
	/** The parent screen */
	private final BetterConfigScreen parent;
	/** The list of labels to display */
	private final List<String> labels;

	public DebugConfigGui(BetterConfigScreen parent, List<String> labels)
	{
		this.parent = parent;
		this.labels = labels;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		FontRenderer font = this.parent.getFont(); 
		this.parent.renderBackground(matrixStack);
		int y = 1;
		font.drawString(matrixStack, "Better Config Debug", 1, y, 0xFFFFFFFF);
		y += font.FONT_HEIGHT + 2;
		font.drawString(matrixStack, "Configs : " + this.parent.getModConfigs().stream().map(config -> "'" + config.getFileName() + "'").collect(Collectors.joining(", ")), 1, y, 0xFFFFFFFF);
		y += font.FONT_HEIGHT + 2;
		for (String txt : this.labels)
		{
			font.drawString(matrixStack, txt, 1, y, 0xFFFFFFFF);
			y += font.FONT_HEIGHT + 2;
		}
	}

	@Override
	public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }

	@Override
	public void setLayoutManager(ILayoutManager manager)
	{ }

	@Override
	public int getWidth()
	{
		return this.parent.width;
	}

	@Override
	public int getHeight()
	{
		return this.parent.height;
	}

	/**
	 * Builds the debug user interface
	 * @param screen the parent screen
	 * @param config the edited configuration
	 * @return the user interface
	 */
	public static DebugConfigGui build(BetterConfigScreen screen, ConfigTable config)
	{
		List<String> list = new ArrayList<>();
		config.exploreEntries(new TableBuilder(list), "").forEach(v -> {});
		return new DebugConfigGui(screen, list);
	}
	
	/** The visitor to build config tables */
	private static class TableBuilder implements IConfigEntryVisitor<String, Void>
	{
		private final List<String> content;

		public TableBuilder(List<String> content)
		{
			this.content = content;
		}
		
		@Override
		public Void visitSubTable(String key, ConfigTable table, String prefix)
		{
			table.exploreEntries(this, prefix + key + ".").forEach(v -> {});
			return null;
		}
		
		@Override
		public <T> Void visitValue(String key, ConfigProperty<T> property, String prefix)
		{
			this.content.add(property.explore(ValueBuilder.INSTANCE,  prefix + key));
			return null;
		}
	}

	/** The visitor to build config value widgets */
	private static enum ValueBuilder implements IConfigPropertyVisitor<String, String>
	{
		INSTANCE;
		
		@Override
		public String visitBoolean(ConfigProperty<Boolean> property, String path)
		{
			return path + " : " + "BOOL" + " = " + property.getValue();
		}
		
		@Override
		public String visitNumber(ConfigProperty<? extends Number> property, String path)
		{
			return path + " : " + "NUMBER" + " = " + property.getValue();
		}
		
		@Override
		public String visitString(ConfigProperty<String> property, String path)
		{
			return path + " : " + "STRING" + " = " + property.getValue();
		}
		
		@Override
		public <E extends Enum<E>> String visitEnum(ConfigProperty<E> property, String path)
		{
			return path + " : " + "ENUM" + " = " + property.getValue();
		}
		
		@Override
		public String visitList(ConfigProperty<? extends List<?>> property, String path)
		{
			StringBuilder str = new StringBuilder();
			List<?> value = property.getValue();
			for (int i = 0; i < value.size(); i++)
			{
				Object obj = value.get(i);
				if (str.length() != 0)
					str.append(", ");
				str.append("[" + i + "] " + Objects.toString(obj));
				
			}
			return path + " : " + "LIST" + " = {" + str.toString() + "}";
		}
		
		@Override
		public String visitUnknown(ConfigProperty<?> property, String path)
		{
			return path + " : " + "UNKNOWN" + " = " + property.getValue();
		}
	}
}