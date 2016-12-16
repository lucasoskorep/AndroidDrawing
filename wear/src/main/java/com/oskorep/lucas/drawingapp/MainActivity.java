package com.oskorep.lucas.drawingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

/**
 * This is the MainActivity class for the Wear Drawing App.
 * The purpose of this class is to facilitate all interactions between the activity and
 * the various views displayed in the android application
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Node mNode;
    CanvasView mCanvasView;

    public static String WEAR_DATA_PATH = "WEAR_DRAWING";
    public static String TAG = "WearListActivity";


    private DismissOverlayView mDismissOverlay;

    /**
     * Sets the content view of the app, and then sets up the various components of the app such as
     * the GoogleAPIClient, the DismissOverlay, and the save+exit button.
     *
     * @param savedInstanceState -  Used to restore previously saved information from the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText("TEST");
        mDismissOverlay.showIntroIfNecessary();

        mCanvasView = (CanvasView) findViewById(R.id.drawing_canvas);

        initializeSaveButton();


    }

    /**
     * Initializes the save button so that it creates a bitmap of the current on-screen image, sends it to Android, and then shows the exit dialogue for the app.
     */
    private void initializeSaveButton(){

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = Bitmap.createBitmap(mCanvasView.getWidth(), mCanvasView.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas c = new Canvas(b);
                mCanvasView.layout(0, 0, mCanvasView.getWidth(), mCanvasView.getHeight());
                mCanvasView.draw(c);

                if (b != null) {
                    sendMessage(b);
                    mDismissOverlay.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    /**
     * Gets the node from the list of connected nodes for the android wear device to send data to.
     * This should be the main node as it is last in the list and non-null.
     */
    private void getNode() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    if (node != null) {
                        mNode = node;
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Sends the specified bitmap to the android wear device for further processing.
     */
    private void sendMessage(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected())

                {
                    Log.d(TAG, "" + mGoogleApiClient.isConnected());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] message = outputStream.toByteArray();
                    Log.d(TAG, new String(message));
//                    byte [] message = "Hello  World".getBytes();
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), WEAR_DATA_PATH, message).await();
                    Log.d(TAG, "MESSAGE SENT");
                }

            }
        }).start();


    }

}
