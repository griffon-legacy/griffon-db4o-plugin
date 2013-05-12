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

import lombok.core.BaseConstants;
import lombok.core.util.MethodDescriptor;

import static lombok.core.util.MethodDescriptor.args;
import static lombok.core.util.MethodDescriptor.type;
import static lombok.core.util.MethodDescriptor.typeParams;

/**
 * @author Andres Almiray
 */
public interface Db4oAwareConstants extends BaseConstants {
    String DB4O_PROVIDER_TYPE = "griffon.plugins.db4o.Db4oProvider";
    String DEFAULT_DB4O_PROVIDER_TYPE = "griffon.plugins.db4o.DefaultDb4oProvider";
    String DB4O_CONTRIBUTION_HANDLER_TYPE = "griffon.plugins.db4o.Db4oContributionHandler";
    String DB4O_PROVIDER_FIELD_NAME = "this$db4oProvider";
    String METHOD_GET_DB4O_PROVIDER = "getDb4oProvider";
    String METHOD_SET_DB4O_PROVIDER = "setDb4oProvider";
    String METHOD_WITH_DB4O = "withDb4o";
    String PROVIDER = "provider";

    MethodDescriptor[] METHODS = new MethodDescriptor[] {
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_DB4O,
            args(type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_DB4O,
            args(
                type(JAVA_LANG_STRING),
                type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_DB4O,
            args(type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_DB4O,
            args(
                type(JAVA_LANG_STRING),
                type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        )
    };
}
