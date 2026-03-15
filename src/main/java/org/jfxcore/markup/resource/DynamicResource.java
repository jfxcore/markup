// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import org.jfxcore.markup.MarkupContext;
import org.jfxcore.markup.MarkupExtension;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Objects;

/**
 * Resolves a resource value for a {@link Property} and updates that property when the resource context or
 * relevant format arguments change.
 * <p>
 * {@code DynamicResource} is a {@link MarkupExtension.PropertyConsumer} markup extension. It can therefore only
 * be applied to properties, and not to method or constructor arguments. The root element of the FXML document
 * must implement {@link ResourceContextProvider}. The resource context obtained from the root element is used
 * to resolve the specified key.
 * <p>
 * In markup, this extension can be written in raw form or in generic form.
 * The raw form {@code {DynamicResource key}} is usually sufficient because the target type is reified at runtime.
 * The generic form {@code {DynamicResource<String> key}} makes the same type explicit in markup.
 * <p>
 * If the target type is compatible with {@link String}, the value is obtained through
 * {@link ResourceContext#getString(String, Object...)}. For all other target types, the value is obtained through
 * {@link ResourceContext#getObject(String, Class)}. This allows string resources to be formatted with arguments
 * and non-string resources to be converted to the target type by the {@link ResourceContext} implementation.
 * If any format argument is an {@link ObservableValue}, the formatted string is recomputed when the value changes.
 * <p>
 * If the {@link ResourceContext} implements {@link Observable}, the property is updated when the resource context
 * is invalidated. This allows locale-sensitive strings and other dynamic values to refresh.
 * <p>
 * Examples:
 * <pre>{@code
 * <Label text="{DynamicResource greeting}"/>
 *
 * <Label text="{DynamicResource<String> greeting}"/>
 *
 * <Label text="{DynamicResource greetingWithNameAndMessage; formatArguments=Jane, Doe, ${message}}"/>
 * }</pre>
 *
 * @param <T> the target type
 */
@DefaultProperty("key")
public final class DynamicResource<T> implements MarkupExtension.PropertyConsumer<T> {

    private final String key;
    private final Object[] formatArguments;

    /**
     * Creates a {@code DynamicResource} that resolves the specified resource key.
     *
     * @param key the resource key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public DynamicResource(@NamedArg("key") String key) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.formatArguments = null;
    }

    /**
     * Creates a {@code DynamicResource} that resolves the specified resource key and optionally formats its value.
     * <p>
     * If the target type is compatible with {@link String}, {@code formatArguments} are passed to
     * {@link ResourceContext#getString(String, Object...)}. For all other target types, {@code formatArguments}
     * are ignored and the resource is resolved through {@link ResourceContext#getObject(String, Class)}.
     * Observable format arguments are observed and cause the formatted value to be recomputed when they change.
     *
     * @param key the resource key
     * @param formatArguments the optional format arguments used for string resources
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public DynamicResource(@NamedArg("key") String key, @NamedArg("formatArguments") Object... formatArguments) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.formatArguments = formatArguments;
    }

    /**
     * Applies the markup extension to the target property.
     * <p>
     * Depending on the target type and on whether the active {@link ResourceContext} is observable, the
     * property is either assigned a one-time value or bound to an observable value that refreshes when the
     * resource context or observable format arguments change.
     *
     * @param property the target property
     * @param context the markup context
     * @throws RuntimeException if the root element does not implement {@link ResourceContextProvider},
     *                          if the key is not present, or if the value cannot be converted to the target type
     */
    @Override
    public void accept(Property<T> property, MarkupContext context) {
        accept(property, key, formatArguments, context);
    }

    @SuppressWarnings("unchecked")
    private static <T> void accept(Property<T> property, String key, Object[] formatArguments,
                                   MarkupContext markupContext) {
        var resourceContext = ResourceContextHelper.getResourceContext(markupContext, DynamicResource.class);

        if (ResourceContextHelper.isFormattedStringTarget(markupContext.getTargetType(), formatArguments)) {
            acceptString((Property<String>)property, key, formatArguments, markupContext, resourceContext);
        } else {
            Object value = ResourceContextHelper.getResource(markupContext.getTargetType(), resourceContext, key, null);
            acceptObject(property, key, (T)value, markupContext.getTargetType(), resourceContext);
        }
    }

    private static <T> void acceptObject(Property<T> property, String key, T value,
                                         Class<?> targetType, ResourceContext resourceContext) {
        if (resourceContext instanceof Observable observableContext) {
            var observableValue = new ObservableValueImpl<>(targetType, resourceContext, key, value);
            property.bind(observableValue);
            observableContext.addListener(new WeakInvalidationListener(observableValue));
        } else {
            property.setValue(value);
        }
    }

    private static void acceptString(Property<String> property, String key, Object[] formatArguments,
                                     MarkupContext markupContext, ResourceContext resourceContext) {
        int numObservables = 0;

        if (formatArguments != null) {
            for (Object formatArg : formatArguments) {
                if (formatArg instanceof ObservableValue<?>) {
                    ++numObservables;
                }
            }
        }

        if (numObservables > 0) {
            acceptStringWithObservableArguments(markupContext.getTargetType(), property, key, formatArguments,
                                                numObservables, resourceContext);
        } else {
            acceptStringWithoutObservableArguments(markupContext.getTargetType(), property, key,
                                                   formatArguments, resourceContext);
        }
    }

    private static void acceptStringWithObservableArguments(
            Class<?> targetType, Property<String> property, String key, Object[] formatArguments,
            int numObservables, ResourceContext resourceContext) {
        Observable[] observables = new Observable[numObservables];

        for (int i = 0, j = 0; i < formatArguments.length; ++i) {
            if (formatArguments[i] instanceof ObservableValue<?> observable) {
                observables[j++] = observable;
            }
        }

        var value = new ObservableStringWithArgsImpl(resourceContext, key, observables, formatArguments);
        property.bind(value);

        if (resourceContext instanceof Observable observableContext) {
            observableContext.addListener(new WeakInvalidationListener(value));
        }
    }

    private static void acceptStringWithoutObservableArguments(
            Class<?> targetType, Property<String> property, String key,
            Object[] formatArguments, ResourceContext resourceContext) {
        String formattedValue = resourceContext.getString(key, formatArguments);

        if (resourceContext instanceof Observable observableContext) {
            var observableValue = new ObservableStringImpl(
                targetType, resourceContext, key, formattedValue, formatArguments);
            property.bind(observableValue);
            observableContext.addListener(new WeakInvalidationListener(observableValue));
        } else {
            property.setValue(formattedValue);
        }
    }

    private static Object getResource(Class<?> targetType, ResourceContext context, String key) {
        return targetType == String.class
            ? context.getString(key)
            : context.getObject(key, targetType);
    }

    private static class ObservableValueImpl<T> extends ObservableValueBase<T> implements InvalidationListener {
        final Class<?> targetType;
        final ResourceContext context;
        final String key;
        T value;

        ObservableValueImpl(Class<?> targetType, ResourceContext context, String key, T value) {
            this.targetType = targetType;
            this.context = context;
            this.key = key;
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invalidated(Observable observable) {
            value = (T)getResource(targetType, context, key);
            fireValueChangedEvent();
        }
    }

    private static class ObservableStringImpl extends ObservableValueImpl<String> {
        final Object[] formatArguments;

        ObservableStringImpl(Class<?> targetType, ResourceContext context, String key,
                             String value, Object[] formatArguments) {
            super(targetType, context, key, value);
            this.formatArguments = formatArguments;
        }

        @Override
        public void invalidated(Observable observable) {
            value = context.getString(key, formatArguments);
            fireValueChangedEvent();
        }
    }

    private static class ObservableStringWithArgsImpl extends StringBinding implements InvalidationListener {
        final String key;
        final ResourceContext context;
        final Observable[] dependencies;
        final Object[] formatArguments;

        ObservableStringWithArgsImpl(ResourceContext context, String key,
                                     Observable[] dependencies,
                                     Object[] formatArguments) {
            bind(dependencies);
            this.key = key;
            this.context = context;
            this.dependencies = dependencies;
            this.formatArguments = formatArguments;
        }

        @Override
        public void dispose() {
            unbind(dependencies);
        }

        @Override
        protected String computeValue() {
            Object[] args = new Object[formatArguments.length];

            for (int i = 0; i < formatArguments.length; ++i) {
                args[i] = formatArguments[i] instanceof ObservableValue<?> value
                    ? value.getValue()
                    : formatArguments[i];
            }

            return context.getString(key, args);
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.observableArrayList(dependencies);
        }

        @Override
        public void invalidated(Observable observable) {
            invalidate();
        }
    }
}
