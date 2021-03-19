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

import com.sun.tools.javac.parser.Tokens.Comment;

public class ConvertedComment implements Comment {

    private Comment mdComment;
    private String converted;

    public ConvertedComment(Comment mdComment, String converted) {
        this.mdComment = mdComment;
        this.converted = converted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.tools.javac.parser.Tokens.Comment#getText()
     */
    @Override
    public String getText() {
        return converted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.tools.javac.parser.Tokens.Comment#getSourcePos(int)
     */
    @Override
    public int getSourcePos(int index) {
        return mdComment.getSourcePos(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.tools.javac.parser.Tokens.Comment#getStyle()
     */
    @Override
    public CommentStyle getStyle() {
        return mdComment.getStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.tools.javac.parser.Tokens.Comment#isDeprecated()
     */
    @Override
    public boolean isDeprecated() {
        return mdComment.isDeprecated();
    }

}
