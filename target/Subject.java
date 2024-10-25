import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.reflect.Array;

public class Subject {

    public class NumberTool {
        /**
         * Converts a string value to a Number.
         *
         * The method examines the value for a type qualifier at the end ('f', 'F', 'd', 'D', 'l', 'L').
         * If a qualifier is found, it attempts to create successively larger number types until it finds
         * one that can accommodate the value.
         *
         * If no type specifier is found, the method checks for a decimal point and then tries successively
         * larger number types from Integer to BigInteger, and from Float to BigDecimal. If the string starts
         * with "0x" or "-0x", it is interpreted as a hexadecimal integer. Values with leading zeros will not
         * be interpreted as octal.
         *
         * @param val the string containing a number
         * @return the Number created from the string
         */
        public Number parseNumber(String val) {
            if (val == null || val.length() == 0 || (val.length() == 1 && !Character.isDigit(val.charAt(0)))) {
                return null;
            }
            if (val.startsWith("--")) {
                return null;
            }
            if (val.startsWith("0x") || val.startsWith("-0x")) {
                return Integer.decode(val);
            }
            char lastChar = val.charAt(val.length() - 1);
            String mant;
            String dec;
            String exp;
            int decPos = val.indexOf('.');
            int expPos = val.indexOf('e') + val.indexOf('E') + 1;
            if (decPos > -1) {
                if (expPos > -1) {
                    if (expPos < decPos) {
                        throw new NumberFormatException(val);
                    }
                    dec = val.substring(decPos + 1, expPos);
                } else dec = val.substring(decPos + 1);
                mant = val.substring(0, decPos);
            } else {
                if (expPos > -1) {
                    mant = val.substring(0, expPos);
                } else mant = val;
                dec = null;
            }
            if (!Character.isDigit(lastChar)) {
                if (expPos > -1 && expPos < val.length() - 1) {
                    exp = val.substring(expPos + 1, val.length() - 1);
                } else exp = null;
                String numeric = val.substring(0, val.length() - 1);
               
                if (lastChar == 'l' || lastChar == 'L') {
                    if (dec == null && exp == null && (numeric.charAt(0) == '-' && isDigits(numeric.substring(1)) || isDigits(numeric))) {
                        BigInteger res = new BigInteger(numeric);
                        if (res == BigInteger.valueOf(res.intValue()))
                            return res.intValue();
                        if (res == BigInteger.valueOf(res.longValue()))
                            return res.longValue();
                        return res;
                    }
                    throw new NumberFormatException(val);
                } else if (lastChar == 'f' || lastChar == 'F') {
                    return Float.valueOf(numeric);
                } else if (lastChar == 'd' || lastChar == 'D') {
                    BigDecimal res = new BigDecimal(numeric);
                    if (res == BigDecimal.valueOf(res.doubleValue()))
                        return res.doubleValue();
                    return res;
                } else {
                    throw new NumberFormatException(val);
                }
            } else {
                if (expPos > -1 && expPos < val.length() - 1)
                    exp = val.substring(expPos + 1);
                else exp = null;
                if (dec == null && exp == null) {
                    // Should be int,long,bigint
                    BigInteger res = new BigInteger(val);
                    if (res == BigInteger.valueOf(res.intValue()))
                        return res.intValue();
                    if (res == BigInteger.valueOf(res.longValue()))
                        return res.longValue();
                    return res;
                } else {
                    // Should be float,double,BigDec
                    BigDecimal res = new BigDecimal(val);
                    if (res == BigDecimal.valueOf(res.floatValue()))
                        return res.floatValue();
                    if (res == BigDecimal.valueOf(res.doubleValue()))
                        return res.doubleValue();
                    return res;
                }
            }
        }

        /**
         * Checks whether the String contains only digit characters.
         *
         * Null and empty String will return false.
         *
         * @param str the String to check
         * @return true if str contains only Unicode numeric characters, false otherwise
         */
        private boolean isDigits(String str) {
            if ((str == null) || (str.length() == 0)) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Retrieves the minimum value among three int values.
         *
         * @param a the first value
         * @param b the second value
         * @param c the third value
         * @return the smallest value among the three
         */
        public int minimum(int a, int b, int c) {
            if (b < a) {
                a = b;
            }
            if (c < b) {
                a = c;
            }
            return a;
        }

        /**
         * Compares two doubles for order.
         *
         * @param lhs the first double value
         * @param rhs the second double value
         * @return -1 if lhs is less than rhs, +1 if lhs is greater than rhs,
         *         0 if lhs is equal to rhs
         */
        public int compare(double lhs, double rhs) {
            if (lhs < rhs) {
                return -1;
            }
            if (lhs > rhs) {
                return +1;
            }
           
            long lhsBits = Double.doubleToLongBits(lhs);
            long rhsBits = Double.doubleToLongBits(rhs);
            if (lhsBits == rhsBits) {
                return 0;
            }
           
            if (lhsBits < rhsBits) {
                return -1;
            } else {
                return +1;
            }
        }

        /**
         * Compares two floats for order.
         *
         * @param lhs the first float value
         * @param rhs the second float value
         * @return -1 if lhs is less than rhs, +1 if lhs is greater than rhs,
         *         0 if lhs is equal to rhs
         */
        public int compare(float lhs, float rhs) {
            if (lhs < rhs) {
                return -1;
            }
            if (lhs > rhs) {
                return +1;
            }

            int lhsBits = Float.floatToIntBits(lhs);
            int rhsBits = Float.floatToIntBits(rhs);
            if (lhsBits == rhsBits) {
                return 0;
            }

            if (lhsBits < rhsBits) {
                return -1;
            } else {
                return +1;
            }
        }

        /**
         * Swaps the values at two slots in an integer array.
         */
        public int[] swap(int[] array, int i, int j) {
            if (i < 0 || j < 0 || i >= array.length || j >= array.length)
            	throw new IllegalArgumentException();
            array[i] += array[j];
            array[j] = array[i] - array[j];
            array[i] = array[i] - array[j];
            return array;
        }

        /** Check if the range is appropriate. */
        public boolean checkRange(int from, int to) {
            return to < from;
        }

        /**
         * Reverses a portion of the given array.
         *
         * @param array the array to reverse
         * @param from  the starting index of the portion to reverse (inclusive)
         * @param to    the ending index of the portion to reverse (non-inclusive)
         * @return the reversed array, or null if the indices are invalid
         */
        public int[] reverse(int[] array, int from, int to) {
            if (!(0 <= from && from <= to && to < array.length))
            	throw new IllegalArgumentException();
            for (to = to - 1; from < to; ++from, --to)
              swap(array, from, to);
            return array;
        }
       
        /**
         * Finds the lower bound index of a given value in a sorted integer array.
         *
         * @param array the sorted integer array to search in
         * @param from the starting index of the search range (inclusive)
         * @param to the ending index of the search range (inclusive)
         * @param val the value to find the lower bound index for
         * @return the lower bound index of the value
         */
        public int lower(int[] array, int from, int to, int val) {
            if (array == null || from < 0 || to < 0 || from >= array.length || to >= array.length)
                throw new IllegalArgumentException();
            int len = to - from;
            while (len > 0) {
                int half = len >>> 1, mid = from + (len >>> 1);
                if (compare(array, mid, val) > 0) {
                    len = half;
                } else {
                    from = mid + 1;
                    len = len - half - 1;
                }
            }
            return from;
        }

        /**
         * Finds the upper bound index of a given value in a sorted integer array.
         *
         * @param array the sorted integer array to search in
         * @param from the starting index of the search range (inclusive)
         * @param to the ending index of the search range (inclusive)
         * @param val the value to find the lower bound index for
         * @return the upper bound index of the value
         */
        public int upper(int[] array, int from, int to, int val) {
            if (array == null || from < 0 || to < 0 || from >= array.length || to >= array.length)
                throw new IllegalArgumentException();
            int len = to - from;
            while (len > 0) {
                final int half = len >>> 1;
                final int mid = from + half;
                if (compare(array, val, mid) < 0) {
                    len = half;
                } else {
                    from = mid + 1;
                    len = len - half - 1;
                }
            }
            return from;
        }

        /**
         * Performs an insertion sort algorithm on a portion of an integer array.
         *
         * @param array the integer array to be sorted
         * @param off the starting index of the portion to be sorted (inclusive)
         * @param len the length of the portion to be sorted
         * @return the sorted array, or null if the input is invalid
         */
        public int[] insertionSort(int[] array, int off, int len) {
            if (array == null || off < 0 || len < 0 || off + len >= array.length)
                return null;
            for (int i = off + 1; i < off + len; ++i) {
                for (int j = i - 1; j > off; --j) {
                    if (array[j - 1] > array[j]) {
                        int tmp = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = tmp;
                    } else {
                        break;
                    }
                }
            }
            return array;
        }

        /**
         * Compares an integer array element to another integer.
         *
         * @param array the integer array containing the elements to compare
         * @param i the index of the target element
         * @param val the value to compare
         * @return -1 if the element at index i is less than val,
         *          0 if the element at index i is equal to val,
         *          1 if the element at index i is greater than val.
         */
        private int compare(int[] array, int i, int val) {
            if (array == null || i < 0 || i >= array.length)
                throw new IllegalArgumentException();
            return (array[i] < val) ? -1 : ((array[i] != val) ? 0 : 1);
        }
    }

    public class TextTool {
        /**
         * Extract the starting integer from a string.
         * Rules:
         * 1234   -> 1234
         * 1234a  -> 1234
         * 1234a1 -> 1234
         *
         * @param str a string to extract the starting integer.
         */
        public int parseStartingInt(String str) {
            if (str == null || str.length() < 1) {
                return 0;
            }
            int num = 0;
            for (int i = 0; i < str.length(); i ++) {
                char ch = str.charAt(i);
                if (ch < '0' || ch > '9') {
                    break;
                }
                num = num * 10 + (ch - '0');
            }
            return num;
        }

        /**
         * Transform version string in the form like a, a.b, or a.b.c into 4 integers.
         * Rules:
         * 4       -> 04 00 00 00
         * 4.3     -> 04 03 00 00
         * 4.3.2   -> 04 03 02 00
         *
         * @param versionString a version string to be transformed.
         */
        public int[] getVersionNums(final String versionString) {
            if (versionString == null || versionString.length() < 1) {
                return null;
            }
            int[] vernos = new int[4];
            vernos[0] = vernos[1] = vernos[2] = vernos[3] = 0;
            int index = 0, cur = 0, pos;
            String segment;
            do {
                if (index > 3) {
                    return null;
                }
                pos = versionString.indexOf('.', cur);
                if (pos == -1) {
                    segment = versionString.substring(cur);
                } else if (cur < pos) {
                    segment = versionString.substring(cur, pos);
                } else {
                    // Illegal format
                    return null;
                }
                vernos[index] = parseStartingInt(segment);
                cur = pos + 1;
                index++;
            } while (pos > 0);
            return vernos;
        }

        /**
         * Capitalize a given string.
         *
         * @param str a string to be transformed.
         */
        public String capitalizeString(String str) {
            return changeFirstCharacterCase(str, true);
        }
       
        /**
         * Gets a substring from the specified String with better compatibility,
         * i.e., "invalid" start (<0) / end (>length) indexes. If start index is
         * not to the left of end index, "" should be returned.
         * If normal, the returned substring starts with the character in `start`
         * position and ends before the `end` position.
         * The position index starts with 0.
         * </pre>
         *
         * @param str the String to get the substring from, may be null
         * @param start the position to start from
         * @param end the position to end at (exclusive)
         * @return substring from start position to end position
         */
        public String substring(final String str, int start, int end) {
            if (str == null) {
                return null;
            }

            // check length next
            if (end >= str.length()) {
                end = str.length();
            }

            if (start < 0) {
                start = 0;
            }

            if (end < 0) {
                end = 0;
            }

            // if start is greater than end, return ""
            if (start > end) {
                return "";
            }

            return str.substring(start, end);
        }
       
        /**
         * Checks if two strings are both null or having identical content.
         *
         * @param str1 a string to compare.
         * @param str2 another string to compare.
         * @return true if both strings are null or having identical content, else false.
         */
        public boolean compareStrs(String str1, String str2) {
            return str1 == null ? str2 == null : str1.equals(str2);
        }
       
        /**
         * Abbreviates a String using a given replacement marker. This will turn
         * "Now is the time for all good men" into "...is the time for..." if "..." was
         * defined as the replacement marker.
         *
         * This API works like `abbreviate(String, String, int)`, but allows specifying
         * a "left edge" offset.
         * Note that this left edge is not necessarily going to be the leftmost character
         * in the result, or the first character following the replacement marker, but it
         * will appear somewhere in the result.
         *
         * Examples:
         * abbreviate(null, null, *, *)                 = null
         * abbreviate("abcdefghijklmno", null, *, *)    = "abcdefghijklmno"
         * abbreviate("", "...", 0, 4)                  = ""
         * abbreviate("abcdefghijklmno", "---", -1, 10) = "abcdefg---"
         * abbreviate("abcdefghijklmno", ",", 0, 10)    = "abcdefghi,"
         * abbreviate("abcdefghijklmno", ",", 1, 10)    = "abcdefghi,"
         * abbreviate("abcdefghijklmno", ",", 2, 10)    = "abcdefghi,"
         * abbreviate("abcdefghijklmno", "::", 4, 10)   = "::efghij::"
         * abbreviate("abcdefghijklmno", "...", 6, 10)  = "...ghij..."
         * abbreviate("abcdefghijklmno", "*", 9, 10)    = "*ghijklmno"
         * abbreviate("abcdefghijklmno", "'", 10, 10)   = "'ghijklmno"
         * abbreviate("abcdefghijklmno", "!", 12, 10)   = "!ghijklmno"
         * abbreviate("abcdefghij", "abra", 0, 4)       = IllegalArgumentException
         * abbreviate("abcdefghij", "...", 5, 6)        = IllegalArgumentException
         *
         * @param str          the String to check, may be null
         * @param abbrevMarker the String used as replacement marker
         * @param offset       left edge of source String
         * @param maxWidth     maximum length of result String, must be at least 4
         * @return abbreviated String, null if null String input
         * @throws IllegalArgumentException if the width is too small
         */
        public String abbreviate(final String str, final String abbrevMarker, int offset, final int maxWidth) {
            if (str != null && "".equals(abbrevMarker) && maxWidth > 0) {
                return substring(str, 0, maxWidth);
            } else if (str == null || str.length() == 0 || abbrevMarker == null || abbrevMarker.length() == 0) {
                return str;
            }
            final int abbrevMarkerLength = abbrevMarker.length();
            final int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;

            if (maxWidth < minAbbrevWidthOffset) {
                throw new IllegalArgumentException();
            }
            final int strLen = str.length();
            if (offset > strLen) {
                offset = strLen;
            }
            if (strLen <= maxWidth) {
                return str;
            }
            if (strLen - offset < maxWidth - abbrevMarkerLength) {
                offset = strLen - (maxWidth - abbrevMarkerLength);
            }
            if (offset <= abbrevMarkerLength + 1) {
                return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
            }
            if (offset + maxWidth - abbrevMarkerLength < strLen) {
                return abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, 0, maxWidth - abbrevMarkerLength);
            }
            return abbrevMarker + str.substring(strLen - (maxWidth - abbrevMarkerLength));
        }
       
        /**
         * Change the case of the first char of a given string.
         *
         * @param str a string to be transformed.
         * @param cap true if the first char should be capitalized, false otherwise.
         */
        private String changeFirstCharacterCase(String str, boolean cap) {
            if (str == null || str.length() == 0) {
                return str;
            }
            StringBuilder buf = new StringBuilder(str.length());
            if (cap) {
                buf.append(Character.toUpperCase(str.charAt(0)));
            } else {
                buf.append(Character.toLowerCase(str.charAt(0)));
            }
            buf.append(str.substring(1));
            return buf.toString();
        }
       
        /**
         * Green implementation of regionMatches.
         *
         * @param cs         the CharSequence to be processed
         * @param ignoreCase whether or not to be case insensitive
         * @param thisStart  the index to start on the cs CharSequence
         * @param substring  the CharSequence to be looked for
         * @param start      the index to start on the substring CharSequence
         * @param length     character length of the region
         * @return whether the region matched
         */
        public boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                final CharSequence substring, final int start, final int length) {
            if (cs instanceof String && substring instanceof String) {
                return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
            }
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            // Extract these first so we detect NPEs the same as the java.lang.String version
            final int srcLen = cs.length() - thisStart;
            final int otherLen = substring.length() - start;

            // Check for invalid parameters
            if (thisStart < 0 || start < 0 || length < 0) {
                return false;
            }

            // Check that the regions are long enough
            if (srcLen < length || otherLen < length) {
                return false;
            }

            while (tmpLen-- > 0) {
                final char c1 = cs.charAt(index1++);
                final char c2 = substring.charAt(index2++);
                if (c1 == c2) {
                    continue;
                }
                if (!ignoreCase) {
                    return false;
                }
                // The real same check as in String.regionMatches():
                final char u1 = Character.toUpperCase(c1);
                final char u2 = Character.toUpperCase(c2);
                if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Creates a new TextBuffer with more than one slot and allocates a char[] with the specified bufferSize.
         *
         * @param bufferSize the size of the buffer.
         */
        public char[] newTextBuffer(final int bufferSize) {
            if (bufferSize >= 1)
                return new char[bufferSize];
            return null;
        }
   
        /**
         * Converts each Unicode code point, starting at the given offset, to lowercase.
         */
        public char[] toLowerCase(final char[] buffer, final int offset, final int limit) {
            if (buffer == null || !(buffer.length >= limit && 0 <= offset && offset <= buffer.length)) {
                return null;
            }
            for (int i = offset; i < limit; ) {
                i += Character.toChars(
                        Character.toLowerCase(Character.codePointAt(buffer, i, limit)), buffer, i);
            }
            return buffer;
        }
       
        /**
         * Returns an uppercase hexadecimal string representation of the given character.
         *
         * @param ch the character to convert.
         */
        public String hex(char ch) {
            return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
        }

        /**
         * Reverses the process of escaping Java literals in a string.
         * For example, it converts a sequence of '\\' and 'n' back
         * to a newline character, unless the '\\' is preceded by another '\\'.
         *
         * @param str the string to unescape.
         */
        public String unescapeJava(String str) {
            if (str == null) {
                return null;
            }
            String res = "";
            int sz = str.length();
            StringBuilder unicode = new StringBuilder();
            boolean hadSlash = false;
            boolean inUnicode = false;

            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (inUnicode) {
                    unicode.append(ch);
                    if (unicode.length() == 4) {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        res += ((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    }
                    continue;
                }
                if (hadSlash) {
                    // for escaped value
                    hadSlash = false;
                    if (ch == '\\') {
                        res += ('\\');
                    } else if (ch == '\'') {
                        res += ('\'');
                    } else if (ch == '\"') {
                        res += ('"');
                    } else if (ch == 'r') {
                        res += ('\r');
                    } else if (ch == 'f') {
                        res += ('\f');
                    } else if (ch == 't') {
                        res += ('\t');
                    } else if (ch == 'n') {
                        res += ('\n');
                    } else if (ch == 'b') {
                        res += ('\b');
                    } else if (ch == 'u') {
                        inUnicode = true;
                    } else {
                        res += (ch);
                    }
                    continue;
                } else if (ch == '\\') {
                    hadSlash = true;
                    continue;
                }
                res += (ch);
            }
            if (hadSlash) {
                res += ('\\');
            }
            return res;
        }
    }
   
    public class ParameterTool {
       
        /** The string to be parsed. */
        private char[] chars = null;
        /** The current position in the string. */
        private int pos = 0;
        /** The maximum position in the string. */
        private int len = 0;
        /** The start of a token. */
        private int i1 = 0;
        /** The end of a token. */
        private int i2 = 0;

        /** Indicates whether names stored in the map should be converted to lower case. */
        private boolean lowerCaseNames = false;

        /**
         * Checks if there are any unparsed characters remaining.
         *
         * @return true if there are unparsed characters, false otherwise
         */
        private boolean hasChar() {
            return this.pos < this.len;
        }

        /**
         * Processes the parsed token by removing leading and trailing blanks, as well as enclosing quotation marks if necessary.
         *
         * @param quoted indicates whether quotation marks are expected
         * @return the processed token
         */
        private String getToken(boolean quoted) {
            // Remove the leading white spaces
            while ((i1 < i2) && (Character.isWhitespace(chars[i1]))) {
                i1++;
            }
            // Remove the trailing white spaces
            while ((i2 > i1) && (Character.isWhitespace(chars[i2 - 1]))) {
                i2--;
            }
            // Remove quotation marks if necessary
            if (quoted && ((i2 - i1) >= 2) && (chars[i1] == '"') && (chars[i2 - 1] == '"')) {
                i1++;
                i2--;
            }
            String result = null;
            if (i2 > i1) {
                result = new String(chars, i1, i2 - 1);
            }
            return result;
        }

        /**
         * Determines whether the specified character is found within the provided array of characters.
         *
         * @param ch the character to check for presence in the array
         * @param charray the array of characters to search within
         * @return true if the character is found in the array, otherwise false
         */
        private boolean isOneOf(char ch, final char[] charray) {
            boolean result = false;
            for (char element : charray) {
                if (ch == element) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        /**
         * Parses a token until any of the specified terminator characters is encountered.
         *
         * @param terminators the array of terminating characters
         * @return the parsed token
         */
        private String parseToken(final char[] terminators) {
            char ch;
            i1 = pos;
            i2 = pos;
            while (hasChar()) {
                ch = chars[pos];
                if (isOneOf(ch, terminators)) {
                    break;
                }
                i2++;
                pos++;
            }
            return getToken(false);
        }

        /**
         * Parses a token until any of the specified terminator characters is encountered outside the quotation marks.
         *
         * @param terminators the array of terminating characters
         * @return the parsed token
         */
        private String parseQuotedToken(final char[] terminators) {
            char ch;
            i1 = pos;
            i2 = pos;
            boolean quoted = false;
            boolean charEscaped = false;
            while (hasChar()) {
                ch = chars[pos];
                if (!quoted && isOneOf(ch, terminators)) {
                    break;
                }
                if (!charEscaped && ch == '"') {
                    quoted = !quoted;
                }
                charEscaped = (!charEscaped && ch == '\\');
                i2++;
                pos++;
            }
            return getToken(true);
        }

        /**
         * Parses a string to extract a map of name/value pairs.
         *
         * @param str the string containing a sequence of name/value pairs
         * @param separators the separators used to separate the name/value pairs
         * @return a map of name/value pairs
         */
        public Map<String, String> parse(final String str, char[] separators) {
            if (separators == null || separators.length == 0) {
                return new HashMap<>();
            }
            char separator = separators[0];
            if (str != null) {
                int idx = str.length();
                for (char separator2 : separators) {
                    int tmp = str.indexOf(separator2);
                    if (tmp != -1 && tmp < idx) {
                        idx = tmp;
                        separator = separator2;
                    }
                }
            }
            return parse(str, separator);
        }

        /**
         * Parses a string to extract a map of name/value pairs.
         *
         * @param str the string containing a sequence of name/value pairs
         * @param separator the separator used to separate the name/value pairs
         * @return a map of name/value pairs
         */
        public Map<String, String> parse(final String str, char separator) {
            if (str == null) {
                return new HashMap<>();
            }
            return parse(str.toCharArray(), separator);
        }

        /**
         * Parses a string to extract a map of name/value pairs.
         *
         * @param charArray an array of characters that includes a sequence of name/value pairs
         * @param separator the separator used to differentiate the name/value pairs
         * @return a map of name/value pairs
         */
        private Map<String, String> parse(final char[] charArray, char separator) {
            if (charArray == null) {
                return new HashMap<>();
            }
            return parse(charArray, 1, charArray.length, separator);
        }

        /**
         * Parses a string to extract a map of name/value pairs.
         *
         * @param charArray the array of characters containing a sequence of name/value pairs
         * @param offset the initial offset
         * @param length the length of the portion to be parsed
         * @param separator the separator used to separate the name/value pairs
         * @return a map of name/value pairs
         */
        public Map<String, String> parse(final char[] charArray, int offset, int length, char separator) {
            if (charArray == null) {
                return new HashMap<>();
            }
            HashMap<String, String> params = new HashMap<>();
            this.chars = charArray;
            this.pos = offset;
            this.len = length > charArray.length ? charArray.length : length;
            String paramName = null;
            String paramValue = null;
            while (hasChar()) {
                char[] terminators = new char[2];
                terminators[0] = '=';
                terminators[1] = separator;
                paramName = parseToken(terminators);
                
                paramValue = null;
                if (hasChar() && (charArray[pos] == '=')) {
                    pos++; // skip '='
                    
                    char[] terminatorsQuoted = new char[1];
                    terminators[0] = separator;
                    paramValue = parseQuotedToken(terminatorsQuoted);
                }
                if (hasChar() && (charArray[pos] == separator)) {
                    pos++; // skip separator
                }
                if ((paramName != null) && (paramName.length() > 0)) {
                    if (this.lowerCaseNames) {
                        paramName = paramName.toLowerCase(Locale.ENGLISH);
                    }
                    params.put(paramName, paramValue);
                }
            }
            return params;
        }
       
        /**
         * Converts an integer value to a Boolean object based on specified true, false, and null values.
         *
         * @param value      the integer value to convert
         * @param trueValue  the value representing true
         * @param falseValue the value representing false
         * @param nullValue  the value representing null
         * @return the corresponding Boolean object or null if the value matches nullValue
         * @throws IllegalArgumentException if the value does not match any of the specified values
         */
        public Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
            if (value == trueValue) {
                return Boolean.TRUE;
            } else if (value == falseValue) {
                return Boolean.FALSE;
            } else if (value == nullValue) {
                return null;
            }
            throw new IllegalArgumentException();
        }

        /**
         * Converts a string to a Boolean object. Supported: yes/no, on/off, true/false.
         *
         * @param str the string to convert
         * @return the corresponding Boolean object or null if the string does not represent a valid boolean value
         */
        public Boolean toBooleanObject(String str) {
            if (str == null) {
                return null;
            }
            if ("true".equals(str)) {
                return Boolean.TRUE;
            }
            if (str.length() == 1) {
                char ch0 = str.charAt(0);
                if ((ch0 == 'y' || ch0 == 'Y') || (ch0 == 't' || ch0 == 'T')) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'n' || ch0 == 'N') || (ch0 == 'f' || ch0 == 'F')) {
                    return Boolean.FALSE;
                }
            }
            else if (str.length() == 2) {
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N')) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'n' || ch0 == 'N') && (ch1 == 'o' || ch1 == 'O')) {
                    return Boolean.FALSE;
                }
            }
            else if (str.length() == 3) {
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char ch2 = str.charAt(2);
                if ((ch0 == 'y' || ch0 == 'Y') && (ch1 == 'e' || ch1 == 'E') && (ch2 == 's' || ch2 == 'S')) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'f' || ch1 == 'F') && (ch2 == 'f' || ch2 == 'F')) {
                    return Boolean.FALSE;
                }
            }
            else if (str.length() == 4) {
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char ch2 = str.charAt(2);
                char ch3 = str.charAt(3);
                if ((ch0 == 't' || ch0 == 'T') && (ch1 == 'r' || ch1 == 'R') && (ch2 == 'u' || ch2 == 'U') && (ch3 == 'e' || ch3 == 'E')) {
                    return Boolean.TRUE;
                }
            }
            else if (str.length() == 5) {
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char ch2 = str.charAt(2);
                char ch3 = str.charAt(3);
                char ch4 = str.charAt(4);
                if ((ch0 == 'f' || ch0 == 'F') && (ch1 == 'a' || ch1 == 'A') && (ch2 == 'l' || ch2 == 'L') && (ch3 == 's' || ch3 == 'S') && (ch4 == 'e' || ch4 == 'E')) {
                    return Boolean.FALSE;
                }
            }
            return null;
        }

        /**
         * Converts a string to a Boolean object based on specified true, false, and null strings.
         *
         * @param str         the string to convert
         * @param trueString  the string representing true
         * @param falseString the string representing false
         * @param nullString  the string representing null
         * @return the corresponding Boolean object or null if the string matches nullString
         * @throws IllegalArgumentException if the string does not match any of the specified strings
         */
        public Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
            if (str == null) {
                if (trueString == null) {
                    return Boolean.TRUE;
                } else if (falseString == null) {
                    return Boolean.FALSE;
                } else if (nullString != null) {
                    return null;
                }
            } else if (str.equals(trueString)) {
                return Boolean.TRUE;
            } else if (str.equals(falseString)) {
                return Boolean.FALSE;
            } else if (str.equals(nullString)) {
                return null;
            }
            throw new IllegalArgumentException();
        }

        /**
         * Checks if an array of primitive booleans is not empty and not null.
         *
         * @param array the array to check
         * @return true if the array is not empty and not null.
         */
        public boolean isNotEmpty(final boolean[] array) {
            return !isEmpty(array);
        }

        /**
         * Checks if an array of primitive integers is not empty and not null.
         *
         * @param array the array to check
         * @return true if the array is not empty and not null.
         */
        public boolean isNotEmpty(final int[] array) {
            return !isEmpty(array);
        }

        /**
         * Checks if an array of primitive chars is not empty and not null.
         *
         * @param array the array to check
         * @return true if the array is not empty and not null.
         */
        public boolean isNotEmpty(final char[] array) {
            return !isEmpty(array);
        }

        /**
         * Checks if an array of primitive chars is not empty and not null and not all whitespace.
         *
         * @param array the array to check
         * @return true if the array is not empty and not null and not all whitespace.
         */
        public boolean isNotBlank(final char[] array) {
            return !isEmpty(array);
        }

        /**
         * Checks if an array of Objects is not empty and not null.
         *
         * @param array the array to check
         * @return true if the array is not empty and not null.
         */
        public <T> boolean isNotEmpty(final T[] array) {
            return !isEmpty(array);
        }
       
        /**
         * Returns the length of a given array.
         *
         * @param array the array to retrieve the length from
         * @return The length of the array, or 0 if the array is null
         * @throws IllegalArgumentException if the object argument is not an array.
         */
        public int getLength(final Object array) {
            if (array != null) {
                return Array.getLength(array);
            }
            else{
                return 0;
            }
        }

        /**
         * Checks if an array of primitive booleans is empty or null.
         *
         * @param array the array to check
         * @return true if the array is empty or null.
         */
        public boolean isEmpty(final boolean[] array) {
            return getLength(array) == 0;
        }

        /**
         * Checks if an array of primitive integers is empty or null.
         *
         * @param array the array to check
         * @return true if the array is empty or null.
         */
        public boolean isEmpty(final int[] array) {
            return getLength(array) == 0;
        }

        /**
         * Checks if an array of primitive chars is empty or null.
         *
         * @param array the array to check
         * @return true if the array is empty or null.
         */
        public boolean isEmpty(final char[] array) {
            return getLength(array) == 0;
        }

        /**
         * Checks if an array of Objects is empty or null.
         *
         * @param array the array to check
         * @return true if the array is empty or null.
         */
        public boolean isEmpty(final Object[] array) {
            return getLength(array) == 0;
        }

        /**
         * Checks if an array of primitive chars is empty, null or only with whitespaces.
         *
         * @param array the array to check
         * @return true if the array is empty or null.
         */
        public boolean isBlank(final char[] array) {
            final int strLen = getLength(array);
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(array[i])) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Determines if the given boolean array has one element with a value of {@code true}.
         *
         * @param array the array of boolean values
         * @return true if there is exactly one true value in the array, false otherwise
         */
        public boolean hasOneTrue(boolean[] array) {
            if (array == null) {
                return false;
            }
            int trueCount = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i]) {
                    if (trueCount >= 1) {
                        return false;
                    }
                    trueCount++;
                }
            }
            return trueCount > 0;
        }
    }

}

