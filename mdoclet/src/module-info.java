/*
 * Copyright (c) 2014, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
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
 * @moduleGraph
 */
module org.jdrupes.mdoclet {
    requires java.xml;

    requires transitive java.compiler;
    requires transitive jdk.compiler;
    requires transitive jdk.javadoc;
    requires jdk.internal.opt;

    requires flexmark;
    requires flexmark.ext.abbreviation;
    requires flexmark.ext.anchorlink;
    requires flexmark.ext.definition;
    requires flexmark.ext.footnotes;
    requires flexmark.ext.tables;
    requires flexmark.ext.toc;
    requires flexmark.ext.typographic;
    requires flexmark.ext.wikilink;
    requires flexmark.util;
    requires flexmark.util.ast;
    requires flexmark.util.builder;
    requires flexmark.util.data;
    requires flexmark.util.html;
    requires flexmark.util.misc;
    requires flexmark.util.sequence;
    requires org.jsoup;
    requires org.jetbrains.annotations;

    exports org.jdrupes.mdoclet;
    exports org.jdrupes.mdoclet.internal.tool;
}
