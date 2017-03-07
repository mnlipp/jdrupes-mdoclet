/*
 * JDrupes MDoclet
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

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdrupes.mdoclet.MDoclet;


/**
 * Renders a tag by invocing the taglet's `toString` method.
 */
public class TagletTagRenderer implements TagRenderer<Tag> {

	private Taglet taglet;
	private Set<Doc> handledDocs = new HashSet<>();

	/**
	 * Create a new renderer for the given taglet.
	 * 
	 * @param taglet the taglet
	 */
    public TagletTagRenderer(Taglet taglet) {
		this.taglet = taglet;
	}

	@Override
    public void render(Tag tag, StringBuilder target, MDoclet doclet) {
		Doc doc = tag.holder();
		if (handledDocs.contains(doc)) {
			return;
		}
		handledDocs.add(doc);
		List<Tag> tags = new ArrayList<>();
		for (Tag t: doc.tags()) {
			if (t.name().equals(tag.name())) {
				tags.add(t);
			}
		}
        target.append(taglet.toString(tags.toArray(new Tag[tags.size()])));
    }

}
