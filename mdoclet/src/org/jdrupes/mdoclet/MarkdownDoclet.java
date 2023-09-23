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
import java.util.SortedSet;

import javax.lang.model.element.TypeElement;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.internal.doclets.formats.html.HtmlDoclet;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.builders.BuilderFactory;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;

/**
 * Ridiculously, there's no way to change the configuration
 * used by {@link HtmlDoclet}. So we have to override the method
 * that obtains the builder factory from it.
 */
public class MarkdownDoclet extends HtmlDoclet {

    protected Doclet initiatingDoclet;
    // Note that this is actually a redundant duplicate of the
    // HtmlConfiguration used by HtmlDoclet because we cannot access
    // the latter.
    protected MarkdownConfiguration configuration;

    public MarkdownDoclet(Doclet initiatingDoclet) {
        super(initiatingDoclet);
        this.initiatingDoclet = initiatingDoclet;
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        super.init(locale, reporter);
        configuration
            = new MarkdownConfiguration(initiatingDoclet, locale, reporter);
    }

    @Override
    protected void generateClassFiles(SortedSet<TypeElement> typeElems,
            ClassTree classTree)
            throws DocletException {
        BuilderFactory f = configuration.getBuilderFactory();
        for (TypeElement te : typeElems) {
            if (utils.hasHiddenTag(te) ||
                !(configuration.isGeneratedDoc(te) && utils.isIncluded(te))) {
                continue;
            }
            f.getClassBuilder(te, classTree).build();
        }
    }

}
