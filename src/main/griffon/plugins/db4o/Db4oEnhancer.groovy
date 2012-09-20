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

package griffon.plugins.db4o

import griffon.util.CallableWithArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
final class Db4oEnhancer {
    private static final Logger LOG = LoggerFactory.getLogger(Db4oEnhancer)

    private Db4oEnhancer() {}

    static void enhance(MetaClass mc, Db4oProvider provider = ObjectContainerHolder.instance) {
        if(LOG.debugEnabled) LOG.debug("Enhancing $mc with $provider")
        mc.withDb4o = {Closure closure ->
            provider.withDb4o('default', closure)
        }
        mc.withDb4o << {String dataSourceName, Closure closure ->
            provider.withDb4o(dataSourceName, closure)
        }
        mc.withDb4o << {CallableWithArgs callable ->
            provider.withDb4o('default', callable)
        }
        mc.withDb4o << {String dataSourceName, CallableWithArgs callable ->
            provider.withDb4o(dataSourceName, callable)
        }
    }
}
