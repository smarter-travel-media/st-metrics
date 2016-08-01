## Changelog

The ST-Metrics library makes use of [Semantic versioning](http://semver.org/). Changes included in
each release of the library are listed below along with an indication if they are breaking changes
or not.


### [v0.4.0](https://github.com//smarter-travel-media/st-metrics/tree/st-metrics-0.4.0) - 2016-08-01
* **Breaking change** - Change `TimingAspect` and `DefaultKeyGenerator` to move the responsibility of
  adding the `timer.` prefix to metrics from the aspect, to the key generator. Per
  [#6](https://github.com/smarter-travel-media/st-metrics/issues/6).
* Add new `MetricSinkStatsdClient` backend that write timings directly to Statsd. Per
  [#5](https://github.com/smarter-travel-media/st-metrics/issues/5).

### [v0.3.0](https://github.com//smarter-travel-media/st-metrics/tree/st-metrics-0.3.0) - 2016-03-25
* Bump parent package to Spring Platform 2.0.1 (from 1.1.2) which in turns bumps the version of
  AspectJ, DropWizard, and Spring Boot Actuator pulled in.

### [v0.2.0](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.2.0) - 2015-10-21
* **Breaking change**  - ``TimingAspect`` is now metric backend agnostic to allow use of DropWizard
  Metrics or Spring Boot Actuator for publishing method timings. As a result the constructor of 
  ``TimingAspect`` now accepts an interface that abstracts away the underlying metrics aggregator.

### [v0.1.1](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.1.1) - 2015-07-07
* Documentation fixes and updates


### [v0.1.0](https://github.com/smarter-travel-media/st-metrics/tree/st-metrics-0.1.0) - 2015-06-29 
* Initial release
