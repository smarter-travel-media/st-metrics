## Changelog

The ST-Metrics library makes use of [Semantic versioning](http://semver.org/). Changes included in
each release of the library are listed below along with an indication if they are breaking changes
or not.

### [v0.2.0](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.2.0) - 2015-10-21
* **Breaking change**  - ``TimingAspect`` is now metric backend agnostic to allow use of DropWizard
  Metrics or Spring Boot Actuator for publishing method timings. As a result the constructor of 
  ``TimingAspect`` now accepts an interface that abstracts away the underlying metrics aggregator.

### [v0.1.1](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.1.1) - 2015-07-07
* Documentation fixes and updates


### [v0.1.0](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.1.0) - 2015-06-29 
* Initial release
