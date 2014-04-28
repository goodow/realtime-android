realtime-android [![Build Status](https://travis-ci.org/goodow/realtime-android.svg?branch=master)](https://travis-ci.org/goodow/realtime-android)
================

Event bus client over WebSocket for java and andorid

## Adding realtime-android to your project

### Maven

```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.goodow.realtime</groupId>
    <artifactId>realtime-android</artifactId>
    <version>0.5.5-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## Usage

### WebSocket mode
See https://github.com/goodow/realtime-android/blob/master/src/test/java/com/goodow/realtime/java/EventBusDemo.java

### Local mode
See https://github.com/goodow/realtime-android/blob/master/src/test/java/com/goodow/realtime/java/LocalEventBusDemo.java

### Mix mode
todo

**NOTE:** You must register a platform first by invoke JavaPlatform.register() or AndroidPlatform.register()
