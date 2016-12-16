package com.oskorep.lucas.drawingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

/**
 * This is the MainActivity class for the Drawing App.
 * The purpose of this class is to facilitate all interactions between the activity and
 * the various views displayed in the android application
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN_ACTIVITY:";
    private final int WRITE_REQUEST = 420;
    private final int LOAD_IMAGE_TO_DRAW = 421;

    //Arbitrary numbers for communication of filters between app modules.
    public static final int FILTER_TINT = 123;
    public static final int FILTER_GREYSCALE = 124;
    public static final int FILTER_INVERT = 125;
    public static final int FILTER_SKETCH = 126;
    public static final int FILTER_OIL = 127;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CanvasView mCanvasView;

    private Context mContext = this;


    /**
     * Sets the content view of the app, and then sets up the various components of the app such as
     * the ColorDrawer, the Toolbar, and the FloatingActionMenu
     *
     * @param savedInstanceState -  Used to restore previously saved information from the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        mCanvasView = (CanvasView) findViewById(R.id.drawing_canvas);

        mCanvasView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        setupColorDrawer();
        setupToolbar();
        setupFloatingActionMenu();

        handleIntents();

        registerForContextMenu(findViewById(R.id.brush_large));

    }

    /**
     * Handles the apps supported intents
     * This app currently supports image importing and this handler is designed to only import an image and save it to the default image storage on the disk.
     */
    public void handleIntents() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        byte[] bytes = intent.getByteArrayExtra("Image");
        if (bytes == null) {
            return;
        }
        Bitmap recievedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (recievedBitmap == null) {
            return;
        }
        MediaStore.Images.Media.insertImage(mContext.getContentResolver(), recievedBitmap, "NEW TITLE", "description HERE");
    }

    /**
     * Handles the result from requesting dangerous permissions from Android.SYSTEM.
     * Should prevent saving/opening if permission is denied, and vice versa
     *
     * @param requestCode  - The .SYSTEM code for the requested permission
     * @param permissions  - The array of granted results
     * @param grantResults - The array of granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted according to android documentation
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    //Permission Denied
                    Toast.makeText(this, "Permission NOT Granted:" +
                            "Not all of this App's functionalities will work.", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * This is a function which  requests write permissions from android.SYSTEM.
     * This function is needed for Android 6.0+ because of the introduction of dangerous/run-time
     * permissions.
     */
    public void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // See Android Documentation for leaving a message to the user
                // Detailing the intent of your usage before
            } else {
                //Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_REQUEST);
            }
        }

    }


    /**
     * Initializes the toolbar for the android app interface.
     */
    void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.options_bar);
        setActionBar(toolbar);
    }

    /**
     * Initializes the Drawer for this app which is being used to choose colors from a list and
     * use them to draw on the given canvas.
     */
    void setupColorDrawer() {

        final ArrayList<NavColor> colorList = createColorList();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new ColorAdapter(this, colorList));

        //TODO: Add custom onClickListener
        mDrawerList.setOnItemClickListener(new ColorDrawerClickListener(mContext, mCanvasView, colorList));

    }

    /**
     * Initializes the FloatingActionMenu. Fills the FloatingActionMenu with
     * FloatingActionButtons and initializes the FloatingActionButtons to close
     * the parent FloatingActionMenu then clicked.
     */
    void setupFloatingActionMenu() {
        final FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.brush_menu);

        final FloatingActionButton fabSmallBrush = (FloatingActionButton) findViewById(R.id.brush_small);
        final FloatingActionButton fabMediumBrush = (FloatingActionButton) findViewById(R.id.brush_medium);
        final FloatingActionButton fabLargeBrush = (FloatingActionButton) findViewById(R.id.brush_large);
        final FloatingActionButton fabFilters = (FloatingActionButton) findViewById(R.id.brush_filters);

        registerForContextMenu(findViewById(R.id.brush_filters));

        fabSmallBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCanvasView.setBrushSize(10);
                menu.close(true);
            }
        });
        fabMediumBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCanvasView.setBrushSize(25);
                menu.close(true);
            }
        });
        fabLargeBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCanvasView.setBrushSize(50);
                menu.close(true);
            }
        });
        fabFilters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                view.showContextMenu();
                menu.close(true);
            }
        });

    }

    /**
     * Creates the Option menu when the settings button is pressed and inflates its view into the application.
     *
     * @param menu - The menu to be inflated
     * @return - True if the menu was inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Listens for the options in the app's options Menu.
     * Currently hte function only shows a toast of which notification was pressed.
     * TODO: Add option item switch and option item functionality
     *
     * @param item - The selected MenuItem
     * @return - super.onOptionsSelected()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_undo:
                //undo last draw call
                Log.d(TAG, "UNDO");
                mCanvasView.undo();
                return true;

            case R.id.action_redo:
                //redo last draw call
                mCanvasView.redo();
                return true;

            case R.id.action_share:
                //share the current canvas to other apps
                mCanvasView.shareImage();
                return true;

            case R.id.action_load:
                //load a picture in from a file
                loadImage();
                return true;
            case R.id.action_save:
                //save a picture to a file
                mCanvasView.save();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Loads an image in from the default android gallery application. to be drawn on top of.
     */
    public void loadImage() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startActivityForResult(intent, LOAD_IMAGE_TO_DRAW);

    }

    /**
     * Taken from Stack Overflow
     * http://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == LOAD_IMAGE_TO_DRAW) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                mCanvasView.loadImage(bitmap);
            }


        }
    }

    /**
     * Creates the Context menu which appears in order to display a list of filters to the user when they select hte filters menu.
     *
     * @param contextMenu - The ContextMenu being created.
     * @param v           The view the menu is being inflated into.
     * @param menuInfo    Information about the contextMenu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, contextMenu);

    }


    /**
     * Handles the callback requests made from the ContextMenu(fitler menu)
     *
     * @param menuItem - The menu item tthat was selected.
     * @return True if the callback is handled or the super.onContextItemSelected if the callback remains unhandled.
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        boolean handled = displayAlert(menuItem.getItemId());

        return handled || super.onContextItemSelected(menuItem);

    }


    /**
     * Displays an alert about filtering images and prompts the user whether or not he/she wants to continue with the filter anyways.
     *
     * @param itemID The id of the filter selected.
     * @return true
     */
    public boolean displayAlert(final int itemID) {
        //Createa  dialogue builder to build our dialog for us.
        AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(mContext);
        alertDialogueBuilder.setTitle("Apply Filter Warning");
        alertDialogueBuilder
                .setMessage("Filters cannot currently be undone, and applying a filter will flatten the current drawing.")
                .setCancelable(false)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //User has accepted the consequences.

                        Toast.makeText(mContext, "Applying Filter", Toast.LENGTH_SHORT).show();
                        switch (itemID) {
                            case R.id.action_filter0:

                                mCanvasView.applyFilter(FILTER_GREYSCALE);
                                break;

                            case R.id.action_filter1:
                                mCanvasView.applyFilter(FILTER_INVERT);
                                break;

                            case R.id.action_filter2:
                                mCanvasView.applyFilter(FILTER_SKETCH);
                                break;

                            case R.id.action_filter3:
                                mCanvasView.applyFilter(FILTER_OIL);
                                break;

                            case R.id.action_filter4:
                                pickFilterColor();
                                break;

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //The user has canceled the filter application.

                        Toast.makeText(mContext, "Canceled Filter", Toast.LENGTH_SHORT).show();
                    }
                });
        // create and show the dialog
        AlertDialog alertDialogue = alertDialogueBuilder.create();
        alertDialogue.show();
        return true;

    }

    private void pickFilterColor() {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(mContext, ColorPickerDialog.LIGHT_THEME);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                mCanvasView.setFilterColor(color);
                mCanvasView.applyFilter(FILTER_TINT);
            }
        });
        colorPickerDialog.show();
    }

    /**
     * Creates an ArrayList of basic colors.
     *
     * @return The specified araylist
     */
    private ArrayList<NavColor> createColorList() {
        ArrayList<NavColor> colorList = new ArrayList<NavColor>();

        colorList.add(new NavColor("Custom", R.drawable.drawer_custom, Color.BLACK));
        colorList.add(new NavColor("Red", R.drawable.drawer_red, Color.parseColor("#ffff0000")));
        colorList.add(new NavColor("Orange", R.drawable.drawer_orange, Color.parseColor("#ffff8C00")));
        colorList.add(new NavColor("Yellow", R.drawable.drawer_yellow, Color.parseColor("#ffffff00")));
        colorList.add(new NavColor("Green", R.drawable.drawer_green, Color.GREEN));
        colorList.add(new NavColor("Blue", R.drawable.drawer_blue, Color.BLUE));
        colorList.add(new NavColor("Purple", R.drawable.drawer_purple, Color.parseColor("#ff551A8B")));
        colorList.add(new NavColor("Brown", R.drawable.drawer_brown, Color.parseColor("#ffA0522D")));
        colorList.add(new NavColor("White", R.drawable.drawer_white, Color.WHITE));
        colorList.add(new NavColor("Grey", R.drawable.drawer_grey, Color.parseColor("#ff666666")));
        colorList.add(new NavColor("Black", R.drawable.drawer_black, Color.BLACK));

        return colorList;
    }
}
