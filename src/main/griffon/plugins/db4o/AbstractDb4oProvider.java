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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.ObjectContainer;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public abstract class AbstractDb4oProvider implements Db4oProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDb4oProvider.class);
    private static final String DEFAULT = "default";

    public <R> R withDb4o(Closure<R> closure) {
        return withDb4o(DEFAULT, closure);
    }

    public <R> R withDb4o(String dataSourceName, Closure<R> closure) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT;
        if (closure != null) {
            ObjectContainer container = getObjectContainer(dataSourceName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statement on dataSourceName '" + dataSourceName + "'");
            }
            return closure.call(dataSourceName, container);
        }
        return null;
    }

    public <R> R withDb4o(CallableWithArgs<R> callable) {
        return withDb4o(DEFAULT, callable);
    }

    public <R> R withDb4o(String dataSourceName, CallableWithArgs<R> callable) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT;
        if (callable != null) {
            ObjectContainer container = getObjectContainer(dataSourceName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statement on dataSourceName '" + dataSourceName + "'");
            }
            callable.setArgs(new Object[]{dataSourceName, container});
            return callable.call();
        }
        return null;
    }

    protected abstract ObjectContainer getObjectContainer(String dataSourceName);
}