package dev.webfx.extras.i18n.spi.impl.ast;


import dev.webfx.extras.i18n.spi.impl.I18nProviderImpl;

/**
 * @author Bruno Salmon
 */
public class AstI18nProvider extends I18nProviderImpl {

    /**
     * Where the WebFX CLI generates the merged dictionaries TODAY, and therefore where they are looked up SECOND.
     */
    public final static String LEGACY_I18N_RESOURCE_PATH_WITH_LANG_PATTERN = "dev/webfx/extras/i18n/{lang}.{format}";

    /**
     * Where they WILL be generated, and therefore where they are looked up FIRST. Nothing writes here yet.
     *
     * <p>Why they move: dev.webfx.extras.i18n is the package of this module's own API ({@code I18n},
     * {@code Dictionary}, {@code I18nKeys}), so an executable carrying generated dictionaries declares that package
     * too, splitting it on the module path and having the boot layer refused with LayerInstantiationException. The
     * same bug, with the same fix, as the one dev.webfx.platform.meta.exe has always avoided by convention and
     * dev.webfx.platform.conf.exe is now adopting. It has gone unnoticed because executables are normally launched
     * from the classpath, where neither split packages nor resource encapsulation apply.
     */
    public final static String I18N_EXE_RESOURCE_PATH_WITH_LANG_PATTERN = "dev/webfx/extras/i18n/exe/{lang}.{format}";

    public AstI18nProvider() {
        this(new String[]{"properties", "json"});
    }

    public AstI18nProvider(String... supportedFormats) {
        // Both locations, newest first: a dictionary is read from its new location when it is there, and from the
        // legacy one until the CLI that generates it has moved it. Passing a pattern explicitly (below) opts out of
        // that and means exactly the location given.
        this(new String[]{I18N_EXE_RESOURCE_PATH_WITH_LANG_PATTERN, LEGACY_I18N_RESOURCE_PATH_WITH_LANG_PATTERN}, supportedFormats);
    }

    public AstI18nProvider(String resourcePathWithLangPattern, String... supportedFormats) {
        this(resourcePathWithLangPattern, "en", supportedFormats);
    }

    public AstI18nProvider(String[] resourcePathWithLangPatterns, String... supportedFormats) {
        super(new ResourceAstDictionaryLoader(resourcePathWithLangPatterns, supportedFormats), "en", "en");
    }

    public AstI18nProvider(String resourcePathWithLangPattern, Object defaultLanguage, String... supportedFormats) {
        this(resourcePathWithLangPattern, defaultLanguage, "en", supportedFormats);
    }

    public AstI18nProvider(String resourcePathWithLangPattern, Object defaultLanguage, Object initialLanguage, String... supportedFormats) {
        super(new ResourceAstDictionaryLoader(resourcePathWithLangPattern, supportedFormats), defaultLanguage, initialLanguage);
    }

}
