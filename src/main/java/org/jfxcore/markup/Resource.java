// Copyright (c) 2025, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup;

import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

@DefaultProperty("value")
public final class Resource implements MarkupExtension.Supplier<Object> {

    private final String value;

    public Resource(@NamedArg("value") String value) {
        this.value = Objects.requireNonNull(value, "value cannot be null").trim();
    }

    @Override
    @ReturnType({String.class, URI.class, URL.class})
    public Object get(MarkupContext context) throws Exception {
        return get(value, context);
    }

    private static Object get(String value, MarkupContext context) throws Exception {
        URL url = value.startsWith("/") ?
            Thread.currentThread().getContextClassLoader().getResource(value.substring(1)) :
            context.getRoot().getClass().getResource(value);

        if (url == null) {
            throw new RuntimeException("Resource not found: " + value);
        }

        Class<?> targetType = context.getTargetType();

        if (targetType.isAssignableFrom(String.class)) {
            return url.toExternalForm();
        }

        if (targetType.isAssignableFrom(URI.class)) {
            return url.toURI();
        }

        return url;
    }
}
