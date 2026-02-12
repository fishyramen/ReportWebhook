# ReportWebhook (Paper Plugin)

## Build

Requirements:
- Java 21+
- Maven

In this folder:

```bash
mvn package
```

Your jar will be at:

```
target/report-webhook-plugin-1.0.1.jar
```

## Install

1. Put the jar into your server's `plugins/` folder
2. Start the server once
3. Edit `plugins/ReportWebhook/config.yml` and set `webhook_url`
4. Reload/restart

## Command

- `/report <player> <reason...>`

This sends an embed to the webhook.
Coordinates are **not** included.
