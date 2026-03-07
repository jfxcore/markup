// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.runtime;

import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BooleanComplementBindingTest {

    static Stream<Arguments> complementCases() {
        return Stream.of(
            Arguments.of(Boolean.TRUE, Boolean.FALSE),
            Arguments.of(Boolean.FALSE, Boolean.TRUE),
            Arguments.of(null, Boolean.TRUE)
        );
    }

    @ParameterizedTest
    @MethodSource("complementCases")
    void bindBidirectionalComplement_initializesProperty1FromProperty2(Boolean property2Value,
                                                                       Boolean expectedProperty1Value) {
        var property1 = new SimpleObjectProperty<Boolean>(null);
        var property2 = new SimpleObjectProperty<>(property2Value);

        BooleanBindings.bindBidirectionalComplement(property1, property2);
        assertEquals(expectedProperty1Value, property1.getValue());
        assertEquals(property2Value, property2.getValue());
    }

    @ParameterizedTest
    @MethodSource("complementCases")
    void bindBidirectionalComplement_updatesProperty2WhenProperty1Changes(Boolean property1Value,
                                                                          Boolean expectedProperty2Value) {
        var property1 = new SimpleObjectProperty<>(false);
        var property2 = new SimpleObjectProperty<>(true);

        BooleanBindings.bindBidirectionalComplement(property1, property2);
        setAndEnsureChange(property1, property1Value);
        assertEquals(property1Value, property1.getValue());
        assertEquals(expectedProperty2Value, property2.getValue());
    }

    @ParameterizedTest
    @MethodSource("complementCases")
    void bindBidirectionalComplement_updatesProperty1WhenProperty2Changes(Boolean property2Value,
                                                                          Boolean expectedProperty1Value) {
        var property1 = new SimpleObjectProperty<>(false);
        var property2 = new SimpleObjectProperty<>(true);

        BooleanBindings.bindBidirectionalComplement(property1, property2);
        setAndEnsureChange(property2, property2Value);
        assertEquals(property2Value, property2.getValue());
        assertEquals(expectedProperty1Value, property1.getValue());
    }

    @Test
    void bindBidirectionalComplement_reportsFailureWhenTargetUpdateFailsAndRollbackSucceeds() {
        var property1 = new SimpleObjectProperty<>(false);
        var property2 = new ThrowingObjectProperty(Boolean.FALSE);
        property2.setShouldThrow(Boolean.TRUE::equals);

        BooleanBindings.bindBidirectionalComplement(property1, property2);
        assertEquals(Boolean.TRUE, property1.getValue());
        assertEquals(Boolean.FALSE, property2.getValue());

        RuntimeException ex = captureUncaughtRuntimeException(() -> property1.setValue(Boolean.FALSE));
        assertEquals("Bidirectional binding failed, setting to the previous value.", ex.getMessage());

        var cause = assertInstanceOf(IllegalStateException.class, ex.getCause());
        assertEquals("simulated failure: true", cause.getMessage());

        // Source change succeeded, target was rolled back.
        assertEquals(Boolean.FALSE, property1.getValue());
        assertEquals(Boolean.FALSE, property2.getValue());

        // Binding should still be active after a successful rollback.
        property2.setShouldThrow(v -> false);
        property1.setValue(null);
        assertNull(property1.getValue());
        assertEquals(Boolean.TRUE, property2.getValue());
    }

    @Test
    void bindBidirectionalComplement_reportsFailureAndRemovesBindingWhenTargetUpdateAndRollbackFails() {
        var property1 = new SimpleObjectProperty<>(false);
        var property2 = new ThrowingObjectProperty(Boolean.FALSE);
        property2.setShouldThrow(v -> true);

        BooleanBindings.bindBidirectionalComplement(property1, property2);
        assertEquals(Boolean.TRUE, property1.getValue());
        assertEquals(Boolean.FALSE, property2.getValue());

        RuntimeException ex = captureUncaughtRuntimeException(() -> property1.setValue(Boolean.FALSE));
        assertTrue(ex.getMessage().startsWith("Bidirectional binding failed together with"));

        var cause = assertInstanceOf(IllegalStateException.class, ex.getCause());
        assertEquals("simulated failure: false", cause.getMessage());
        assertEquals(1, cause.getSuppressed().length);
        assertEquals("simulated failure: true", cause.getSuppressed()[0].getMessage());

        // Source change succeeded, target is unchanged.
        assertEquals(Boolean.FALSE, property1.getValue());
        assertEquals(Boolean.FALSE, property2.getValue());

        // Binding should have been removed.
        property2.setShouldThrow(v -> false);
        property1.setValue(null);
        assertNull(property1.getValue());
        assertEquals(Boolean.FALSE, property2.getValue());
    }

    private static void setAndEnsureChange(SimpleObjectProperty<Boolean> property, Boolean newValue) {
        if (Objects.equals(property.getValue(), newValue)) {
            property.setValue(Boolean.TRUE.equals(newValue) ? Boolean.FALSE : Boolean.TRUE);
        }

        property.setValue(newValue);
    }

    private static final class ThrowingObjectProperty extends SimpleObjectProperty<Boolean> {
        private Predicate<Boolean> shouldThrow = value -> false;

        private ThrowingObjectProperty(Boolean initialValue) {
            super(initialValue);
        }

        private void setShouldThrow(Predicate<Boolean> shouldThrow) {
            this.shouldThrow = shouldThrow;
        }

        @Override
        public void set(Boolean newValue) {
            if (shouldThrow.test(newValue)) {
                throw new IllegalStateException("simulated failure: " + newValue);
            }

            super.set(newValue);
        }
    }

    private static RuntimeException captureUncaughtRuntimeException(Runnable action) {
        var thread = Thread.currentThread();
        var previousHandler = thread.getUncaughtExceptionHandler();
        var captured = new java.util.concurrent.atomic.AtomicReference<Throwable>();
        thread.setUncaughtExceptionHandler((t, e) -> captured.set(e));

        try {
            action.run();
        } finally {
            thread.setUncaughtExceptionHandler(previousHandler);
        }

        Throwable throwable = captured.get();
        assertNotNull(throwable, "Expected listener exception to be reported to the uncaught exception handler.");
        return assertInstanceOf(RuntimeException.class, throwable);
    }
}
