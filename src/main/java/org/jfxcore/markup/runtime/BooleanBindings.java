// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.runtime;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Objects;

/**
 * Boolean binding implementations that are used by the FXML 2.0 compiler.
 */
public final class BooleanBindings {

    private BooleanBindings() {}

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is zero,
     * and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isZero(ObservableIntegerValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() == 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is zero,
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotZero(ObservableIntegerValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() != 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is zero,
     * and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isZero(ObservableLongValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() == 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is zero,
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotZero(ObservableLongValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() != 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is zero,
     * and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isZero(ObservableFloatValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() == 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is zero,
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotZero(ObservableFloatValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() != 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is zero,
     * and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isZero(ObservableDoubleValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() == 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is zero,
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotZero(ObservableDoubleValue value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.get() != 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is zero
     * or {@code null}, and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isZero(ObservableValue<? extends Number> value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                Number n = value.getValue();
                if (n == null) return true;
                if (n instanceof Double v) return v == 0;
                if (n instanceof Integer v) return v == 0;
                if (n instanceof Long v) return v == 0;
                if (n instanceof Float v) return v == 0;
                if (n instanceof Byte v) return v == 0;
                if (n instanceof Short v) return v == 0;
                return n.doubleValue() == 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is zero,
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotZero(ObservableValue<? extends Number> value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                bind(value);
            }

            @Override
            public void dispose() {
                unbind(value);
            }

            @Override
            protected boolean computeValue() {
                Number n = value.getValue();
                if (n == null) return false;
                if (n instanceof Double v) return v != 0;
                if (n instanceof Integer v) return v != 0;
                if (n instanceof Long v) return v != 0;
                if (n instanceof Float v) return v != 0;
                if (n instanceof Byte v) return v != 0;
                if (n instanceof Short v) return v != 0;
                return n.doubleValue() != 0;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is {@code null},
     * and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNull(ObservableValue<?> value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                super.bind(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.getValue() == null;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code false} when the {@code value} is {@code null},
     * and {@code true} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNotNull(ObservableValue<?> value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                super.bind(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.getValue() != null;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a {@code BooleanBinding} that is {@code true} when the {@code value} is {@code false}
     * or {@code null}, and {@code false} otherwise.
     *
     * @param value the observable value
     * @return the binding
     */
    public static BooleanBinding isNot(ObservableValue<Boolean> value) {
        Objects.requireNonNull(value, "value cannot be null");

        return new BooleanBinding() {
            {
                super.bind(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }

            @Override
            protected boolean computeValue() {
                return value.getValue() != Boolean.TRUE;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    /**
     * Creates a bidirectional binding between two boolean properties, ensuring that one property is the
     * logical complement of the other property. More precisely, when one property is {@code true}, the
     * other property is not {@code true}; that is, it can be {@code false} or {@code null}. The binding
     * will never set a property value to {@code null}, but it will leave an existing {@code null} value
     * of the source property in place if the binding is satisfied.
     *
     * @param property1 the first property
     * @param property2 the second property
     */
    public static void bindBidirectionalComplement(Property<Boolean> property1, Property<Boolean> property2) {
        var binding = new BooleanComplementBinding(property1, property2);
        Boolean value = property2.getValue();
        property1.setValue(value != null ? !value : Boolean.TRUE);
        property1.addListener(binding);
        property2.addListener(binding);
    }
}
