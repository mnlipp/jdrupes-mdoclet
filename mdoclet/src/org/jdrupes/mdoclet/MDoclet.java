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

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import org.jdrupes.mdoclet.processors.FlexmarkProcessor;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;

/**
 * The Doclet implementation. It converts the Markdown from the JavaDoc 
 * comments and tags to HTML.
 * 
 * @see <a href='https://openjdk.java.net/groups/compiler/using-new-doclet.html'>Using the new doclet API</a>
 */
public class MDoclet implements Doclet {

    private StandardDoclet standardDoclet;
    private Reporter reporter;

    private String markdownProcessorName = FlexmarkProcessor.class.getName();
    private MarkdownProcessor processor;

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
        options.add(new MDocletOption("markdown-processor", 1) {
            @Override
            public boolean process(String option, List<String> arguments) {
                markdownProcessorName = arguments.get(0);
                return true;
            }
        });
        options.add(new MDocletOption("disable-highlight", 0) {
            @Override
            public boolean process(String option, List<String> arguments) {
                return true;
            }
        });
        options.add(new MDocletOption("disable-auto-highlight", 0) {
            @Override
            public boolean process(String option, List<String> arguments) {
                return true;
            }
        });
        options.add(new MDocletOption("highlight-style", 1) {
            @Override
            public boolean process(String option, List<String> arguments) {
                return true;
            }
        });
        return options;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        MDocletEnvironment env = new MDocletEnvironment(this, environment);
        processor = createProcessor();
        processor.start(new String[0][0]);
        boolean result = standardDoclet.run(env);
        return result;
    }

    private MarkdownProcessor createProcessor() {
        try {
            @SuppressWarnings("unchecked")
            Class<MarkdownProcessor> mpc = (Class<MarkdownProcessor>) getClass()
                .getClassLoader().loadClass(markdownProcessorName);
            return (MarkdownProcessor) mpc.getDeclaredConstructor()
                .newInstance();
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | ClassCastException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            reporter.print(Diagnostic.Kind.ERROR,
                "Markdown processor \"" + markdownProcessorName
                    + "\" cannot be loaded (" + e.getMessage()
                    + "), check name and docletpath");
            return null;
        }
    }

    /**
     * Returns the processor selected by the options.
     * 
     * @return the processor
     */
    public MarkdownProcessor getProcessor() {
        return processor;
    }

    /**
     * Converts Markdown source to HTML according to the options object. If
     * `fixLeadingSpaces` is `true`, exactly one leading whitespace character ('\\u0020')
     * will be removed, if it exists.
     *
     * @param markup           The Markdown source.
     * @param fixLeadingSpaces `true` if leading spaces should be fixed.
     *
     * @return The resulting HTML.
     */
    public String toHtml(String markup, boolean fixLeadingSpaces) {
//        if (fixLeadingSpaces) {
//            markup = LINE_START.matcher(markup).replaceAll("");
//        }
//        List<String> tags = new ArrayList<>();
//        String html = processor.toHtml(Tags.extractInlineTags(markup, tags));
//        return Tags.insertInlineTags(html, tags);
        return "";
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
