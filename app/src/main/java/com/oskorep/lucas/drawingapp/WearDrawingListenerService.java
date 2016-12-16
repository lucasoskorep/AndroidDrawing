package com.oskorep.lucas.drawingapp;

/**
 * Created by lucas on 11/26/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * This class is a service which is designed to listen for messages from an Android Wear device and to handle the
 * messages it recieves
 */
public class WearDrawingListenerService extends WearableListenerService {

    public static String WEAR_SERVICE_CALL = "WEAR_DRAWING";
    public static final String TAG = "WearListener";
    Context mContext;

    /**
     * Recives a message event and if the message event is of type WEAR_DRAWING then it starts the android drawing app and sends the picture in as the intent.
     *
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        String event = messageEvent.getPath();

        Log.d(TAG, event);

        if (event.equals(WEAR_SERVICE_CALL)) {
            Log.d("Listclicked", new String(messageEvent.getData()));

            byte[] recievedData = messageEvent.getData();
            Bitmap recievedBitmap = BitmapFactory.decodeByteArray(recievedData, 0, recievedData.length);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("Image", recievedData);
            startActivity(intent);
        }
    }
}