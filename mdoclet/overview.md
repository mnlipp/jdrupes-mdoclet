A Doclet that enables Markdown in JavaDoc comments. 

MDoclet
=======

MDoclet converts all JavaDoc
documentation to HTML using a configurable Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default)

It's a simple preprocessor to the standard Doclet: It processes all JavaDoc 
comments in the documentation tree and then forwards the result to the 
standard Doclet.

Javadoc Tags
------------

The following known tags are processed as Markdown:

 *  `@author`
 *  `@deprecated`
 *  `@param`
 *  `@return`
 *  `@since`
 *  `@throws`
 *  `@version`

### `@see` Tags

The `@see` tag is a special case, as there are several variants of this tag. 
These two variants will remain unchanged:

 *  Javadoc-Links: `@see Foo#bar()`
 *  Links: `@see <a href="http://www.example.com/">Example</a>`

The third variant however, which is originally meant to refer to a printed book, may
also contain Markdown-style links:

 *  `@see "[Example](http://www.example.com/)"`
 *  `@see "<http://www.example.com/>"`
 *  `@see "Example <http://www.example.com/>"`

These are all rendered as `@see <a href="http://www.example.com/">LABEL</a>`, where
LABEL falls back to the link's URL, if no label is given.

### Inline Tags

Inline tags will be removed before processing the Markdown source and 
re-inserted afterwards. Therefore, Markdown within inline tags won't work.


Syntax Highlighting
-------------------

MDoclet integrates
[highlight.js](http://softwaremaniacs.org/soft/highlight/en/) to enable syntax
highlighting for fenced blocks.


Invoking
--------

Specify the Doclet on JavaDoc's command line:

```
javadoc -doclet org.jdrupes.mdoclet.MDoclet -docletpath /path/to/org.jdrupes.mdoclet.jar
```

A prebuilt version can be downloaded from ...
(use the JAR with the suffix "-all" for a JAR file that includes all dependencies).

`--markdown-processor`
:   Specify the Markdown processor, see below.

`-overview <file>`
:   Specify an overview page. This is an option from the standard doclet.
    If the file name ends with ".md", the file will be converted by 
    the Markdown processor.

`-highlight-style <style>`
:   The style to be used for syntax highlighting.

`-disable-highlight`
:   Disable syntax highlighting entirely.

`-disable-auto-highlight`
:   Disable auto-highlighting. If no language is specified for a fenced block, the
    highlighter will not try to guess the correct language. This option has
    to be implemented by the Markdown processor.


### Gradle

You can simply configure the doclet in the javadoc task with the
`doclet` and `docletpath` options as shown in the 
[DSL Reference](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.javadoc.Javadoc.html).

As a convenience, there is a plugin available that configures all your
javadoc task(s) to use MDoclet. The build script snippets can be found on the 
[Gradle Plugins Site](https://plugins.gradle.org/plugin/org.jdrupes.mdoclet).

Note that the plugin adds a dependency to the artifact providing MDoclet
to the configurations. It does not include the MDoclet jar itself. 
Therefore, you have to make sure that your repositories include 
maven central, so that the MDoclet artifact can be downloaded.

```gradle
repositories {
    mavenCentral() // For finding the doclet at compile time
}
```

The latest version available on maven central is shown in the badge on the 
[project page](https://github.com/mnlipp/jdrupes-mdoclet). Note that
you cannot use snapshot versions of the plugin.


Selecting a Markdown processor
------------------------------

The doclet accesses the Markdown processor using the interface
{@link org.jdrupes.mdoclet.MarkdownProcessor}. If you want to use another
Markdown processor than the default flexmark-java processor, you must provide
an adapter class that implements the interface and has a default (no parameters) 
constructor. To make the doclet use your class, supply its fully qualified class 
name as parameter to the option `--markdown-processor`. The class 
(and all its dependencies) must be in the doclet classpath.

The default behavior is equivalent to "``--markdown-processor 
{@link org.jdrupes.mdoclet.processors.FlexmarkProcessor 
org.jdrupes.mdoclet.processors.FlexmarkProcessor}``".
 
Configuring the Markdown processor
----------------------------------
 
Markdown processors may support further configuration. As the available options
are unknown to this doclet, it uses a "flag forwarding" mechanism. The 
argument of flag `-M` is forwarded to the Markdown processor. E.g. 
"`-M -profile=kramdown`" is passed to the Markdown processor as "`-profile=kramdown`".
The option may be used multiple times.
 
The flags supported by the default Markdown processor can be found in the 
description of its {@linkplain org.jdrupes.mdoclet.processors.FlexmarkProcessor 
adapter class}.


Notes
-----

While based on JDK 1.8 (doclet version < 2.0), this project was an 
architectural redesign and extension of [Abnaxos'](https://github.com/Abnaxos) 
great [pegdown-doclet](https://github.com/Abnaxos/pegdown-doclet).
Aside from making the Markdown processor configurable, the PlantUML
functionality had been factored out in a 
[project of its own](https://github.com/mnlipp/jdrupes-taglets). 

Starting with doclet version 2.0.0, this project is an independent
development based on the API introduced in JDK 9.

This Doclet is released under the
[AGPL 3.0](http://www.gnu.org/licenses/#AGPL).

@see "flexmark-java <https://github.com/vsch/flexmark-java>"
