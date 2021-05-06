# ImageManager

## Local load generator:

To setup local load generator:
1. download Gatling from https://gatling.io/open-source/start-testing/
2. setup local origin server with sample image set (e.g. https://cocodataset.org/#download)
3. run local HTTP server - you can use Python: `python3 -m http.server [port]`
4. run Gatling by running script in `/bin` directory from downloaded bundle (from step 1): `./gatling.sh` (or `gatling.bat` on Windows)

## Local Prometheus and Grafana:

To setup Grafana and Prometheus locally run `docker-compose up -d` in `/docker` directory. All dashboards and source
will be set up automatically out of the box. To connect edge server application with them run with profile `local` on port `8080`.