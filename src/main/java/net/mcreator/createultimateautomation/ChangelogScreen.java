
package net.mcreator.createultimateautomation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.util.List;

public class ChangelogScreen extends Screen {

    private final ModInfoScreen parentScreen;

    private int scrollOffset = 0;

    private static final int LINE_HEIGHT = 12;
    private static final int ENTRY_GAP = 5;

    private static final List<ChangelogLine> CHANGELOG = loadChangelog();

    private static List<ChangelogLine> loadChangelog() {
        List<ChangelogLine> lines = new java.util.ArrayList<>();

        try (var stream = ChangelogScreen.class.getResourceAsStream("/changelog.json")) {

            if (stream == null) {
                System.err.println("Could not find changelog.json!");
                return lines;
            }

            JsonObject root = JsonParser.parseReader(
                new InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8)
            ).getAsJsonObject();

            JsonArray versions = root.getAsJsonArray("versions");

            for (var versionElement : versions) {
                JsonObject version = versionElement.getAsJsonObject();

                lines.add(new ChangelogLine(
                    "Version " + version.get("version").getAsString(),
                    0xFFFFFF,
                    true
                ));

                lines.add(new ChangelogLine(
                    version.get("date").getAsString(),
                    0xAAAAAA,
                    false
                ));

                addCategory(lines, version, "added", "ADDED", 0x55FF55);
                addCategory(lines, version, "changed", "CHANGED", 0x55FFFF);
                addCategory(lines, version, "fixed", "FIXED", 0xFFAA00);
                addCategory(lines, version, "removed", "REMOVED", 0xFF5555);
            }

        } catch (Exception e) {
            System.err.println("Failed to load changelog.json!");
            e.printStackTrace();
        }

        return lines;
    }

    private static void addCategory(
        List<ChangelogLine> lines,
        JsonObject version,
        String key,
        String heading,
        int color
    ) {
        JsonArray entries = version.getAsJsonArray(key);

        if (entries == null || entries.isEmpty()) {
            return;
        }

        lines.add(new ChangelogLine(heading, color, true));

        for (var entry : entries) {
            lines.add(new ChangelogLine(
                entry.getAsString(),
                0xFFFFFF,
                false
            ));
        }
    }

    public ChangelogScreen(ModInfoScreen parentScreen) {
        super(Component.literal("Create: Ultimate Automation"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        scrollOffset = 0;

        // Play notification sound when the changelog opens
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F)
            );
        }
    }

    @Override
    public void render(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {

        renderBackground(guiGraphics);

        int panelLeft = 20;
        int panelRight = this.width - 20;
        int panelTop = 20;
        int panelBottom = this.height - 20;

        // Main panel
        guiGraphics.fill(
            panelLeft, panelTop,
            panelRight, panelBottom,
            0xDD202020
        );

        // Panel border
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 2, 0xFFFFFFFF);
        guiGraphics.fill(panelLeft, panelBottom - 2, panelRight, panelBottom, 0xFFFFFFFF);
        guiGraphics.fill(panelLeft, panelTop, panelLeft + 2, panelBottom, 0xFFFFFFFF);
        guiGraphics.fill(panelRight - 2, panelTop, panelRight, panelBottom, 0xFFFFFFFF);

        // Screen title
        guiGraphics.drawCenteredString(
            this.font,
            "Create: Ultimate Automation",
            this.width / 2,
            32,
            0xFFFFFF
        );

        guiGraphics.drawCenteredString(
            this.font,
            "CHANGELOG",
            this.width / 2,
            48,
            0xAAAAAA
        );

        // Changelog viewing area
        int contentTop = 75;
        int contentBottom = this.height - 55;
        int contentLeft = panelLeft + 20;
        int contentRight = panelRight - 20;

        int contentWidth = contentRight - contentLeft;

        // Calculate total content height including wrapped lines
        int contentHeight = 0;

        for (ChangelogLine line : CHANGELOG) {
            List<FormattedCharSequence> wrapped =
                this.font.split(Component.literal(line.text()), contentWidth);

            contentHeight += Math.max(1, wrapped.size()) * LINE_HEIGHT;
            contentHeight += ENTRY_GAP;
        }

        int maxScroll = Math.max(0, contentHeight - (contentBottom - contentTop));

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        guiGraphics.enableScissor(
            contentLeft,
            contentTop,
            contentRight,
            contentBottom
        );

        int y = contentTop - scrollOffset;

        for (ChangelogLine line : CHANGELOG) {

            List<FormattedCharSequence> wrapped =
                this.font.split(Component.literal(line.text()), contentWidth);

            for (FormattedCharSequence wrappedLine : wrapped) {
                guiGraphics.drawString(
                    this.font,
                    wrappedLine,
                    contentLeft,
                    y,
                    line.color()
                );

                y += LINE_HEIGHT;
            }

            y += ENTRY_GAP;
        }

        guiGraphics.disableScissor();

        // Footer
        guiGraphics.drawCenteredString(
            this.font,
            "Scroll to view older updates",
            this.width / 2,
            this.height - 43,
            0xAAAAAA
        );

        guiGraphics.drawCenteredString(
            this.font,
            "Press ESC to return to Create: Ultimate Automation",
            this.width / 2,
            this.height - 30,
            0xFFFFFF
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        scrollOffset -= (int) (delta * LINE_HEIGHT * 3);

        int contentTop = 75;
        int contentBottom = this.height - 55;
        int contentWidth = this.width - 80;

        int contentHeight = 0;

        for (ChangelogLine line : CHANGELOG) {
            List<FormattedCharSequence> wrapped =
                this.font.split(Component.literal(line.text()), contentWidth);

            contentHeight += Math.max(1, wrapped.size()) * LINE_HEIGHT;
            contentHeight += ENTRY_GAP;
        }

        int maxScroll = Math.max(0, contentHeight - (contentBottom - contentTop));

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        return true;
    }

    // Return to ModInfoScreen when ESC is pressed
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(parentScreen);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Return to ModInfoScreen when the screen is closed
    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record ChangelogLine(String text, int color, boolean heading) {
    }
}
