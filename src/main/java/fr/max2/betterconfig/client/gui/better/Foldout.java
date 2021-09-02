package fr.max2.betterconfig.client.gui.better;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.BetterConfigScreen;
import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.gui.component.IGuiComponent;
import fr.max2.betterconfig.client.gui.component.INestedGuiComponent;
import fr.max2.betterconfig.config.ConfigFilter;
import fr.max2.betterconfig.config.value.IConfigNode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import static fr.max2.betterconfig.client.gui.better.Constants.*;

/** The ui for a expand/collapse subsection */
public class Foldout extends AbstractContainerEventHandler implements INestedGuiComponent, IBetterElement
{
	/** The height of the fouldout header */
	private static final int FOLDOUT_HEADER_HEIGHT = 24;
	/** The parent screen */
	private final BetterConfigScreen screen;
	
	/** The edited table */
	private final IConfigNode<?> node;
	/** The content that will be collapsed */
	private final IBetterElement content;
	/** The extra info to show on the tooltip */
	private final List<FormattedText> extraInfo = new ArrayList<>();
	/** The layout to notify for layout update */
	private ILayoutManager layout;
	/** The x coordinate of this component */
	private final int baseX;
	/** The y coordinate of this component */
	private int baseY = 0;
	/** The current height of this component */
	private int height = 0;
	
	/** {@code true} when the content is collapsed, {@code false} otherwise */
	private boolean folded = false;
	/** Indicates if the section is hidden or not */
	private boolean hidden = false;

	public Foldout(BetterConfigScreen screen, IConfigNode<?> node, IBetterElement content, int x)
	{
		this.screen = screen;
		this.node = node;
		this.content = content;
		this.extraInfo.add(FormattedText.of(node.getName(), Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));
		this.extraInfo.addAll(node.getDisplayComment());
		this.baseX = x;
	}
	
	// Layout

	@Override
	public List<? extends IGuiComponent> children()
	{
		return this.folded || this.hidden ? Collections.emptyList() : Arrays.asList(this.content);
	}

	@Override
	public int setYgetHeight(int y, ConfigFilter filter)
	{
		boolean matchFilter = filter.matches(this.node);
		this.baseY = y;
		int contentHeight = this.content.setYgetHeight(y + FOLDOUT_HEADER_HEIGHT, matchFilter ? ConfigFilter.ALL : filter);
		
		if (contentHeight == 0)
		{
			// Disable this section
			this.hidden = true;
			this.height = 0;
			updateTexts();
			return 0;
		}
		
		this.hidden = false;

		if (this.folded)
		{
			contentHeight = 0;
		}
		
		this.height = contentHeight + FOLDOUT_HEADER_HEIGHT;
		updateTexts();
		return this.height;
	}
	
	private void updateTexts()
	{
		this.extraInfo.clear();
		this.extraInfo.add(FormattedText.of(this.node.getName(), Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));
		this.extraInfo.addAll(this.node.getDisplayComment());
	}
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		this.content.setLayoutManager(manager);
	}
	
	@Override
	public void onLayoutChanged()
	{
		this.content.onLayoutChanged();
	}

	@Override
	public int getWidth()
	{
		return this.screen.width - X_PADDING - RIGHT_PADDING - this.baseX - this.layout.getLayoutX();
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}
	
	// Mouse interaction
	
	public void toggleFolding()
	{
		this.folded = !this.folded;
		this.layout.marksLayoutDirty();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (this.isOverHeader(mouseX, mouseY))
		{
			this.screen.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			this.toggleFolding();
			return true;
		}
		
		if (this.folded)
			return false;
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		if (this.hidden)
			return false;
		
		int y = this.baseY  + this.layout.getLayoutY();
		return mouseX >= this.baseX + this.layout.getLayoutX()
		    && mouseY >= y
		    && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING
		    && mouseY < y + this.height;
	}
	
	private boolean isOverHeader(double mouseX, double mouseY)
	{
		if (this.hidden)
			return false;
		
		int y = this.baseY  + this.layout.getLayoutY();
		return mouseX >= this.baseX + this.layout.getLayoutX()
		    && mouseY >= y
		    && mouseX < this.screen.width - X_PADDING - RIGHT_PADDING
		    && mouseY < y + FOLDOUT_HEADER_HEIGHT;
	}
	
	// Rendering
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.hidden)
			return;
		
		INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderFoldoutHeader(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	protected void renderFoldoutHeader(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		int x = this.baseX  + this.layout.getLayoutX();
		int y = this.baseY  + this.layout.getLayoutY();
		// Draw background
		fill(matrixStack, x, y + 2, this.screen.width - X_PADDING - RIGHT_PADDING, y + FOLDOUT_HEADER_HEIGHT - 2, 0xC0_33_33_33);

		// Draw foreground arrow icon
		int arrowU = this.folded ? 16 : 32;
		int arrowV = this.isOverHeader(mouseX, mouseY) ? 16 : 0;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BETTER_ICONS);
		blit(matrixStack, x, y + 4, arrowU, arrowV, 16, 16, 256, 256);
		
		// Draw foreground text
		Font font = this.screen.getFont(); 
		font.draw(matrixStack, this.node.getDisplayName(), x + 16, y + 1 + (FOLDOUT_HEADER_HEIGHT - font.lineHeight) / 2, 0xFF_FF_FF_FF);
	}
	
	@Override
	public void renderOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.hidden)
			return;
		
		INestedGuiComponent.super.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
		if (this.isOverHeader(mouseX, mouseY))
		{
			Font font = this.screen.getFont();
			GuiUtils.drawHoveringText(matrixStack, this.extraInfo, mouseX, mouseY, this.screen.width, this.screen.height, 200, font);
		}
	}
	
	@Override
	public void invalidate()
	{
		this.content.invalidate();
	}
}