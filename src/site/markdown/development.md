## Development

The sections below will go over how to start developing the ST-Metrics library, how to perform
releases of JARs, and how to publish documentation about the library. 

### Requirements

The following things are required for development of the ST-Metrics library.

* Java 8
* Maven 3
* A [GitHub](https://github.com/) account (for pull requests, updating docs, and reporting bugs)
* A [Bintray](https://bintray.com/) account (for publishing new release binaries)

Note that publishing documentation or builds relies on having commit access to the
`smarter-travel-media/st-metrics` repository on GitHub and being part of the `smartertravel`
organization on Bintray.

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
      <password>YOUR_GITHUB_OAUTH_TOKEN_HERE</password>
    </server>
  </servers>
    
  <!-- The rest of your settings here... -->
</settings>
```

After that, you should be able to build and publish the Maven site like so (this is typically done
from your local development environment).

```
mvn -P publish clean site-deploy
```

For more information, check out the [site plugin](https://github.com/github/maven-plugins).

### Pushing Builds

ST-Metrics uses [oss.jfrog.org](https://oss.jfrog.org/) and [Bintray](https://bintray.com/) to host snapshot
and release builds (as JARs). If you'd like to publish a new build, you'll have to set up an account on Bintray
and get yourself added to the `smartertravel` organization. Once you've done that, add your username and API
key to your `~/.m2/settings.xml` file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <servers>
    <!-- Your Github server setup from earlier here... -->
    
    <server>
      <id>bintray</id>
      <username>YOUR_BINTRAY_USERNAME_HERE</username>
      <password>YOUR_BINTRAY_API_KEY_HERE</password>
    </server>
  </servers>
  
  <!-- The rest of your settings here... -->
</settings>
```

After that, you should be able to publish snapshot and release builds (this is typically done from your local 
development environment).

#### Snapshot Builds

Snapshot builds are pushed to `oss.jfrog.org` using your Bintray credentials.

```
mvn -P release clean deploy -DaltDeploymentRepository=bintray::default::https://oss.jfrog.org/artifactory/oss-snapshot-local
```

#### Release Builds

Release builds are pushed to Bintray. After pushing a build to Bintray, if everything looks good, use the UI to
synchronize the new version with Maven Central.

```
mvn -P release clean release:clean release:prepare release:perform -Darguments=-DaltDeploymentRepository=bintray::default::https://api.bintray.com/maven/smartertravel/jars/st-metrics
```

### Further Reading

[Bintray JCenter](https://bintray.com/docs/usermanual/uploads/uploads_includingyourpackagesinjcenter.html)

[Syncing with Maven Central](https://bintray.com/docs/usermanual/uploads/uploads_syncingartifactswithmavencentral.html)

[OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html)

