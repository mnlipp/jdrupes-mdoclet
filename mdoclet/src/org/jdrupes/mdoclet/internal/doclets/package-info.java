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

/**
 * Doclets provide the user-selectable back ends for processing the
 * documentation comments in Java source code.
 *
 * <p>Doclets are implementations of the {@link jdk.javadoc.doclet Doclet API}.</p>
 *
 * <p>Currently, there is only one supported doclet, the
 * {@link org.jdrupes.mdoclet.internal.doclets.formats.html.HtmlDoclet HtmlDoclet},
 * for writing API documentation in HTML. Nevertheless, in order to
 * separate the high-level code for the general content of each page
 * from the low-level details of how to write such content, the code is
 * organized in two sections: a format-neutral
 * {@link org.jdrupes.mdoclet.internal.doclets.toolkit toolkit API},
 * and a specific {@link org.jdrupes.mdoclet.internal.doclets.formats format},
 * such as {@link org.jdrupes.mdoclet.internal.doclets.formats.html HTML format}.
 */
package org.jdrupes.mdoclet.internal.doclets;
