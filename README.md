# Springy-Boxy

This software is in the Public Domain.  Please see [LICENSE.md](LICENSE.md).

## Axon gaps

* Spring AOP does not do constructors, so missing command handlers on an 
aggregates ctor.  AspectJ seems best, but how best to make it harmonious?
* Three sources of call records (unit of work, command interceptor, aspect)
How to turn this into one coherent piece?
* Sort out unit of work for nested and disruptor versions
* Currently call records accumulate globally for all commands.  Needs to 
become scoped chunks with logical delineations

## Boxfuse gaps
* Configuration in Travis CI for auto-deploy to boxfuse
