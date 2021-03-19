/*
 * JDrupes MDoclet
 * Copyright 2013 Raffael Herzog
 * Copyright (C) 2017, 2021 Michael N. Lipp
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along 
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.jdrupes.mdoclet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing some helpers for working with tags.
 */
public final class Tags {

    /**
     * The name of text tags in the resulting array of
     * {@link com.sun.javadoc.Doc#inlineTags() Doc.inlineTags()}.
     */
    public static final String TEXT_TAG_NAME = "Text";

    private static final Pattern TAG_RE = Pattern.compile("\\{@[^\\}]*\\}");
    private static final Pattern SUBST_RE = Pattern.compile("\\{@\\}");

    private static Map<String, String> KINDS = new HashMap<>();

    static {
        KINDS.put("@exception", "@throws");
        KINDS.put("@link", "@see");
        KINDS.put("@linkplain", "@see");
        KINDS.put("@serialData", "@serial");
    }

    private Tags() {
    }

    /**
     * Gets the {@link com.sun.javadoc.Tag#kind() kind} of the tag with the given name.
     * Returns the original name if the tag is unknown.
     *
     * @param name    The tag's name.
     *
     * @return The kind of the tag.
     *
     * @see com.sun.javadoc.Tag#kind()
     */
    public static String kindOf(String name) {
        String kind = KINDS.get(name);
        return kind == null ? name : kind;
    }

    /**
     * Extracts all inline tags from the given comment and saves them in a target list.
     *
     * **Example**
     *
     * ```
     * /**
     *  * Foo {{@literal @}link bar} bar.
     *  *{@literal /}
     * ```
     *
     * becomes
     *
     * ```
     * /**
     *  * Foo {{@literal @}} bar.
     *  *{@literal /}
     * ```
     *
     * and the target list contains the string `"{{@literal @}link bar}"`.
     *
     * @param comment    The comment to extract the inline tags from.
     * @param target     The target list to save the extracted inline tags to.
     *
     * @return The comment with all inline tags replaced with `"{{@literal @}}"`.
     *
     * @see #insertInlineTags(String, java.util.List)
     */
    public static String extractInlineTags(String comment,
            List<String> target) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = TAG_RE.matcher(comment);
        while (matcher.find()) {
            target.add(matcher.group());
            matcher.appendReplacement(result, "{@}");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Re-inserts all inline tags previously extracted using
     * {@link #extractInlineTags(String, java.util.List) extractInlineTags()}. All
     * occurrences of `"{{@literal @}}"` will be replaced by the string at the
     * corresponding index of the list.
     *
     * @param comment    The comment text.
     * @param tags       The list of saved inline tags.
     *
     * @return The String with all inline tags re-inserted.
     *
     * @see #extractInlineTags(String, java.util.List)
     */
    public static String insertInlineTags(String comment, List<String> tags) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = SUBST_RE.matcher(comment);
        int index = 0;
        while (matcher.find()) {
            String tag;
            if (index < tags.size()) {
                tag = tags.get(index++);
            } else {
                tag = "{@}";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(tag));
        }
        matcher.appendTail(result);
        return result.toString();
    }

}
