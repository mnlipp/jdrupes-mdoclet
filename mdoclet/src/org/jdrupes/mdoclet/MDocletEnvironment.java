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

import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject.Kind;

import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.internal.tool.DocEnvImpl;

/**
 * Wraps the {@link DocEnvImpl} passed to the doclet for the
 * sole purpose of inserting a wrapper for the doctrees access.
 */
public class MDocletEnvironment extends DocEnvImpl
        implements DocletEnvironment {

    DocletEnvironment defaultEnvironment;
    DocTreesWrapper docTrees;

    public MDocletEnvironment(MDoclet doclet, DocletEnvironment environment) {
        super(((DocEnvImpl) environment).toolEnv,
            ((DocEnvImpl) environment).etable);
        this.defaultEnvironment = environment;
        docTrees = new DocTreesWrapper(doclet, this, environment.getDocTrees());
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getSpecifiedElements()
     */
    public Set<? extends Element> getSpecifiedElements() {
        return defaultEnvironment.getSpecifiedElements();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getIncludedElements()
     */
    public Set<? extends Element> getIncludedElements() {
        return defaultEnvironment.getIncludedElements();
    }

    /**
     * Wraps the doctrees from the environment passed in the constructor
     * in a {@link DocTreesWrapper}.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getDocTrees()
     */
    public DocTrees getDocTrees() {
        return docTrees;
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getElementUtils()
     */
    public Elements getElementUtils() {
        return defaultEnvironment.getElementUtils();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getTypeUtils()
     */
    public Types getTypeUtils() {
        return defaultEnvironment.getTypeUtils();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#isIncluded(javax.lang.model.element.Element)
     */
    public boolean isIncluded(Element e) {
        return defaultEnvironment.isIncluded(e);
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#isSelected(javax.lang.model.element.Element)
     */
    public boolean isSelected(Element e) {
        return defaultEnvironment.isSelected(e);
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getJavaFileManager()
     */
    public JavaFileManager getJavaFileManager() {
        return defaultEnvironment.getJavaFileManager();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getSourceVersion()
     */
    public SourceVersion getSourceVersion() {
        return defaultEnvironment.getSourceVersion();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * @return the module mode
     * @see jdk.javadoc.doclet.DocletEnvironment#getModuleMode()
     */
    public ModuleMode getModuleMode() {
        return defaultEnvironment.getModuleMode();
    }

    /**
     * Delegates to the environment passed in the constructor.
     * 
     * {@inheritDoc}
     * @see jdk.javadoc.doclet.DocletEnvironment#getFileKind(javax.lang.model.element.TypeElement)
     */
    public Kind getFileKind(TypeElement type) {
        return defaultEnvironment.getFileKind(type);
    }

}
