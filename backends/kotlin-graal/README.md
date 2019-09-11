# gRPC Server example

## Build & Run Project

Project require Java 8 (or later) JVM installed.  
To build and start server use gradle wrapper command : 

Linux/MacOS
```SH
./gradlew run
```

Windows
```SH
./gradlew.bat run
```

By default server run on port 6565


## Build & Run native binary with GraalVM
[GraalVM](https://www.graalvm.org/) >= 19.x is required to build native binary
  
Use gradle nativeImage custom task: 
```
./gradlew nativeImage
```

Use native-image command from project folder:
```
native-image -jar build/libs/kotlin-graal-1.0-SNAPSHOT.jar -H:ReflectionConfigurationFiles=./graal-reflection.json  --delay-class-initialization-to-runtime=io.netty.handler.codec.http2.Http2CodecUtil,io.netty.handler.codec.http2.DefaultHttp2FrameWriter  -H:+ReportExceptionStackTraces --allow-incomplete-classpath
```

Run binary:  
```
./kotlin-graal-1.0-SNAPSHOT
```