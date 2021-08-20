package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.Button;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.client.gui.component.TextField;
import fr.max2.betterconfig.config.ConfigFilter;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.config.ModConfig;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The container for the main section */
public class GuiRoot extends FocusableGui implements INestedGuiComponent
{
	/** The the height of the header */
	private static final int CONTAINER_HEADER_HEIGHT = 60;
	/** The x position of the input field of the search bar */
	private static final int SEARCH_LABEL_WIDTH = 80;
	/** The parent screen */
	private final BetterConfigScreen screen;
	/** The text field of the search bar */
	private final TextField searchField;
	/** The scroll panel */
	private final BetterScrollPane scrollPane;
	/** The tab buttons */
	private final List<IGuiComponent> components = new ArrayList<>();
	/** The filter from the search bar */
	private final ConfigFilter filter = new ConfigFilter();

	public GuiRoot(BetterConfigScreen screen, IBetterElement content)
	{
		this.screen = screen;
		int x = X_PADDING;
		
		// Tabs
		int buttonWidth = (this.screen.width - 2 * X_PADDING) / ModConfig.Type.values().length;
		int i = 0;
		for (ModConfig config : screen.getModConfigs())
		{
			final int index = i;
			Button b = new Button(x, Y_PADDING, buttonWidth, 20, new StringTextComponent(config.getFileName()), thisButton -> this.screen.openConfig(index), Button.NO_TOOLTIP);
			b.active = index != screen.getCurrentConfigIndex();
			this.components.add(b);
			
			x += buttonWidth;
			i++;
		}
		
		// Search bar
		this.searchField = new TextField(screen.getFont(), X_PADDING + SEARCH_LABEL_WIDTH + 1, 20 + 2 * Y_PADDING + 1, this.screen.width - 2 * X_PADDING - SEARCH_LABEL_WIDTH - 2, 20 - 2, new TranslationTextComponent(SEARCH_BAR_KEY));
		this.searchField.setResponder(this::updateFilter);
		this.components.add(this.searchField);
		
		// Scroll
		this.scrollPane = new BetterScrollPane(screen.getMinecraft(), X_PADDING, Y_PADDING + CONTAINER_HEADER_HEIGHT, screen.width - 2 * X_PADDING, screen.height - 2 * Y_PADDING - CONTAINER_HEADER_HEIGHT, content);
		this.components.add(this.scrollPane);
		this.scrollPane.setYgetHeight(Y_PADDING + CONTAINER_HEADER_HEIGHT, this.filter);
	}
	
	// Layout

	@Override
	public List<? extends IGuiComponent> getEventListeners()
	{
		return this.components;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		return true;
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{ }

	@Override
	public int getWidth()
	{
		return this.screen.width;
	}

	@Override
	public int getHeight()
	{
		return this.screen.height;
	}
	
	/** Updates the content using the given filter string */
	private void updateFilter(String filterStr)
	{
		this.filter.setFilter(filterStr);
		this.scrollPane.marksLayoutDirty();
		this.scrollPane.checkLayout();
	}
	
	// Rendering
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.screen.renderBackground(matrixStack);
		INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
		FontRenderer font = this.screen.getFont();
		font.drawText(matrixStack, this.searchField.getMessage(), X_PADDING, 20 + 2 * Y_PADDING + (20 - font.FONT_HEIGHT) / 2, 0xFF_FF_FF_FF);
		this.renderHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{ }
	
}