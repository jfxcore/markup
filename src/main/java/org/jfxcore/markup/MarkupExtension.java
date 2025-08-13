// Copyright (c) 2025, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code MarkupExtension} represents an extension of the FXML 2.0 markup language.
 * <p>
 * Markup extensions can be implemented as either value suppliers or property consumers.
 * A value supplier can provide a value for a {@link Property}, a getter/setter pair, a named
 * constructor argument, or a function argument. It can also provide a value when added to a collection.
 * Property consumer extensions, on the other hand, can only be applied to a {@link Property}.
 * The extension receives the property instance to which it is applied, which it can use to set a value,
 * set up a binding, etc.
 */
public sealed interface MarkupExtension {

    /**
     * A markup extension that consumes a property.
     *
     * @param <T> the type of the property value
     */
    non-sealed interface PropertyConsumer<T> extends MarkupExtension {

        /**
         * Applies the markup extension to the specified property.
         *
         * @param property the target property
         * @param context the markup context
         */
        void accept(Property<T> property, MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that consumes a read-only property.
     *
     * @param <T> the type of the property value
     */
    non-sealed interface ReadOnlyPropertyConsumer<T> extends MarkupExtension {

        /**
         * Applies the markup extension to the specified read-only property.
         *
         * @param property the target property
         * @param context the markup context
         */
        void accept(ReadOnlyProperty<T> property, MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides a value.
     *
     * @param <T> the type of the value
     */
    non-sealed interface Supplier<T> extends MarkupExtension {

        /**
         * Specifies the range of return types provided by the {@link Supplier} markup extension,
         * allowing the FXML compiler to verify the applicability of the extension for a property
         * at compile time. For example, a {@code Supplier<Object>} markup extension could declare
         * its return types as {@code @ReturnType({URI.class, URL.class})} to indicate that it can
         * supply either object depending on the type of the targeted property.
         * <p>
         * This annotation must be applied to the {@link Supplier#get(MarkupContext)} method.
         */
        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.CLASS)
        @interface ReturnType {
            Class<?>[] value() default {};
        }

        /**
         * Provides a value for the target property or argument.
         *
         * @param context the markup context
         * @return the value
         */
        T get(MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides a boolean value.
     */
    non-sealed interface BooleanSupplier extends MarkupExtension {

        /**
         * Provides a boolean value for the target property or argument.
         *
         * @param context the markup context
         * @return the boolean value
         */
        boolean get(MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides an integer value.
     */
    non-sealed interface IntSupplier extends MarkupExtension {

        /**
         * Provides an integer value for the target property or argument.
         *
         * @param context the markup context
         * @return the integer value
         */
        int get(MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides a long value.
     */
    non-sealed interface LongSupplier extends MarkupExtension {

        /**
         * Provides a long value for the target property or argument.
         *
         * @param context the markup context
         * @return the long value
         */
        long get(MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides a float value.
     */
    non-sealed interface FloatSupplier extends MarkupExtension {

        /**
         * Provides a float value for the target property or argument.
         *
         * @param context the markup context
         * @return the float value
         */
        float get(MarkupContext context) throws Exception;
    }

    /**
     * A markup extension that provides a double value.
     */
    non-sealed interface DoubleSupplier extends MarkupExtension {

        /**
         * Provides a double value for the target property or argument.
         *
         * @param context the markup context
         * @return the double value
         */
        double get(MarkupContext context) throws Exception;
    }
}
