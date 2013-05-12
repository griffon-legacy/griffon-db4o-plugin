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

/**
 * @author Andres Almiray
 */
class Db4oGriffonPlugin {
    // the plugin version
    String version = '1.0.0'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '1.3.0 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [lombok: '0.5.0']
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
The Db4o plugin enables lightweight access to [Db4o][1] dataSources.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in 
`$appdir/griffon-app/conf`:

 * Db4oConfig.groovy - contains the dataSource definitions.
 * BootstrapDb4o.groovy - defines init/destroy hooks for data to be
   manipulated during app startup/shutdown.

A new dynamic method named `withDb4o` will be injected into all controllers,
giving you access to a `com.db4o.ObjectContainer` object, with which you'll be
able to make calls to the dataSource. Remember to make all dataSource calls off the
UI thread otherwise your application may appear unresponsive when doing long
computations inside the UI thread.

This method is aware of multiple dataSources. If no dataSourceName is specified
when calling it then the default dataSource will be selected. Here are two example
usages, the first queries against the default dataSource while the second queries
a dataSource whose name has been configured as 'internal'

    package sample
    class SampleController {
        def queryAllDatabases = {
            withDb4o { dataSourceName, conn -> ... }
            withDb4o('internal') { dataSourceName, conn -> ... }
        }
    }

The following list enumerates all the variants of the injected method

 * `<R> R withDb4o(Closure<R> stmts)`
 * `<R> R withDb4o(CallableWithArgs<R> stmts)`
 * `<R> R withDb4o(String dataSourceName, Closure<R> stmts)`
 * `<R> R withDb4o(String dataSourceName, CallableWithArgs<R> stmts)`

These methods are also accessible to any component through the singleton
`griffon.plugins.db4o.Db4oConnector`. You can inject these methods to
non-artifacts via metaclasses. Simply grab hold of a particular metaclass and
call `Db4oEnhancer.enhance(metaClassInstance, db4oProviderInstance)`.

Configuration
-------------
### Db4oAware AST Transformation

The preferred way to mark a class for method injection is by annotating it with
`@griffon.plugins.db4o.Db4oAware`. This transformation injects the
`griffon.plugins.db4o.Db4oContributionHandler` interface and default
behavior that fulfills the contract.

### Dynamic method injection

Dynamic methods will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.db4o.injectInto = ['controller', 'service']

Dynamic method injection will be skipped for classes implementing
`griffon.plugins.db4o.Db4oContributionHandler`.

### DataSource
The following settings apply to `Db4oConfig.groovy`:

 * **dataSource.name** - defines the name of the db file to use
 * **dataSource.delete** - will delete the db file upon application shutdown if true

If the default configuration settings prove to be inadequate for your needs you
still have a chance to tweak the configuration programmatically by registering
and event handler for `Db4oConfigurationSetup`.

### Events

The following events will be triggered by this addon

 * Db4oConnectStart[config, dataSourceName] - triggered before connecting to the dataSource
 * Db4oConfigurationSetup[config, configuration] - triggered when the ObjectContainer's 
   configuration (`EmbeddedConfiguration`) is ready. 
 * Db4oConnectEnd[dataSourceName, objectContainer] - triggered after connecting to the dataSource
 * Db4oDisconnectStart[config, dataSourceName, objectContainer] - triggered before disconnecting from the dataSource
 * Db4oDisconnectEnd[config, dataSourceName] - triggered after disconnecting from the dataSource

### Multiple Databases

The config file `Db4oConfig.groovy` defines a default dataSource block. As the name
implies this is the dataSource used by default, however you can configure named dataSources
by adding a new config block. For example connecting to a dataSource whose name is 'internal'
can be done in this way

    dataSources {
        internal {
            name = 'internal-db'
        }
    }

This block can be used inside the `environments()` block in the same way as the
default dataSource block is used.

### Configuration Storage

The plugin will load and store the contents of `Db4oConfig.groovy` inside the
application's configuration, under the `pluginConfig` namespace. You may retrieve
and/or update values using

    app.config.pluginConfig.db4o

### Connect at Startup

The plugin will attempt a connection to the default dataSource at startup. If this
behavior is not desired then specify the following configuration flag in
`Config.groovy`

    griffon.db4o.connect.onstartup = false

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/db4o][2]

Testing
-------

Dynamic methods will not be automatically injected during unit testing, because
addons are simply not initialized for this kind of tests. However you can use
`Db4oEnhancer.enhance(metaClassInstance, db4oProviderInstance)` where
`db4oProviderInstance` is of type `griffon.plugins.db4o.Db4oProvider`.
The contract for this interface looks like this

    public interface Db4oProvider {
        <R> R withDb4o(Closure<R> closure);
        <R> R withDb4o(CallableWithArgs<R> callable);
        <R> R withDb4o(String dataSourceName, Closure<R> closure);
        <R> R withDb4o(String dataSourceName, CallableWithArgs<R> callable);
    }

It's up to you define how these methods need to be implemented for your tests.
For example, here's an implementation that never fails regardless of the
arguments it receives

    class MyDb4oProvider implements Db4oProvider {
        public <R> R withDb4o(Closure<R> closure) { null }
        public <R> R withDb4o(CallableWithArgs<R> callable) { null }
        public <R> R withDb4o(String dataSourceName, Closure<R> closure) { null }
        public <R> R withDb4o(String dataSourceName, CallableWithArgs<R> callable) { null }
    }

This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            Db4oEnhancer.enhance(service.metaClass, new MyDb4oProvider())
            // exercise service methods
        }
    }

On the other hand, if the service is annotated with `@Db4oAware` then usage
of `Db4oEnhancer` should be avoided at all costs. Simply set `db4oProviderInstance`
on the service instance directly, like so, first the service definition

    @griffon.plugins.db4o.Db4oAware
    class MyService {
        def serviceMethod() { ... }
    }

Next is the test

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            service.db4oProvider = new MyDb4oProvider()
            // exercise service methods
        }
    }

Tool Support
------------

### DSL Descriptors

This plugin provides DSL descriptors for Intellij IDEA and Eclipse (provided
you have the Groovy Eclipse plugin installed). These descriptors are found
inside the `griffon-db4o-compile-x.y.z.jar`, with locations

 * dsdl/db4o.dsld
 * gdsl/db4o.gdsl

### Lombok Support

Rewriting Java AST in a similar fashion to Groovy AST transformations is
possible thanks to the [lombok][3] plugin.

#### JavaC

Support for this compiler is provided out-of-the-box by the command line tools.
There's no additional configuration required.

#### Eclipse

Follow the steps found in the [Lombok][3] plugin for setting up Eclipse up to
number 5.

 6. Go to the path where the `lombok.jar` was copied. This path is either found
    inside the Eclipse installation directory or in your local settings. Copy
    the following file from the project's working directory

         $ cp $USER_HOME/.griffon/<version>/projects/<project>/plugins/db4o-<version>/dist/griffon-db4o-compile-<version>.jar .

 6. Edit the launch script for Eclipse and tweak the boothclasspath entry so
    that includes the file you just copied

        -Xbootclasspath/a:lombok.jar:lombok-pg-<version>.jar:\
        griffon-lombok-compile-<version>.jar:griffon-db4o-compile-<version>.jar

 7. Launch Eclipse once more. Eclipse should be able to provide content assist
    for Java classes annotated with `@Db4oAware`.

#### NetBeans

Follow the instructions found in [Annotation Processors Support in the NetBeans
IDE, Part I: Using Project Lombok][4]. You may need to specify
`lombok.core.AnnotationProcessor` in the list of Annotation Processors.

NetBeans should be able to provide code suggestions on Java classes annotated
with `@Db4oAware`.

#### Intellij IDEA

Follow the steps found in the [Lombok][3] plugin for setting up Intellij IDEA
up to number 5.

 6. Copy `griffon-db4o-compile-<version>.jar` to the `lib` directory

         $ pwd
           $USER_HOME/Library/Application Support/IntelliJIdea11/lombok-plugin
         $ cp $USER_HOME/.griffon/<version>/projects/<project>/plugins/db4o-<version>/dist/griffon-db4o-compile-<version>.jar lib

 7. Launch IntelliJ IDEA once more. Code completion should work now for Java
    classes annotated with `@Db4oAware`.


[1]: http://db4o.com
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/db4o
[3]: /plugin/lombok
[4]: http://netbeans.org/kb/docs/java/annotations-lombok.html
'''
}
