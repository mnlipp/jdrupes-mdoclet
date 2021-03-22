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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import org.jdrupes.mdoclet.processors.FlexmarkProcessor;

import com.sun.source.doctree.DocCommentTree;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;

/**
 * The Doclet implementation, which converts the Markdown from the JavaDoc 
 * comments and tags to HTML.
 * 
 * The doclet works by installing wrappers to intercept the 
 * {@link StandardDoclet}'s calls to access the {@link DocCommentTree}s 
 * (see {@link DocCommentTreeWrapper}). At the root of this interception
 * is a modified doclet environment ({@link MDocletEnvironment}) that 
 * installs a wrapper around doc trees access.
 * 
 * For some strange reason, the `StandardDoclet` does not work
 * with interface {@link DocletEnvironment} but insists on the instance
 * being a `DocEnvImpl`. Therefore {@link MDocletEnvironment} has
 * to extend this class which requires to allow module access with
 * `--add-exports=jdk.javadoc/jdk.javadoc.internal.tool=ALL-UNNAMED`.
 * 
 * @version 2.0
 * @see <a href='https://docs.oracle.com/javase/10/javadoc/javadoc-command.htm'>Javadoc command</a>
 * @see <a href='https://openjdk.java.net/groups/compiler/using-new-doclet.html'>Using the new doclet API</a>
 */
public class MDoclet implements Doclet {

    private StandardDoclet standardDoclet;
    private Reporter reporter;

    private String markdownProcessorName = FlexmarkProcessor.class.getName();
    private MarkdownProcessor processor;
    private List<String> processorOptions = new ArrayList<>();

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
        options.add(new MDocletOption("M", 1) {
            @Override
            public boolean process(String option, List<String> arguments) {
                return processorOptions.add(arguments.get(0));
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
        processor.start(processorOptions.toArray(new String[0]));
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

}
