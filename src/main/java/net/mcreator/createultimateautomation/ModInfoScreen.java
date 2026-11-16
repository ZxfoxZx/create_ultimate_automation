package net.mcreator.createultimateautomation;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModInfoScreen extends Screen {

    private static final String MODID = "create_ultimate_automation";

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/gui/background/space_background.png"
        );

    private static final ResourceLocation LOGO_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/gui/mod_logo.png"
        );

    // Background image dimensions
    private static final int BACKGROUND_WIDTH = 1648;
    private static final int BACKGROUND_HEIGHT = 928;

    // Animation
    private float transitionProgress = 0.0F;
    private float animationTime = 0.0F;

    // Responsive layout variables
    private float responsiveScale = 1.0F;
    private float logoScale = 0.45F;
    private int logoY;

    // Starfield
    private static final Star[] STARS = createStars(100);

    private static final class Star {
        final float x;
        final float y;
        final float speedX;
        final float speedY;
        final float phase;
        final float frequency;
        final int size;
        final int color;

        Star(
            float x,
            float y,
            float speedX,
            float speedY,
            float phase,
            float frequency,
            int size,
            int color
        ) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.phase = phase;
            this.frequency = frequency;
            this.size = size;
            this.color = color;
        }
    }

    private static Star[] createStars(int count) {
        Random random = new Random(92741L);
        Star[] stars = new Star[count];

        int[] colors = {
            0xFFFFC477,
            0xFFFF9B68,
            0xFFD69BFF,
            0xFFFFD9A0,
            0xFFB99AFF
        };

        for (int i = 0; i < count; i++) {
            stars[i] = new Star(
                random.nextFloat(),
                random.nextFloat(),
                0.01F + random.nextFloat() * 0.07F,
                0.002F + random.nextFloat() * 0.012F,
                random.nextFloat() * 6.28F,
                0.025F + random.nextFloat() * 0.045F,
                random.nextInt(2) + 1,
                colors[random.nextInt(colors.length)]
            );
        }

        return stars;
    }

    public ModInfoScreen() {
        super(Component.literal("Create: Ultimate Automation"));
    }

    // Fade in the 2D background and advance animation
    @Override
    public void tick() {
        super.tick();

        transitionProgress = Math.min(
            1.0F,
            transitionProgress + 0.08F
        );

        animationTime += 1.0F;
    }

    @SuppressWarnings("null")
    @Override
    protected void init() {

        // Calculate proportional scaling based on window dimensions
        responsiveScale = Math.min(
            this.width / 854.0F,
            this.height / 480.0F
        );

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

    // Animated starfield
    private void renderStars(GuiGraphics graphics) {

        for (Star star : STARS) {

            // Slowly drift stars across the screen
            float xPosition =
                (star.x * this.width + animationTime * star.speedX)
                % this.width;

            float yPosition =
                (star.y * this.height + animationTime * star.speedY)
                % this.height;

            // Gentle twinkling
            float twinkle = (
                (float) Math.sin(
                    animationTime * star.frequency + star.phase
                ) + 1.0F
            ) / 2.0F;

            int alpha = (int) (65 + twinkle * 190);

            int color = (alpha << 24) | (star.color & 0x00FFFFFF);

            int x = (int) xPosition;
            int y = (int) yPosition;

            graphics.fill(
                x,
                y,
                x + star.size,
                y + star.size,
                color
            );
        }
    }

    // Occasional animated shooting stars
    private void renderShootingStars(GuiGraphics graphics) {

        renderShootingStar(
            graphics,
            animationTime,
            0.0F,
            420.0F,
            0.22F
        );

        renderShootingStar(
            graphics,
            animationTime,
            150.0F,
            750.0F,
            0.18F
        );
    }

    private void renderShootingStar(
        GuiGraphics graphics,
        float time,
        float offset,
        float interval,
        float speed
    ) {

        float cycle = (time + offset) % interval;

        // Only visible briefly during each cycle
        if (cycle > 18.0F) {
            return;
        }

        float progress = cycle / 18.0F;

        float startX = this.width * 0.15F;
        float startY = this.height * 0.20F;

        float endX = this.width * 0.85F;
        float endY = this.height * 0.75F;

        float x = startX + (endX - startX) * progress;
        float y = startY + (endY - startY) * progress;

        // Pixel-style meteor trail
        for (int i = 0; i < 7; i++) {

            int trailX = (int) (x - i * 3.0F);
            int trailY = (int) (y - i * 2.0F);

            int alpha = Math.max(
                0,
                255 - i * 35
            );

            int color = (alpha << 24) | 0x00FFB878;

            graphics.fill(
                trailX,
                trailY,
                trailX + 2,
                trailY + 2,
                color
            );
        }
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

            graphics.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.width,
                this.getY() + this.height,
                0xFF303030
            );

            graphics.renderOutline(
                this.getX(),
                this.getY(),
                this.width,
                this.height,
                0xFF707070
            );

            graphics.drawCenteredString(
                Minecraft.getInstance().font,
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                0xFF909090
            );
        }
    }

    // Disable vanilla background rendering. In 1.21.1 Screen.render() calls
    // renderBackground(), which blurs everything already drawn (the space
    // background and logo). This screen draws its own background in render().
    @Override
    public void renderBackground(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        // Intentionally empty.
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {

        // Dark background underneath the space texture
        graphics.fill(
            0,
            0,
            this.width,
            this.height,
            0xFF080611
        );

        // Fade in the new 2D background
        RenderSystem.setShaderColor(
            1.0F,
            1.0F,
            1.0F,
            transitionProgress
        );

        graphics.blit(
            BACKGROUND_TEXTURE,
            0,
            0,
            0,
            0,
            this.width,
            this.height,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT
        );

        // Reset texture color
        RenderSystem.setShaderColor(
            1.0F,
            1.0F,
            1.0F,
            1.0F
        );

        // Animated space effects
        renderStars(graphics);
        renderShootingStars(graphics);

        // Darken background slightly for menu readability
        graphics.fill(
            0,
            0,
            this.width,
            this.height,
            0x33000000
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