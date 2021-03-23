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

import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.util.DocTreeFactory;
import com.sun.source.util.SimpleDocTreeVisitor;

public class TreeConverter {

    private static final Pattern SCAN_RE
        = Pattern.compile("««@([0-9]+)»»");

    private MarkdownProcessor processor;
    private DocTreeFactory docTreeFactory;

    public TreeConverter(MarkdownProcessor processor,
            DocTreeFactory docTreeFactory) {
        this.processor = processor;
        this.docTreeFactory = docTreeFactory;
    }

    private String toMarkdownSource(List<DocTree> specials,
            List<? extends DocTree> tree) {
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
                public Void visitErroneous(ErroneousTree node,
                        StringBuilder sb) {
                    sb.append(node.toString());
                    return null;
                }

                @Override
                protected Void defaultAction(DocTree node, StringBuilder sb) {
                    sb.append("««@" + specials.size() + "»»");
                    specials.add(node);
                    return null;
                }
            };
        StringBuilder sb = new StringBuilder();
        v.visit(tree, sb);
        return sb.toString();
    }

    private List<DocTree> mdOutToDocTrees(List<DocTree> specials,
            String htmlText) {
        // Re-insert specials
        List<DocTree> replacement = new ArrayList<>();
        Matcher matcher = SCAN_RE.matcher(htmlText);
        int emittedUpTo = 0;
        while (matcher.find()) {
            if (matcher.start() > emittedUpTo) {
                replacement.add(docTreeFactory.newTextTree(
                    htmlText.substring(emittedUpTo, matcher.start())));
            }
            try {
                int idx = Integer.parseInt(matcher.group(1));
                replacement.add(specials.get(idx));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            }
            emittedUpTo = matcher.end();
        }
        if (htmlText.length() > emittedUpTo) {
            replacement.add(docTreeFactory.newTextTree(
                htmlText.substring(emittedUpTo, htmlText.length())));
        }
        return replacement;
    }

    /**
     * Converts a complete description.
     * 
     * @param tree the tree to convert
     * @return the result
     */
    public List<? extends DocTree>
            convertDescription(List<? extends DocTree> tree) {
        if (tree.isEmpty()) {
            return tree;
        }
        List<DocTree> specials = new ArrayList<>();
        String markdownSource = toMarkdownSource(specials, tree);
        String transformed = processor.toHtml(markdownSource);
        List<DocTree> replacement = mdOutToDocTrees(specials, transformed);
        return replacement;
    }

    /**
     * Converts a fragment such as the description of a tag. An attempt is
     * made to remove any surrounding HTML tag added by the markdown
     * processor.
     * 
     * @param tree the tree to convert
     * @return the result
     */
    public List<? extends DocTree>
            convertFragment(List<? extends DocTree> tree) {
        if (tree.isEmpty()) {
            return tree;
        }
        List<DocTree> specials = new ArrayList<>();
        String markdownSource = toMarkdownSource(specials, tree);
        String transformed = processor.toHtmlFragment(markdownSource);
        List<DocTree> replacement = mdOutToDocTrees(specials, transformed);
        return replacement;
    }

    /**
     * Default conversion is a noop. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, DocTree tree) {
        // Late binding doesn't work with interfaces, sigh...
        if (tree instanceof AuthorTree) {
            convertTag(target, (AuthorTree) tree);
            return;
        }
        if (tree instanceof DeprecatedTree) {
            convertTag(target, (DeprecatedTree) tree);
            return;
        }
        if (tree instanceof ParamTree) {
            convertTag(target, (ParamTree) tree);
            return;
        }
        if (tree instanceof ReturnTree) {
            convertTag(target, (ReturnTree) tree);
            return;
        }
        if (tree instanceof SeeTree) {
            convertTag(target, (SeeTree) tree);
            return;
        }
        if (tree instanceof SinceTree) {
            convertTag(target, (SinceTree) tree);
            return;
        }
        if (tree instanceof ThrowsTree) {
            convertTag(target, (ThrowsTree) tree);
            return;
        }
        if (tree instanceof VersionTree) {
            convertTag(target, (VersionTree) tree);
            return;
        }
        target.add(tree);
    }

    /**
     * Converts a {@link AuthorTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, AuthorTree tree) {
        target.add(docTreeFactory
            .newAuthorTree(convertFragment(tree.getName())));
    }

    /**
     * Converts a {@link DeprecatedTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, DeprecatedTree tree) {
        target.add(docTreeFactory
            .newDeprecatedTree(convertFragment(tree.getBody())));
    }

    /**
     * Converts a {@link ParamTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, ParamTree tree) {
        target.add(docTreeFactory.newParamTree(tree.isTypeParameter(),
            tree.getName(), convertFragment(tree.getDescription())));
    }

    /**
     * Converts a {@link ReturnTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, ReturnTree tree) {
        target.add(docTreeFactory
            .newReturnTree(convertFragment(tree.getDescription())));
    }

    /**
     * Converts a {@link SeeTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, SeeTree tree) {
        if (tree.getReference().size() > 0
            && (tree.getReference().get(0) instanceof ReferenceTree
                || tree.getReference().get(0) instanceof StartElementTree)) {
            target.add(tree);
            return;
        }
        target.add(docTreeFactory
            .newSeeTree(convertFragment(tree.getReference())));
    }

    /**
     * Converts a {@link SinceTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, SinceTree tree) {
        target.add(docTreeFactory.newSinceTree(tree.getBody()));
    }

    /**
     * Converts a {@link ThrowsTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, ThrowsTree tree) {
        target.add(docTreeFactory.newThrowsTree(tree.getExceptionName(),
            convertFragment(tree.getDescription())));
    }

    /**
     * Converts a {@link VersionTree}. See the overview for a description
     * of the behavior. 
     * 
     * @param target the list to append the result to 
     * @param tree the tree to convert
     */
    public void convertTag(List<DocTree> target, VersionTree tree) {
        target.add(docTreeFactory.newVersionTree(tree.getBody()));
    }

}
