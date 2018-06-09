package inducesmile.com.sid.App;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import inducesmile.com.sid.R;

@SuppressWarnings("all")
public class DatePickerActivity extends AppCompatActivity {
    private DatePickerActivity instance;
    private String lastDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_activitiy);
        instance = this;
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        lastDate = sp.getString("datePickerDate", getIntent().getStringExtra("date"));
    }

    public void confirmChoice(View v) {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                int[] yearMonthDay = new int[3];
                int year = yearMonthDay[0] = datePicker.getYear();
                int month = yearMonthDay[1] = (datePicker.getMonth() + 1);
                int day = yearMonthDay[2] = datePicker.getDayOfMonth();

                if (!equalDates(year, month, day, lastDate)) {
                    lastDate = day + "/" + month + "/" + year;
                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putString("datePickerDate", lastDate);
                    Ed.apply();
                }
               /* Intent intent = new Intent(instance, MainActivity.class);
                startActivity(intent);*/
                finish();
                return null;
            }
        }.execute();
    }

    private boolean equalDates(int intYear, int intMonth, int intDay, String stringDate) {
        String[] splitDate = stringDate.split("/");
        int year = Integer.parseInt(splitDate[2]);
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[0]);
        if (year == intYear && month == intMonth && day == intDay)
            return true;
        return false;
    }

}
