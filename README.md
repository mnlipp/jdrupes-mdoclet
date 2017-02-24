MDoclet
=======

[![Build Status](https://travis-ci.org/mnlipp/jdrupes-mdoclet.svg?branch=master)](https://travis-ci.org/mnlipp/jdrupes-mdoclet)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/36484e621eb243d793df9bccfbb502e3)](https://www.codacy.com/app/mnlipp/jdrupes-mdoclet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mnlipp/jdrupes-mdoclet&amp;utm_campaign=Badge_Grade)
[![Release](https://jitpack.io/v/mnlipp/jdrupes-mdoclet.svg)](https://jitpack.io/#mnlipp/jdrupes-mdoclet)
[![Download](https://api.bintray.com/packages/mnlipp/org.jdrupes/mdoclet-doclet/images/download.svg) ](https://bintray.com/mnlipp/org.jdrupes/mdoclet-doclet/_latestVersion)

A Doclet that allows the use of Markdown in JavaDoc. It uses a configurable 
Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default). 
It's a simple preprocessor to the standard Doclet: It processes all JavaDoc 
comments in the documentation tree and then forwards the result to the 
standard Doclet.

See the [javadoc package description](https://mnlipp.github.io/org.jdrupes.mdoclet/javadoc/)
for details. 

This project is an architectural redesign of 
[Abnaxos'](https://github.com/Abnaxos) 
great [pegdown-doclet](https://github.com/Abnaxos/pegdown-doclet). As its
name suggests, pegdown-doclet is closely tied to 
[pegdown](https://github.com/sirthias/pegdown), which is now
deprecated. pegdown-doclet's [PlanUML](http://plantuml.com/) 
integration has been factored out in the independent project 
[org.jdrupes.taglets](https://github.com/mnlipp/jdrupes-taglets).

This doclet is released under the
[GPL 3.0](http://www.gnu.org/licenses/gpl-3.0-standalone.html).

