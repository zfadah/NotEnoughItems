package codechicken.nei.history;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.*;
import codechicken.nei.config.ConfigSet;
import codechicken.nei.guihook.GuiContainerManager;
import java.util.ArrayList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class HistoryPanel extends PanelWidget {

    public static ConfigSet world;

    private int size;

    public ArrayList<ItemStack> realItems = new ArrayList<>();

    public ItemStack getItem(int idx) {
        return realItems.get(idx);
    }

    protected static class HistoryPanelGrid extends ItemsGrid {
        public ArrayList<ItemStack> newItems;

        public void setItems(ArrayList<ItemStack> items) {
            realItems = items;
            onGridChanged();
        }

        @Override
        public int getNumPages() {
            return 1;
        }

        protected void clear() {
            newItems.clear();
        }
    }

    public HistoryPanel() {
        grid = new HistoryPanelGrid();
        ((HistoryPanelGrid) grid).newItems = new ArrayList<>();
    }

    @Override
    public void resize(GuiContainer gui) {
        final Rectangle4i margin =
                new Rectangle4i(getMarginLeft(gui), getMarginTop(gui), getWidth(gui), getHeight(gui));
        x = margin.x;
        y = margin.y;
        w = margin.w;
        h = margin.h;
        size = w / ItemsGrid.SLOT_SIZE * h / ItemsGrid.SLOT_SIZE;
        grid.setGridSize(x, y, w, h);
        grid.refresh(gui);
    }

    @Override
    public void draw(int mousex, int mousey) {
        GuiContainer gui = NEIClientUtils.getGuiContainer();
        GuiContainerManager.enableMatrixStackLogging();
        GuiDraw.drawRect(getMarginLeft(gui), getMarginTop(gui), getWidth(gui), getHeight(gui), 1157627903);
        GuiContainerManager.disableMatrixStackLogging();
        super.draw(mousex, mousey);
    }

    @Override
    public void setVisible() {}

    @Override
    public String getLabelText() {
        return String.format("(%d/%d)", getPage(), Math.max(1, getNumPages()));
    }

    @Override
    protected String getPositioningSettingName() {
        return "world.panels.history";
    }

    @Override
    public void init() {}

    @Override
    public int getMarginLeft(GuiContainer gui) { // X
        return ItemPanels.itemPanel.getMarginLeft(gui);
    }

    @Override
    public int getMarginTop(GuiContainer gui) { // Y
        return ItemPanels.itemPanel.getMarginTop(gui) + ItemPanels.itemPanel.getHeight(gui) + PADDING;
    }

    @Override
    public int getWidth(GuiContainer gui) { // W
        return ItemPanels.itemPanel.getWidth(gui);
    }

    @Override
    public int getHeight(GuiContainer gui) { // H
        return ItemsGrid.SLOT_SIZE * 3;
    }

    @Override
    protected int resizeFooter(GuiContainer gui) {
        return 0;
    }

    @Override
    protected int resizeHeader(GuiContainer gui) {
        return 0;
    }

    @Override
    protected ItemStack getDraggedStackWithQuantity(int mouseDownSlot) {
        return null;
    }

    public void addHistorys(ItemStack stack) {
        ArrayList<ItemStack> items = grid.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getDisplayName().equals(stack.getDisplayName())) {
                items.remove(i);
                break;
            }
        }
        if (items.size() == size) {
            items.remove(size - 1);
        }
        items.add(0, stack);
        ((HistoryPanelGrid) ItemPanels.historyPanel.getGrid()).setItems(items);
    }
}
