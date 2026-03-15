// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides string and object resources for {@link StaticResource} and {@link DynamicResource} markup extensions.
 * <p>
 * A {@code ResourceContext} defines how resource keys are resolved for the current FXML document.
 * Implementations may read values from a {@link ResourceBundle}, a service, an application-specific
 * localization store, or another source. String-valued resources are typically used for localized text,
 * while object-valued resources may represent enums, numbers, booleans, or arbitrary application objects.
 * <p>
 * The factory methods in this interface create resource contexts backed by a {@link ResourceBundle}.
 * The returned contexts use {@link MessageFormat} for formatted string lookup and perform conversions for
 * common requested target types when object values are read from the bundle.
 * <p>
 * Example:
 * <pre>{@code
 * public class MyPane extends MyPaneBase implements ResourceContextProvider {
 *
 *     private final ResourceContext resourceContext =
 *         ResourceContext.ofResourceBundle(ResourceBundle.getBundle("com.example.messages"));
 *
 *     @Override
 *     public ResourceContext getResourceContext() {
 *         return resourceContext;
 *     }
 * }
 * }</pre>
 */
public interface ResourceContext {

    /**
     * Resolves the value associated with the specified key as a string.
     * <p>
     * Implementations may interpret {@code args} according to their own formatting rules.
     * The {@code ResourceBundle}-based factory methods in this interface use {@link MessageFormat}
     * formatting when at least one argument is supplied.
     *
     * @param key the resource key
     * @param args the optional format arguments
     * @return the resolved string value
     * @throws RuntimeException if the key is not present, or the value cannot be produced as a string
     */
    String getString(String key, Object... args);

    /**
     * Resolves the value associated with the specified key as an object of the requested type.
     * <p>
     * Implementations may return an existing instance, perform type conversion, or throw an exception when
     * the value is incompatible with the requested {@code type}. The {@code ResourceBundle}-based factory
     * methods in this interface return compatible values directly and convert string-valued resources to
     * common primitive, wrapper, character, and enum target types.
     *
     * @param key the resource key
     * @param type the requested result type
     * @param <T> the result type
     * @return the resolved object value
     * @throws RuntimeException if the key is not present or the value cannot be converted to {@code type}
     */
    <T> T getObject(String key, Class<T> type);

    /**
     * Creates a resource context backed by the specified {@code ResourceBundle}.
     * <p>
     * The locale reported by {@link ResourceBundle#getLocale()} is used for formatted string values.
     *
     * @param bundle the resource bundle
     * @return a resource context backed by {@code bundle}
     * @throws NullPointerException if {@code bundle} is {@code null}
     */
    static ResourceContext ofResourceBundle(ResourceBundle bundle) {
        return ofResourceBundle(bundle, bundle.getLocale());
    }

    /**
     * Creates a resource context backed by the specified {@code ResourceBundle} and formatting locale.
     * <p>
     * String values are obtained from the bundle and formatted with {@link MessageFormat} when arguments are
     * supplied. Object values are obtained from the bundle and returned directly when already compatible with
     * the requested type. String-valued resources are converted to common primitive, wrapper, character, and
     * enum target types.
     *
     * @param bundle the resource bundle
     * @param locale the locale used for formatted string values
     * @return a resource context backed by {@code bundle}
     * @throws NullPointerException if {@code bundle} or {@code locale} is {@code null}
     */
    static ResourceContext ofResourceBundle(ResourceBundle bundle, Locale locale) {
        return new ResourceContext() {
            @Override
            public String getString(String key, Object... args) {
                return args != null && args.length > 0
                    ? new MessageFormat(bundle.getString(key), locale).format(args)
                    : bundle.getString(key);
            }

            @Override
            public <T> T getObject(String key, Class<T> type) {
                return ValueConverter.convert(key, bundle.getObject(key), type);
            }
        };
    }

    /**
     * Creates a resource context backed by the specified {@code ResourceBundle} and formatting locale.
     * <p>
     * When the locale changes, the resource context is invalidated so that markup extensions such as
     * {@link DynamicResource} can refresh values that are locale-dependent.
     *
     * @param bundle the resource bundle
     * @param locale the observable locale used for formatted string values
     * @return a resource context backed by {@code bundle}
     * @throws NullPointerException if {@code bundle} or {@code locale} is {@code null}
     */
    static ResourceContext ofResourceBundle(ResourceBundle bundle, ObservableValue<Locale> locale) {
        class Impl implements ResourceContext, Observable {
            final List<InvalidationListener> listeners = new ArrayList<>(4);
            final InvalidationListener listener = obs -> fireValueChangedEvent();

            List<InvalidationListener> toBeAdded;
            List<InvalidationListener> toBeRemoved;
            boolean locked;

            {
                locale.addListener(new WeakInvalidationListener(listener));
            }

            @Override
            public String getString(String key, Object... args) {
                return args != null && args.length > 0
                    ? new MessageFormat(bundle.getString(key), locale.getValue()).format(args)
                    : bundle.getString(key);
            }

            @Override
            public <T> T getObject(String key, Class<T> type) {
                return ValueConverter.convert(key, bundle.getObject(key), type);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                if (!locked) {
                    listeners.add(listener);
                } else if (toBeRemoved == null || !toBeRemoved.remove(listener)) {
                    if (toBeAdded == null) {
                        toBeAdded = new ArrayList<>(4);
                    }

                    toBeAdded.add(listener);
                }
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                if (!locked) {
                    listeners.remove(listener);
                } else if (toBeAdded == null || !toBeAdded.remove(listener)) {
                    if (toBeRemoved == null) {
                        toBeRemoved = new ArrayList<>(4);
                    }

                    toBeRemoved.add(listener);
                }
            }

            private void fireValueChangedEvent() {
                locked = true;

                for (InvalidationListener listener : listeners) {
                    try {
                        listener.invalidated(this);
                    } catch (Throwable ex) {
                        Thread currentThread = Thread.currentThread();
                        currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, ex);
                    }
                }

                if (toBeRemoved != null) {
                    listeners.removeAll(toBeRemoved);
                    toBeRemoved = null;
                }

                if (toBeAdded != null) {
                    listeners.addAll(toBeAdded);
                    toBeAdded = null;
                }

                locked = false;
            }
        }

        return new Impl();
    }
}
