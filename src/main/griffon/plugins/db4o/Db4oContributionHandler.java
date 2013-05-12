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

package griffon.plugins.db4o;

import griffon.util.CallableWithArgs;
import groovy.lang.Closure;

/**
 * @author Andres Almiray
 */
public interface Db4oContributionHandler {
    void setDb4oProvider(Db4oProvider provider);

    Db4oProvider getDb4oProvider();

    <R> R withDb4o(Closure<R> closure);

    <R> R withDb4o(String dataSourceName, Closure<R> closure);

    <R> R withDb4o(CallableWithArgs<R> callable);

    <R> R withDb4o(String dataSourceName, CallableWithArgs<R> callable);
}