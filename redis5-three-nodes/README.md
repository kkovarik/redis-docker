## Three nodes setup

* master + replicas
* sentinel for each
* persistence disabled

## How to use memtier

https://github.com/RedisLabs/memtier_benchmark

* run docker-compose
```
docker-compose up -d
```
* shell into running container and run tests
```
docker-compose exec memtier /bin/bash
```
* run tests using
```
memtier_beachmark
``` 
example:
```
memtier_benchmark -s redis1 -d 256 -t 4 -c 50 --ratio=1:2 -n 1000
```
* monitor using docker stats
