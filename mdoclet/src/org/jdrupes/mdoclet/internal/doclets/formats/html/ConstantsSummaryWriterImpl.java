/*
 * Copyright (c) 2001, 2023, Oracle and/or its affiliates. All rights reserved.
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

package org.jdrupes.mdoclet.internal.doclets.formats.html;

import java.util.Collection;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.jdrupes.mdoclet.internal.doclets.formats.html.Navigation.PageMode;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.BodyContents;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.ContentBuilder;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.Entity;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.HtmlId;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.HtmlStyle;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.HtmlTree;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.TagName;
import org.jdrupes.mdoclet.internal.doclets.formats.html.markup.Text;
import org.jdrupes.mdoclet.internal.doclets.toolkit.ConstantsSummaryWriter;
import org.jdrupes.mdoclet.internal.doclets.toolkit.Content;
import org.jdrupes.mdoclet.internal.doclets.toolkit.util.DocFileIOException;
import org.jdrupes.mdoclet.internal.doclets.toolkit.util.DocLink;
import org.jdrupes.mdoclet.internal.doclets.toolkit.util.DocPaths;
import org.jdrupes.mdoclet.internal.doclets.toolkit.util.IndexItem;

/**
 * Write the Constants Summary Page in HTML format.
 */
public class ConstantsSummaryWriterImpl extends HtmlDocletWriter
        implements ConstantsSummaryWriter {

    /**
     * The current class being documented.
     */
    private TypeElement currentTypeElement;

    private final TableHeader constantsTableHeader;

    /**
     * The HTML tree for constant values summary currently being written.
     */
    private HtmlTree summarySection;

    private final BodyContents bodyContents = new BodyContents();

    private boolean hasConstants = false;

    /**
     * Construct a ConstantsSummaryWriter.
     * @param configuration the configuration used in this run
     *        of the standard doclet.
     */
    public ConstantsSummaryWriterImpl(HtmlConfiguration configuration) {
        super(configuration, DocPaths.CONSTANT_VALUES);
        constantsTableHeader = new TableHeader(
            contents.modifierAndTypeLabel, contents.constantFieldLabel,
            contents.valueLabel);
        configuration.conditionalPages
            .add(HtmlConfiguration.ConditionalPage.CONSTANT_VALUES);
    }

    @Override
    public Content getHeader() {
        String label = resources.getText("doclet.Constants_Summary");
        HtmlTree body = getBody(getWindowTitle(label));
        bodyContents.setHeader(getHeader(PageMode.CONSTANT_VALUES));
        return body;
    }

    @Override
    public Content getContentsHeader() {
        return HtmlTree.UL(HtmlStyle.contentsList);
    }

    @Override
    public void addLinkToPackageContent(String abbrevPackageName,
            Content content) {
        // add link to summary
        Content link;
        if (abbrevPackageName.isEmpty()) {
            link = links.createLink(HtmlIds.UNNAMED_PACKAGE_ANCHOR,
                contents.defaultPackageLabel, "");
        } else {
            Content packageNameContent = Text.of(abbrevPackageName + ".*");
            link = links.createLink(DocLink.fragment(abbrevPackageName),
                packageNameContent, "");
        }
        content.add(HtmlTree.LI(link));
    }

    @Override
    public void addContentsList(Content content) {
        Content titleContent = contents.constantsSummaryTitle;
        var pHeading = HtmlTree.HEADING_TITLE(Headings.PAGE_TITLE_HEADING,
            HtmlStyle.title, titleContent);
        var div = HtmlTree.DIV(HtmlStyle.header, pHeading);
        bodyContents.addMainContent(div);
        Content headingContent = contents.contentsHeading;
        var heading = HtmlTree.HEADING_TITLE(Headings.CONTENT_HEADING,
            headingContent);
        var section = HtmlTree.SECTION(HtmlStyle.packages, heading);
        section.add(content);
        bodyContents.addMainContent(section);
    }

    @Override
    public Content getConstantSummaries() {
        return new ContentBuilder();
    }

    @Override
    public void addPackageGroup(String abbrevPackageName, Content toContent) {
        Content headingContent;
        HtmlId anchorName;
        if (abbrevPackageName.isEmpty()) {
            anchorName = HtmlIds.UNNAMED_PACKAGE_ANCHOR;
            headingContent = contents.defaultPackageLabel;
        } else {
            anchorName = htmlIds.forPackageName(abbrevPackageName);
            headingContent = new ContentBuilder(
                getPackageLabel(abbrevPackageName),
                Text.of(".*"));
        }
        var heading = HtmlTree.HEADING_TITLE(
            Headings.ConstantsSummary.PACKAGE_HEADING,
            headingContent);
        summarySection = HtmlTree.SECTION(HtmlStyle.constantsSummary, heading)
            .setId(anchorName);

        toContent.add(summarySection);
    }

    @Override
    public Content getClassConstantHeader() {
        return HtmlTree.UL(HtmlStyle.blockList);
    }

    @Override
    public void addClassConstant(Content fromClassConstant) {
        summarySection.add(fromClassConstant);
        hasConstants = true;
    }

    @Override
    public void addConstantMembers(TypeElement typeElement,
            Collection<VariableElement> fields,
            Content target) {
        currentTypeElement = typeElement;

        // generate links backward only to public classes.
        Content classLink
            = (utils.isPublic(typeElement) || utils.isProtected(typeElement))
                ? getLink(new HtmlLinkInfo(configuration,
                    HtmlLinkInfo.Kind.SHOW_TYPE_PARAMS_IN_LABEL, typeElement))
                : Text.of(utils.getFullyQualifiedName(typeElement));

        PackageElement enclosingPackage = utils.containingPackage(typeElement);
        Content caption = new ContentBuilder();
        if (!enclosingPackage.isUnnamed()) {
            caption.add(enclosingPackage.getQualifiedName());
            caption.add(".");
        }
        caption.add(classLink);

        var table = new Table<Void>(HtmlStyle.summaryTable)
            .setCaption(caption)
            .setHeader(constantsTableHeader)
            .setColumnStyles(HtmlStyle.colFirst, HtmlStyle.colSecond,
                HtmlStyle.colLast);

        for (VariableElement field : fields) {
            table.addRow(getTypeColumn(field), getNameColumn(field),
                getValue(field));
        }
        target.add(HtmlTree.LI(table));
    }

    /**
     * Get the type column for the constant summary table row.
     *
     * @param member the field to be documented.
     * @return the type column of the constant table row
     */
    private Content getTypeColumn(VariableElement member) {
        Content typeContent = new ContentBuilder();
        var code = new HtmlTree(TagName.CODE)
            .setId(htmlIds.forMember(currentTypeElement, member));
        for (Modifier mod : member.getModifiers()) {
            code.add(Text.of(mod.toString()))
                .add(Entity.NO_BREAK_SPACE);
        }
        Content type = getLink(new HtmlLinkInfo(configuration,
            HtmlLinkInfo.Kind.LINK_TYPE_PARAMS_AND_BOUNDS, member.asType()));
        code.add(type);
        typeContent.add(code);
        return typeContent;
    }

    /**
     * Get the name column for the constant summary table row.
     *
     * @param member the field to be documented.
     * @return the name column of the constant table row
     */
    private Content getNameColumn(VariableElement member) {
        Content nameContent = getDocLink(HtmlLinkInfo.Kind.PLAIN,
            member, member.getSimpleName());
        return HtmlTree.CODE(nameContent);
    }

    /**
     * Get the value column for the constant summary table row.
     *
     * @param member the field to be documented.
     * @return the value column of the constant table row
     */
    private Content getValue(VariableElement member) {
        String value = utils.constantValueExpression(member);
        return HtmlTree.CODE(Text.of(value));
    }

    @Override
    public void addConstantSummaries(Content content) {
        bodyContents.addMainContent(content);
    }

    @Override
    public void addFooter() {
        bodyContents.setFooter(getFooter());
    }

    @Override
    public void printDocument(Content content) throws DocFileIOException {
        content.add(bodyContents);
        printHtmlDocument(null, "summary of constants", content);

        if (hasConstants && configuration.mainIndex != null) {
            configuration.mainIndex.add(IndexItem.of(IndexItem.Category.TAGS,
                resources.getText("doclet.Constants_Summary"), path));
        }
    }
}
