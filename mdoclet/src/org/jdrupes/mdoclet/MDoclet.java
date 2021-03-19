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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;

/**
 * The Doclet implementation. It converts the Markdown from the JavaDoc 
 * comments and tags to HTML.
 * 
 * "Preprocessing" the text in the comments sounds simple. But the
 * {@link StandardDoclet} seems to be designed deliberately in way that
 * does not allow anybody to "plug into it".
 * 
 * This doclet therefore uses the same approach as found
 * in the implementation of the 
 * [asciidoclet](https://github.com/chrisvest/asciidoclet). The
 * AST is modified (in a hackish way, because this is not really
 * implemented with extensibility in mind either).
 * 
 * @see https://openjdk.java.net/groups/compiler/using-new-doclet.html
 */
public class MDoclet implements Doclet {

    private StandardDoclet standardDoclet;
    private Reporter reporter;

    public MDoclet() {
        standardDoclet = new StandardDoclet();
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
        standardDoclet.init(locale, reporter);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        Set<Option> options
            = new HashSet<>(standardDoclet.getSupportedOptions());
        Arrays.stream(Options.values()).forEach(options::add);
        return options;
    }

//    @Override
//    public Set<? extends Option> getSupportedOptions() {
//        Set<Option> options
//            = new HashSet<>(standardDoclet.getSupportedOptions());
//        Arrays.stream(AsciidocletOptions.values())
//            .map(o -> new OptionProcessor(o, docletOptions))
//            .forEach(options::add);
//        return options;
//    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        MDocletEnvironment env = new MDocletEnvironment(environment);
        boolean result = standardDoclet.run(env);
        return result;
    }

//    @Override
//    public boolean run(DocletEnvironment environment) {
//        docletOptions.validateOptions();
//        AsciidoctorRenderer renderer
//            = new AsciidoctorRenderer(docletOptions, reporter);
//        boolean result;
//        try (AsciidoctorFilteredEnvironment env
//            = new AsciidoctorFilteredEnvironment(environment, renderer)) {
//            result = standardDoclet.run(env);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//        return result && postProcess(environment);
//    }

//    private boolean postProcess(DocletEnvironment environment) {
//        if (docletOptions.stylesheet().isPresent()) {
//            return true;
//        }
//        return stylesheets.copy(environment);
//    }
}
