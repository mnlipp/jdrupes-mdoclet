/*
 * JDrupes MDoclet
 * Copyright 2013 Raffael Herzog
 * Copyright (C) 2017 Michael N. Lipp
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.jdrupes.mdoclet;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.standard.Standard;
import com.sun.tools.javadoc.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jdrupes.mdoclet.renderers.ParamTagRenderer;
import org.jdrupes.mdoclet.renderers.SeeTagRenderer;
import org.jdrupes.mdoclet.renderers.SimpleTagRenderer;
import org.jdrupes.mdoclet.renderers.TagRenderer;
import org.jdrupes.mdoclet.renderers.ThrowsTagRenderer;

/**
 * The Doclet implementation. It converts the Markdown from the JavaDoc comments and tags
 * to HTML and sets a resulting JavaDoc comment using
 * {@link Doc#setRawCommentText(String)}. It then passes the `RootDoc` to the standard
 * Doclet.
 *
 * @see "[The Doclet Specification](http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html)"
 */
public class MDoclet extends Doclet implements DocErrorReporter {

    public static final String HIGHLIGHT_JS_HTML =
            "<script type=\"text/javascript\" charset=\"utf-8\" "
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
    private static final Pattern LINE_START = Pattern.compile("^ ", Pattern.MULTILINE);

    private final Map<String, TagRenderer<?>> tagRenderers = new HashMap<>();

    private final Set<PackageDoc> packages = new HashSet<>();
    private final Options options;
    private final RootDoc rootDoc;
    private MarkdownProcessor processor = null;

    private boolean error = false;

    /**
     * Construct a new doclet.
     *
     * @param options The command line options.
     * @param rootDoc The root document.
     */
    public MDoclet(Options options, RootDoc rootDoc) {
        this.options = options;
        this.rootDoc = rootDoc;
        tagRenderers.put("@author", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@version", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@return", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@deprecated", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@since", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@param", ParamTagRenderer.INSTANCE);
        tagRenderers.put("@throws", ThrowsTagRenderer.INSTANCE);
        tagRenderers.put("@see", SeeTagRenderer.INSTANCE);
        for (String tag: options.getMarkedDownTags()) {
        	tagRenderers.put("@" + tag, SimpleTagRenderer.INSTANCE);
        }
    }

    /**
     * As specified by the Doclet specification.
     *
     * @return Java 1.5.
     *
     * @see com.sun.javadoc.Doclet#languageVersion()
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param option The option name.
     *
     * @return The length of the option.
     *
     * @see com.sun.javadoc.Doclet#optionLength(String)
     */
    public static int optionLength(String option) {
        return Options.optionLength(option);
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param options       The command line options.
     * @param errorReporter An error reporter to print errors.
     *
     * @return `true`, if the options are valid.
     */
    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        return Options.validOptions(options, errorReporter);
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param rootDoc The root doc.
     *
     * @return `true`, if process was successful.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     */
    public static boolean start(RootDoc rootDoc) {
        Options options = new Options();
        String[][] forwardedOptions = options.load(rootDoc.options(), rootDoc);
        if ( forwardedOptions == null ) {
            return false;
        }
        MDoclet doclet = new MDoclet(options, rootDoc);
        doclet.process();
        if ( doclet.isError() ) {
            return false;
        }
        RootDocWrapper rootDocWrapper = new RootDocWrapper(rootDoc, forwardedOptions);
        if ( options.isHighlightEnabled() ) {
            // find the footer option
            int optIndex = 0;
            for ( ; optIndex < rootDocWrapper.options().length; optIndex++ ) {
                if ( rootDocWrapper.options()[optIndex][0].equals("-footer") ) {
                    rootDocWrapper.options()[optIndex][1] += HIGHLIGHT_JS_HTML;
                    break;
                }
            }
            if ( optIndex >= rootDocWrapper.options().length ) {
                rootDocWrapper.appendOption("-footer", HIGHLIGHT_JS_HTML);
            }
            if (Standard.optionLength("--allow-script-in-comments") == 1) {
            	if (rootDocWrapper.findOption("--allow-script-in-comments") == null) {
            		rootDocWrapper.appendOption("--allow-script-in-comments");
            	}
            }
        }
        return Standard.start(rootDocWrapper) && doclet.postProcess();
    }

    /**
     * Removes all tag renderers.
     */
    public void clearTagRenderers() {
        tagRenderers.clear();
    }

    /**
     * Adds a tag renderer for the specified {@link com.sun.javadoc.Tag#kind() kind}.
     *
     * @param kind        The kind of the tag the renderer renders.
     * @param renderer    The tag renderer.
     */
    public void addTagRenderer(String kind, TagRenderer<?> renderer) {
        tagRenderers.put(kind, renderer);
    }

    /**
     * Removes a tag renderer for the specified {@link com.sun.javadoc.Tag#kind() kind}.
     *
     * @param kind        The kind of the tag.
     */
    public void removeTagRenderer(String kind) {
        tagRenderers.remove(kind);
    }

    /**
     * Get the options.
     *
     * @return The options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Get the root doc.
     *
     * @return The root doc.
     */
    public RootDoc getRootDoc() {
        return rootDoc;
    }

    /**
     * Process the documentation tree. If any errors occur during processing,
     * {@link #isError()} will return `true` afterwards.
     */
    public void process() {
    	processor = options.getProcessor();
    	try {
    		processor.start(options.getProcessorOptions());
    	} catch (Throwable e) {
    		printError(e.getMessage());
    		return;
    	}
        processOverview();
        for ( ClassDoc doc : rootDoc.classes() ) {
            packages.add(doc.containingPackage());
            processClass(doc);
        }
        for ( PackageDoc doc : packages ) {
            processPackage(doc);
        }
    }

    /**
     * Called after the standard Doclet *successfully* did its work.
     *
     * @return `true` if postprocessing succeeded.
     */
    public boolean postProcess() {
        boolean success = true;
        if ( options.isHighlightEnabled() ) {
            success &= copyResource("highlight.pack.js", 
            		"highlight.pack.js", "highlight.js");
            success &= copyResource("highlight-LICENSE.txt", 
            		"highlight-LICENSE.txt", "highlight.js license");
            success &= copyResource("highlight-styles/" + options.getHighlightStyle() + ".css", 
            		"highlight.css", 
            		"highlight.js style '" + options.getHighlightStyle() + "'");
        }
        return success;
    }

    private boolean copyResource(String resource, String destination, String description) {
        try (
                InputStream in = MDoclet.class.getResourceAsStream(resource);
                OutputStream out = new FileOutputStream(
                		new File(options.getDestinationDir(), destination))
        )
        {
        	Files.copy(in, options.getDestinationDir().toPath().resolve(destination),
        			StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch ( IOException e ) {
            printError("Error writing " + description + ": " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Check whether any errors occurred during processing of the documentation tree.
     *
     * @return `true` if there were errors processing the documentation tree.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Process the overview file, if specified.
     */
    protected void processOverview() {
        if ( options.getOverviewFile() != null ) {
            try {
            	rootDoc.setRawCommentText(new String(Files.readAllBytes(
            			options.getOverviewFile().toPath()), options.getEncoding()));
                defaultProcess(rootDoc, false);
            } catch ( IOException e ) {
                printError("Error loading overview from " + options.getOverviewFile() 
                	+ ": " + e.getLocalizedMessage());
                rootDoc.setRawCommentText("");
            }
        }
    }

    /**
     * Process the class documentation.
     *
     * @param doc   The class documentation.
     */
    protected void processClass(ClassDoc doc) {
        defaultProcess(doc, true);
        for ( MemberDoc member : doc.fields() ) {
            processMember(member);
        }
        for ( MemberDoc member : doc.constructors() ) {
            processMember(member);
        }
        for ( MemberDoc member : doc.methods() ) {
            processMember(member);
        }
        if ( doc instanceof AnnotationTypeDoc ) {
            for ( MemberDoc member : ((AnnotationTypeDoc)doc).elements() ) {
                processMember(member);
            }
        }
    }

    /**
     * Process the member documentation.
     *
     * @param doc    The member documentation.
     */
    protected void processMember(MemberDoc doc) {
        defaultProcess(doc, true);
    }

    /**
     * Process the package documentation.
     *
     * @param doc    The package documentation.
     */
    protected void processPackage(PackageDoc doc) {
        // (#1) Set foundDoc to false if possible.
        // foundDoc will be set to true when setRawCommentText() is called, if the method
        // is called again, JavaDoc will issue a warning about multiple sources for the
        // package documentation. If there actually *are* multiple sources, the warning
        // has already been issued at this point, we will, however, use it to set the
        // resulting HTML. So, we're setting it back to false here, to suppress the
        // warning.
        try {
            Field foundDoc = doc.getClass().getDeclaredField("foundDoc");
            foundDoc.setAccessible(true);
            foundDoc.set(doc, false);
        } catch ( Exception e ) {
            printWarning(doc.position(), 
            		"Cannot suppress warning about multiple package sources: " + e);
        }
        defaultProcess(doc, true);
    }

    /**
     * Default processing of any documentation node.
     *
     * @param doc              The documentation.
     * @param fixLeadingSpaces `true` if leading spaces should be fixed.
     */
    protected void defaultProcess(Doc doc, boolean fixLeadingSpaces) {
        try {
            StringBuilder buf = new StringBuilder();
            buf.append(toHtml(doc.commentText(), fixLeadingSpaces));
            buf.append('\n');
            for ( Tag tag : doc.tags() ) {
                processTag(tag, buf);
                buf.append('\n');
            }
            doc.setRawCommentText(buf.toString());
        } catch ( Throwable e ) {
            if ( doc instanceof RootDoc ) {
                printError(new SourcePosition() {
                    @Override
                    public File file() {
                        return options.getOverviewFile();
                    }
                    
                    @Override
                    public int line() {
                        return 0;
                    }
                    
                    @Override
                    public int column() {
                        return 0;
                    }
                }, e.getMessage());
            } else {
                printError(doc.position(), e.getMessage());
            }
        }
    }

    /**
     * Process a tag.
     *
     * @param tag      The tag.
     * @param target   The target string builder.
     */
    @SuppressWarnings("unchecked")
    protected void processTag(Tag tag, StringBuilder target) {
        TagRenderer<Tag> renderer = (TagRenderer<Tag>)tagRenderers.get(tag.kind());
        if ( renderer == null ) {
            renderer = TagRenderer.VERBATIM;
        }
        renderer.render(tag, target, this);
    }

    /**
     * Clears the processor.
     */
    public void clearProcessor() {
    	processor = null;
    }
    
    /**
     * Convert the given markup to HTML according to the {@link Options}.
     *
     * @param markup    The Markdown source.
     *
     * @return The resulting HTML.
     */
    public String toHtml(String markup) {
        return toHtml(markup, true);
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
        if ( fixLeadingSpaces ) {
            markup = LINE_START.matcher(markup).replaceAll("");
        }
        List<String> tags = new ArrayList<>();
        String html = processor.toHtml(Tags.extractInlineTags(markup, tags));
        return Tags.insertInlineTags(html, tags);
    }

    /**
     * Indicate that an error occurred. This method will also be called by
     * {@link #printError(String)} and
     * {@link #printError(com.sun.javadoc.SourcePosition, String)}.
     */
    public void error() {
        error = true;
    }

    @Override
    public void printError(String msg) {
        error();
        rootDoc.printError(msg);
    }

    @Override
    public void printError(SourcePosition pos, String msg) {
        error();
        rootDoc.printError(pos, msg);
    }

    @Override
    public void printWarning(String msg) {
        rootDoc.printWarning(msg);
    }

    @Override
    public void printWarning(SourcePosition pos,
                             String msg)
    {
        rootDoc.printWarning(pos, msg);
    }

    @Override
    public void printNotice(String msg) {
        rootDoc.printNotice(msg);
    }

    @Override
    public void printNotice(SourcePosition pos, String msg) {
        rootDoc.printNotice(pos, msg);
    }

    /**
     * Returns a prefix for relative URLs from a documentation element relative to the
     * given package. This prefix can be used to refer to the root URL of the
     * documentation:
     *
     * ```java
     * doc = "<script type=\"text/javascript\" src=\""
     *     + rootUrlPrefix(classDoc.containingPackage()) + "highlight.js"
     *     + "\"></script>";
     * ```
     *
     * @param doc    The package containing the element from where to reference the root.
     *
     * @return A URL prefix for URLs referring to the doc root.
     */
    public String rootUrlPrefix(PackageDoc doc) {
        if ( doc == null || doc.name().isEmpty() ) {
            return "";
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append("../");
            for ( int i = 0; i < doc.name().length(); i++ ) {
                if ( doc.name().charAt(i) == '.' ) {
                    buf.append("../");
                }
            }
            return buf.toString();
        }
    }

    /**
     * Just a main method for debugging.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If anything goes wrong.
     */
    public static void main(String[] args) throws Exception {
        args = Arrays.copyOf(args, args.length + 2);
        args[args.length - 2] = "-doclet";
        args[args.length - 1] = MDoclet.class.getName();
        Main.main(args);
    }

}
