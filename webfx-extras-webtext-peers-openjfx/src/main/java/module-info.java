// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.webtext.peers.openjfx {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.web;
    requires jdk.jsobject;
    requires webfx.extras.webtext;
    requires webfx.extras.webtext.peers.base;
    requires webfx.extras.webtext.registry;
    requires webfx.extras.webtext.util;
    requires webfx.kit.javafxgraphics.openjfx;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.kit.util;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.webtext.peers.openjfx;
    exports dev.webfx.extras.webtext.registry.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider with dev.webfx.extras.webtext.registry.spi.impl.openjfx.OpenJFXWebTextRegistryProvider;

}