# Vanilla

> "having no special or extra features; ordinary or standard."

The Vanilla library is my attempt to bring together a bunch of my reusable and interesting code in a manner that allows simple reuse without a lot of external dependencies. 

## Artifacts

They are available via the JCenter repository:

    compile 'com.stehno.vanilla:vanilla-core:0.2.0'

## Build

To build the project:

    ./gradlew build
    
## Documentation

To compile the documentation:

    ./gradlew clean build test jacocoTestReport groovydoc compileGuide compileSite
    
You can run a simple local web server for the documentation using:

    cd vanilla-site
    groovy serve.groovy

which will make the site available at http://localhost:8080

## Publish Site

To publish the web site (after full build with documentation - see above):

    cd vanilla-site
    ./gradlew publish

## Publish Artifact

To publish the built artifacts:

    ./gradle bintrayUpload -Pbintray-user=USERNAME -Pbintray-key=USER_KEY
