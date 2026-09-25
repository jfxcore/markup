// Copyright (c) 2025, 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import org.jfxcore.markup.MarkupContext;
import org.jfxcore.markup.MarkupExtension;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * Finds a resource by name and supplies its location in the form required by the target type.
 * <p>
 * When {@code classLoader} is omitted or {@code null}, embedded resources take precedence.
 * If no matching embedded resource is found, resource lookup uses {@link Class#getResource(String)}
 * on the document's root class. Names beginning with {@code /} are absolute; other names are relative
 * to the root class's package. The lookup runs from within the module of the root class, so its
 * resource packages do not need to be opened to the module of the markup extension.
 * <p>
 * When a custom class loader is supplied, resource lookup uses {@link ClassLoader#getResource(String)}
 * after removing a leading {@code /}, if present. Names are resolved from the class loader's resource
 * root. Embedded resources are skipped, and resource lookup does not fall back to the root class.
 */
@DefaultProperty("value")
public final class ClassPathResource implements MarkupExtension.Supplier<Object> {

    private final String value;
    private final ClassLoader classLoader;

    /**
     * Creates a {@code ClassPathResource} with the specified resource name.
     *
     * @param value the name of the resource
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public ClassPathResource(@NamedArg("value") String value) {
        this.value = Objects.requireNonNull(value, "value cannot be null").trim();
        this.classLoader = null;
    }

    /**
     * Creates a {@code ClassPathResource} with the specified resource name and an optional class loader.
     * <p>
     * When a class loader is supplied, resource lookup uses {@link ClassLoader#getResource(String)} after
     * removing a leading {@code /}, if present. When a class loader is not supplied, resource lookup first
     * checks for an embedded resource and then uses {@link Class#getResource(String)} on the document's
     * root class.
     *
     * @param value the name of the resource
     * @param classLoader the class loader, or {@code null} to use the default resource lookup
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public ClassPathResource(@NamedArg("value") String value,
                             @NamedArg("classLoader") ClassLoader classLoader) {
        this.value = Objects.requireNonNull(value, "value cannot be null").trim();
        this.classLoader = classLoader;
    }

    @Override
    @ReturnType({String.class, URI.class, URL.class})
    public Object get(MarkupContext context) throws Exception {
        return get(value, classLoader, context);
    }

    private static Object get(String value, ClassLoader classLoader, MarkupContext context) throws Exception {
        URL url = findResource(value, classLoader, context);
        if (url == null) {
            throw new RuntimeException("Resource not found: " + value);
        }

        return convert(url, context.getTargetType());
    }

    private static URL findResource(String value, ClassLoader classLoader, MarkupContext context) {
        if (classLoader != null) {
            return classLoader.getResource(value.startsWith("/") ? value.substring(1) : value);
        }

        if (value.indexOf('/') < 0 && value.indexOf('\\') < 0) {
            String resourceName = deriveResourceName(context.getDocumentName(), value);
            URL embedded = context.getResource(resourceName);
            if (embedded != null) {
                return embedded;
            }
        }

        return context.getResource(value);
    }

    private static String deriveResourceName(String documentName, String resourceName) {
        var crc = new CRC32();
        crc.update(documentName.getBytes(StandardCharsets.UTF_8));
        crc.update(resourceName.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
        String hash = Long.toHexString(crc.getValue());
        return documentName + "$" + hash + "$" + ResourceNameEncoder.encode(resourceName);
    }

    private static Object convert(URL url, Class<?> targetType) throws Exception {
        if (targetType.isAssignableFrom(String.class)) {
            return url.toExternalForm();
        }

        if (targetType.isAssignableFrom(URI.class)) {
            return url.toURI();
        }

        return url;
    }
}
