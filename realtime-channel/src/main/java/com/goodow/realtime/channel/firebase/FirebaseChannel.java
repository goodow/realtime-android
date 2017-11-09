package com.goodow.realtime.channel.firebase;

import android.util.Log;

import com.goodow.realtime.channel.AsyncResult;
import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Handler;
import com.goodow.realtime.channel.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by larry on 2017/11/4.
 */

public class FirebaseChannel {

  private final String instanceId;
  private final DatabaseReference busRef;
  private final DatabaseReference toRemoveRef;
  private final Bus bus;
  private final ChildEventListener childEventListener;

  public FirebaseChannel(final Bus bus) {
    this.bus = bus;
    instanceId = FirebaseInstanceId.getInstance().getId();
    Log.d("FirebaseChannel", "FirebaseInstanceId: " + instanceId);

    busRef = FirebaseDatabase.getInstance().getReference("bus");
    this.toRemoveRef = busRef.child("queue").child(instanceId);

    this.childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
        dataSnapshot.getRef().onDisconnect().removeValue();
        Map msg = (Map) dataSnapshot.getValue();
        String topic = (String) msg.get("topic");
        Object payload = msg.get("payload");
        bus.sendLocal(topic, payload, new Handler<AsyncResult<Message<Object>>>() {
          @Override
          public void handle(AsyncResult<Message<Object>> asyncResult) {
            Message<Object> message = asyncResult.result();

            Map<String, Object> reply = new HashMap<>();
            reply.put("local", Boolean.valueOf(message.isLocal()));
            reply.put("replyTopic", message.replyTopic());
            reply.put("payload", message.payload());
            dataSnapshot.getRef().child("reply").setValue(reply);
          }
        });
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    toRemoveRef.addChildEventListener(childEventListener);

  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();

    toRemoveRef.removeEventListener(childEventListener);
  }
}
