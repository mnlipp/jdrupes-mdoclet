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

import java.io.IOException;
import java.text.BreakIterator;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTreeFactory;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.tree.JCTree;

/**
 * Wraps the {@link DocTrees} passed to the constructor.
 * 
 * The sole purpose of the wrapper is to detect the (hopefully)
 * first access of the parsed AST by an invocation of
 * {@link #getDocCommentTree(TreePath)} which triggers the
 * markdown processind.
 */
public class DocTreesWrapper extends DocTrees {

    private final DocTrees docTrees;

    public DocTreesWrapper(DocTrees docTrees) {
        super();
        this.docTrees = docTrees;
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getBreakIterator()
     */
    public BreakIterator getBreakIterator() {
        return docTrees.getBreakIterator();
    }

    /**
     * Triggers the markdown processing and then delegates to the 
     * docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocCommentTree(com.sun.source.util.TreePath)
     */
    public DocCommentTree getDocCommentTree(TreePath path) {
        JCTree.JCCompilationUnit cu
            = (JCTree.JCCompilationUnit) path.getCompilationUnit();
        CommentProcessor.processComments(cu.docComments,
            this::convertMarkdown);
        return docTrees.getDocCommentTree(path);
    }

    private Tokens.Comment convertMarkdown(Tokens.Comment mdComment) {
        String javadoc = mdComment.getText();
//        String asciidoc = renderer.renderDoc( javadoc );
//        AsciidocComment result = new AsciidocComment( asciidoc, comment );System.err.println( "" );
        return new ConvertedComment(mdComment, "CONVERTED: " + javadoc);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocCommentTree(javax.lang.model.element.Element)
     */
    public DocCommentTree getDocCommentTree(Element e) {
        return docTrees.getDocCommentTree(e);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocCommentTree(javax.tools.FileObject)
     */
    public DocCommentTree getDocCommentTree(FileObject fileObject) {
        return docTrees.getDocCommentTree(fileObject);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTree(javax.lang.model.element.Element)
     */
    public Tree getTree(Element element) {
        return docTrees.getTree(element);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocCommentTree(javax.lang.model.element.Element, java.lang.String)
     */
    public DocCommentTree getDocCommentTree(Element e, String relativePath)
            throws IOException {
        return docTrees.getDocCommentTree(e, relativePath);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTree(javax.lang.model.element.TypeElement)
     */
    public ClassTree getTree(TypeElement element) {
        return docTrees.getTree(element);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTree(javax.lang.model.element.ExecutableElement)
     */
    public MethodTree getTree(ExecutableElement method) {
        return docTrees.getTree(method);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTree(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror)
     */
    public Tree getTree(Element e, AnnotationMirror a) {
        return docTrees.getTree(e, a);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocTreePath(javax.tools.FileObject, javax.lang.model.element.PackageElement)
     */
    public DocTreePath getDocTreePath(FileObject fileObject,
            PackageElement packageElement) {
        return docTrees.getDocTreePath(fileObject, packageElement);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTree(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationValue)
     */
    public Tree getTree(Element e, AnnotationMirror a, AnnotationValue v) {
        return docTrees.getTree(e, a, v);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getPath(com.sun.source.tree.CompilationUnitTree, com.sun.source.tree.Tree)
     */
    public TreePath getPath(CompilationUnitTree unit, Tree node) {
        return docTrees.getPath(unit, node);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getPath(javax.lang.model.element.Element)
     */
    public TreePath getPath(Element e) {
        return docTrees.getPath(e);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getElement(com.sun.source.util.DocTreePath)
     */
    public Element getElement(DocTreePath path) {
        return docTrees.getElement(path);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getPath(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror)
     */
    public TreePath getPath(Element e, AnnotationMirror a) {
        return docTrees.getPath(e, a);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getFirstSentence(java.util.List)
     */
    public List<DocTree> getFirstSentence(List<? extends DocTree> list) {
        return docTrees.getFirstSentence(list);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getPath(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, javax.lang.model.element.AnnotationValue)
     */
    public TreePath getPath(Element e, AnnotationMirror a, AnnotationValue v) {
        return docTrees.getPath(e, a, v);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getSourcePositions()
     */
    public DocSourcePositions getSourcePositions() {
        return docTrees.getSourcePositions();
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getElement(com.sun.source.util.TreePath)
     */
    public Element getElement(TreePath path) {
        return docTrees.getElement(path);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#printMessage(javax.tools.Diagnostic.Kind, java.lang.CharSequence, com.sun.source.doctree.DocTree, com.sun.source.doctree.DocCommentTree, com.sun.source.tree.CompilationUnitTree)
     */
    public void printMessage(Kind kind, CharSequence msg, DocTree t,
            DocCommentTree c, CompilationUnitTree root) {
        docTrees.printMessage(kind, msg, t, c, root);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getTypeMirror(com.sun.source.util.TreePath)
     */
    public TypeMirror getTypeMirror(TreePath path) {
        return docTrees.getTypeMirror(path);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#setBreakIterator(java.text.BreakIterator)
     */
    public void setBreakIterator(BreakIterator breakiterator) {
        docTrees.setBreakIterator(breakiterator);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getScope(com.sun.source.util.TreePath)
     */
    public Scope getScope(TreePath path) {
        return docTrees.getScope(path);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.DocTrees#getDocTreeFactory()
     */
    public DocTreeFactory getDocTreeFactory() {
        return docTrees.getDocTreeFactory();
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getDocComment(com.sun.source.util.TreePath)
     */
    public String getDocComment(TreePath path) {
        return docTrees.getDocComment(path);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#isAccessible(com.sun.source.tree.Scope, javax.lang.model.element.TypeElement)
     */
    public boolean isAccessible(Scope scope, TypeElement type) {
        return docTrees.isAccessible(scope, type);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#isAccessible(com.sun.source.tree.Scope, javax.lang.model.element.Element, javax.lang.model.type.DeclaredType)
     */
    public boolean isAccessible(Scope scope, Element member,
            DeclaredType type) {
        return docTrees.isAccessible(scope, member, type);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getOriginalType(javax.lang.model.type.ErrorType)
     */
    public TypeMirror getOriginalType(ErrorType errorType) {
        return docTrees.getOriginalType(errorType);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#printMessage(javax.tools.Diagnostic.Kind, java.lang.CharSequence, com.sun.source.tree.Tree, com.sun.source.tree.CompilationUnitTree)
     */
    public void printMessage(Kind kind, CharSequence msg, Tree t,
            CompilationUnitTree root) {
        docTrees.printMessage(kind, msg, t, root);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see com.sun.source.util.Trees#getLub(com.sun.source.tree.CatchTree)
     */
    public TypeMirror getLub(CatchTree tree) {
        return docTrees.getLub(tree);
    }

    /**
     * Delegates to the docTrees passed to the constructor.
     * 
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return docTrees.toString();
    }

}
