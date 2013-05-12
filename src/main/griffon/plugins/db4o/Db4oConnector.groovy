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

import griffon.core.GriffonApplication
import griffon.util.Environment
import griffon.util.Metadata
import griffon.util.ConfigUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.db4o.*
import com.db4o.config.EmbeddedConfiguration

/**
 * @author Andres Almiray
 */
@Singleton
final class Db4oConnector {
    private static final String DEFAULT = 'default'
    private static final Logger LOG = LoggerFactory.getLogger(Db4oConnector)
    private bootstrap

    ConfigObject createConfig(GriffonApplication app) {
        if (!app.config.pluginConfig.db4o) {
            app.config.pluginConfig.db4o = ConfigUtils.loadConfigWithI18n('Db4oConfig')
        }
        app.config.pluginConfig.db4o
    }

    private ConfigObject narrowConfig(ConfigObject config, String dataSourceName) {
        if (config.containsKey('dataSource') && dataSourceName == DEFAULT) {
            return config.dataSource
        } else if (config.containsKey('dataSources')) {
            return config.dataSources[dataSourceName]
        }
        return config
    }

    ObjectContainer connect(GriffonApplication app, ConfigObject config, String dataSourceName = DEFAULT) {
        if (ObjectContainerHolder.instance.isObjectContainerConnected(dataSourceName)) {
            return ObjectContainerHolder.instance.getObjectContainer(dataSourceName)
        }

        config = narrowConfig(config, dataSourceName)
        app.event('Db4oConnectStart', [config, dataSourceName])
        ObjectContainer container = startDb4o(app, config)
        ObjectContainerHolder.instance.setObjectContainer(dataSourceName, container)
        bootstrap = app.class.classLoader.loadClass('BootstrapDb4o').newInstance()
        bootstrap.metaClass.app = app
        resolveDb4oProvider(app).withDb4o { dn, c -> bootstrap.init(dn, c) }
        app.event('Db4oConnectEnd', [dataSourceName, container])
        container
    }

    void disconnect(GriffonApplication app, ConfigObject config, String dataSourceName = DEFAULT) {
        if (ObjectContainerHolder.instance.isObjectContainerConnected(dataSourceName)) {
            config = narrowConfig(config, dataSourceName)
            ObjectContainer container = ObjectContainerHolder.instance.getObjectContainer(dataSourceName)
            app.event('Db4oDisconnectStart', [config, dataSourceName, container])
            resolveDb4oProvider(app).withDb4o { dn, c -> bootstrap.destroy(dn, c) }
            stopDb4o(config, container)
            app.event('Db4oDisconnectEnd', [config, dataSourceName])
            ObjectContainerHolder.instance.disconnectObjectContainer(dataSourceName)
        }
    }

    Db4oProvider resolveDb4oProvider(GriffonApplication app) {
        def db4oProvider = app.config.db4oProvider
        if (db4oProvider instanceof Class) {
            db4oProvider = db4oProvider.newInstance()
            app.config.db4oProvider = db4oProvider
        } else if (!db4oProvider) {
            db4oProvider = DefaultDb4oProvider.instance
            app.config.db4oProvider = db4oProvider
        }
        db4oProvider
    }

    private ObjectContainer startDb4o(GriffonApplication app, ConfigObject config) {
        String dbfileName = config.name ?: 'db.yarv'
        File dbfile = new File(dbfileName)
        if (!dbfile.absolute) dbfile = new File(Metadata.current.getGriffonWorkingDir(), dbfileName)
        dbfile.parentFile.mkdirs()
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration()
        app.event('Db4oConfigurationSetup', [config, configuration])
        return Db4oEmbedded.openFile(configuration, dbfile.absolutePath)
    }

    private void stopDb4o(ConfigObject config, ObjectContainer container) {
        container.close()

        if (config.delete) {
            String dbfileName = config.name ?: 'db.yarv'
            File dbfile = new File(dbfileName)
            if (!dbfile.absolute) dbfile = new File(Metadata.current.getGriffonWorkingDir(), dbfileName)
            dbfile.delete()
        }
    }
}