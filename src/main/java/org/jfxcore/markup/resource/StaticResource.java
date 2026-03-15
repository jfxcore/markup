// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import org.jfxcore.markup.MarkupContext;
import org.jfxcore.markup.MarkupExtension;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import java.util.Objects;

/**
 * Resolves a resource value once and supplies the result to the markup extension target.
 * <p>
 * {@code StaticResource} is intended for values that are read from a {@link ResourceContext} during object
 * construction and do not need to be updated afterward. The root element of the FXML document must implement
 * {@link ResourceContextProvider}. The resource context is obtained from the root element and is used to
 * resolve the specified key.
 * <p>
 * In markup, this extension can be written in raw form or in generic form.
 * The raw form {@code {StaticResource key}} is usually sufficient because the target type is reified at runtime.
 * The generic form {@code {StaticResource<String> key}} makes the same type explicit in markup.
 * <p>
 * If the target type is compatible with {@link String}, the value is obtained through
 * {@link ResourceContext#getString(String, Object...)}. For all other target types, the value is obtained through
 * {@link ResourceContext#getObject(String, Class)}. This allows string resources to be formatted with arguments
 * and non-string resources to be converted to the target type by the {@link ResourceContext} implementation.
 * <p>
 * Observable format arguments are not supported. If any element of {@code formatArguments} is an
 * {@link ObservableValue}, an {@link IllegalArgumentException} is thrown when the markup extension is
 * constructed. Use {@link DynamicResource} when formatted output must react to observable arguments or to
 * changes in the underlying resource context.
 * <p>
 * Examples:
 * <pre>{@code
 * <Label text="{StaticResource greeting}"/>
 *
 * <Label text="{StaticResource<String> greeting}"/>
 *
 * <Label text="{StaticResource greetingWithNameAndNumber; formatArguments=Jane, Doe, 1234.5}"/>
 *
 * <Separator orientation="{StaticResource separatorOrientation}"/>
 * }</pre>
 *
 * @param <T> the type of the supplied value
 */
@DefaultProperty("key")
public final class StaticResource<T> implements MarkupExtension.Supplier<T> {

    private final String key;
    private final Object[] formatArguments;

    /**
     * Creates a {@code StaticResource} that resolves the specified resource key.
     *
     * @param key the resource key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public StaticResource(@NamedArg("key") String key) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.formatArguments = null;
    }

    /**
     * Creates a {@code StaticResource} that resolves the specified resource key and optionally formats its value.
     * <p>
     * If the target type is compatible with {@link String}, {@code formatArguments} are passed to
     * {@link ResourceContext#getString(String, Object...)}. For all other target types, {@code formatArguments}
     * are ignored and the resource is resolved through {@link ResourceContext#getObject(String, Class)}.
     *
     * @param key the resource key
     * @param formatArguments the optional format arguments used for string resources
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws IllegalArgumentException if any format argument is an {@link ObservableValue}
     */
    public StaticResource(@NamedArg("key") String key, @NamedArg("formatArguments") Object... formatArguments) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.formatArguments = formatArguments;

        if (formatArguments != null) {
            for (Object arg : formatArguments) {
                if (arg instanceof ObservableValue<?>) {
                    throw new IllegalArgumentException("StaticResource does not support observable format arguments");
                }
            }
        }
    }

    /**
     * Resolves the configured resource key for the current markup target.
     *
     * @param context the markup context
     * @return the resolved resource value
     * @throws RuntimeException if the root element does not implement {@link ResourceContextProvider},
     *                          if the key is not present, or if the value cannot be converted to the target type
     */
    @Override
    @SuppressWarnings("unchecked")
    public T get(MarkupContext context) {
        return (T)get(key, formatArguments, context);
    }

    private static Object get(String key, Object[] formatArguments, MarkupContext markupContext) {
        var resourceContext = ResourceContextHelper.getResourceContext(markupContext, StaticResource.class);
        return ResourceContextHelper.getResource(markupContext.getTargetType(), resourceContext, key, formatArguments);
    }
}
