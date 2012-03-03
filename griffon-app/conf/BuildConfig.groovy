griffon.project.dependency.resolution = {
    inherits "global"
    log "warn" 
    repositories {
        griffonHome()
        mavenCentral()
        mavenRepo 'http://repository.sonatype.org/content/groups/public'
    }
    dependencies {
        compile 'com.db4o:db4o-core-java5:8.0.184.15484'
    }
}

griffon {
    doc {
        logo = '<a href="http://griffon.codehaus.org" target="_blank"><img alt="The Griffon Framework" src="../img/griffon.png" border="0"/></a>'
        sponsorLogo = "<br/>"
        footer = "<br/><br/>Made with Griffon (@griffon.version@)"
    }
}

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error 'org.codehaus.griffon',
          'org.springframework',
          'org.apache.karaf',
          'groovyx.net'
    warn  'griffon'
}