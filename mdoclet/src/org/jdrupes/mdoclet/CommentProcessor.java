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

import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.JCTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

import com.sun.tools.javac.parser.LazyDocCommentTable;
import com.sun.tools.javac.parser.Tokens.Comment;

public class CommentProcessor {

    static void processComments(DocCommentTable table,
            Function<Comment, Comment> mapper) {
        Map<JCTree, Object> map;
        Function<Object, Object> converter;
        try {
            Field tableField
                = LazyDocCommentTable.class.getDeclaredField("table");
            tableField.setAccessible(true);
            map = (Map<JCTree, Object>) tableField.get(table);

            Class<?> entryClass = Class.forName(
                "com.sun.tools.javac.parser.LazyDocCommentTable$Entry");
            Constructor<?> ctor
                = entryClass.getDeclaredConstructor(Comment.class);
            ctor.setAccessible(true);
            Field commentField = entryClass.getDeclaredField("comment");
            commentField.setAccessible(true);
            Function<Object, Comment> fieldGetter = entry -> {
                try {
                    return (Comment) commentField.get(entry);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            Function<Comment, Object> instantiator = comment -> {
                try {
                    return ctor.newInstance(comment);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            converter = entry -> {
                Comment comment = fieldGetter.apply(entry);
                if (comment instanceof ConvertedComment) {
                    return entry;
                }
                return mapper.andThen(instantiator).apply(comment);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        map.replaceAll((jcTree, entry) -> converter.apply(entry));
    }
}
