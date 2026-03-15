// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

final class ValueConverter {

    private ValueConverter() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> T convert(String key, Object value, Class<T> type) {
        Class<?> boxedType = box(type);
        if (boxedType.isInstance(value)) {
            return (T)value;
        }

        if (value instanceof String stringValue) {
            String text = stringValue.trim();

            try {
                if (boxedType == Boolean.class) {
                    if (text.equals("true")) {
                        return (T)Boolean.TRUE;
                    }

                    if (text.equals("false")) {
                        return (T)Boolean.FALSE;
                    }
                } else if (boxedType == Byte.class) {
                    return (T)Byte.valueOf(Byte.parseByte(text));
                } else if (boxedType == Short.class) {
                    return (T)Short.valueOf(Short.parseShort(text));
                } else if (boxedType == Integer.class) {
                    return (T)Integer.valueOf(Integer.parseInt(text));
                } else if (boxedType == Long.class) {
                    return (T)Long.valueOf(Long.parseLong(text));
                } else if (boxedType == Float.class) {
                    return (T)Float.valueOf(Float.parseFloat(text));
                } else if (boxedType == Double.class) {
                    return (T)Double.valueOf(Double.parseDouble(text));
                } else if (boxedType == Character.class) {
                    if (text.length() == 1) {
                        return (T)Character.valueOf(text.charAt(0));
                    }
                } else if (boxedType.isEnum()) {
                    return (T)Enum.valueOf((Class<? extends Enum>)boxedType.asSubclass(Enum.class), text);
                }
            } catch (RuntimeException ignored) {
                // Fall through to the unified error below.
            }
        }

        throw unexpectedValue(key, type, value);
    }

    private static Class<?> box(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == char.class) return Character.class;
        if (type == void.class) return Void.class;
        return type;
    }

    private static RuntimeException unexpectedValue(String key, Class<?> expectedType, Object value) {
        return new RuntimeException(
            String.format(
                "Unexpected value for resource key '%s': expected = %s, actual = %s",
                key,
                expectedType.getName(),
                value != null ? value.getClass().getName() : "null"));
    }
}
