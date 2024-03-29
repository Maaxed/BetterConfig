package fr.max2.betterconfig.data;

import static fr.max2.betterconfig.client.gui.style.StyleRule.when;
import static fr.max2.betterconfig.client.gui.layout.ComponentLayoutConfig.*;
import static fr.max2.betterconfig.client.gui.layout.CompositeLayoutConfig.*;
import static fr.max2.betterconfig.client.gui.component.BCComponent.*;

import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.betterconfig.client.gui.better.Foldout;
import fr.max2.betterconfig.client.gui.better.IBetterElement;
import fr.max2.betterconfig.client.gui.better.ListGroup;
import fr.max2.betterconfig.client.gui.component.widget.WidgetComponent;
import fr.max2.betterconfig.client.gui.layout.Alignment;
import fr.max2.betterconfig.client.gui.layout.Axis;
import fr.max2.betterconfig.client.gui.layout.Padding;
import fr.max2.betterconfig.client.gui.layout.Size;
import fr.max2.betterconfig.client.gui.layout.Visibility;
import fr.max2.betterconfig.client.gui.rendering.DrawBox;
import fr.max2.betterconfig.client.gui.rendering.IRenderLayer;
import fr.max2.betterconfig.client.gui.rendering.NineSliceMaterial;
import fr.max2.betterconfig.client.gui.rendering.NoRendering;
import fr.max2.betterconfig.client.gui.style.StyleRule;
import fr.max2.betterconfig.client.gui.style.StyleSerializer;
import fr.max2.betterconfig.client.gui.style.StyleSheet;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ModStyleSheetProvider implements DataProvider
{
	public static Gson GSON = StyleSerializer.INSTANCE.registerSerializers(new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final PackOutput output;

	public ModStyleSheetProvider(PackOutput output)
	{
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput pOutput)
	{
		return this.extracted(pOutput, DefaultStyleSheet.builder(), StyleSheet.DEFAULT_STYLESHEET);
	}

	private CompletableFuture<?> extracted(CachedOutput pOutput, StyleSheet.Builder styleSheet, ResourceLocation styleSheetId)
	{
		return DataProvider.saveStable(pOutput, GSON.toJsonTree(styleSheet), this.output.getOutputFolder().resolve(PackType.CLIENT_RESOURCES.getDirectory() + "/" + styleSheetId.getNamespace() + "/" + StyleSheet.STYLESHEET_DIR + "/" + styleSheetId.getPath() + ".json"));
	}

	@Override
	public String getName()
	{
		return "StyleSheets: BetterConfig";
	}

	private static final class DefaultStyleSheet
	{
		/** The left and right padding around the screen */
		private static final int X_PADDING = 10;
		/** The top and bottom padding around the screen */
		private static final int Y_PADDING = 10;
		/** The height of the value widget */
		private static final int VALUE_HEIGHT = 20;
		/** The width of the value widget */
		private static final int VALUE_WIDTH = 150;
		/** The height of the value entries */
		private static final int VALUE_CONTAINER_HEIGHT = 24;
		/** The width of the indentation added for each nested section */
		private static final int SECTION_TAB_SIZE = 22;
		/** The x position of the input field of the search bar */
		private static final int SEARCH_LABEL_WIDTH = 80;
		/** The default width of the '+' and '-' buttons */
		private static final int NUMBER_FIELD_BUTTON_SIZE = 20;
		/** The height of the foldout header */
		private static final int FOLDOUT_HEADER_HEIGHT = 24;

		private static final StyleRule BETTER_NUMBER_FIELD_STYLE = when().hasClass("better:number_field").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule NUMBER_FIELD_STYLE = when().type("number_field").then()
				.set(SPACING, 2)
				.set(DIR, Axis.HORIZONTAL)
				.build();
		private static final StyleRule NUMBER_FIELD_MINUS_STYLE = when().hasClass("number_field:minus_button").then()
				.set(SIZE_OVERRIDE, new Size(NUMBER_FIELD_BUTTON_SIZE, Size.UNCONSTRAINED))
				.build();
		private static final StyleRule NUMBER_FIELD_PLUS_STYLE = when().hasClass("number_field:plus_button").then()
				.set(SIZE_OVERRIDE, new Size(NUMBER_FIELD_BUTTON_SIZE, Size.UNCONSTRAINED))
				.build();

		private static final StyleRule FILTERED_OUT_STYLE = when().is(IBetterElement.FILTERED_OUT).then()
				.set(VISIBILITY, Visibility.COLLAPSED)
				.build();

		private static final StyleRule FOLDOUT_STYLE = when().type("better:foldout").then()
				.set(DIR, Axis.VERTICAL)
				.build();
		private static final StyleRule FOLDED_STYLE = when().parent().is(Foldout.FOLDED).then()
				.set(VISIBILITY, Visibility.COLLAPSED)
				.build();
		private static final StyleRule FOLDOUT_HEADER_STYLE = when().type("better:foldout_header").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, FOLDOUT_HEADER_HEIGHT))
				.set(VISIBILITY, Visibility.VISIBLE)
				.build();

		private static final StyleRule ROOT_GROUP_STYLE = when().hasClass("better:root_group").then()
				.set(OUTER_PADDING, new Padding(6, 6 + 6, 6, 6))
				.build();
		private static final StyleRule TABLE_STYLE = when().hasClass("better:table_group").then()
				.set(OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
				.build();
		private static final StyleRule LIST_STYLE = when().hasClass("better:list_group").then()
				.set(OUTER_PADDING, new Padding(0, 0, 0, SECTION_TAB_SIZE))
				.build();

		private static final StyleRule LIST_ADD_LAST_STYLE = when()
			.and()
				.parent()
					.and()
						.hasClass("better:list_group")
						.is(ListGroup.EMPTY)
						.end()
				.hasClass("better:list_add_last")
				.end()
			.then()
				.set(VISIBILITY, Visibility.COLLAPSED)
				.build();
		private static final StyleRule LIST_ENTRY_STYLE = when().type("better:list_entry").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(OUTER_PADDING, new Padding(0, 0, 0, -VALUE_HEIGHT))
				.build();
		private static final StyleRule LIST_ENTRY_REMOVE_STYLE = when().hasClass("better:list_remove").then()
				.set(OUTER_PADDING, new Padding((VALUE_CONTAINER_HEIGHT - VALUE_HEIGHT) / 2, 0, 0, 0))
				.build();
		private static final StyleRule LIST_ENTRY_REMOVE_HIDDEN_STYLE = when()
			.and()
				.hasClass("better:list_remove")
				.not()
					.parent()
						.or()
							.is(HOVERED)
							.is(FOCUSED)
							.end()
				.end()
			.then()
				.set(VISIBILITY, Visibility.HIDDEN)
				.build();

		private static final StyleRule ROOT_STYLE = when().type("better:root").then()
				.set(DIR, Axis.VERTICAL)
				.set(SPACING, Y_PADDING)
				.set(INNER_PADDING, new Padding(Y_PADDING, X_PADDING, Y_PADDING, X_PADDING))
				.build();
		private static final StyleRule SEARCH_BAR_STYLE = when().hasClass("better:search_field").then()
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, 18))
				.build();
		private static final StyleRule SEARCH_LABEL_STYLE = when().hasClass("better:search_label").then()
				.set(SIZE_OVERRIDE, new Size(SEARCH_LABEL_WIDTH, 18))
				.build();
		private static final StyleRule TAB_BAR_STYLE = when().hasClass("better:tab_bar").then()
				.set(DIR, Axis.HORIZONTAL)
				.build();
		private static final StyleRule BOTTOM_BAR_STYLE = when().hasClass("better:bottom_bar").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(SPACING, X_PADDING)
				.build();

		private static final StyleRule VALUE_ENTRY_STYLE = when().type("better:value_entry").then()
				.set(DIR, Axis.HORIZONTAL)
				.set(JUSTIFICATION, Alignment.CENTER)
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, VALUE_CONTAINER_HEIGHT))
				.build();

		private static final StyleRule ENTRY_UNDO_HIDDEN_STYLE = when()
			.and()
				.hasClass("better:undo")
				.not()
					.parent()
						.or()
							.is(HOVERED)
							.is(FOCUSED)
							.parent()
								.and()
									.type("better:list_entry")
									.or()
										.is(HOVERED)
										.is(FOCUSED)
										.end()
									.end()
							.end()
				.end()
			.then()
				.set(VISIBILITY, Visibility.HIDDEN)
				.build();

		private static final StyleRule OPTION_BUTTON_STYLE = when().hasClass("better:option_button").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule STRING_INPUT_FIELD_STYLE = when().hasClass("better:string_input").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule UNKNOWN_OPTION_STYLE = when().hasClass("better:unknown").then()
				.set(SIZE_OVERRIDE, new Size(VALUE_WIDTH, VALUE_HEIGHT))
				.build();

		private static final StyleRule HBOX_STYLE = when().type("hbox").then()
				.set(DIR, Axis.HORIZONTAL)
				.build();

		private static final StyleRule BETTER_BUTTON_STYLE = when().hasClass("better:button").then()
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, VALUE_HEIGHT))
				.build();
		private static final StyleRule BETTER_ICON_BUTTON_STYLE = when().hasClass("better:icon_button").then()
				.<IRenderLayer>assign(new NoRendering()).atIndex(0).into(BACKGROUND)
				.set(SIZE_OVERRIDE, new Size(VALUE_HEIGHT, VALUE_HEIGHT))
				.build();

		private static final StyleRule ACTIVE_BUTTON_STYLE = when()
			.and()
				.type("button")
				.not().is(WidgetComponent.ACTIVE)
				.end()
			.then()
				.set(TEXT_COLOR, 0xFF_A0_A0_A0)
				.<IRenderLayer>assign(new DrawBox(new Padding(), new NineSliceMaterial(AbstractWidget.WIDGETS_LOCATION, 0, 46, 200, 20, 2, 3, 2, 2))).atIndex(0).into(BACKGROUND)
				.build();
		private static final StyleRule FOCUSED_BUTTON_STYLE = when()
			.and()
				.type("button")
				.or()
					.is(HOVERED)
					.is(FOCUSED)
					.end()
				.end()
			.then()
				.<IRenderLayer>assign(new DrawBox(new Padding(), new NineSliceMaterial(AbstractWidget.WIDGETS_LOCATION, 0, 86, 200, 20, 2, 3, 2, 2))).atIndex(0).into(BACKGROUND)
				.build();
		private static final StyleRule BUTTON_STYLE = when().type("button").then()
				.set(SIZE_OVERRIDE, new Size(Size.UNCONSTRAINED, 20))
				.<IRenderLayer>assign(new DrawBox(new Padding(), new NineSliceMaterial(AbstractWidget.WIDGETS_LOCATION, 0, 66, 200, 20, 2, 3, 2, 2))).atIndex(0).into(BACKGROUND)
				.build();

		public static StyleSheet.Builder builder()
		{
			return new StyleSheet.Builder().add(
					FILTERED_OUT_STYLE, ROOT_STYLE, SEARCH_BAR_STYLE, SEARCH_LABEL_STYLE, TAB_BAR_STYLE, BOTTOM_BAR_STYLE, FOLDOUT_STYLE, FOLDOUT_HEADER_STYLE, FOLDED_STYLE,
					LIST_ADD_LAST_STYLE, LIST_ENTRY_STYLE, LIST_ENTRY_REMOVE_HIDDEN_STYLE, LIST_ENTRY_REMOVE_STYLE,
					VALUE_ENTRY_STYLE, ENTRY_UNDO_HIDDEN_STYLE,
					OPTION_BUTTON_STYLE, STRING_INPUT_FIELD_STYLE, UNKNOWN_OPTION_STYLE, ROOT_GROUP_STYLE, TABLE_STYLE, LIST_STYLE,
					BETTER_NUMBER_FIELD_STYLE, NUMBER_FIELD_STYLE, NUMBER_FIELD_PLUS_STYLE, NUMBER_FIELD_MINUS_STYLE,
					BETTER_ICON_BUTTON_STYLE, BETTER_BUTTON_STYLE, HBOX_STYLE, ACTIVE_BUTTON_STYLE, FOCUSED_BUTTON_STYLE, BUTTON_STYLE);
		}
	}
}
