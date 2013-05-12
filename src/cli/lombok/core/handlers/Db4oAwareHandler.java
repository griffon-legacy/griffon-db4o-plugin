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

package lombok.core.handlers;

import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IType;

import static lombok.ast.AST.*;

/**
 * @author Andres Almiray
 */
public abstract class Db4oAwareHandler<TYPE_TYPE extends IType<? extends IMethod<?, ?, ?, ?>, ?, ?, ?, ?, ?>> extends AbstractHandler<TYPE_TYPE> implements Db4oAwareConstants {
    private Expression<?> defaultDb4oProviderInstance() {
        return Call(Name(DEFAULT_DB4O_PROVIDER_TYPE), "getInstance");
    }

    public void addDb4oProviderField(final TYPE_TYPE type) {
        addField(type, DB4O_PROVIDER_TYPE, DB4O_PROVIDER_FIELD_NAME, defaultDb4oProviderInstance());
    }

    public void addDb4oProviderAccessors(final TYPE_TYPE type) {
        type.editor().injectMethod(
            MethodDecl(Type(VOID), METHOD_SET_DB4O_PROVIDER)
                .makePublic()
                .withArgument(Arg(Type(DB4O_PROVIDER_TYPE), PROVIDER))
                .withStatement(
                    If(Equal(Name(PROVIDER), Null()))
                        .Then(Block()
                            .withStatement(Assign(Field(DB4O_PROVIDER_FIELD_NAME), defaultDb4oProviderInstance())))
                        .Else(Block()
                            .withStatement(Assign(Field(DB4O_PROVIDER_FIELD_NAME), Name(PROVIDER)))))
        );

        type.editor().injectMethod(
            MethodDecl(Type(DB4O_PROVIDER_TYPE), METHOD_GET_DB4O_PROVIDER)
                .makePublic()
                .withStatement(Return(Field(DB4O_PROVIDER_FIELD_NAME)))
        );
    }

    public void addDb4oContributionMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, METHODS, Field(DB4O_PROVIDER_FIELD_NAME));
    }
}
