/*
 * JDrupes MDoclet
 * Copyright 2013 Raffael Herzog
 * Copyright (C) 2017 Michael N. Lipp
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.jdrupes.mdoclet.renderers;

/**
 * Utilities for tag rendering.
 */
public final class TagRendering {

    private TagRendering() {
    }

    /**
     * Removes the `<p>` tag if the given HTML contains only one paragraph.
     *
     * **Note:** This implementation may be a bit simplistic: If the HTML starts with a
     * `<p>` tag, it will be removed. If it ends with `</p>`, this one will be removed,
     * too. In 99% of the cases, this *exactly* what we wanted. It some special cases,
     * the result may be invalid HTML -- it should never break things, however, because
     * HTML is designed to handle "forgotten" tags gracefully.
     *
     * @return The HTML without leading `<p>` or trailing `</p>`.
     */
    public static String simplifySingleParagraph(String html) {
        html = html.trim();
        String upper = html.toUpperCase();
        if ( upper.startsWith("<P>") ) {
            html = html.substring(3);
        }
        if ( upper.endsWith("</P>") ) {
            html = html.substring(0, html.length() - 4);
        }
        return html;
    }

}
