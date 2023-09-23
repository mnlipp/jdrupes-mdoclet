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

package org.jdrupes.mdoclet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.jdrupes.mdoclet.writers.AnnotationTypeMemberWriterImpl;

import jdk.javadoc.internal.doclets.formats.html.ClassWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.ConstantsSummaryWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.ConstructorWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.DocFilesHandlerImpl;
import jdk.javadoc.internal.doclets.formats.html.EnumConstantWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.FieldWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.MethodWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.ModuleWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.NestedClassWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.PackageWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.PropertyWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.SerializedFormWriterImpl;
import jdk.javadoc.internal.doclets.formats.html.SubWriterHolderWriter;
import jdk.javadoc.internal.doclets.toolkit.ClassWriter;
import jdk.javadoc.internal.doclets.toolkit.ConstantsSummaryWriter;
import jdk.javadoc.internal.doclets.toolkit.DocFilesHandler;
import jdk.javadoc.internal.doclets.toolkit.MemberSummaryWriter;
import jdk.javadoc.internal.doclets.toolkit.ModuleSummaryWriter;
import jdk.javadoc.internal.doclets.toolkit.PackageSummaryWriter;
import jdk.javadoc.internal.doclets.toolkit.SerializedFormWriter;
import jdk.javadoc.internal.doclets.toolkit.WriterFactory;
import jdk.javadoc.internal.doclets.toolkit.util.ClassTree;
import jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberTable;

public class MarkdownWriterFactory implements WriterFactory {

    private final MarkdownConfiguration configuration;

    public MarkdownWriterFactory(MarkdownConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ConstantsSummaryWriter getConstantsSummaryWriter() {
        return new ConstantsSummaryWriterImpl(configuration);
    }

    @Override
    public PackageSummaryWriter
            getPackageSummaryWriter(PackageElement packageElement) {
        return new PackageWriterImpl(configuration, packageElement);
    }

    @Override
    public ModuleSummaryWriter getModuleSummaryWriter(ModuleElement mdle) {
        return new ModuleWriterImpl(configuration, mdle);
    }

    @Override
    public ClassWriter getClassWriter(TypeElement typeElement,
            ClassTree classTree) {
        return new ClassWriterImpl(configuration, typeElement, classTree);
    }

    @Override
    public AnnotationTypeMemberWriterImpl getAnnotationTypeMemberWriter(
            ClassWriter classWriter) {
        TypeElement te = classWriter.getTypeElement();
        return new AnnotationTypeMemberWriterImpl(
            (ClassWriterImpl) classWriter, te,
            AnnotationTypeMemberWriterImpl.Kind.ANY);
    }

    @Override
    public AnnotationTypeMemberWriterImpl getAnnotationTypeOptionalMemberWriter(
            ClassWriter classWriter) {
        TypeElement te = classWriter.getTypeElement();
        return new AnnotationTypeMemberWriterImpl(
            (ClassWriterImpl) classWriter, te,
            AnnotationTypeMemberWriterImpl.Kind.OPTIONAL);
    }

    @Override
    public AnnotationTypeMemberWriterImpl getAnnotationTypeRequiredMemberWriter(
            ClassWriter classWriter) {
        TypeElement te = classWriter.getTypeElement();
        return new AnnotationTypeMemberWriterImpl(
            (ClassWriterImpl) classWriter, te,
            AnnotationTypeMemberWriterImpl.Kind.REQUIRED);
    }

    @Override
    public EnumConstantWriterImpl
            getEnumConstantWriter(ClassWriter classWriter) {
        return new EnumConstantWriterImpl((ClassWriterImpl) classWriter,
            classWriter.getTypeElement());
    }

    @Override
    public FieldWriterImpl getFieldWriter(ClassWriter classWriter) {
        return new FieldWriterImpl((ClassWriterImpl) classWriter,
            classWriter.getTypeElement());
    }

    @Override
    public PropertyWriterImpl getPropertyWriter(ClassWriter classWriter) {
        return new PropertyWriterImpl((ClassWriterImpl) classWriter,
            classWriter.getTypeElement());
    }

    @Override
    public MethodWriterImpl getMethodWriter(ClassWriter classWriter) {
        return new MethodWriterImpl((ClassWriterImpl) classWriter,
            classWriter.getTypeElement());
    }

    @Override
    public ConstructorWriterImpl getConstructorWriter(ClassWriter classWriter) {
        return new ConstructorWriterImpl((ClassWriterImpl) classWriter,
            classWriter.getTypeElement());
    }

    @Override
    public MemberSummaryWriter getMemberSummaryWriter(ClassWriter classWriter,
            VisibleMemberTable.Kind memberType) {
        switch (memberType) {
        case CONSTRUCTORS:
            return getConstructorWriter(classWriter);
        case ENUM_CONSTANTS:
            return getEnumConstantWriter(classWriter);
        case ANNOTATION_TYPE_MEMBER_OPTIONAL:
            return getAnnotationTypeOptionalMemberWriter(classWriter);
        case ANNOTATION_TYPE_MEMBER_REQUIRED:
            return getAnnotationTypeRequiredMemberWriter(classWriter);
        case FIELDS:
            return getFieldWriter(classWriter);
        case PROPERTIES:
            return getPropertyWriter(classWriter);
        case NESTED_CLASSES:
            return new NestedClassWriterImpl(
                (SubWriterHolderWriter) classWriter,
                classWriter.getTypeElement());
        case METHODS:
            return getMethodWriter(classWriter);
        default:
            return null;
        }
    }

    @Override
    public SerializedFormWriter getSerializedFormWriter() {
        return new SerializedFormWriterImpl(configuration);
    }

    @Override
    public DocFilesHandler getDocFilesHandler(Element element) {
        return new DocFilesHandlerImpl(configuration, element);
    }

}
