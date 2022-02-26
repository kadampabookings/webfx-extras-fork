package dev.webfx.extras.webtext.controls;

import dev.webfx.extras.webtext.controls.registry.WebTextRegistry;
import dev.webfx.kit.mapper.peers.javafxgraphics.HasNoChildrenPeers;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class HtmlText extends Control implements HasNoChildrenPeers {

    public HtmlText() {
    }

    public HtmlText(String text) {
        setText(text);
    }

    private final Property<String> textProperty = new SimpleObjectProperty<>();
    public Property<String> textProperty() {
        return textProperty;
    }
    public void setText(String text) {
        textProperty.setValue(text);
    }
    public String getText() {
        return textProperty.getValue();
    }

    private final Property<Font> fontProperty = new SimpleObjectProperty<>();
    public Property<Font> fontProperty() {
        return fontProperty;
    }
    public void setFont(Font font) {
        fontProperty.setValue(font);
    }
    public Font getFont() {
        return fontProperty.getValue();
    }

    private final Property<Paint> fillProperty = new SimpleObjectProperty<>();
    public Property<Paint> fillProperty() {
        return fillProperty;
    }
    public void setFill(Paint fill) {
        fillProperty.setValue(fill);
    }
    public Paint getFill() {
        return fillProperty.getValue();
    }

    // Size computing functions set by FxHtmlTextTextFlowPeer to redirect the computation to the TextFlow component
    public Function<Double, Double> computeMinWidthFunction, computeMinHeightFunction, computePrefWidthFunction, computePrefHeightFunction, computeMaxWidthFunction, computeMaxHeightFunction;

    private void checkPeer() {
        if (DEFAULT_SKIN_FACTORY != null && getSkin() == null)
            setSkin(DEFAULT_SKIN_FACTORY.apply(this));
    }

    @Override
    protected double computeMinWidth(double height) {
        checkPeer();
        if (computeMinWidthFunction != null)
            return computeMinWidthFunction.apply(height);
        return super.computeMinWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        checkPeer();
        if (computeMinHeightFunction != null)
            return computeMinHeightFunction.apply(width);
        return super.computeMinHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        checkPeer();
        if (computePrefWidthFunction != null)
            return computePrefWidthFunction.apply(height);
        return super.computePrefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        checkPeer();
        if (computePrefHeightFunction != null)
            return computePrefHeightFunction.apply(width);
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        checkPeer();
        if (computeMaxWidthFunction != null)
            return computeMaxWidthFunction.apply(height);
        return super.computeMaxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        checkPeer();
        if (computeMaxHeightFunction != null)
            return computeMaxHeightFunction.apply(width);
        return super.computeMaxHeight(width);
    }

    private static Function<Control, Skin<?>> DEFAULT_SKIN_FACTORY;

    public static void setDefaultSkinFactory(Function<Control, Skin<?>> defaultSkinFactory) {
        DEFAULT_SKIN_FACTORY = defaultSkinFactory;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return DEFAULT_SKIN_FACTORY == null ? null : DEFAULT_SKIN_FACTORY.apply(this);
    }

    static {
        WebTextRegistry.registerHtmlText();
    }
}
