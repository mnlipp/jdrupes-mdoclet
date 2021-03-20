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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreeFactory;
import com.sun.source.util.SimpleDocTreeVisitor;

/**
 * Pipes the results of the methods through the markdown processor.
 */
public class DocCommentTreeWrapper implements DocCommentTree {

    private static final Pattern SCAN_RE
        = Pattern.compile("\\{@([0-9]+)\\}");

    private final MDoclet doclet;
    private final MDocletEnvironment environment;
    private DocCommentTree tree;

    public DocCommentTreeWrapper(MDoclet doclet, MDocletEnvironment environment,
            DocCommentTree tree) {
        super();
        this.doclet = doclet;
        this.environment = environment;
        this.tree = tree;
    }

    private List<? extends DocTree>
            buildReplacement(List<? extends DocTree> tree) {

        if (tree.isEmpty()) {
            return tree;
        }

        List<DocTree> specials = new ArrayList<>();
        SimpleDocTreeVisitor<Void, StringBuilder> v
            = new SimpleDocTreeVisitor<>() {

                @Override
                public Void visitText(TextTree node, StringBuilder sb) {
                    sb.append(node.toString());
                    return null;
                }

                /**
                 * Parsing is done, literals can be converted to text.
                 * 
                 * {@inheritDoc}
                 */
                @Override
                public Void visitLiteral(LiteralTree node, StringBuilder sb) {
                    sb.append(node.getBody().toString());
                    return null;
                }

                @Override
                public Void visitStartElement(StartElementTree node,
                        StringBuilder sb) {
                    sb.append(node.toString());
                    return null;
                }

                @Override
                public Void visitEndElement(EndElementTree node,
                        StringBuilder sb) {
                    sb.append(node.toString());
                    return null;
                }

                @Override
                protected Void defaultAction(DocTree node, StringBuilder sb) {
                    sb.append("\\{@" + specials.size() + "\\}");
                    specials.add(node);
                    return null;
                }
            };
        StringBuilder sb = new StringBuilder();
        v.visit(tree, sb);

        // Transform to HTML
        String transformed = doclet.getProcessor().toHtml(sb.toString());
        List<DocTree> replacement = buildMdTree(specials, transformed);

        return replacement;
    }

    private List<DocTree> buildMdTree(List<DocTree> specials, String mdText) {
        // Re-insert specials
        DocTreeFactory dtf = environment.getDocTrees().getDocTreeFactory();
        List<DocTree> replacement = new ArrayList<>();
        Matcher matcher = SCAN_RE.matcher(mdText);
        int emittedUpTo = 0;
        while (matcher.find()) {
            if (matcher.start() > emittedUpTo) {
                replacement.add(dtf.newTextTree(
                    mdText.substring(emittedUpTo, matcher.start())));
            }
            try {
                int idx = Integer.parseInt(matcher.group(1));
                replacement.add(specials.get(idx));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            }
            emittedUpTo = matcher.end();
        }
        if (mdText.length() > emittedUpTo) {
            replacement.add(dtf.newTextTree(
                mdText.substring(emittedUpTo, mdText.length())));
        }
        return replacement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getFirstSentence()
     */
    public List<? extends DocTree> getFirstSentence() {
        return buildReplacement(tree.getFirstSentence());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getBody()
     */
    public List<? extends DocTree> getBody() {
        return buildReplacement(tree.getBody());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.sun.source.doctree.DocCommentTree#getBlockTags()
     */
    public List<? extends DocTree> getBlockTags() {
        return tree.getBlockTags();
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
        return tree.accept(visitor, data);
    }

}
