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

import java.util.ArrayList;
import java.util.List;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.tools.javac.tree.DCTree;

/**
 * Feeds the results of the methods through the {@link TreeConverter}
 * where appropriate.
 * 
 * With JDK 11, {@link DocCommentTreeWrapper} only had to implement
 * {@link DocCommentTree}. Starting with (at least) JDK 15, javadoc
 * casts (without check) {@link DocCommentTree} instances to
 * {@link DCTree.DCDocComment} when reporting errors e.g. about
 * a missing link target. That's the only reason why the
 * wrapper extends {@link DCTree.DCDocComment}.
 */
public class DocCommentTreeWrapper extends DCTree.DCDocComment
        implements DocCommentTree {

    private DocCommentTree tree;
    private TreeConverter treeConverter;
    private List<DocTree> fullBody;
    private List<DocTree> firstSentence;
    private List<DocTree> body;
    private List<DocTree> blockTags;

    public DocCommentTreeWrapper(MDoclet doclet, MDocletEnvironment environment,
            DocCommentTree tree) {
        super(tree instanceof DCTree.DCDocComment
            ? ((DCTree.DCDocComment) tree).comment
            : null,
            null, null, null, null, null, null);
        this.tree = tree;
        treeConverter = new TreeConverter(doclet.getProcessor(),
            environment.getDocTrees().getDocTreeFactory(),
            environment.getElementUtils());
    }

    /**
     * Overridden by {@link DCTree.DCDocComment}, restore to default.
     */
    @Override
    public List<? extends DocTree> getFullBody() {
        if (fullBody == null) {
            fullBody = new ArrayList<>();
            fullBody.addAll(getFirstSentence());
            fullBody.addAll(getBody());
        }
        return fullBody;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getFirstSentence()
     */
    public List<? extends DocTree> getFirstSentence() {
        if (firstSentence == null) {
            firstSentence
                = treeConverter.convertFragment(tree.getFirstSentence());
        }
        return firstSentence;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getBody()
     */
    public List<? extends DocTree> getBody() {
        if (body == null) {
            body = treeConverter.convertDescription(tree.getBody());
        }
        return body;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getBlockTags()
     */
    public List<? extends DocTree> getBlockTags() {
        if (blockTags == null) {
            List<? extends DocTree> origTags = tree.getBlockTags();
            blockTags = new ArrayList<>();
            for (DocTree tree : origTags) {
                treeConverter.convertTag(blockTags, tree);
            }
        }
        return blockTags;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getPreamble()
     */
    public List<? extends DocTree> getPreamble() {
        return tree.getPreamble();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getPostamble()
     */
    public List<? extends DocTree> getPostamble() {
        return tree.getPostamble();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocTree#getKind()
     */
    public Kind getKind() {
        return tree.getKind();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocTree#accept(com.sun.source.doctree.DocTreeVisitor, java.lang.Object)
     */
    public <R, D> R accept(DocTreeVisitor<R, D> visitor, D data) {
        // Work around NPE when reporting param related problems.
        if (tree instanceof DCTree.DCDocComment
            && visitor.getClass().getName()
                .equals("com.sun.source.util.DocTreePath$1PathFinder")) {
            return visitor.visitDocComment(this, data);
        }
        return tree.accept(visitor, data);
    }

}
