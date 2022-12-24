package fr.max2.betterconfig.client.gui.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import fr.max2.betterconfig.client.gui.layout.Rectangle;
import net.minecraft.client.renderer.GameRenderer;

public class ColorMaterial implements IMaterial
{
	private final int[] topRightColor, topLeftColor, botRightColor, botLeftColor;

	public ColorMaterial(int topRightColor, int topLeftColor, int botRightColor, int botLeftColor)
	{
		this.topRightColor = splitRGBA(topRightColor);
		this.topLeftColor = splitRGBA(topLeftColor);
		this.botRightColor = splitRGBA(botRightColor);
		this.botLeftColor = splitRGBA(botLeftColor);
	}

	@Override
	public void renderMaterial(Rectangle rect, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		vertex(bufferbuilder, matrix, rect.getLeft(), rect.getBottom(), this.botLeftColor);
		vertex(bufferbuilder, matrix, rect.getRight(), rect.getBottom(), this.botRightColor);
		vertex(bufferbuilder, matrix, rect.getRight(), rect.getTop(), this.topRightColor);
		vertex(bufferbuilder, matrix, rect.getLeft(), rect.getTop(), this.topLeftColor);
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	@Override
	public String typeName()
	{
		return "color";
	}

	private static void vertex(BufferBuilder bufferbuilder, Matrix4f matrix, float x, float y, int[] color)
	{
		bufferbuilder.vertex(matrix, x, y, 0.0F).color(color[0], color[1], color[2], color[3]).endVertex();
	}

	private static int[] splitRGBA(int color)
	{
		return new int[]
		{
			color >> 16 & 255,
			color >> 8 & 255,
			color & 255,
			color >> 24 & 255
		};
	}
}