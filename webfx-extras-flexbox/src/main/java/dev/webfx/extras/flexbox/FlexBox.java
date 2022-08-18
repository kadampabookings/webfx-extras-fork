package dev.webfx.extras.flexbox;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import dev.webfx.platform.util.collection.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public final class FlexBox extends Pane {
    private static final String ORDER_CONSTRAINT = "flexbox-order";
    private static final String GROW_CONSTRAINT = "flexbox-grow";
    private static final String MARGIN_CONSTRAINT = "flexbox-margin";
    private final DoubleProperty horizontalSpace = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };
    private final DoubleProperty verticalSpace = new SimpleDoubleProperty(0)  {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    private final BooleanProperty spaceTop = new SimpleBooleanProperty() {
        protected void invalidated() {
            requestLayout();
        }
    };
    private final BooleanProperty spaceLeft = new SimpleBooleanProperty() {
        protected void invalidated() {
            requestLayout();
        }
    };
    private final BooleanProperty spaceRight = new SimpleBooleanProperty() {
        protected void invalidated() {
            requestLayout();
        }
    };
    private final BooleanProperty spaceBottom = new SimpleBooleanProperty() {
        protected void invalidated() {
            requestLayout();
        }
    };
    private final BooleanProperty flexLastRow = new SimpleBooleanProperty(true) {
        protected void invalidated() {
            requestLayout();
        }
    };
    private double computedMinHeight;
    private boolean performingLayout;

    public FlexBox(Node... children) {
        getChildren().setAll(children);
        // This is necessary to clear the previous computed min/pref/max height cached value memorized in Region.min/pref/maxHeight()
        widthProperty().addListener(observable -> clearSizeCache());
        getChildren().addListener((InvalidationListener) observable -> clearSizeCache());
    }

    public FlexBox(double horizontalSpace, double verticalSpace, Node... children) {
        this(children);
        setHorizontalSpace(horizontalSpace);
        setVerticalSpace(verticalSpace);
    }

    private void clearSizeCache() {
        // Parent.clearSizeCache() is not accessible (package visibility) but requestLayout() will call it
        requestLayout();
    }

    public double getHorizontalSpace() {
        return horizontalSpace.get();
    }

    public DoubleProperty horizontalSpaceProperty() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(double horizontalSpace) {
        this.horizontalSpace.set(horizontalSpace);
    }

    public double getVerticalSpace() {
        return verticalSpace.get();
    }

    public DoubleProperty verticalSpaceProperty() {
        return verticalSpace;
    }

    public void setVerticalSpace(double verticalSpace) {
        this.verticalSpace.set(verticalSpace);
    }

    public boolean isSpaceTop() {
        return spaceTop.get();
    }

    public BooleanProperty spaceTopProperty() {
        return spaceTop;
    }

    public void setSpaceTop(boolean spaceTop) {
        this.spaceTop.set(spaceTop);
    }

    public boolean isSpaceLeft() {
        return spaceLeft.get();
    }

    public BooleanProperty spaceLeftProperty() {
        return spaceLeft;
    }

    public void setSpaceLeft(boolean spaceLeft) {
        this.spaceLeft.set(spaceLeft);
    }

    public boolean isSpaceRight() {
        return spaceRight.get();
    }

    public BooleanProperty spaceRightProperty() {
        return spaceRight;
    }

    public void setSpaceRight(boolean spaceRight) {
        this.spaceRight.set(spaceRight);
    }

    public boolean isSpaceBottom() {
        return spaceBottom.get();
    }

    public BooleanProperty spaceBottomProperty() {
        return spaceBottom;
    }

    public void setSpaceBottom(boolean spaceBottom) {
        this.spaceBottom.set(spaceBottom);
    }

    public boolean isFlexLastRow() {
        return flexLastRow.get();
    }

    public BooleanProperty flexLastRowProperty() {
        return flexLastRow;
    }

    public void setFlexLastRow(boolean flexLastRow) {
        this.flexLastRow.set(flexLastRow);
    }

    private final Map<Integer, FlexBoxRow> grid = new HashMap<>();

    /**
     * By default, flex items are laid out in the source order.
     * However, the order property controls the order in which they appear in the flex container.
     *
     * @param child the child of an FlexBox
     * @param value the order in which the child appear in the flex container
     */
    public static void setOrder(Node child, int value)
    {
        setConstraint(child, ORDER_CONSTRAINT, value);
    }

    public static int getOrder(Node child) {
        Object constraint = getConstraint(child, ORDER_CONSTRAINT);
        return constraint == null ? 0 : (int) constraint;
    }

    /**
     * This defines the ability for a flex item to grow if necessary.
     * It accepts a unitless value that serves as a proportion.
     * It dictates what amount of the available space inside the flex container the item should take up.
     * If all items have flex-grow set to 1, the remaining space in the container will be distributed equally to all children.
     * If one of the children has a value of 2, the remaining space would take up twice as much space as the others (or it will try to, at least).
     *
     * @param child the child of an FlexBox
     * @param value grow proportion
     */
    public static void setGrow(Node child, double value) {
        setConstraint(child, GROW_CONSTRAINT, value);
    }

    public static double getGrow(Node child) {
        Object o = getConstraint(child, GROW_CONSTRAINT);
        return o == null ? 1 : (double) o;
    }

    public static void setMargin(Node child, Insets value) {
        setConstraint(child, MARGIN_CONSTRAINT, value);
    }

    public static Insets getMargin(Node child) {
        return (Insets) getConstraint(child, MARGIN_CONSTRAINT);
    }

    // Writing setConstraint() again as Pane.setConstraint() is package private
    private static void setConstraint(Node node, Object key, Object value) {
        if (value == null)
            node.getProperties().remove(key);
        else
            node.getProperties().put(key, value);
        if (node.getParent() != null)
            node.getParent().requestLayout();
    }

    // Writing getConstraint() again as Pane.getConstraint() is package private
    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties())
            return node.getProperties().get(key);
        return null;
    }

    @Override
    protected double computeMinHeight(double width) {
        if (width < 0) {
            width = getWidth();
            if (width == 0) // This usually happens on first flex box layout when the box width is still unknown
                width = Double.MAX_VALUE; // resetting width to avoid a wrong first min height
        }
        computeLayout(width, false);
        return computedMinHeight;
    }

    @Override
    protected double computePrefHeight(double width) {
        return computeMinHeight(width);
    }

    @Override
    protected void layoutChildren() {
        performingLayout = true;
        computeLayout(getWidth(),true);
        performingLayout = false;
    }


    @Override
    public void requestLayout() {
        if (!performingLayout)
            super.requestLayout();
    }

    private void computeLayout(double width, boolean apply) {
        grid.clear();
        /*
         * First we transform all Nodes to a FlexBoxItem for caching purposes.
         */
        List<FlexBoxItem> flexBoxItems = Collections.filterMap(getManagedChildren(), Node::isVisible, FlexBoxItem::new);

        if (Collections.anyMatch(flexBoxItems, item -> item.order != 0))
            flexBoxItems.sort(Collections.comparingInt(item -> item.order));

        /*
         * Calculate column-row-grid for auto wrapping
         */
        int row = 0;
        FlexBoxRow flexBoxRow = new FlexBoxRow(), previousFlexBoxRow = null;
        addToGrid(row, flexBoxRow);

        double horizontalSpace = getHorizontalSpace(), verticalSpace = getVerticalSpace();
        // Removing the possible left & right space from computation
        width -= horizontalSpace * ( (isSpaceLeft() ? 1 : 0) + (isSpaceRight() ? 1 : 0) );
        double minWidthSum = 0, previousMinWidthSum = 0;

        for (int i = 0, n = flexBoxItems.size(); i < n; i++) {
            FlexBoxItem flexBoxItem = flexBoxItems.get(i);
            double nodeWidth = flexBoxItem.minWidth;
            minWidthSum += nodeWidth;

            if (minWidthSum >= width) {
                previousFlexBoxRow = flexBoxRow;
                previousMinWidthSum = minWidthSum;
                addToGrid(++row,  flexBoxRow = new FlexBoxRow());
                minWidthSum = nodeWidth;
            }

            flexBoxRow.addItem(flexBoxItem);

            // Adding space if not the last node
            if (i + 1 < n)
                minWidthSum += horizontalSpace;
        }

        // Moving tight items to last row to equalize pressure
        if (isFlexLastRow() && previousFlexBoxRow != null) {
            List<FlexBoxItem> previousItems = previousFlexBoxRow.items;
            while (previousItems.size() > 1) {
                FlexBoxItem lastItem = Collections.last(previousItems);
                double lastWidth = lastItem.minWidth + horizontalSpace;
                if (minWidthSum + lastWidth > Math.min(width, previousMinWidthSum - lastWidth))
                    break;
                previousFlexBoxRow.removeItem(lastItem);
                flexBoxRow.addFirstItem(lastItem);
                previousMinWidthSum -= lastWidth;
                minWidthSum += lastWidth;
            }
        }

        // iterate rows and calculate width
        Insets padding = getPadding();
        double y = padding.getTop() + (isSpaceTop() ? verticalSpace : 0);
        int i = 0;
        int noGridRows = grid.size();

        /*
         * iterate grid to calculate node sizes and positions
         * iterate grid-rows first
         */
        for (Integer rowIndex : grid.keySet()) {
            boolean isLastRow = i >= noGridRows - 1;
            // contains all nodes per row
            flexBoxRow = grid.get(rowIndex);
            List<FlexBoxItem> rowItems = flexBoxRow.getItems();
            int noRowItems = rowItems.size();

            double remainingWidth = width - flexBoxRow.rowMinWidth - (horizontalSpace * (noRowItems - 1)) - padding.getLeft() - padding.getRight();
            double flexGrowCellWidth = remainingWidth / flexBoxRow.flexGrowSum;

            double x = padding.getLeft() + (isSpaceLeft() ? horizontalSpace : 0);
            double rowMaxHeight = 0;

            // iterate nodes of row
            for (FlexBoxItem flexBoxItem : rowItems) {
                Node rowNode = flexBoxItem.node;

                double rowNodeMinWidth = flexBoxItem.minWidth;
                double rowNodeMaxWidth = rowNode.maxWidth(-1);
                double rowNodeStretchedWidth = rowNodeMinWidth + (flexGrowCellWidth * flexBoxItem.grow);
                double rowNodeWidth = isLastRow && !isFlexLastRow() ? rowNodeMinWidth : Math.min(rowNodeMaxWidth, Math.max(rowNodeStretchedWidth, rowNodeMinWidth));

                double h = rowNode.prefHeight(rowNodeWidth);
                if (apply)
                    layoutInArea(rowNode, snapPositionX(x), snapPositionY(y), snapSizeX(x + rowNodeWidth) - snapPositionX(x), snapSizeY(h), 0, flexBoxItem.margin, HPos.LEFT, VPos.TOP);
                rowMaxHeight = Math.max(rowMaxHeight, h);
                x += rowNodeWidth + horizontalSpace;
            }

            y += rowMaxHeight;
            if (!isLastRow)
                y += verticalSpace;
            i++;
        }

        computedMinHeight = y + (spaceBottom.get() ? verticalSpace : 0) + padding.getBottom();
        //lastComputationWidthInput = width;
    }

    private void addToGrid(int row, FlexBoxRow flexBoxRow) {
        grid.put(row, flexBoxRow);
    }

    private static final class FlexBoxItem {
        final Node node;
        final int order;
        final double grow;
        final double minWidth;
        final Insets margin;

        FlexBoxItem(Node node) {
            this.node = node;
            minWidth = node.minWidth(-1);
            order = getOrder(node);
            grow = getGrow(node);
            margin = getMargin(node);
        }
    }

    private static final class FlexBoxRow {
        private final ArrayList<FlexBoxItem> items = new ArrayList<>();
        double rowMinWidth;
        double flexGrowSum;

        void addItem(FlexBoxItem flexBoxItem) {
            items.add(flexBoxItem);
            rowMinWidth += flexBoxItem.minWidth;
            flexGrowSum += flexBoxItem.grow;
        }

        void addFirstItem(FlexBoxItem flexBoxItem) {
            items.add(0, flexBoxItem);
            rowMinWidth += flexBoxItem.minWidth;
            flexGrowSum += flexBoxItem.grow;
        }

        void removeItem(FlexBoxItem flexBoxItem) {
            if (items.remove(flexBoxItem)) {
                rowMinWidth -= flexBoxItem.minWidth;
                flexGrowSum -= flexBoxItem.grow;
            }
        }

        ArrayList<FlexBoxItem> getItems() {
            return items;
        }
    }
}
