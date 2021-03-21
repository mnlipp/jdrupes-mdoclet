MDoclet
=======

[![Java CI](https://github.com/mnlipp/jdrupes-mdoclet/actions/workflows/main.yml/badge.svg)](https://github.com/mnlipp/jdrupes-mdoclet/actions/workflows/main.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/36484e621eb243d793df9bccfbb502e3)](https://www.codacy.com/app/mnlipp/jdrupes-mdoclet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mnlipp/jdrupes-mdoclet&amp;utm_campaign=Badge_Grade)
[![Code Climate](https://lima.codeclimate.com/github/mnlipp/jdrupes-mdoclet/badges/gpa.svg)](https://lima.codeclimate.com/github/mnlipp/jdrupes-mdoclet)
[![Maven Central](https://img.shields.io/maven-central/v/org.jdrupes.mdoclet/doclet.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.jdrupes.mdoclet%22%20AND%20a%3A%22doclet%22)

A Doclet that allows the use of Markdown in JavaDoc. It uses a configurable 
Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default). 
It's a simple preprocessor to the standard Doclet: it processes all JavaDoc 
comments in the documentation tree when the standard doclet tries to access 
them.

See the [javadoc package description](https://mnlipp.github.io/jdrupes-mdoclet/javadoc/)
for details. 

This Doclet is released under the
[AGPL 3.0](http://www.gnu.org/licenses/#AGPL).