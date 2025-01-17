package dev.webfx.extras.webtext.peers.openjfx;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerBase;
import dev.webfx.extras.webtext.peers.base.HtmlTextPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxNodePeer;
import dev.webfx.platform.util.Numbers;
import dev.webfx.platform.util.Strings;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

/**
 * @author Bruno Salmon
 */
public class OpenJFXHtmlTextWebViewPeer
        <FxN extends WebView, N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>
        extends FxNodePeer<FxN, N, NB, NM>
        implements HtmlTextPeerMixin<N, NB, NM>, FxLayoutMeasurable {

    protected final WebView webView = new WebView();

    public OpenJFXHtmlTextWebViewPeer() {
        this((NB) new HtmlTextPeerBase());
    }

    OpenJFXHtmlTextWebViewPeer(NB base) {
        super(base);
        updateText(null);
    }

    @Override
    protected FxN createFxNode() {
        return (FxN) webView;
    }

    Object executeScript(String script) {
        try {
            return webView.getEngine().executeScript(script);
        } catch (Exception e) { // probably the jsFunctions were not injected
            try {
                webView.getEngine().executeScript(jsFunctions);
                return webView.getEngine().executeScript(script);
            } catch (Exception e2) { // executeScript() might not be implemented (ex: Gluon)
                System.out.println("WARNING: WebEngine doesn't seem to support executeScript() method");
                return null;
            }
        }
    }

    @Override
    public void updateText(String text) {
        if (text == null || !text.contains("<html"))
            text = "<div style='margin: 0; padding: 0;'>" + Strings.toSafeString(text) + "</div>";
        webView.getEngine().loadContent(text);
    }

    @Override
    public void updateFont(Font font) {
        // TODO
    }

    @Override
    public void updateFill(Paint fill) {
        // TODO
    }

    @Override
    public void updateWidth(Number width) {
        double w = Numbers.doubleValue(width);
        executeScript("setDocumentWidth(" + documentWidth(w) + ");");
        resizeWebView(w, getNode().getHeight());
    }

    @Override
    public void updateHeight(Number height) {
        double h = Numbers.doubleValue(height);
        resizeWebView(getNode().getWidth(), h);
    }

    protected void resizeWebView(double width, double height) {
        webView.resize(width, height);
    }

    @Override
    public void updateBackground(Background background) {
    }

    @Override
    public void updateBorder(Border border) {
    }

    @Override
    public void updatePadding(Insets padding) {
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefHeight(double width) {
        String heightText = Strings.toString(executeScript("documentPrefHeight(" + documentWidth(width) + ")"));
        if (heightText != null)
            return webViewHeight(Double.valueOf(heightText));
        return 50; // webView.prefHeight(width);
    }

    private double documentWidth(double webViewWidth) {
        return webViewWidth - 12;
    }

    private double webViewHeight(double documentHeight) {
        return documentHeight + 25;
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    private final static String jsFunctions = "" +
            "function documentPrefHeight(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return 0;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "var h = e.offsetHeight;" +
            "s.width = w;" +
            "return h;" +
            "};" +
            "function setDocumentWidth(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "};" +
            "document.body.style.overflow = 'hidden';";
}