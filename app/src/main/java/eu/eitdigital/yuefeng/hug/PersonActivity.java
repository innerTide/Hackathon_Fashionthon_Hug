package eu.eitdigital.yuefeng.hug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        TextView textView = (TextView) findViewById(R.id.timeText);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\nhh:mm:ss" +
                "");
        Date date = new Date(System.currentTimeMillis());
        textView.setText(simpleDateFormat.format(date));
    }
}
