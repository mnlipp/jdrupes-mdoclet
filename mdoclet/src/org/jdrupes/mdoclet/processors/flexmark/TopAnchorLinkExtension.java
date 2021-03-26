/*
 * JDrupes MDoclet
 * Copyright (C) 2017, 2021  Michael N. Lipp
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

package org.jdrupes.mdoclet.processors.flexmark;

import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlRenderer.Builder;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.html.MutableAttributes;
import com.vladsch.flexmark.util.misc.Extension;

/**
 * Provides an extension that adds "`target='_top'`" to all anchor links.
 */
public class TopAnchorLinkExtension
        implements HtmlRenderer.HtmlRendererExtension {

    /**
     * Called with all options. Currently this extension supports no options and
     * does nothing.
     */
    @Override
    public void rendererOptions(MutableDataHolder options) {
        // Currently no options supported.
    }

    /**
     * This method is called once when the builder for the output is created.
     * It must modify the passed in builder as required by the extension.  
     */
    @Override
    public void extend(Builder rendererBuilder, String rendererType) {
        rendererBuilder.attributeProviderFactory(
            new IndependentAttributeProviderFactory() {

                @Override
                public @NotNull AttributeProvider
                        apply(@NotNull LinkResolverContext context) {
                    return new AttributeProvider() {

                        @Override
                        public void setAttributes(@NotNull Node node,
                                @NotNull AttributablePart part,
                                @NotNull MutableAttributes attributes) {
                            if (node instanceof Link
                                && part == AttributablePart.LINK) {
                                attributes.replaceValue("target", "_top");
                            }
                        }

                    };
                }
            });
    }

    /**
     * Extensions are added by providing a class to flexmark. Flexmark then
     * invokes the extension's static `create` method through reflection.
     * 
     * @return an instance of the extension
     */
    public static Extension create() {
        return new TopAnchorLinkExtension();
    }
}