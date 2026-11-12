
package net.mcreator.createultimateautomation;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModInfoScreen extends Screen {

    private static final ResourceLocation PANORAMA_TEXTURE =
        new ResourceLocation(
            "create_ultimate_automation",
            "textures/gui/background/panorama"
        );

    private static final ResourceLocation LOGO_TEXTURE =
        new ResourceLocation(
            "create_ultimate_automation",
            "textures/gui/mod_logo.png"
        );

    private final PanoramaRenderer panorama;
    private final PanoramaRenderer vanillaPanorama;

    private float transitionProgress = 0.0F;

    // Responsive layout variables
    private float responsiveScale = 1.0F;
    private float logoScale = 0.45F;
    private int logoY;

    public ModInfoScreen() {
        super(Component.literal("Create: Ultimate Automation"));

        // Custom mod panorama
        this.panorama = new PanoramaRenderer(
            new CubeMap(PANORAMA_TEXTURE)
        );

        // Minecraft vanilla panorama
        this.vanillaPanorama = new PanoramaRenderer(
            new CubeMap(
                new ResourceLocation(
                    "minecraft",
                    "textures/gui/title/background/panorama"
                )
            )
        );
    }

    // Fade from vanilla panorama into the custom panorama
    @Override
    public void tick() {
        super.tick();

        transitionProgress = Math.min(
            1.0F,
            transitionProgress + 0.08F
        );
    }

    @Override
    protected void init() {

        // Calculate proportional scaling based on window dimensions
        responsiveScale = Math.min(
            this.width / 854.0F,
            this.height / 480.0F
        );

        // Prevent scaling from becoming excessively small or large
        responsiveScale = Math.max(
            0.5F,
            Math.min(responsiveScale, 1.25F)
        );

        int centerX = this.width / 2;

        // Responsive logo size
        logoScale = 0.45F * responsiveScale;

        int logoHeight = (int) (294 * logoScale);

        // Responsive button dimensions
        int buttonWidth = Math.max(
            160,
            (int) (200 * responsiveScale)
        );

        // Prevent buttons from exceeding the screen width
        buttonWidth = Math.min(
            buttonWidth,
            Math.max(100, this.width - 20)
        );

        int buttonHeight = Math.max(
            20,
            (int) (20 * responsiveScale)
        );

        int buttonGap = Math.max(
            4,
            (int) (5 * responsiveScale)
        );

        int logoGap = Math.max(
            6,
            (int) (12 * responsiveScale)
        );

        int returnGap = Math.max(
            3,
            (int) (5 * responsiveScale)
        );

        // Calculate total menu height
        int buttonSpacing = buttonHeight + buttonGap;

        int buttonsHeight =
            (7 * buttonHeight) +
            (6 * buttonGap) +
            returnGap;

        int totalHeight =
            logoHeight +
            logoGap +
            buttonsHeight;

        // Center the entire menu vertically
        int layoutTop = Math.max(
            0,
            (this.height - totalHeight) / 2
        );

        // Logo position
        logoY = layoutTop;

        // Button positioning
        int buttonX = centerX - buttonWidth / 2;

        int startY = logoY + logoHeight + logoGap;

        // Changelogs button
        this.addRenderableWidget(
            Button.builder(
                Component.literal("Changelogs"),
                button -> this.minecraft.setScreen(
                    new ChangelogScreen(this)
                )
            )
            .bounds(
                buttonX,
                startY,
                buttonWidth,
                buttonHeight
            )
            .build()
        );

        // Discord button
        this.addRenderableWidget(
            Button.builder(
                Component.literal("Discord"),
                button -> this.minecraft.setScreen(
                    new ConfirmLinkScreen(
                        confirmed -> {
                            if (confirmed) {
                                Util.getPlatform().openUri(
                                    "https://discord.gg/qVstsYBZ2C"
                                );
                            }

                            this.minecraft.setScreen(this);
                        },
                        "https://discord.gg/qVstsYBZ2C",
                        false
                    )
                )
            )
            .bounds(
                buttonX,
                startY + buttonSpacing,
                buttonWidth,
                buttonHeight
            )
            .build()
        );

        // CurseForge button
        this.addRenderableWidget(
            Button.builder(
                Component.literal("CurseForge"),
                button -> this.minecraft.setScreen(
                    new ConfirmLinkScreen(
                        confirmed -> {
                            if (confirmed) {
                                Util.getPlatform().openUri(
                                    "https://www.curseforge.com/minecraft/mc-mods/create-ultimate-automation"
                                );
                            }

                            this.minecraft.setScreen(this);
                        },
                        "https://www.curseforge.com/minecraft/mc-mods/create-ultimate-automation",
                        false
                    )
                )
            )
            .bounds(
                buttonX,
                startY + buttonSpacing * 2,
                buttonWidth,
                buttonHeight
            )
            .build()
        );

        // Modrinth button (disabled)
        this.addRenderableWidget(
            new DisabledMenuButton(
                buttonX,
                startY + buttonSpacing * 3,
                buttonWidth,
                buttonHeight,
                "Modrinth"
            )
        );

        // Report Issues button (disabled)
        this.addRenderableWidget(
            new DisabledMenuButton(
                buttonX,
                startY + buttonSpacing * 4,
                buttonWidth,
                buttonHeight,
                "Report Issues"
            )
        );

        // Support Us button (disabled)
        this.addRenderableWidget(
            new DisabledMenuButton(
                buttonX,
                startY + buttonSpacing * 5,
                buttonWidth,
                buttonHeight,
                "Support Us"
            )
        );

        // Return to Menu button
        this.addRenderableWidget(
            Button.builder(
                Component.literal("Return to Menu"),
                button -> this.minecraft.setScreen(
                    new TitleScreen()
                )
            )
            .bounds(
                buttonX,
                startY + buttonSpacing * 6 + returnGap,
                buttonWidth,
                buttonHeight
            )
            .build()
        );
    }

    // Custom grey disabled button
    private static class DisabledMenuButton extends Button {

        public DisabledMenuButton(
            int x,
            int y,
            int width,
            int height,
            String text
        ) {
            super(
                x,
                y,
                width,
                height,
                Component.literal(text),
                button -> {},
                Button.DEFAULT_NARRATION
            );

            this.active = false;

            this.setTooltip(
                Tooltip.create(
                    Component.literal("In the next update")
                )
            );
        }

        @Override
        public void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
        ) {
            // Dark grey button background
            graphics.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.width,
                this.getY() + this.height,
                0xFF303030
            );

            // Grey button border
            graphics.renderOutline(
                this.getX(),
                this.getY(),
                this.width,
                this.height,
                0xFF707070
            );

            // Muted grey button text
            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                0xFF909090
            );
        }
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        // Render Minecraft's original panorama underneath
        this.vanillaPanorama.render(partialTick, 1.0F);

        // Fade in the custom panorama
        this.panorama.render(partialTick, transitionProgress);

        // Darken background
        graphics.fill(
            0,
            0,
            this.width,
            this.height,
            0x66000000
        );

        // Render proportionally scaled mod logo
        graphics.pose().pushPose();

        graphics.pose().translate(
            this.width / 2.0,
            logoY,
            0
        );

        graphics.pose().scale(
            logoScale,
            logoScale,
            1.0F
        );

        graphics.blit(
            LOGO_TEXTURE,
            -147,
            0,
            0,
            0,
            294,
            294,
            294,
            294
        );

        graphics.pose().popPose();

        // Render buttons
        super.render(
            graphics,
            mouseX,
            mouseY,
            partialTick
        );
    }
}
