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
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * Finds a resource with a given name.
 * <p>
 * If the name starts with {@code /}, the name is resolved with the {@link ClassLoader#getResource(String)}
 * method of the context class loader of the current thread. Otherwise, the name is resolved with the
 * {@link Class#getResource(String)} method of the class of the root element of the FXML document.
 */
@DefaultProperty("value")
public final class ClassPathResource implements MarkupExtension.Supplier<Object> {

    private final String value;

    /**
     * Creates a {@code ClassPathResource} with the specified resource name.
     *
     * @param value the name of the resource
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public ClassPathResource(@NamedArg("value") String value) {
        this.value = Objects.requireNonNull(value, "value cannot be null").trim();
    }

    @Override
    @ReturnType({String.class, URI.class, URL.class})
    public Object get(MarkupContext context) throws Exception {
        return get(value, context);
    }

    private static Object get(String value, MarkupContext context) throws Exception {
        URL url = findResource(value, context);
        if (url == null) {
            throw new RuntimeException("Resource not found: " + value);
        }

        return convert(url, context.getTargetType());
    }

    private static URL findResource(String value, MarkupContext context) {
        if (value.startsWith("/")) {
            return Thread.currentThread().getContextClassLoader().getResource(value.substring(1));
        }

        Class<?> rootClass = context.getRoot().getClass();

        if (value.indexOf('/') < 0 && value.indexOf('\\') < 0) {
            String resourceName = deriveResourceName(context.getDocumentName(), value);
            URL embedded = rootClass.getResource(resourceName);
            if (embedded != null) {
                return embedded;
            }
        }

        return rootClass.getResource(value);
    }

    private static String deriveResourceName(String documentName, String resourceName) {
        var crc = new CRC32();
        crc.update(documentName.getBytes(StandardCharsets.UTF_8));
        crc.update(resourceName.getBytes(StandardCharsets.UTF_8));
        String hash = Long.toHexString(crc.getValue());
        return documentName + "$" + hash + "$" + resourceName;
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
