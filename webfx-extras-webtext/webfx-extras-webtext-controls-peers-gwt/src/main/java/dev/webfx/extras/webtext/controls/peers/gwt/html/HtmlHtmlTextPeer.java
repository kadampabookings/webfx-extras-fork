package dev.webfx.extras.webtext.controls.peers.gwt.html;

import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlPaints;
import elemental2.dom.Element;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.extras.webtext.controls.peers.base.HtmlTextPeerBase;
import dev.webfx.extras.webtext.controls.peers.base.HtmlTextPeerMixin;
import dev.webfx.kit.mapper.peers.javafxcontrols.gwt.html.HtmlControlPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.NormalWhiteSpacePeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.html.layoutmeasurable.HtmlLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlUtil;
import dev.webfx.platform.shared.util.Strings;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public final class HtmlHtmlTextPeer
        <N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>
        extends HtmlControlPeer<N, NB, NM>
        implements HtmlTextPeerMixin<N, NB, NM>, HtmlLayoutMeasurable, NormalWhiteSpacePeer {

    public HtmlHtmlTextPeer() {
        this((NB) new HtmlTextPeerBase());
    }

    HtmlHtmlTextPeer(NB base) {
        super(base, HtmlUtil.createElement("fx-htmltext"));
    }

    @Override
    public void updateText(String text) {
        text = Strings.toSafeString(text);
        getElement().innerHTML = text;
        if (text.contains("<script"))
            executeScripts(getElement());
        clearCache();
    }

    @Override
    public void updateFont(Font font) {
        setFontAttributes(font);
        clearCache();
    }

    @Override
    public void updateFill(Paint fill) {
        setElementStyleAttribute("color", HtmlPaints.toHtmlCssPaint(fill));
    }

    private void executeScripts(Node node) {
        if (node instanceof Element) {
            Element element = (Element) node;
            if ("SCRIPT".equalsIgnoreCase(element.tagName)) {
                HTMLScriptElement script = HtmlUtil.createElement("script");
                script.text = element.innerHTML;
                for (int i = 0; i < element.attributes.length; i++)
                    script.setAttribute(element.attributes.getAt(i).name, element.attributes.getAt(i).value);
                element.parentNode.replaceChild(script, element);
                return;
            }
        }
        NodeList<Node> children = node.childNodes;
        for (int i = 0; i < children.length; i++)
            executeScripts(children.item(i));
    }

}