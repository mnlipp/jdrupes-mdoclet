/*
 * JDrupes MDoclet
 * Copyright (C) 2023 Michael N. Lipp
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

package org.jdrupes.mdoclet.writers;

import jdk.javadoc.internal.doclets.formats.html.markup.HtmlId;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.HtmlIds;
import jdk.javadoc.internal.doclets.formats.html.Navigation.PageMode;

public class HtmlIdsWrapper {

    private HtmlIds delegee;

    private HtmlConfiguration configuration;

    private Utils utils;

    public static final HtmlId ANNOTATION_TYPE_REQUIRED_ELEMENT_SUMMARY
        = HtmlId.of("annotation-interface-required-element-summary");
    public static final HtmlId ANNOTATION_TYPE_OPTIONAL_ELEMENT_SUMMARY
        = HtmlId.of("annotation-interface-optional-element-summary");

    public HtmlIdsWrapper(HtmlConfiguration configuration, HtmlIds delegee) {
        this.configuration = configuration;
        this.utils = configuration.utils;
        this.delegee = delegee;
    }

    /**
     * Returns an id for an executable element, including the context
     * of its documented enclosing class or interface.
     *
     * @param typeElement the enclosing class or interface
     * @param member      the element
     *
     * @return the id
     */
    HtmlId forMember(TypeElement typeElement, ExecutableElement member) {
        return HtmlId.of(
            utils.getSimpleName(member) + utils.signature(member, typeElement));
    }
}
