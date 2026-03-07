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
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanBindingsTest {

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

    @Test
    void isZero_floatValue_tracksSourceAndDisposes() {
        var source = new SimpleFloatProperty(0f);
        var binding = BooleanBindings.isZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0.5f);
        assertFalse(binding.get());

        source.set(-0.0f);
        assertTrue(binding.get());

        binding.dispose();
        source.set(1f);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNotZero_floatValue_tracksSourceAndDisposes() {
        var source = new SimpleFloatProperty(2f);
        var binding = BooleanBindings.isNotZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0f);
        assertFalse(binding.get());

        source.set(Float.NaN);
        assertTrue(binding.get());

        binding.dispose();
        source.set(0f);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isZero_doubleValue_tracksSourceAndDisposes() {
        var source = new SimpleDoubleProperty(0d);
        var binding = BooleanBindings.isZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0.25d);
        assertFalse(binding.get());

        source.set(-0.0d);
        assertTrue(binding.get());

        binding.dispose();
        source.set(9d);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isNotZero_doubleValue_tracksSourceAndDisposes() {
        var source = new SimpleDoubleProperty(-1d);
        var binding = BooleanBindings.isNotZero(source);

        assertTrue(binding.get());
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0d);
        assertFalse(binding.get());

        source.set(Double.NaN);
        assertTrue(binding.get());

        binding.dispose();
        source.set(0d);
        assertTrue(binding.get()); // disposed binding must no longer observe source changes
    }

    @Test
    void isZero_numberValue_handlesNullAndSupportedNumberTypes() {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(null);
        BooleanBinding binding = BooleanBindings.isZero(source);

        assertTrue(binding.get(), "null must be treated as zero");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0);
        assertTrue(binding.get());

        source.set(0L);
        assertTrue(binding.get());

        source.set(0f);
        assertTrue(binding.get());

        source.set(0d);
        assertTrue(binding.get());

        source.set((byte)0);
        assertTrue(binding.get());

        source.set((short)0);
        assertTrue(binding.get());

        source.set(1);
        assertFalse(binding.get());

        source.set(1L);
        assertFalse(binding.get());

        source.set(1f);
        assertFalse(binding.get());

        source.set(1d);
        assertFalse(binding.get());

        source.set((byte)1);
        assertFalse(binding.get());

        source.set((short)1);
        assertFalse(binding.get());
    }

    @Test
    void isZero_numberValue_usesDoubleValueForCustomNumberSubclass() {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(new CustomNumber(0.0));
        BooleanBinding binding = BooleanBindings.isZero(source);
        assertTrue(binding.get());

        source.set(new CustomNumber(5.0));
        assertFalse(binding.get());

        source.set(new CustomNumber(-3.0));
        assertFalse(binding.get());

        source.set(new CustomNumber(0.0));
        assertTrue(binding.get());

        source.set(new CustomNumber(0.00001));
        assertFalse(binding.get());
    }

    @Test
    void isNotZero_numberValue_handlesNullAndSupportedNumberTypes() {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(null);
        BooleanBinding binding = BooleanBindings.isNotZero(source);

        assertFalse(binding.get(), "null must be treated as non-nonzero");
        assertSame(source, binding.getDependencies().get(0));
        assertEquals(1, binding.getDependencies().size());

        source.set(0);
        assertFalse(binding.get());

        source.set(0L);
        assertFalse(binding.get());

        source.set(0f);
        assertFalse(binding.get());

        source.set(0d);
        assertFalse(binding.get());

        source.set((byte)0);
        assertFalse(binding.get());

        source.set((short)0);
        assertFalse(binding.get());

        source.set(1);
        assertTrue(binding.get());

        source.set(1L);
        assertTrue(binding.get());

        source.set(1f);
        assertTrue(binding.get());

        source.set(1d);
        assertTrue(binding.get());

        source.set((byte)1);
        assertTrue(binding.get());

        source.set((short)1);
        assertTrue(binding.get());
    }

    @Test
    void isNotZero_numberValue_usesDoubleValueForCustomNumberSubclass() {
        ObjectProperty<Number> source = new SimpleObjectProperty<>(new CustomNumber(0.0));
        BooleanBinding binding = BooleanBindings.isNotZero(source);
        assertFalse(binding.get());

        source.set(new CustomNumber(5.0));
        assertTrue(binding.get());

        source.set(new CustomNumber(-3.0));
        assertTrue(binding.get());

        source.set(new CustomNumber(0.0));
        assertFalse(binding.get());

        source.set(new CustomNumber(0.00001));
        assertTrue(binding.get());
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
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableFloatValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((javafx.beans.value.ObservableDoubleValue)null));
        assertNullArgument(() -> BooleanBindings.isZero((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isNotZero((ObservableValue<? extends Number>)null));
        assertNullArgument(() -> BooleanBindings.isNull((ObservableValue<?>)null));
        assertNullArgument(() -> BooleanBindings.isNotNull((ObservableValue<?>)null));
        assertNullArgument(() -> BooleanBindings.isNot(null));
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
