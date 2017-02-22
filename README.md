MDoclet
=======

[![Build Status](https://travis-ci.org/mnlipp/org.jdrupes.mdoclet.svg?branch=master)](https://travis-ci.org/mnlipp/org.jdrupes.mdoclet)
[![Release](https://jitpack.io/v/mnlipp/org.jdrupes.mdoclet.svg)](https://jitpack.io/mnlipp/org.jdrupes.mdoclet)

A Doclet that allows the use of Markdown in JavaDoc and
provides an extended taglet interface. It uses a configurable Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default). 
It's a simple preprocessor to the standard Doclet: It processes all JavaDoc 
comments in the documentation tree and then forwards the result to the standard Doclet.

As an example for using the extended taglet interface, a taglet 
for including UML diagrams [PlantUML](http://plantuml.sourceforge.net/) is
provided as part of this project.

See the javadoc package description for details. 

This project is an architectural redesign and
extension of [Abnaxos'](https://github.com/Abnaxos) 
great [pegdown-doclet](https://github.com/Abnaxos/pegdown-doclet).

This Doclet is released under the
[GPL 3.0](http://www.gnu.org/licenses/gpl-3.0-standalone.html).

