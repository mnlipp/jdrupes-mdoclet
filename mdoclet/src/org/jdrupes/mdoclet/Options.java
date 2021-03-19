/*
 * JDrupes MDoclet
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

import java.util.List;

import jdk.javadoc.doclet.Doclet;

/**
 * Defines the command line options.
 */
public enum Options implements Doclet.Option {

    MARKDOWN_PROCESSOR("markdown-processor", 1),
    DISABLE_HIGHLIGHT("disable-highlight", 0),
    DISABLE_AUTO_HIGHLIGHT("disable-auto-highlight", 0),
    HIGHLIGHT_STYLE("highlight-style", 1);

    private final String name;
    private final int args;

    Options(String name, int args) {
        this.name = name;
        this.args = args;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#getArgumentCount()
     */
    @Override
    public int getArgumentCount() {
        return args;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#getDescription()
     */
    @Override
    public String getDescription() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#getKind()
     */
    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#getNames()
     */
    @Override
    public List<String> getNames() {
        return List.of("--" + name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#getParameters()
     */
    @Override
    public String getParameters() {
        return "<>";
    }

    /*
     * (non-Javadoc)
     * 
     * @see jdk.javadoc.doclet.Doclet.Option#process(java.lang.String,
     * java.util.List)
     */
    @Override
    public boolean process(String arg0, List<String> arg1) {
        return true;
    }

}