/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2010-2013 Andres Almiray

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

import griffon.core.GriffonClass
import griffon.core.GriffonApplication
import griffon.plugins.db4o.Db4oConnector
import griffon.plugins.db4o.Db4oEnhancer
import griffon.plugins.db4o.Db4oContributionHandler

import static griffon.util.ConfigUtils.getConfigValueAsBoolean

/**
 * @author Andres Almiray
 */
class Db4oGriffonAddon {
    void addonPostInit(GriffonApplication app) {
        Db4oConnector.instance.createConfig(app)
        def types = app.config.griffon?.db4o?.injectInto ?: ['controller']
        for (String type : types) {
            for (GriffonClass gc : app.artifactManager.getClassesOfType(type)) {
                if (Db4oContributionHandler.isAssignableFrom(gc.clazz)) continue
                Db4oEnhancer.enhance(gc.metaClass)
            }
        }
    }

    Map events = [
        LoadAddonsEnd: { app, addons ->
            if (getConfigValueAsBoolean(app.config, 'griffon.db4o.connect.onstartup', true)) {
                ConfigObject config = Db4oConnector.instance.createConfig(app)
                Db4oConnector.instance.connect(app, config)
            }
        },
        ShutdownStart: { app ->
            ConfigObject config = Db4oConnector.instance.createConfig(app)
            Db4oConnector.instance.disconnect(app, config)
        }
    ]
}