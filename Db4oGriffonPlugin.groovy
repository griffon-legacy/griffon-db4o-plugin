/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2010-2012 Andres Almiray

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

/**
 * @author Andres Almiray
 */
class Db4oGriffonPlugin {
    // the plugin version
    String version = '0.6'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '0.9.5 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'GNU General Public License v3'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-db4o-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'Db4o support'
    String description = '''
The Db4o plugin enables lightweight access to [Db4o][1] databases.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * Db4oConfig.groovy - contains the database definitions.
 * BootstrapDb4o.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

A new dynamic method named `withDb4o` will be injected into all controllers,
giving you access to a `com.db4o.ObjectContainer` object, with which you'll be able
to make calls to the database. Remember to make all database calls off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.
This method is aware of multiple databases. If no databaseName is specified when calling
it then the default database will be selected. Here are two example usages, the first
queries against the default database while the second queries a database whose name has
been configured as 'internal'

	package sample
	class SampleController {
	    def queryAllDatabases = {
	        withDb4o { databaseName, objectContainer -> ... }
	        withDb4o('internal') { databaseName, objectContainer -> ... }
	    }
	}
	
This method is also accessible to any component through the singleton `griffon.plugins.db4o.Db4oConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`Db4oEnhancer.enhance(metaClassInstance, db4oProviderInstance)`.

Configuration
-------------
### Dynamic method injection

The `withDb4o()` dynamic method will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.db4o.injectInto = ['controller', 'service']

DataSource
----------

The following settings apply to `Db4oConfig.groovy`:

 * **dataSource.name** - defines the name of the db file to use
 * **dataSource.delete** - will delete the db file upon application shutdown if true

If the default configuration settings prove to be inadequate for your needs you still have a chance to tweak
the configuration programmatically by implementing

        def configure(EmbeddedConfiguration configuration) {
            // empty
        }

### Events

The following events will be triggered by this addon

 * Db4oConnectStart[config, databaseName] - triggered before connecting to the database
 * Db4oConnectEnd[databaseName, objectContainer] - triggered after connecting to the database
 * Db4oDisconnectStart[config, databaseName, objectContainer] - triggered before disconnecting from the database
 * Db4oDisconnectEnd[config, databaseName] - triggered after disconnecting from the database

### Multiple Stores

The config file `Db4oConfig.groovy` defines a default dataSource block. As the name
implies this is the database used by default, however you can configure named dataSources
by adding a new config block. For example connecting to a database whose name is 'internal'
can be done in this way

	dataSources {
	    internal {
		    name = 'internal-db'
		}
	}

This block can be used inside the `environments()` block in the same way as the
default dataSource block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/db4o][2]

Testing
-------
The `withDb4o()` dynamic method will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `Db4oEnhancer.enhance(metaClassInstance, db4oProviderInstance)` where 
`db4oProviderInstance` is of type `griffon.plugins.db4o.Db4oProvider`. The contract for this interface looks like this

    public interface Db4oProvider {
        Object withDb4o(Closure closure);
        Object withDb4o(String dataSourceName, Closure closure);
        <T> T withDb4o(CallableWithArgs<T> callable);
        <T> T withDb4o(String dataSourceName, CallableWithArgs<T> callable);
    }

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyDb4oProvider implements Db4oProvider {
        Object withDb4o(String dataSourceName = 'default', Closure closure) { null }
        public <T> T withDb4o(String dataSourceName = 'default', CallableWithArgs<T> callable) { null }      
    }
    
This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            Db4oEnhancer.enhance(service.metaClass, new MyDb4oProvider())
            // exercise service methods
        }
    }


[1]: http://db4o.com/
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/db4o
'''
}
