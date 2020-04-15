## Redis Java journey

How to use:

* docker-compose mount input file to `/var/journey/data.log`
* (optional) configure SPRING_REDIS_HOST
* (optional) configure SPRING_REDIS_PORT
* run docker-compose
```
docker-compose up
```
* test using `http://localhost:8080/journey`