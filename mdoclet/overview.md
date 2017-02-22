A Doclet that enables Markdown in JavaDoc comments. It converts all JavaDoc
documentation to HTML using a configurable Markdown processor
([flexmark-java](https://github.com/vsch/flexmark-java) by default) and 
provides an extended taglet interface.

See the package description of {@link org.jdrupes.mdoclet} for full documentation.

This project is an architectural redesign and
extension of [Abnaxos'](https://github.com/Abnaxos) 
great [pegdown-doclet](https://github.com/Abnaxos/pegdown-doclet).
Aside from making the Markdown processor configurable, the PlantUML
functionality has been factored out in a 
[project of its own](https://github.com/mnlipp/org.jdrupes.taglets). 

@see "flexmark-java <https://github.com/vsch/flexmark-java>"
