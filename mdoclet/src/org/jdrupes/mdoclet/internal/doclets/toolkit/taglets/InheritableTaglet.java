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

import java.util.List;

import javax.lang.model.element.Element;

import org.jdrupes.mdoclet.internal.doclets.toolkit.BaseConfiguration;

import com.sun.source.doctree.DocTree;

/**
 * A taglet should implement this interface if it supports an {@code {@inheritDoc}}
 * tag or is automatically inherited if it is missing.
 */
public interface InheritableTaglet extends Taglet {

    /*
     * Called by InheritDocTaglet on an inheritable taglet to expand
     * {@inheritDoc}
     * found inside a tag corresponding to that taglet.
     *
     * When inheriting failed some assumption, or caused an error, the taglet
     * can return either of:
     *
     * - new Output(null, null, List.of(), false)
     * - new Output(null, null, List.of(), true)
     *
     * In the future, this could be reworked using some other mechanism,
     * such as throwing an exception.
     */
    Output inherit(Element owner, DocTree tag, boolean isFirstSentence,
            BaseConfiguration configuration);

    record Output(DocTree holderTag,
            Element holder,
            List<? extends DocTree> inlineTags,
            boolean isValidInheritDocTag) {
    }
}
