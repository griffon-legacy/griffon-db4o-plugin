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

package griffon.plugins.db4o

import com.db4o.ObjectContainer

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class ObjectContainerHolder {
    private static final String DEFAULT = 'default'
    private static final Object[] LOCK = new Object[0]
    private final Map<String, ObjectContainer> containers = [:]

    private static final ObjectContainerHolder INSTANCE

    static {
        INSTANCE = new ObjectContainerHolder()
    }

    static ObjectContainerHolder getInstance() {
        INSTANCE
    }

    private ObjectContainerHolder() {}

    String[] getObjectContainerNames() {
        List<String> dataSourceNames = new ArrayList().addAll(containers.keySet())
        dataSourceNames.toArray(new String[dataSourceNames.size()])
    }

    ObjectContainer getObjectContainer(String dataSourceName = DEFAULT) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        retrieveObjectContainer(dataSourceName)
    }

    void setObjectContainer(String dataSourceName = DEFAULT, ObjectContainer container) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        storeObjectContainer(dataSourceName, container)
    }

    boolean isObjectContainerConnected(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        retrieveObjectContainer(dataSourceName) != null
    }
    
    void disconnectObjectContainer(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        storeObjectContainer(dataSourceName, null)
    }

    ObjectContainer fetchObjectContainer(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        ObjectContainer container = retrieveObjectContainer(dataSourceName)
        if (container == null) {
            GriffonApplication app = ApplicationHolder.application
            ConfigObject config = Db4oConnector.instance.createConfig(app)
            container = Db4oConnector.instance.connect(app, config, dataSourceName)
        }

        if (container == null) {
            throw new IllegalArgumentException("No such ObjectContainer configuration for name $dataSourceName")
        }
        container
    }

    private ObjectContainer retrieveObjectContainer(String dataSourceName) {
        synchronized(LOCK) {
            containers[dataSourceName]
        }
    }

    private void storeObjectContainer(String dataSourceName, ObjectContainer container) {
        synchronized(LOCK) {
            containers[dataSourceName] = container
        }
    }
}
