MDoclet
=======

[![Build Status](https://travis-ci.org/mnlipp/org.jdrupes.mdoclet.svg?branch=master)](https://travis-ci.org/mnlipp/org.jdrupes.mdoclet)
[![Release](https://jitpack.io/v/mnlipp/org.jdrupes.mdoclet.svg)](https://jitpack.io/mnlipp/org.jdrupes.mdoclet)

A Doclet that allows the use of Markdown in JavaDoc. It uses a configurable 
Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default). 
It's a simple preprocessor to the standard Doclet: It processes all JavaDoc 
comments in the documentation tree and then forwards the result to the 
standard Doclet.

See the javadoc package description for details. 

This project is an architectural redesign of 
[Abnaxos'](https://github.com/Abnaxos) 
great [pegdown-doclet](https://github.com/Abnaxos/pegdown-doclet). As its
name suggests, pegdown-doclet is closely tied to 
[pegdown](https://github.com/sirthias/pegdown), which is now
deprecated. pegdown-doclet's [PlanUML](http://plantuml.com/) 
integration has been factored out in the independent project 
[org.jdrupes.taglets](https://github.com/mnlipp/org.jdrupes.taglets).

This doclet is released under the
[GPL 3.0](http://www.gnu.org/licenses/gpl-3.0-standalone.html).

