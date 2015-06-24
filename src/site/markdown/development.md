## Development

The sections below will go over how to start developing the ST-Metrics library, how to perform
releases of JARs, and how to publish documentation about the library. 

### Requirements

The following things are required for development of the ST-Metrics library.

* Java 8
* Maven 3
* A GitHub account (for committing code and reporting bugs)
* A Bintray account (for publishing new releases)

### Publish Docs

ST-Metrics uses [GitHub Pages](https://pages.github.com/) and the GitHub 
[site plugin](https://github.com/github/maven-plugins) to host and publish documentation. 

If you'd like to publish a new version of the ST-Metrics site, you'll have to set up a GitHub
OAuth token with the `public_repo` and `user` permissions. Once you've done that, add the token
to your `~/.m2/settings.xml` file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <server>
      <id>github</id>
      <password>YOUR_OAUTH_TOKEN_HERE</password>
    </server>
    
    <!-- The rest of your settings here... -->
</settings>
```

After that, you should be able to build and publish the Maven site like so.

```
mvn -P publish clean site-deploy
```

For more information, check out the [site plugin](https://github.com/github/maven-plugins).

### Push Release Builds

TODO: Do this, then document it

```
mvn -P release release:clean release:prepare release:perform -Darguments=-DaltDeploymentRepository=bintray::default::https://api.bintray.com/maven/smartertravel/jars/st-metrics
```
