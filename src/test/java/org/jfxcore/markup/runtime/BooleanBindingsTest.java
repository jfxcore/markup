// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.runtime;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BooleanBindingsTest {

    static Stream<BindingCase<ObservableFloatValue>> floatBindingCases() {
        return Stream.of(
            new BindingCase<ObservableFloatValue>("isZero", BooleanBindings::isZero, true, false, false),
            new BindingCase<ObservableFloatValue>("isZeroOrNaN", BooleanBindings::isZeroOrNaN, true, false, true),
            new BindingCase<ObservableFloatValue>("isNotZero", BooleanBindings::isNotZero, false, true, true),
            new BindingCase<ObservableFloatValue>("isNotZeroOrNaN", BooleanBindings::isNotZeroOrNaN, false, true, false)
        );
    }

    static Stream<BindingCase<ObservableDoubleValue>> doubleBindingCases() {
        return Stream.of(
            new BindingCase<ObservableDoubleValue>("isZero", BooleanBindings::isZero, true, false, false),
            new BindingCase<ObservableDoubleValue>("isZeroOrNaN", BooleanBindings::isZeroOrNaN, true, false, true),
            new BindingCase<ObservableDoubleValue>("isNotZero", BooleanBindings::isNotZero, false, true, true),
            new BindingCase<ObservableDoubleValue>("isNotZeroOrNaN", BooleanBindings::isNotZeroOrNaN, false, true, false)
        );
    }

    static Stream<BindingCase<ObservableValue<? extends Number>>> numberBindingCases() {
        return Stream.of(
            new BindingCase<ObservableValue<? extends Number>>("isZero", BooleanBindings::isZero, true, false, false),
            new BindingCase<ObservableValue<? extends Number>>("isZeroOrNaN", BooleanBindings::isZeroOrNaN, true, false, true),
            new BindingCase<ObservableValue<? extends Number>>("isNotZero", BooleanBindings::isNotZero, false, true, true),
            new BindingCase<ObservableValue<? extends Number>>("isNotZeroOrNaN", BooleanBindings::isNotZeroOrNaN, false, true, false)
        );
    }

    @Test
    void isZero_integerValue_tracksSourceAndDisposes() {
        var source = new SimpleIntegerProperty(0);
        var binding = BooleanBindings.isZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(5);
        assertFalse(binding.get());

        source.set(0);
        assertTrue(binding.get());

        binding.dispose();
        source.set(42);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNotZero_integerValue_tracksSourceAndDisposes() {
        var source = new SimpleIntegerProperty(0);
        var binding = BooleanBindings.isNotZero(source);

        assertFalse(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(-1);
        assertTrue(binding.get());

        source.set(0);
        assertFalse(binding.get());

        binding.dispose();
        source.set(7);
        assertFalse(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isZero_longValue_tracksSourceAndDisposes() {
        var source = new SimpleLongProperty(0L);
        var binding = BooleanBindings.isZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(1L);
        assertFalse(binding.get());

        binding.dispose();
        source.set(0L);
        assertFalse(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNotZero_longValue_tracksSourceAndDisposes() {
        var source = new SimpleLongProperty(10L);
        var binding = BooleanBindings.isNotZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0L);
        assertFalse(binding.get());

        binding.dispose();
        source.set(3L);
        assertFalse(binding.get()); // disposed binding must no longer observe source changes
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("floatBindingCases")
    void zeroComparison_floatValue_tracksSourceAndDisposes(BindingCase<ObservableFloatValue> testCase) {
        var source = new SimpleFloatProperty(0f);
        var binding = testCase.factory().apply(source);

        assertEquals(testCase.zeroResult(), binding.get(), "0");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0.5f);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero");

        source.set(-0.0f);
        assertEquals(testCase.zeroResult(), binding.get(), "-0");

        source.set(Float.NaN);
        assertEquals(testCase.nanResult(), binding.get(), "NaN");

        source.set(Float.POSITIVE_INFINITY);
        assertEquals(testCase.nonZeroResult(), binding.get(), "infinity");

        binding.dispose();
        source.set(0f);
        assertEquals(testCase.nonZeroResult(), binding.get(), "disposed binding must retain its cached value");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("doubleBindingCases")
    void zeroComparison_doubleValue_tracksSourceAndDisposes(BindingCase<ObservableDoubleValue> testCase) {
        var source = new SimpleDoubleProperty(0d);
        var binding = testCase.factory().apply(source);

        assertEquals(testCase.zeroResult(), binding.get(), "0");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0.25d);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero");

        source.set(-0.0d);
        assertEquals(testCase.zeroResult(), binding.get(), "-0");

        source.set(Double.NaN);
        assertEquals(testCase.nanResult(), binding.get(), "NaN");

        source.set(Double.POSITIVE_INFINITY);
        assertEquals(testCase.nonZeroResult(), binding.get(), "infinity");

        binding.dispose();
        source.set(0d);
        assertEquals(testCase.nonZeroResult(), binding.get(), "disposed binding must retain its cached value");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("numberBindingCases")
    void zeroComparison_numberValue_handlesNullAndSupportedNumberTypes(
            BindingCase<ObservableValue<? extends Number>> testCase) {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(null);
        BooleanBinding binding = testCase.factory().apply(source);

        assertEquals(testCase.zeroResult(), binding.get(), "null");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0);
        assertEquals(testCase.zeroResult(), binding.get(), "integer zero");

        source.set(0L);
        assertEquals(testCase.zeroResult(), binding.get(), "long zero");

        source.set(0f);
        assertEquals(testCase.zeroResult(), binding.get(), "float zero");

        source.set(0d);
        assertEquals(testCase.zeroResult(), binding.get(), "double zero");

        source.set(Float.NaN);
        assertEquals(testCase.nanResult(), binding.get(), "float NaN");

        source.set(Double.NaN);
        assertEquals(testCase.nanResult(), binding.get(), "double NaN");

        source.set((byte)0);
        assertEquals(testCase.zeroResult(), binding.get(), "byte zero");

        source.set((short)0);
        assertEquals(testCase.zeroResult(), binding.get(), "short zero");

        source.set(1);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero integer");

        source.set(1L);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero long");

        source.set(1f);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero float");

        source.set(1d);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero double");

        source.set((byte)1);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero byte");

        source.set((short)1);
        assertEquals(testCase.nonZeroResult(), binding.get(), "non-zero short");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("numberBindingCases")
    void zeroComparison_numberValue_usesDoubleValueForCustomNumberSubclass(
            BindingCase<ObservableValue<? extends Number>> testCase) {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(new CustomNumber(0.0));
        BooleanBinding binding = testCase.factory().apply(source);
        assertEquals(testCase.zeroResult(), binding.get(), "zero");

        source.set(new CustomNumber(5.0));
        assertEquals(testCase.nonZeroResult(), binding.get(), "positive");

        source.set(new CustomNumber(-3.0));
        assertEquals(testCase.nonZeroResult(), binding.get(), "negative");

        source.set(new CustomNumber(0.0));
        assertEquals(testCase.zeroResult(), binding.get(), "zero after change");

        source.set(new CustomNumber(0.00001));
        assertEquals(testCase.nonZeroResult(), binding.get(), "small non-zero");

        source.set(new CustomNumber(Double.NaN));
        assertEquals(testCase.nanResult(), binding.get(), "NaN");
    }

    @Test
    void isNull_tracksSourceAndDisposes() {
        ObjectProperty<String> source = new SimpleObjectProperty<>(null);
        BooleanBinding binding = BooleanBindings.isNull(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set("value");
        assertFalse(binding.get());

        source.set(null);
        assertTrue(binding.get());

        binding.dispose();
        source.set("later");
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNotNull_tracksSourceAndDisposes() {
        ObjectProperty<String> source = new SimpleObjectProperty<>("value");
        BooleanBinding binding = BooleanBindings.isNotNull(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(null);
        assertFalse(binding.get());

        source.set("again");
        assertTrue(binding.get());

        binding.dispose();
        source.set(null);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNot_treatsFalseAndNullAsTrueAndTracksSource() {
        ObjectProperty<Boolean> source = new SimpleObjectProperty<>(null);
        BooleanBinding binding = BooleanBindings.isNot(source);

        assertTrue(binding.get(), "null must be treated as not true");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(Boolean.FALSE);
        assertTrue(binding.get());

        source.set(Boolean.TRUE);
        assertFalse(binding.get());

        source.set(null);
        assertTrue(binding.get());

        binding.dispose();
        source.set(Boolean.TRUE);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void allFactoryMethods_rejectNullSources() {
        assertNullArgument(() -> BooleanBindings.isZero((javafx.beans.value.ObservableIntegerValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableIntegerValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((javafx.beans.value.ObservableLongValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableLongValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((javafx.beans.value.ObservableFloatValue)null));
        assertNullArgument(() -> BooleanBindings.isZeroOrNaN((javafx.beans.value.ObservableFloatValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableFloatValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZeroOrNaN((javafx.beans.value.ObservableFloatValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isZeroOrNaN((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZeroOrNaN((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isZeroOrNaN((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isNotZeroOrNaN((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isNull((ObservableValue<?>)null));
        assertNullArgument(() -> BooleanBindings.isNotNull((ObservableValue<?>)null));
        assertNullArgument(() -> BooleanBindings.isNot(null));
    }

    private record BindingCase<T>(String name,
                                  Function<T, BooleanBinding> factory,
                                  boolean zeroResult,
                                  boolean nonZeroResult,
                                  boolean nanResult) {
        @Override
        public String toString() {
            return name;
        }
    }

    private static void assertNullArgument(Runnable action) {
        NullPointerException ex = assertThrows(NullPointerException.class, action::run);
        assertEquals("value cannot be null", ex.getMessage());
    }

    private static final class CustomNumber extends Number {
        private final double value;

        private CustomNumber(double value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return (int)value;
        }

        @Override
        public long longValue() {
            return (long)value;
        }

        @Override
        public float floatValue() {
            return (float)value;
        }

        @Override
        public double doubleValue() {
            return value;
        }
    }
}
