realtime-android [![Build Status](https://travis-ci.org/goodow/realtime-android.svg?branch=master)](https://travis-ci.org/goodow/realtime-android)
================

Event bus client over WebSocket for java and andorid

Visit [Google groups](https://groups.google.com/forum/#!forum/goodow-realtime) for discussions and announcements.

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
```java
AndroidPlatform.register(); // or JavaPlatform.register();

Bus bus = new ReconnectBus("ws://localhost:1986/channel/websocket", null);

bus.subscribe("some/topic", new MessageHandler<JsonObject>() {
  @Override
  public void handle(Message<JsonObject> message) {
    JsonObject body = message.body();
    System.out.println("Name: " + body.get("name"));
  }
});

bus.publish("some/topic", Json.createObject().set("name", "Larry Tin"));
```

```java
AndroidPlatform.register(); // or JavaPlatform.register();

Store store = new StoreImpl("ws://localhost:1986/channel/websocket", null);
Bus bus = store.getBus();

Handler<Document> onLoaded = new Handler<Document>() {
  @Override
  public void handle(Document document) {
    Model model = document.getModel();
    CollaborativeMap root = model.getRoot();
    CollaborativeString name = root.get("name");
    System.out.println("Name: " + name.getText());
  }
};

Handler<Model> opt_initializer = new Handler<Model>() {
  @Override
  public void handle(Model model) {
    CollaborativeString name = model.createString("Larry Tin");
    CollaborativeMap root = mod.getRoot();
    root.set("name", name);
  }
};

store.load("docType/docId", onLoaded, opt_initializer, null);
```

See [WebSocketBusTest](https://github.com/goodow/realtime-android/blob/master/src/test/java/com/goodow/realtime/java/WebSocketBusTest.java)
and [ServerStoreTest](https://github.com/goodow/realtime-store/blob/master/src/test/java/com/goodow/realtime/store/impl/ServerStoreTest.java)
for more usage.

### Local mode
See https://github.com/goodow/realtime-android/blob/master/src/test/java/com/goodow/realtime/java/SimpleBusTest.java

**NOTE:** You must register a platform first by invoke JavaPlatform.register() or AndroidPlatform.register()
