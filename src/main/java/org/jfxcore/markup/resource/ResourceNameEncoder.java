// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

final class ResourceNameEncoder {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private ResourceNameEncoder() {}

    static String encode(String s) {
        int n = s.length();
        int encodedLength = n;
        boolean changed = false;

        // First pass: determine exact encoded length.
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (isSafe(c)) {
                continue;
            }

            changed = true;

            if (c == ' ') {
                // ' ' -> '+': same length
                continue;
            }

            if (c < 0x80) {
                // ASCII byte -> ~hh
                encodedLength += 2; // 1 char becomes 3
            } else if (c < 0x800) {
                // 2 UTF-8 bytes -> ~hh~hh
                encodedLength += 5; // 1 char becomes 6
            } else if (Character.isHighSurrogate(c)) {
                if (i + 1 >= n || !Character.isLowSurrogate(s.charAt(i + 1))) {
                    throw new IllegalArgumentException("Unpaired high surrogate at " + i);
                }

                // Surrogate pair: 2 UTF-16 chars -> 4 UTF-8 bytes -> 12 chars
                encodedLength += 10;
                i++;
            } else if (Character.isLowSurrogate(c)) {
                throw new IllegalArgumentException("Unpaired low surrogate at " + i);
            } else {
                // 3 UTF-8 bytes -> 9 chars
                encodedLength += 8; // 1 char becomes 9
            }
        }

        if (!changed) {
            return s;
        }

        char[] out = new char[encodedLength];
        int p = 0;

        // Second pass: encode directly into the final-sized buffer.
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            if (isSafe(c)) {
                out[p++] = c;
            } else if (c == ' ') {
                out[p++] = '+';
            } else if (c < 0x80) {
                p = appendByte(out, p, c);
            } else if (c < 0x800) {
                p = appendByte(out, p, 0xC0 | (c >>> 6));
                p = appendByte(out, p, 0x80 | (c & 0x3F));
            } else if (Character.isHighSurrogate(c)) {
                int cp = Character.toCodePoint(c, s.charAt(++i));
                p = appendByte(out, p, 0xF0 | (cp >>> 18));
                p = appendByte(out, p, 0x80 | ((cp >>> 12) & 0x3F));
                p = appendByte(out, p, 0x80 | ((cp >>> 6) & 0x3F));
                p = appendByte(out, p, 0x80 | (cp & 0x3F));
            } else {
                p = appendByte(out, p, 0xE0 | (c >>> 12));
                p = appendByte(out, p, 0x80 | ((c >>> 6) & 0x3F));
                p = appendByte(out, p, 0x80 | (c & 0x3F));
            }
        }

        return new String(out);
    }

    private static boolean isSafe(char c) {
        return (c >= 'A' && c <= 'Z')
            || (c >= 'a' && c <= 'z')
            || (c >= '0' && c <= '9')
            || c == '_'
            || c == '-'
            || c == '.';
    }

    private static int appendByte(char[] out, int p, int b) {
        out[p++] = '~';
        out[p++] = HEX[(b >>> 4) & 0xF];
        out[p++] = HEX[b & 0xF];
        return p;
    }
}
