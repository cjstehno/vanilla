# Vanilla

> "having no special or extra features; ordinary or standard."

The Vanilla library is my attempt to bring together a bunch of my reusable and interesting code in a manner that allows simple reuse without a lot of external dependencies. 

[![Build Status](https://drone.io/github.com/cjstehno/vanilla/status.png)](https://drone.io/github.com/cjstehno/vanilla/latest)

## Quick Links

* **Project:** https://github.org/cjstehno/vanilla
* **Site:** http://stehno.com/vanilla
* **User Guide:** http://stehno.com/vanilla/guide/html5
* **API Docs:** http://stehno.com/vanilla/groovydoc

## Artifacts

They are available via the JCenter repository. For Gradle use:

    compile 'com.stehno.vanilla:vanilla-core:0.5.1'
    
and for Maven:

    <dependency>
        <groupId>com.stehno.vanilla</groupId>
        <artifactId>vanilla-core</artifactId>
        <version>0.5.1</version>
    </dependency>

## Build

To build the project:

    ./gradlew build
    
## Documentation

To compile the documentation:

    ./gradlew clean site
    
You can run a simple local web server for the documentation using:

    ./gradlew startPreview
   
which will make the site available at on a random available port (the URL will be added to your copy buffer).

## Publish Site

To publish the web site (after full build with documentation - see above):

    ./gradlew publishSite --no-daemon

## Publish Artifact

To publish the built artifacts:

    ./gradle bintrayUpload -PbintrayUser=USERNAME -PbintrayKey=USER_KEY
