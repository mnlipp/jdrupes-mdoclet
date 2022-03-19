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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.DocumentationTool.Location;
import javax.tools.JavaFileManager;

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
 */
public class MDoclet implements Doclet {

    public static final String HIGHLIGHT_JS_HTML
        = "<script type=\"text/javascript\" charset=\"utf-8\" "
            + "src=\"" + "{@docRoot}/highlight.pack.js" + "\"></script>\n"
            + "<script type=\"text/javascript\"><!--\n"
            + "var cssId = 'highlightCss';\n"
            + "if (!document.getElementById(cssId))\n"
            + "{\n"
            + "    var head  = document.getElementsByTagName('head')[0];\n"
            + "    var link  = document.createElement('link');\n"
            + "    link.id   = cssId;\n"
            + "    link.rel  = 'stylesheet';\n"
            + "    link.type = 'text/css';\n"
            + "    link.charset = 'utf-8';\n"
            + "    link.href = '{@docRoot}/highlight.css';\n"
            + "    link.media = 'all';\n"
            + "    head.appendChild(link);\n"
            + "}"
            + "hljs.initHighlightingOnLoad();\n"
            + "//--></script>";

    private StandardDoclet standardDoclet;
    private Reporter reporter;
    private JavaFileManager fileManager;

    private String markdownProcessorName = FlexmarkProcessor.class.getName();
    private MarkdownProcessor processor;
    private List<String> processorOptions = new ArrayList<>();
    private Option origHeaderOpt;
    private String bufferedHeader = "";
    private Option allowScriptsOpt;
    private boolean disableHighlight;
    private boolean disableAutoHighlight;
    private String highlightStyle = "default";

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
        Set<Option> options = new HashSet<>();
        for (Option opt : standardDoclet.getSupportedOptions()) {
            if (opt.getNames().contains("-header")) {
                origHeaderOpt = opt;
            } else {
                options.add(opt);
            }
            if (opt.getNames().contains("--allow-script-in-comments")) {
                allowScriptsOpt = opt;
            }
        }
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
                disableHighlight = true;
                return true;
            }
        });
        options.add(new MDocletOption("disable-auto-highlight", 0) {
            @Override
            public boolean process(String option, List<String> arguments) {
                disableAutoHighlight = true;
                return true;
            }
        });
        options.add(new MDocletOption("highlight-style", 1) {
            @Override
            public boolean process(String option, List<String> arguments) {
                highlightStyle = arguments.get(0);
                return true;
            }
        });
        options.add(new MDocletOption("M", 1) {
            @Override
            public boolean process(String option, List<String> arguments) {
                return processorOptions.add(arguments.get(0));
            }
        });
        options.add(new HeaderOverride());
        return options;
    }

    private class HeaderOverride implements Option {

        @Override
        public int getArgumentCount() {
            return 1;
        }

        @Override
        public String getDescription() {
            return origHeaderOpt.getDescription();
        }

        @Override
        public Kind getKind() {
            return origHeaderOpt.getKind();
        }

        @Override
        public List<String> getNames() {
            return origHeaderOpt.getNames();
        }

        @Override
        public String getParameters() {
            return origHeaderOpt.getParameters();
        }

        @Override
        public boolean process(String option, List<String> arguments) {
            bufferedHeader = arguments.get(0);
            return true;
        }

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        fileManager = environment.getJavaFileManager();
        if (disableHighlight) {
            if (bufferedHeader.length() > 0) {
                origHeaderOpt.process("-header", List.of(bufferedHeader));
            }
        } else {
            bufferedHeader += HIGHLIGHT_JS_HTML;
            origHeaderOpt.process("-header", List.of(bufferedHeader));
            allowScriptsOpt.process("--allow-script-in-comments",
                Collections.emptyList());
        }

        MDocletEnvironment env = new MDocletEnvironment(this, environment);
        processor = createProcessor();
        processor.start(processorOptions.toArray(new String[0]));
        return standardDoclet.run(env) && postProcess();
    }

    private MarkdownProcessor createProcessor() {
        try {
            @SuppressWarnings("unchecked")
            Class<MarkdownProcessor> mpc = (Class<MarkdownProcessor>) getClass()
                .getClassLoader().loadClass(markdownProcessorName);
            MarkdownProcessor mdp = (MarkdownProcessor) mpc
                .getDeclaredConstructor().newInstance();
            if (disableAutoHighlight && mdp.isSupportedOption(
                MarkdownProcessor.INTERNAL_OPT_DISABLE_AUTO_HIGHLIGHT) >= 0) {
                processorOptions
                    .add(MarkdownProcessor.INTERNAL_OPT_DISABLE_AUTO_HIGHLIGHT);
            }
            return mdp;
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

    private boolean postProcess() {
        if (disableHighlight) {
            return true;
        }
        return copyResource("highlight.pack.js", "highlight.pack.js",
            "highlight.js")
            && copyResource("highlight-LICENSE.txt", "highlight-LICENSE.txt",
                "highlight.js license")
            && copyResource("highlight-styles/" + highlightStyle + ".css",
                "highlight.css",
                "highlight.js style '" + highlightStyle + "'");
    }

    private boolean copyResource(String resource, String destination,
            String description) {
        try {
            InputStream in = MDoclet.class.getResourceAsStream(resource);
            if (in == null) {
                throw new FileNotFoundException();
            }
            in.transferTo(
                fileManager.getFileForOutput(Location.DOCUMENTATION_OUTPUT, "",
                    destination, null).openOutputStream());
            return true;
        } catch (IOException e) {
            reporter.print(javax.tools.Diagnostic.Kind.ERROR,
                "Error writing " + description + ": "
                    + e.getLocalizedMessage());
            return false;
        }
    }

}
