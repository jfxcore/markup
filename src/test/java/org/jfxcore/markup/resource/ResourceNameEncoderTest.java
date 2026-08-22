// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import static org.jfxcore.markup.resource.ResourceNameEncoder.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResourceNameEncoderTest {

    @Test
    void leavesAllowedCharactersUnchanged() {
        assertEquals(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-",
            encode("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-"));
    }

    @Test
    @SuppressWarnings("StringOperationCanBeSimplified")
    void returnsOriginalStringWhenNoEncodingIsNecessary() {
        String input = new String("Test_123-foo");
        assertSame(input, encode(input));
    }

    @Test
    void handlesEmptyString() {
        String input = "";
        assertSame(input, encode(input));
    }

    @Test
    void encodesSpacesAsPlus() {
        assertEquals("hello+foo", encode("hello foo"));
        assertEquals("+hello+", encode(" hello "));
        assertEquals("hello++foo", encode("hello  foo"));
    }

    @Test
    void encodesTilde() {
        assertEquals("foo~7ebar", encode("foo~bar"));
    }

    @Test
    void encodesAsciiPunctuation() {
        assertEquals("foo.bar~2fbaz~3aqux~2b", encode("foo.bar/baz:qux+"));
    }

    @Test
    void encodesDollarSign() {
        assertEquals("Test~24hello+foo.css", encode("Test$hello foo.css"));
    }

    @Test
    void usesLowercaseHexDigits() {
        assertEquals("~2f~3a~3f~7e", encode("/:?~"));
    }

    @Test
    void encodesTwoByteUtf8Character() {
        // é = UTF-8 C3 A9
        assertEquals("caf~c3~a9", encode("café"));
    }

    @Test
    void encodesThreeByteUtf8Character() {
        // € = UTF-8 E2 82 AC
        assertEquals("~e2~82~ac", encode("€"));
    }

    @Test
    void encodesFourByteUtf8Character() {
        // 😀 = UTF-8 F0 9F 98 80
        assertEquals("~f0~9f~98~80", encode("😀"));
    }

    @Test
    void encodesMixedAsciiAndUnicode() {
        assertEquals(
            "hello+caf~c3~a9-~e2~82~ac-~f0~9f~98~80",
            encode("hello café-€-😀"));
    }

    @Test
    void rejectsUnpairedHighSurrogate() {
        String input = "foo\uD800bar";

        assertThrows(
            IllegalArgumentException.class,
            () -> encode(input));
    }

    @Test
    void rejectsHighSurrogateAtEndOfString() {
        String input = "foo\uD800";

        assertThrows(
            IllegalArgumentException.class,
            () -> encode(input));
    }

    @Test
    void rejectsUnpairedLowSurrogate() {
        String input = "foo\uDC00bar";

        assertThrows(
            IllegalArgumentException.class,
            () -> encode(input));
    }
}
