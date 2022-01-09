package codechicken.nei.recipe;

import codechicken.nei.api.INEIGuiAdapter;
import codechicken.nei.drawable.DrawableNineSliceTexture;
import codechicken.nei.drawable.TextureInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Rectangle;

public class GuiRecipeCatalyst extends INEIGuiAdapter {
    private final DrawableNineSliceTexture catalystTabImage = new DrawableNineSliceTexture(getTextureInfo("catalyst_tab.png", 28, 28).slice(6, 6, 6, 6));
    private final DrawableNineSliceTexture slotImage = new DrawableNineSliceTexture(getTextureInfo("slot.png", 18, 18).slice(1, 1, 1, 1));

    private GuiRecipe guiRecipe;
    public static final int ingredientSize = 16;
    public static final int ingredientBorder = 1;
    public static final int tabBorder = 5;
    public static final int fullBorder = ingredientBorder + tabBorder;

    private static final Rectangle catalystRect = new Rectangle();
    private static final Rectangle targetRect = new Rectangle();

    public GuiRecipeCatalyst(GuiRecipe guiRecipe) {
        this.guiRecipe = guiRecipe;
    }

    public void draw() {
        if (guiRecipe == null) return;
        int catalystsSize = RecipeCatalysts.getRecipeCatalysts(guiRecipe.getHandler().getClass()).size();
        if (catalystsSize == 0) return;

        int availableHeight = RecipeCatalysts.getHeight();
        int columnCount = RecipeCatalysts.getColumnCount(availableHeight, catalystsSize);
        int rowCount = RecipeCatalysts.getRowCount(availableHeight, catalystsSize);
        int width, height, xOffset, yOffset;

        width = (ingredientBorder * 2) + (tabBorder * 2) + (columnCount * ingredientSize);
        height = (ingredientBorder * 2) + (tabBorder * 2) + (rowCount * ingredientSize);
        xOffset = guiRecipe.guiLeft - width + tabBorder;
        yOffset = guiRecipe.guiTop;
        catalystTabImage.draw(xOffset, yOffset, width, height);

        width = (ingredientBorder * 2) + (columnCount * ingredientSize);
        height = (ingredientBorder * 2) + (rowCount * ingredientSize);
        xOffset = guiRecipe.guiLeft - width + ingredientBorder;
        yOffset = guiRecipe.guiTop + fullBorder - ingredientBorder;
        slotImage.draw(xOffset, yOffset, width, height);
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        if (!(gui instanceof GuiRecipe)) return false;
        guiRecipe = (GuiRecipe) gui;
        int catalystsSize = RecipeCatalysts.getRecipeCatalysts(guiRecipe.getHandler().getClass()).size();
        if (catalystsSize == 0) return false;

        int availableHeight = RecipeCatalysts.getHeight();
        int columnCount = RecipeCatalysts.getColumnCount(availableHeight, catalystsSize);
        int rowCount = RecipeCatalysts.getRowCount(availableHeight, catalystsSize);
        int margin = 4;

        int width = (ingredientBorder * 2) + (tabBorder * 2) + (columnCount * ingredientSize) + margin;
        int height = (ingredientBorder * 2) + (tabBorder * 2) + (rowCount * ingredientSize) + margin;
        int xOffset = guiRecipe.guiLeft - width + tabBorder;
        int yOffset = guiRecipe.guiTop;
        catalystRect.setBounds(xOffset, yOffset, width, height);
        targetRect.setBounds(x, y, w, h);
        return targetRect.intersects(catalystRect);
    }

    private TextureInfo getTextureInfo(String name, int width, int height) {
        ResourceLocation location = new ResourceLocation("nei:textures/" + name);
        return new TextureInfo(location, width, height);
    }
}
