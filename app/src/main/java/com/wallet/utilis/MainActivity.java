package com.wallet.utilis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.BeaconManager;
import com.yoyowallet.yoyo.YoyoException;
import com.yoyowallet.yoyo.api.YoyoCallback;
import com.yoyowallet.yoyo.api.model.Card;
import com.yoyowallet.yoyo.api.model.SessionData;
import com.yoyowallet.yoyo.card.CardManager;
import com.yoyowallet.yoyo.card.YoyoAddCardActivity;
import com.yoyowallet.yoyo.login.LoginManager;
import com.yoyowallet.yoyo.login.YoyoLoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements YoyoCallback<SessionData>, MenuViewAdapter.QuantityChangedListener {

    Context mContext = this;

    private YoyoCallback<Card> mCardCallback = new YoyoCallback<Card>() {
        @Override
        public void onSuccess(Card card) {
            // TODO: SUCCESS
        }

        @Override
        public void onCancel() {
// TODO: CANCEL
        }

        @Override
        public void onError(YoyoException e, Card card) {
// TODO: ERROR
        }
    };

    private BeaconManager beaconManager;
    private Region region;

    @Override
    public void onItemsChanged(List<MenuItem> items) {
        // iterate through items
        // items.quannity * item.price
        int subtotal = 0;
        for (MenuItem item : items) {
            subtotal += item.quantity * 3;
        }
        subtotalCostView.setText("£" + String.valueOf(subtotal));

    }

    // Views
    private TextView tableNumberView;
    private ImageView logoImageView;
    private RecyclerView menuView;
    private TextView subtotalCostView;
    private ImageView yoyoButton;

    // State
    private Integer currentTable = -1;

    // Data
    private MenuViewAdapter starbucksMenuAdapter;
    private MenuViewAdapter pizzahutMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginManager.register(this);
        login();
        setContentView(R.layout.activity_main);

        // Register views
        tableNumberView = (TextView) findViewById(R.id.tableNumber);
        logoImageView = (ImageView) findViewById(R.id.logoImageView);
        menuView = (RecyclerView) findViewById(R.id.menuView);
        subtotalCostView = (TextView) findViewById(R.id.subtotalCost);
        yoyoButton = (ImageView) findViewById(R.id.yoyoButton);

        // Prepare view
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        menuView.setHasFixedSize(true);
        menuView.setLayoutManager(mLayoutManager);

        // Register listeners
//        menuView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                        Log.v(UtilisConstants.TAG, "Item tapped.");
//                        subtotalCostView.setText("£3.00");
//                    }
//                })
//        );
        yoyoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, QR_code.class);
                startActivity(i);
            }
        });

        // Prepare data
        ArrayList<MenuItem> starbucksMenu;
        starbucksMenu = new ArrayList<>();
        starbucksMenu.add(new MenuItem("Iced Coffee", R.drawable.starbucks_iced_coffee, "£3.00"));
        starbucksMenu.add(new MenuItem("Dark Roast", R.drawable.starbucks_dark_roast, "£3.00"));
        starbucksMenu.add(new MenuItem("Caffe Misto", R.drawable.starbucks_caffe_misto, "£3.00"));
        starbucksMenu.add(new MenuItem("Iced Coffee w/ Milk", R.drawable.starbucks_icedcoffee_w_milk, "£3.00"));
        starbucksMenuAdapter = new MenuViewAdapter(starbucksMenu);
        starbucksMenuAdapter.setListener(this);
        ArrayList<MenuItem> pizzahutMenu;
        pizzahutMenu = new ArrayList<>();
        pizzahutMenu.add(new MenuItem("Hickory BBQ", R.drawable.pizzahut_hickory_bbq, "£3.00"));
        pizzahutMenu.add(new MenuItem("Argentinian Beef", R.drawable.pizzahut_argentina_pulled, "£3.00"));
        pizzahutMenu.add(new MenuItem("Grand Supreme", R.drawable.pizzahut_grand_supreme, "£3.00"));
        pizzahutMenu.add(new MenuItem("Chicken Cranberry", R.drawable.pizzahut_chicken_cranberry, "£3.00"));
        pizzahutMenuAdapter = new MenuViewAdapter(pizzahutMenu);
        pizzahutMenuAdapter.setListener(this);
        beaconManager = new BeaconManager(this);
        //beaconManager.setBackgroundScanPeriod(1, 3);
        //beaconManager.setForegroundScanPeriod(1, 0);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = getNearestKnownBeacon(list);
                    if (nearestBeacon != null) {
                        Integer table = tableForBeacon(nearestBeacon);
                        // Update the UI
                        if (table > -1 && !table.equals(currentTable)) {
                            Log.v(UtilisConstants.TAG, "Nearest beacon: " + nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor() + ". Associated table: " + table);
                            tableNumberView.setText(table.toString());
                            showNotification("Connected to beacon.", "Connected to table: " + table);
                            currentTable = table;

                            int logoResource = -1;
                            MenuViewAdapter menuViewAdapter = null;

                            switch (table) {
                                case 1:
                                    subtotalCostView.setText("£");
                                    logoResource = R.drawable.starbucks_logo;
                                    menuViewAdapter = starbucksMenuAdapter;
                                    break;
                                case 3:
                                    subtotalCostView.setText("£");
                                    logoResource = R.drawable.pizzahut_logo;
                                    menuViewAdapter = pizzahutMenuAdapter;
                                    break;
                            }

                            // Set logo
                            if (logoResource > -1) {
                                logoImageView.setImageResource(logoResource);
                            }
                            // Set menu - set adapter
                            if (menuViewAdapter != null) {
                                menuView.setAdapter(menuViewAdapter);
                            }

                        } else if (table == -1) {
                            Log.v(UtilisConstants.TAG, "No registered beacon found. Nearest beacon: " + nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor() + ". Associated table: " + table);
                            tableNumberView.setText("...");
                            currentTable = -1;
                        }
                    } else {
                        tableNumberView.setText("...");
                    }
                }
            }
        });
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            CardManager.register(mCardCallback);
            addCard();
        }

        return super.onOptionsItemSelected(item);
    }


    // When you want to start the add card process call:
    private void addCard() {
        Intent intent = YoyoAddCardActivity.createAddCardActivityIntent(MainActivity.this);
        startActivityForResult(intent, CardManager.REQUEST_CODE);
    }

    // In order to handle callbacks, you need the following


    @Override
    protected void onDestroy() {
        LoginManager.unregister(this);
        CardManager.unregister(mCardCallback);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    public void onSuccess(SessionData sessiondata) {
        // The user has successfully logged in
    }

    @Override
    public void onCancel() {
        // user has closed the login activity
    }

    @Override
    public void onError(YoyoException exception, SessionData noData) {
        // an error occurred -> you may display an error
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoginManager.onActivityResult(requestCode, resultCode, data);
        CardManager.onActivityResult(requestCode, resultCode, data);
    }

    // When you want to start the login process call:
    private void login() {
        Intent intent = YoyoLoginActivity.createLoginActivityIntent(mContext);
        startActivityForResult(intent, LoginManager.REQUEST_CODE);
    }


    /*public void onButtonClick(View v)
    {
        if(v.getId()==R.id.Bcard_info)
        {
            Intent i =new Intent(MainActivity.this,card_info.class);
            startActivity(i);
        }

        else
        {
            Intent i = new Intent(MainActivity.this,QR_code.class);
            startActivity(i);
        }
    }*/

    private Integer tableForBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (TABLES_BY_BEACONS.containsKey(beaconKey)) {
            return TABLES_BY_BEACONS.get(beaconKey);
        }
        return -1;  // No table found
    }

    private Beacon getNearestKnownBeacon(List<Beacon> beaconList) {
        for (int i = 0; i < beaconList.size(); i++) {
            String beaconKey = String.format("%d:%d", beaconList.get(i).getMajor(), beaconList.get(i).getMinor());
            if (TABLES_BY_BEACONS.containsKey(beaconKey)) {
                return beaconList.get(i);
            }
        }
        return null;
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    // DATA
    private static final Map<String, Integer> TABLES_BY_BEACONS;

    static {
        Map<String, Integer> tablesByBeacons = new HashMap<>();
        tablesByBeacons.put("11386:54474", 1);  // Table 1
        tablesByBeacons.put("65441:39719", 2);  // Table 2
        tablesByBeacons.put("28922:14237", 3);  // Table 3
        TABLES_BY_BEACONS = Collections.unmodifiableMap(tablesByBeacons);
    }
}