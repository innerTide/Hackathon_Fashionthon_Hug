package eu.eitdigital.yuefeng.hug;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

public class InformationActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private String info = "";

    private TextView informationTextView;
    private TextView partnerView;
    private EditText rssiThrottle;

    private Timer timer;
    private Timer timerNotification;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_information);
        informationTextView = (TextView) findViewById(R.id.InformationTextView);
        partnerView = (TextView) findViewById(R.id.partnerView);
        Button resetButton = (Button) findViewById(R.id.resetButton);
        rssiThrottle = (EditText) findViewById(R.id.rssiThrottle);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = new Intent(this, PersonActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("A new HUG is detected!")
                .setSmallIcon(R.drawable.notification_logo)
                .setContentText("Click to see who he or she is.")
                .setTicker("A New HUG!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());
        final Notification notification = builder.build();


        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    Integer minimunRSSI;
                    try {
                        minimunRSSI = -Integer.parseInt(rssiThrottle.getText().toString());
                    } catch (NumberFormatException e) {
                        minimunRSSI = -65;
                    }
                    info = info + bluetoothDevice.getName() + "\nMAC: " + bluetoothDevice.getAddress() + "\nRSSI: " + rssi.toString() + "\n\n";
                    informationTextView.setText(info);

                    if (bluetoothDevice.getAddress().contains("84:EB:18:58:C8:C6") && (rssi > minimunRSSI)) {
                        String partnerInfo = partnerView.getText().toString();
                        partnerInfo = partnerInfo + "A nearby partner is detected!\nName: "
                                + bluetoothDevice.getName() + "\nMAC: " +
                                bluetoothDevice.getAddress() + "\nRSSI: "
                                + rssi.toString() + "\n";
                        unregisterReceiver(broadcastReceiver);
                        timer.cancel();
                        informationTextView.setText("Here is info");
                        notificationManager.notify(1, notification);
                        /*Toast.makeText(context,"A nearby partner is detected!\nName: "
                                + bluetoothDevice.getName() + "\nMAC: " +
                                bluetoothDevice.getAddress() + "\nRSSI: "
                                + rssi.toString(),Toast.LENGTH_LONG).show();*/
                        partnerView.setText(partnerInfo);
                    }

                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info = "";
                informationTextView.setText("");
                partnerView.setText("");
                registerReceiver(broadcastReceiver, intentFilter);
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }, 1000, 5000);
                bluetoothAdapter.cancelDiscovery();
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 1000, 5000);

        timerNotification = new Timer();
        timerNotification.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }, 3000);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1)
                {
                    info = "";
                    informationTextView.setText("");
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.startDiscovery();
                }
                else {
                    unregisterReceiver(broadcastReceiver);
                    timer.cancel();
                    timerNotification.cancel();
                    informationTextView.setText("Here is info");
                    notificationManager.notify(1, notification);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.disable();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
