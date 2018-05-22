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

import com.sun.javadoc.ThrowsTag;

import org.jdrupes.mdoclet.MDoclet;

import static org.jdrupes.mdoclet.renderers.TagRendering.*;


/**
 * Renderer for `@throws` and `@exception` tags.
 */
public class ThrowsTagRenderer implements TagRenderer<ThrowsTag> {

    public static final ThrowsTagRenderer INSTANCE = new ThrowsTagRenderer();

    @Override
    public void render(ThrowsTag tag, StringBuilder target, MDoclet doclet) {
        target.append(tag.name())
                .append(' ').append(tag.exceptionName())
                .append(' ').append(simplifySingleParagraph(doclet.toHtml(tag.exceptionComment())));
    }
}