package inducesmile.com.sid.App;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import inducesmile.com.sid.R;

@SuppressWarnings("all")
public class DatePickerActivity extends AppCompatActivity {
    private DatePickerActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_activitiy);
        instance = this;
    }

    public void confirmChoice(View v) {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                int[] yearMonthDay = new int[3];
                yearMonthDay[0] = datePicker.getYear();
                yearMonthDay[1] = (datePicker.getMonth() + 1);
                yearMonthDay[2] = datePicker.getDayOfMonth();
                Intent intent = new Intent(instance, MainActivity.class);
                intent.putExtra("date", yearMonthDay);
                startActivity(intent);
                finish();
                return null;
            }
        }.execute();
    }
}
