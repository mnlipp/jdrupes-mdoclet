/*
 * Copyright (c) 2003, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.jdrupes.mdoclet.internal.doclets.toolkit.taglets;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import org.jdrupes.mdoclet.internal.doclets.toolkit.Content;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LiteralTree;
import jdk.javadoc.doclet.Taglet.Location;

/**
 * An inline taglet used to denote literal code fragments.
 * The enclosed text is interpreted as not containing HTML markup or
 * nested javadoc tags, and is rendered in a font suitable for code.
 *
 * <p> The tag {@code {@code ...}} is equivalent to
 * {@code <code>{@literal ...}</code>}.
 * For example, the text:
 * <blockquote>  The type {@code {@code List<P>}}  </blockquote>
 * displays as:
 * <blockquote>  The type {@code List<P>}  </blockquote>
 */
public class CodeTaglet extends BaseTaglet {

    CodeTaglet() {
        super(DocTree.Kind.CODE, true, EnumSet.allOf(Location.class));
    }

    @Override
    public Content getInlineTagOutput(Element element, DocTree tag,
            TagletWriter writer) {
        return writer.codeTagOutput(element, (LiteralTree) tag);
    }
}
