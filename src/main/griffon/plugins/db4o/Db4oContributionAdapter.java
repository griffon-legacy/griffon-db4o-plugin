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

import java.util.Map;

/**
 * @author Andres Almiray
 */
public class Db4oContributionAdapter implements Db4oContributionHandler {
    private static final String DEFAULT = "default";

    private Db4oProvider provider = DefaultDb4oProvider.getInstance();

    public void setDb4oProvider(Db4oProvider provider) {
        this.provider = provider != null ? provider : DefaultDb4oProvider.getInstance();
    }

    public Db4oProvider getDb4oProvider() {
        return provider;
    }

    public <R> R withDb4o(Closure<R> closure) {
        return withDb4o(DEFAULT, closure);
    }

    public <R> R withDb4o(String dataSourceName, Closure<R> closure) {
        return provider.withDb4o(dataSourceName, closure);
    }

    public <R> R withDb4o(CallableWithArgs<R> callable) {
        return withDb4o(DEFAULT, callable);
    }

    public <R> R withDb4o(String dataSourceName, CallableWithArgs<R> callable) {
        return provider.withDb4o(dataSourceName, callable);
    }
}