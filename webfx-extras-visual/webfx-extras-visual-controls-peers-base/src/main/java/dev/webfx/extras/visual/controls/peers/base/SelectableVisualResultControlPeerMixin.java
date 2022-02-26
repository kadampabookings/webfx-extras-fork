package dev.webfx.extras.visual.controls.peers.base;

import dev.webfx.extras.visual.VisualSelection;
import dev.webfx.extras.visual.controls.SelectableVisualResultControl;
import dev.webfx.extras.visual.SelectionMode;

/**
 * @author Bruno Salmon
 */
public interface SelectableVisualResultControlPeerMixin
        <C, N extends SelectableVisualResultControl, NB extends SelectableVisualResultControlPeerBase<C, N, NB, NM>, NM extends SelectableVisualResultControlPeerMixin<C, N, NB, NM>>

        extends VisualResultControlPeerMixin<C, N, NB, NM> {

    void updateSelectionMode(SelectionMode mode);

    void updateVisualSelection(VisualSelection selection);
}
