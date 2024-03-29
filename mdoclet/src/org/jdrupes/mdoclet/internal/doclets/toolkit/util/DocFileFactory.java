/*
 * Copyright (c) 1998, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.jdrupes.mdoclet.internal.doclets.toolkit.util;

import java.nio.file.Path;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;

import org.jdrupes.mdoclet.internal.doclets.toolkit.BaseConfiguration;
import org.jdrupes.mdoclet.internal.doclets.toolkit.DocletException;

import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

/**
 * Factory for DocFile objects.
 */
public abstract class DocFileFactory {
    /**
     * Get the appropriate factory, based on the file manager given in the
     * configuration.
     *
     * @param configuration the configuration for this doclet
     * @return the factory associated with this configuration
     */
    public static synchronized DocFileFactory
            getFactory(BaseConfiguration configuration) {
        DocFileFactory f = configuration.docFileFactory;
        if (f == null) {
            JavaFileManager fm = configuration.getFileManager();
            if (fm instanceof StandardJavaFileManager) {
                f = new StandardDocFileFactory(configuration);
            } else {
                throw new IllegalStateException();
            }
            configuration.docFileFactory = f;
        }
        return f;
    }

    protected BaseConfiguration configuration;

    protected DocFileFactory(BaseConfiguration configuration) {
        this.configuration = configuration;
    }

    public abstract void setDestDir(String dir) throws DocletException;

    /** Create a DocFile for a directory. */
    abstract DocFile createFileForDirectory(String file);

    /** Create a DocFile for a file that will be opened for reading. */
    abstract DocFile createFileForInput(String file);

    /** Create a DocFile for a file that will be opened for reading. */
    abstract DocFile createFileForInput(Path file);

    /** Create a DocFile for a file that will be opened for writing. */
    abstract DocFile createFileForOutput(DocPath path);

    /**
     * List the directories and files found in subdirectories along the
     * elements of the given location.
     * @param location currently, only {@link StandardLocation#SOURCE_PATH} is supported.
     * @param path the subdirectory of the directories of the location for which to
     *  list files
     */
    abstract Iterable<DocFile> list(Location location, DocPath path);
}
