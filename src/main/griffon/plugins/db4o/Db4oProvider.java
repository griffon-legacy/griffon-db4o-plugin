/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2012 Andres Almiray

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

package griffon.plugins.db4o;

import groovy.lang.Closure;
import griffon.util.CallableWithArgs;

/**
 * @author Andres Almiray
 */
public interface Db4oProvider {
    Object withDb4o(Closure closure);

    Object withDb4o(String dataSourceName, Closure closure);

    <T> T withDb4o(CallableWithArgs<T> callable);

    <T> T withDb4o(String dataSourceName, CallableWithArgs<T> callable);
}
