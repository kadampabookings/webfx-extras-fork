package dev.webfx.extras.i18n.spi.impl.ast;

import dev.webfx.platform.async.Future;
import dev.webfx.platform.async.Promise;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Strings;
import dev.webfx.extras.i18n.Dictionary;
import dev.webfx.extras.i18n.spi.impl.DictionaryLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple resource-based dictionary loader assuming a resource file in AST format (properties or json) for each
 * language. It assumes that the AST formats in use in these resource files are already registered in the AST API.
 * It loads the whole dictionary resource file with all its keys (and not just the requested keys).
 * It doesn't know in advance in which AST format the dictionary will be, so it will try to load them all
 * (ex: en.properties and en.json) and return the one found in the resources (this may cause "... 404 (Not Found)"
 * logs in the browser console). When a dictionary is found in the requested language, it is cached, because it
 * contains all keys and can therefore be used on subsequent keys requests. This cache also prevents repeating
 * resource loading, including failed attempts.
 *
 * <p>Several resource path patterns may be given, which are tried IN ORDER: all formats of the first pattern are
 * attempted, and only once every one of them has come back missing is the next pattern tried. That order is the point
 * — see {@link AstI18nProvider}, where it covers the move of the generated dictionaries into an .exe sub-package. The
 * formats within one pattern are still attempted in parallel, as they always were, so this costs no extra round trip
 * when the dictionaries are where they are expected.
 *
 * <p>Note that only a MISSING dictionary falls through to the next pattern. A dictionary that is found but does not
 * parse fails the whole load, rather than being quietly replaced by an older copy of itself from the previous
 * location.
 *
 * @author Bruno Salmon
 */
final class ResourceAstDictionaryLoader implements DictionaryLoader {

    private static final boolean CACHE_DICTIONARIES = true;

    private final String[] astResourcePathWithLangPatterns;
    private final String[] supportedFormats;
    private final Map<Object /* lang */, Dictionary> dictionaryCache = new HashMap<>();

    ResourceAstDictionaryLoader(String astResourcePathWithLangPattern, String... supportedFormats) {
        this(new String[]{astResourcePathWithLangPattern}, supportedFormats);
    }

    ResourceAstDictionaryLoader(String[] astResourcePathWithLangPatterns, String... supportedFormats) {
        this.astResourcePathWithLangPatterns = astResourcePathWithLangPatterns;
        this.supportedFormats = supportedFormats;
    }

    private String getDictionaryResourcePath(String astResourcePathWithLangPattern, Object lang, String format) {
        String path = astResourcePathWithLangPattern;
        path = Strings.replaceAll(path, "{lang}", Strings.toString(lang));
        path = Strings.replaceAll(path, "{format}", Strings.toString(format));
        return path;
    }

    @Override
    public Future<Dictionary> loadDictionary(Object lang, Set<Object> keys) {
        Dictionary cachedDictionary = dictionaryCache.get(lang);
        if (cachedDictionary != null)
            return Future.succeededFuture(cachedDictionary);
        Future<Dictionary> future = loadDictionaryFromPattern(lang, 0);
        for (int patternIndex = 1; patternIndex < astResourcePathWithLangPatterns.length; patternIndex++) {
            int nextPatternIndex = patternIndex;
            future = future.recover(cause -> cause instanceof DictionaryNotFoundException
                    ? loadDictionaryFromPattern(lang, nextPatternIndex) // missing here, so try the next location
                    : Future.failedFuture(cause)); // found but unusable: that is the answer, don't look elsewhere
        }
        return future;
    }

    private Future<Dictionary> loadDictionaryFromPattern(Object lang, int patternIndex) {
        Promise<Dictionary> promise = Promise.promise();
        String astResourcePathWithLangPattern = astResourcePathWithLangPatterns[patternIndex];
        AtomicInteger failureCounter = new AtomicInteger();
        for (String format : supportedFormats) {
            String dictionaryResourcePath = getDictionaryResourcePath(astResourcePathWithLangPattern, lang, format);
            Resource.loadText(dictionaryResourcePath, text -> {
                if (text != null) { // null happens on JRE when resource doesn't exist
                    try {
                        AstDictionary dictionary = new AstDictionary(text, format);
                        if (CACHE_DICTIONARIES)
                            dictionaryCache.put(lang, dictionary);
                        promise.tryComplete(dictionary);
                    } catch (Exception e) { // Can happen while parsing the text in the format
                        promise.tryFail(new RuntimeException("⛔️ Format error in i18n dictionary " + dictionaryResourcePath + " - error: " + e.getMessage()));
                    }
                } else { // null = resource not found
                    if (failureCounter.incrementAndGet() == supportedFormats.length)
                        promise.tryFail(new DictionaryNotFoundException("No dictionary found for language " + lang));
                }
            }, e -> {
                if (failureCounter.incrementAndGet() == supportedFormats.length)
                    promise.tryFail(new DictionaryNotFoundException(Strings.toString(e)));
            });
        }
        return promise.future();
    }

    /**
     * Marks the one failure that another resource path pattern may still answer: the dictionary was not there. Any
     * other failure is about a dictionary that WAS there, so trying an older location would hide it.
     */
    private static final class DictionaryNotFoundException extends RuntimeException {
        DictionaryNotFoundException(String message) {
            super(message);
        }
    }
}
