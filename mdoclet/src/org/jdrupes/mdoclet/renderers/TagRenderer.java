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

import com.sun.javadoc.Tag;

import org.jdrupes.mdoclet.MDoclet;


/**
 * An abstraction for rendering tags.
 */
public interface TagRenderer<T extends Tag> {

    /**
     * A do-nothing renderer. It just renders the tag without any processing.
     */
    TagRenderer<Tag> VERBATIM = new TagRenderer<Tag>() {
        @Override
        public void render(Tag tag, StringBuilder target, MDoclet doclet) {
            target.append(tag.name()).append(" ").append(tag.text());
        }
    };
    /**
     * A renderer that completely elides the tag.
     */
    TagRenderer<Tag> ELIDE = new TagRenderer<Tag>() {
        @Override
        public void render(Tag tag, StringBuilder target, MDoclet doclet) {
            // do nothing
        }
    };

    /**
     * Render the tag to the given target {@link StringBuilder}.
     *
     * @param tag       The tag to render.
     * @param target    The target {@link StringBuilder}.
     * @param doclet    The doclet.
     */
    void render(T tag, StringBuilder target, MDoclet doclet);

}
