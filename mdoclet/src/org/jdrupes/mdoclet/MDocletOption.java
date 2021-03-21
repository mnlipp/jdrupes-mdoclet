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
public abstract class MDocletOption implements Doclet.Option {

    private String name;
    private int argCount;

    public MDocletOption(String name, int argCount) {
        super();
        this.name = name;
        this.argCount = argCount;
    }

    @Override
    public int getArgumentCount() {
        return argCount;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return List.of((name.length() == 1 ? "-" : "--") + name);
    }

    @Override
    public String getParameters() {
        return "<>";
    }

}
