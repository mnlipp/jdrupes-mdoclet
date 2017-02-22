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
package org.jdrupes.mdoclet.integrations.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc

class MDocletPlugin implements Plugin<Project> {
    final def CONFIGURATION_NAME = "org.jdrupes.mdoclet"

    void apply(Project project) {
        // create a new configuration for the doclet dependency
        def config = project.configurations.create(CONFIGURATION_NAME)

        // add the doclet dependency
        def mdocletVersion = new InputStreamReader(MDocletPlugin.class.getResourceAsStream('version.txt')).withReader { reader ->
            reader.text.trim()
        }
        project.dependencies.add(CONFIGURATION_NAME, "org.jdrupes:org.jdrupes.mdoclet:$mdocletVersion")

        // after the user buildscript is evaluated ...
        project.gradle.projectsEvaluated {

            // ... adjust the javadoc tasks
            project.tasks.withType Javadoc.class, {
                it.options {
                    docletpath = config.files.asType(List)
                    doclet = "org.jdrupes.mdoclet.MDoclet"
                }
            }
        }
    }
}
