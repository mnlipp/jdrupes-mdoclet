/*
 * Copyright (c) 2016, 2022, Oracle and/or its affiliates. All rights reserved.
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

import org.jdrupes.mdoclet.internal.doclets.toolkit.DocletException;


/**
 * An exception with a user-friendly detail message.
 */
public class SimpleDocletException extends DocletException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly detail message.
     *
     * @param message a localized detail message, suitable for direct presentation to the end user
     */
    public SimpleDocletException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-friendly detail message, and underlying cause.
     * The cause may be used for debugging but in normal use, should not be presented to the user.
     *
     * @param message a localized detail message, suitable for direct presentation to the end user
     * @param cause the underlying cause for the exception
     */
    public SimpleDocletException(String message, Throwable cause) {
        super(message, cause);
    }
}
