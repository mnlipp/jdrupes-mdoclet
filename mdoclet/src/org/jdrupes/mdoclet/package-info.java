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

/**
 * A doclet that allows the use of Markdown in JavaDoc and
 * provides an extended taglet interface. It uses a configurable Markdown processor
 * ([flexmark-java](https://github.com/vsch/flexmark-java) by default). 
 * It's a simple preprocessor to the standard Doclet: It processes all JavaDoc 
 * comments in the documentation tree and then forwards the result to the standard Doclet.
 * 
 * This Doclet is released under the
 * [GPL 3.0](http://www.gnu.org/licenses/gpl-3.0-standalone.html).
 *
 *
 * Selecting a Markdown processor
 * ------------------------------
 * 
 * The doclet accesses the Markdown processor using the interface
 * {@link org.jdrupes.mdoclet.MarkdownProcessor}. If you want to use another
 * Markdown processor than the default flexmark-java processor, you must provide
 * an adapter class that implements the interface and has a default (no parameters) 
 * constructor. To make the doclet use your class, supply its fully qualified class 
 * name as parameter to the option `-markdown-processor`. The class 
 * (and all its dependencies) must be in the doclet classpath.
 * 
 * The default behavior is equivalent to "`-markup-processor 
 * {@link org.jdrupes.mdoclet.processors.FlexmarkProcessor 
 * org.jdrupes.mdoclet.processors.FlexmarkProcessor}`".
 *
 * 
 * Configuring the Markdown processor
 * ----------------------------------
 * 
 * Markup processors may support further configuration. As the available options
 * are unknown to this doclet, it uses the "flag forwarding" mechanism known from
 * the javadoc tool. The javadoc tool forwards flags prefixed with `-J` to the
 * java that runs javadoc. This doclet passes flags prefixed with `-M` to the
 * Markdown processor (after removing the prefix, of course). E.g. "`-M-profile=kramdown`"
 * is passed to the Markdown processor as "`-profile kramdown`".
 * 
 * The flags supported by the default Markdown processor can be found in the 
 * description of its {@linkplain org.jdrupes.mdoclet.processors.FlexmarkProcessor 
 * adapter class}.
 *
 *
 * Leading Spaces
 * --------------
 *
 * Sometimes, leading whitespaces are significant in Markdown. Because of the way we
 * usually write JavaDoc comments and the way JavaDoc is implemented, this may lead to
 * some problems:
 *
 * ```
 * /**
 *  * Title
 *  * =====
 *  *
 *  * Text
 *  {@literal *}/
 * ```
 *
 * In this example, each line has one leading space. Because of this, the title won't be
 * recognised as such by the Markdown processor. To work around this problem, the 
 * doclet uses a simple trick: The first leading space character (the *actual* space 
 * character, i.e. `\\u0020`) will be cut off, if it exists.
 *
 * This may be important e.g. for code blocks, which should be indented by 4 spaces: Well,
 * it's 5 spaces now. ;)
 *
 * *Note:* If an `overview.md` file is specified, leading spaces will be treated normally
 * in this file. The first space will *not* be ignored.
 *
 * This behaviour is currently *not* customisable.
 *
 *
 * Javadoc Tags
 * ------------
 *
 * The following known tags handled are processed as Markdown:
 *
 *  *  `@author`
 *  *  `@version`
 *  *  `@return`
 *  *  `@deprecated`
 *  *  `@since`
 *  *  `@param`
 *  *  `@throws`
 *
 * ### `@see` Tags
 *
 * The `@see` tag is a special case, as there are several variants of this tag. These two
 * variants will remain unchanged:
 *
 *  *  Javadoc-Links: `@see Foo#bar()`
 *  *  Links: `@see <a href="http://www.example.com/">Example</a>`
 *
 * The third variant however, which is originally meant to refer to a printed book, may
 * also contain Markdown-style links:
 *
 *  *  `@see "[Example](http://www.example.com/)"`
 *  *  `@see "<http://www.example.com/>"`
 *  *  `@see "Example <http://www.example.com/>"`
 *
 * These are all rendered as `@see <a href="http://www.example.com/">LABEL</a>`, where
 * LABEL falls back to the link's URL, if no label is given.
 *
 * ### Inline Tags
 *
 * Inline tags will be removed before processing the Markdown source and re-inserted
 * afterwards. Therefore, markup within inline tags won't work.
 *
 *
 * Syntax Highlighting
 * -------------------
 *
 * MDoclet integrates
 * [highlight.js](http://softwaremaniacs.org/soft/highlight/en/) to enable syntax
 * highlighting for fenced blocks.
 *
 *
 * Invoking
 * --------
 *
 * Specify the Doclet on JavaDoc's command line:
 *
 * ```
 * javadoc -doclet org.jdrupes.mdoclet.MDoclet -docletpath /path/to/org.jdrupes.mdoclet.jar
 * ```
 *
 * A prebuilt version can be downloaded from ...
 * (use the JAR with the suffix "-all" for a JAR file that includes all dependencies).
 *
 * `-markdown-processor`
 * :   Specify the markdown processor, see above.
 *
 * `-overview <page>`
 * :   Specify an overview page. This is basically the same as with the
 *     standard doclet, however, the specified page will be rendered by the Markup processor.
 *
 * `-tag <definition>`
 * :   This is tha same as with the standard doclet. However, the flags for the
 *     tag can include an "`M`" which means that the tag will be preprocessed
 *     by the markdown processor. 
 *
 * `-highlight-style <style>`
 * :   The style to be used for syntax highlighting.
 *
 * `-disable-highlight`
 * :   Disable syntax highlighting entirely.
 *
 * `-disable-auto-highlight`
 * :   Disable auto-highlighting. If no language is specified for a fenced block, the
 *     highlighter will not try to guess the correct language. This option has
 *     to be implemented by the markdown processor.
 *
 * `-todo-title`
 * :   Set the title of TODO boxes.
 *
 *
 * ### Gradle
 *
 * Add the following to your `build.gradle` to use the doclet with Gradle:
 *
 * ```groovy
 * buildscript {
 *     repositories {
 *         mavenCentral() // or jcenter()
 *     }
 *     dependencies {
 *         classpath 'org.jdrupes:org.jdrupes.mdoclet-javadoc:0.9'
 *     }
 * }
 *
 * apply plugin: 'org.jdrupes.mdoclet-javadoc'
 * ```
 * 
 */
package org.jdrupes.mdoclet;
