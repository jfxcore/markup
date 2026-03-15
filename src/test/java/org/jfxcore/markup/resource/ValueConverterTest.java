// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import javafx.geometry.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueConverterTest {

    @Test
    public void returnsObjectUnchangedWhenAlreadyCompatible() {
        Orientation value = ValueConverter.convert("orientation", Orientation.HORIZONTAL, Orientation.class);
        assertSame(Orientation.HORIZONTAL, value);
    }

    @Test
    public void convertStringToIntPrimitive() {
        int value = ValueConverter.convert("count", "42", int.class);
        assertEquals(42, value);
    }

    @Test
    public void convertStringToIntegerWrapper() {
        Integer value = ValueConverter.convert("count", "42", Integer.class);
        assertEquals(42, value);
    }

    @Test
    public void invalidIntegerThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ValueConverter.convert("count", "not-a-number", Integer.class));

        assertTrue(ex.getMessage().startsWith("Unexpected value for resource key 'count'"));
        assertTrue(ex.getMessage().contains("expected = java.lang.Integer"));
        assertTrue(ex.getMessage().contains("actual = java.lang.String"));
    }

    @Test
    public void convertStringToLongPrimitive() {
        long value = ValueConverter.convert("count", "42", long.class);
        assertEquals(42, value);
    }

    @Test
    public void convertStringToLongWrapper() {
        Long value = ValueConverter.convert("count", "42", Long.class);
        assertEquals(42, value);
    }

    @Test
    public void convertStringToDoublePrimitive() {
        double value = ValueConverter.convert("amount", "1234.5", double.class);
        assertEquals(1234.5, value);
    }

    @Test
    public void convertStringToDoubleWrapper() {
        Double value = ValueConverter.convert("amount", "1234.5", Double.class);
        assertEquals(1234.5, value);
    }

    @Test
    public void convertStringToFloatPrimitive() {
        float value = ValueConverter.convert("amount", "1234.5", float.class);
        assertEquals(1234.5f, value);
    }

    @Test
    public void convertStringToFloatWrapper() {
        Float value = ValueConverter.convert("amount", "1234.5", Float.class);
        assertEquals(1234.5f, value);
    }

    @Test
    public void convertStringToBooleanPrimitive() {
        boolean value = ValueConverter.convert("active", "true", boolean.class);
        assertTrue(value);
    }

    @Test
    public void convertStringToBooleanWrapper() {
        Boolean value = ValueConverter.convert("active", "true", Boolean.class);
        assertEquals(Boolean.TRUE, value);
    }

    @Test
    public void invalidBooleanThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ValueConverter.convert("active", "yes", Boolean.class));

        assertTrue(ex.getMessage().startsWith("Unexpected value for resource key 'active'"));
        assertTrue(ex.getMessage().contains("expected = java.lang.Boolean"));
        assertTrue(ex.getMessage().contains("actual = java.lang.String"));
    }

    @Test
    public void convertStringToEnum() {
        Orientation value = ValueConverter.convert("orientation", "VERTICAL", Orientation.class);
        assertEquals(Orientation.VERTICAL, value);
    }

    @Test
    public void convertStringToCharPrimitive() {
        char value = ValueConverter.convert("letter", "X", char.class);
        assertEquals('X', value);
    }

    @Test
    public void convertStringToCharWrapper() {
        Character value = ValueConverter.convert("letter", "X", Character.class);
        assertEquals('X', value);
    }

    @Test
    public void invalidEnumThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> ValueConverter.convert("orientation", "DIAGONAL", Orientation.class));

        assertTrue(ex.getMessage().startsWith("Unexpected value for resource key 'orientation'"));
        assertTrue(ex.getMessage().contains("expected = " + Orientation.class.getName()));
        assertTrue(ex.getMessage().contains("actual = java.lang.String"));
    }
}
