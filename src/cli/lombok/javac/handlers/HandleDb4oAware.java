/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2012-2013 Andres Almiray

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ---------------------------------------------------------------------
*/

package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import griffon.plugins.db4o.Db4oAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.Db4oAwareConstants;
import lombok.core.handlers.Db4oAwareHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
public class HandleDb4oAware extends JavacAnnotationHandler<Db4oAware> {
    private final JavacDb4oAwareHandler handler = new JavacDb4oAwareHandler();

    @Override
    public void handle(final AnnotationValues<Db4oAware> annotation, final JCTree.JCAnnotation source, final JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, Db4oAware.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(Db4oAware.class));
            return;
        }

        JavacUtil.addInterface(type.node(), Db4oAwareConstants.DB4O_CONTRIBUTION_HANDLER_TYPE);
        handler.addDb4oProviderField(type);
        handler.addDb4oProviderAccessors(type);
        handler.addDb4oContributionMethods(type);
        type.editor().rebuild();
    }

    private static class JavacDb4oAwareHandler extends Db4oAwareHandler<JavacType> {
    }
}
