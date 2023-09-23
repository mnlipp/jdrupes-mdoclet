/*
 * JDrupes MDoclet
 * Copyright (C) 2021 Michael N. Lipp
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

import java.util.Locale;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.WriterFactoryImpl;
import jdk.javadoc.internal.doclets.toolkit.WriterFactory;
import jdk.javadoc.internal.doclets.toolkit.builders.BuilderFactory;

/**
 * Required for using a different writerFactory (cannot be changed
 * for {@link HtmlConfiguration}) and to enable access to some
 * attributes. 
 */
public class MarkdownConfiguration extends HtmlConfiguration {

    public MarkdownConfiguration(Doclet doclet, Locale locale,
            Reporter reporter) {
        super(doclet, locale, reporter);
        builderFactory = new BuilderFactory(this);
    }

    @Override
    public WriterFactory getWriterFactory() {
        return new WriterFactoryImpl(this);
    }

}
