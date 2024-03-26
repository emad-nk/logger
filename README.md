# Logger

## Implementation details

Each log target has its own LogLevel. Log targets implement `LogTarget` interface, therefore it is easy to extend targets by creating new targets and implement the `LogTarget` interface.

In order to avoid creating the same instance of a log target over and over `ConsoleLogTarget` is singleton. Other log targets use `LogTargetFactory` to create the instance.
The reason that `LogTargetFactory` is used for other log targets is because there might be multiple APIs that need to be used to log the messages for example:
```
APILogTarget.createOrGetInstance("api-1.com", logger)
```
and
```
APILogTarget.createOrGetInstance("api-2.com", logger)
```
`LogTargetFactory` makes sure there will be always one instance of APILogTarget with url `api-1.com`

In case the user decides to remove a log target from the logger the instance associated with that log target created in the `LogTargetFactory` will be removed for the purpose of memory management.

### Concurrency

Throughout the entire application those variables that can be accessed via multiple thread are thread safe.
In this case `ConcurrentHashMap` is used which manages thread locks internally.

### Complexity

The code complexity to log the message to all the log targets is `O(n)`, and `n` is the number of log targets.

## Usage

To use the logger, a logger instance should get created with the class name:

```kotlin
val logger = Logger("someClassName")
```

Log targets should get added to the Logger via the `addLogTargets(logTargets: Map<LogTarget, LogLevel>)` method.

If no log targets is added logging a message will throw a `RuntimeException` with message: `No log targets have been added`

In order to add a target use the `createOrGetInstance()` method for these logs targets:

```kotlin
APILogTarget.createOrGetInstance("example-api.com", logger)

EmailLogTarget.createOrGetInstance("example@example.com", logger)

FileSystemLogTarget.createOrGetInstance("/file/location", logger)
```

`ConsoleLogTarget` is singleton so it doesn't require `createInstance()` method.

Here is an example code to create a logger and register all the log targets:

```kotlin
val logger = Logger(className = CLASS_NAME)
logger.addLogTargets(
  logTargets = mapOf(
    ConsoleLogTarget to INFO,
    APILogTarget.createOrGetInstance(apiUrl, logger) to INFO,
    FileSystemLogTarget.createOrGetInstance(fileLocation, logger) to INFO,
    EmailLogTarget.createOrGetInstance(emailAddress, logger) to ERROR
  )
)

logger.info(message = LOG_MESSAGE)
```

If a target needs to be deleted/unregistered, here is the function that needs to be called. The function can receive multiple log targets comma separated:

```kotlin
logger.deleteLogTargets(
  APILogTarget.createOrGetInstance(api, logger),
  FileSystemLogTarget.createOrGetInstance(location, logger),
  EmailLogTarget.createOrGetInstance(email, logger),
)
```

To delete all the registered log targets:
```
logger.deleteAllLogTargets()
```

To get all the registered targets:

```
logger.logTargetsMap
```

Log methods to log messages:
```
log.debug(message, throwable) // throwable is optional and can be omited
log.debug(message)

log.info(message, throwable) // throwable is optional and can be omited
log.info(message)

log.warn(message, throwable) // throwable is optional and can be omited
log.warn(message)

log.error(message, throwable) // throwable is optional and can be omited
log.error(message)
```

## Current Log targets

- ConsoleLogTarget
- APILogTarget
- EmailLogTarget
- FileSystemLogTarget

## Future Development

- Field validation (eg. email and url)
- Change Log targets to actually send logs to the respective targets
- Better test coverage
