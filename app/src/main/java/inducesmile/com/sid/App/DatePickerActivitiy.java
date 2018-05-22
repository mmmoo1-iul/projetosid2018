package inducesmile.com.sid.App;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import inducesmile.com.sid.R;

public class DatePickerActivitiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_activitiy);
    }

    public void confirmChoice(View v) {
        DatePicker datePicker = findViewById(R.id.datePicker);
        int[] yearMonthDay = new int[3];

        yearMonthDay[0] = datePicker.getYear();
        yearMonthDay[1] = datePicker.getMonth() + 1;
        yearMonthDay[2] = datePicker.getDayOfMonth();

        Intent intent = new Intent(this, GraphicActivity.class);
        intent.putExtra("date", yearMonthDay);
        startActivity(intent);
        finish();

    }
}
