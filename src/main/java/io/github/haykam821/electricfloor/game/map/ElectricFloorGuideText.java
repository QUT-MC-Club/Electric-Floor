package io.github.haykam821.electricfloor.game.map;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ElectricFloorGuideText {	
	private static final Formatting FORMATTING = Formatting.GOLD;

	private static final Text TEXT = Text.empty()
		.append(Text.translatable("gameType.electricfloor.electric_floor").formatted(Formatting.BOLD))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.electricfloor.guide.keep_moving"))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.electricfloor.guide.red_eliminates"))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.electricfloor.guide.last_player_standing"))
		.formatted(FORMATTING);

	private ElectricFloorGuideText() {
		return;
	}

	public static ElementHolder createElementHolder(boolean night) {
		TextDisplayElement element = new TextDisplayElement(TEXT);

		element.setBillboardMode(BillboardMode.CENTER);
		element.setLineWidth(350);
		element.setInvisible(true);

		if (night) {
			element.setBrightness(Brightness.FULL);
		}

		ElementHolder holder = new ElementHolder();
		holder.addElement(element);

		return holder;
	}
}
