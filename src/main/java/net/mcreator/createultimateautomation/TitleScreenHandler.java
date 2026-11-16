package net.mcreator.createultimateautomation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(
    modid = "create_ultimate_automation",
    value = Dist.CLIENT
)
public class TitleScreenHandler {

    private static final ResourceLocation WRENCH_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(
            "create_ultimate_automation",
            "textures/gui/wrench.png"
        );

    // Must match the actual pixel size of textures/gui/wrench.png.
    // If you downscale the PNG (e.g. to 32x32), change this to match.
    private static final int WRENCH_TEXTURE_SIZE = 1254;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {

        if (!(event.getScreen() instanceof TitleScreen)) {
            return;
        }

        int buttonSize = 20;
        int x = event.getScreen().width - buttonSize - 10;
        int y = 10;

        event.addListener(
            new WrenchButton(x, y, buttonSize, buttonSize)
        );
    }

    private static class WrenchButton extends AbstractButton {

        public WrenchButton(int x, int y, int width, int height) {
            super(
                x,
                y,
                width,
                height,
                Component.literal("Mod Information")
            );

            setTooltip(
                Tooltip.create(
                    Component.literal("Create: Ultimate Automation")
                )
            );
        }

        @Override
        public void onPress() {
            Minecraft.getInstance().setScreen(
                new ModInfoScreen()
            );
        }

        @Override
        protected void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
        ) {
            graphics.blit(
                WRENCH_TEXTURE,
                getX(),
                getY(),
                width,
                height,
                0,
                0,
                WRENCH_TEXTURE_SIZE,
                WRENCH_TEXTURE_SIZE,
                WRENCH_TEXTURE_SIZE,
                WRENCH_TEXTURE_SIZE
            );
        }

        @Override
        protected void updateWidgetNarration(
            NarrationElementOutput narration
        ) {
            defaultButtonNarrationText(narration);
        }
    }
}