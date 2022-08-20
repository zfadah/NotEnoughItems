package codechicken.nei.recipe;

import codechicken.nei.ItemPanels;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class RecipeItemInputHandler implements IContainerInputHandler {
    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
        ItemStack stackover = GuiContainerManager.getStackMouseOver(gui);
        if (stackover == null) return false;

        if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
            return GuiCraftingRecipe.openRecipeGui("item", stackover.copy());

        if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")) {
            ItemStack copy = stackover.copy();
            copy.stackSize = 1;
            ItemPanels.historyPanel.addHistorys(copy);
            return GuiUsageRecipe.openRecipeGui("item", stackover.copy());
        }
        if (keyCode == NEIClientConfig.getKeyBinding("gui.bookmark")) {
            NEIClientConfig.logger.debug("Adding or removing {} from bookmarks", stackover.getDisplayName());
            List<PositionedStack> ingredients = null;
            String handlerName = "";

            if (gui instanceof GuiRecipe && NEIClientConfig.saveCurrentRecipeInBookmarksEnabled()) {
                ingredients = ((GuiRecipe<?>) gui).getFocusedRecipeIngredients();
                handlerName = ((GuiRecipe<?>) gui).getHandlerName();
            }

            ItemPanels.bookmarkPanel.addOrRemoveItem(stackover.copy(), handlerName, ingredients);
        }

        if (keyCode == NEIClientConfig.getKeyBinding("gui.overlay")) {
            if (NEIClientUtils.controlKey()) {
                LayoutManager.overlayRenderer = null;
                return true;
            }
            if (!NEIClientConfig.saveCurrentRecipeInBookmarksEnabled()) return false;
            final BookmarkRecipeId mouseOverRecipeId = ItemPanels.bookmarkPanel.getBookmarkMouseOverRecipeId();
            if (mouseOverRecipeId == null
                    || mouseOverRecipeId.ingredients == null
                    || mouseOverRecipeId.ingredients.isEmpty()) return false;
            return GuiCraftingRecipe.openRecipeGui("item", true, stackover.copy());
        }

        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        ItemStack stackover = GuiContainerManager.getStackMouseOver(gui);

        if (stackover == null) return false;

        if (!(gui instanceof GuiRecipe)) return false;

        // disabled open recipe gui if hold shift (player have move recipe)
        if (button == 0
                && ItemPanels.bookmarkPanel.getStackMouseOver(mousex, mousey) != null
                && NEIClientUtils.shiftKey()) {
            return false;
        }

        if (button == 0) return GuiCraftingRecipe.openRecipeGui("item", stackover.copy());

        if (button == 1) return GuiUsageRecipe.openRecipeGui("item", stackover.copy());

        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}

    @Override
    public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyID) {
        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}

    @Override
    public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}
}
