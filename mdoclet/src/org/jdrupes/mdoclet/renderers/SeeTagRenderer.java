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

import static org.jdrupes.mdoclet.renderers.TagRendering.simplifySingleParagraph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdrupes.mdoclet.MDoclet;

import com.sun.javadoc.SeeTag;


/**
 * Renderer for `@see` tags.
 */
public class SeeTagRenderer implements TagRenderer<SeeTag> {

    public static final SeeTagRenderer INSTANCE = new SeeTagRenderer();

    private static final Pattern SIMPLE_LINK 
    	= Pattern.compile("(?<label>[^<]*)<(?<url>[^>]+)>");

    @Override
    public void render(SeeTag tag, StringBuilder target, MDoclet doclet) {
        if ( tag.text().startsWith("\"") && tag.text().endsWith("\"") 
        		&& tag.text().length() > 1 ) {
        	String text = tag.text().substring(1, tag.text().length() - 1).trim();
	        Matcher matcher = SIMPLE_LINK.matcher(text);
	        if (matcher.matches()) {
	            String label = matcher.group("label");
	            if ( label == null || label.isEmpty()) {
	                label = matcher.group("url");
	            } else {
	                label = label.trim();
	            }
	        	text = "[" + label + "](" + matcher.group("url") + ")";
	        }
	        target.append(tag.name()).append(" ")
	        	.append(simplifySingleParagraph(doclet.toHtml(text)));
	        return;
        }
        VERBATIM.render(tag, target, doclet);
    }
}
