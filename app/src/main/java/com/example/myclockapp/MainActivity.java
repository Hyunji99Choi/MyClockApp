package com.example.myclockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    MyClock Clock;
    Button sButton, dButton;
    ToggleButton tButton;
    TextView rText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Clock = new MyClock(this);
        FrameLayout view = findViewById(R.id.clock_SurfaceView);
        view.addView(Clock);



        sButton = findViewById(R.id.set_button); //시간설정
        tButton = findViewById(R.id.toggleButton); // 알람 토글 버튼
        dButton = findViewById(R.id.dsgn_button); //디자인 변경

        rText = findViewById(R.id.reservationText); // 예약한 시간



    }

    public void onClick(View view){

        switch (view.getId()){
            case R.id.set_button:
                final Dialog setTimeD = new Dialog(this);
                settingTimeDialog(setTimeD);
                setTimeD.show();
                break;
            case R.id.toggleButton:
                // on/off 확인
                if(tButton.isChecked()){
                    //예약
                    final Dialog reservation = new Dialog(this);
                    settingReservationDialog(reservation);
                    reservation.show();
                }else{
                    //예약 취소
                    rText.setText("예약 : 없음");
                    Clock.cancelReservation();
                }

                break;
            case R.id.dsgn_button:
                Clock.changeDesign();
                break;
        }
    }

    private void settingTimeDialog(final Dialog setTimeD){
        setTimeD.setContentView(R.layout.custom_time_dialog);

        Button setting = setTimeD.findViewById(R.id.ok);
        Button cancel = setTimeD.findViewById(R.id.cancel);

        final EditText hour = setTimeD.findViewById(R.id.hour);
        final EditText min = setTimeD.findViewById(R.id.min);
        final EditText sec = setTimeD.findViewById(R.id.sec);

        // 리스너
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 시간 설정
                //null 체크
                if(hour.getText().length()==0||min.getText().length()==0||sec.getText().length()==0){
                    Toast.makeText(getApplicationContext(),"정확한 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                int h = Integer.parseInt(String.valueOf(hour.getText()));
                int m = Integer.parseInt(String.valueOf(min.getText()));
                int s = Integer.parseInt(String.valueOf(sec.getText()));
                //시간 체크
                if(!checkClock(h,m,s)){
                    return;
                }
                //시간 세팅
                Clock.changeTime(h,m,s);
                setTimeD.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeD.dismiss();
            }
        });
    }

    private void settingReservationDialog(final Dialog reservation){
        reservation.setContentView(R.layout.custom_time_dialog);

        Button setting = reservation.findViewById(R.id.ok);
        Button cancel = reservation.findViewById(R.id.cancel);

        final EditText hour = reservation.findViewById(R.id.hour);
        final EditText min = reservation.findViewById(R.id.min);
        final EditText sec = reservation.findViewById(R.id.sec);

        // 리스너
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 시간 설정
                //null 체크
                if(hour.getText().length()==0||min.getText().length()==0||sec.getText().length()==0){
                    Toast.makeText(getApplicationContext(),"정확한 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                int h = Integer.parseInt(String.valueOf(hour.getText()));
                int m = Integer.parseInt(String.valueOf(min.getText()));
                int s = Integer.parseInt(String.valueOf(sec.getText()));
                //시간 체크
                if(!checkClock(h,m,s)){
                    return;
                }
                //예약 하기
                Clock.settingReservation(h,m,s);
                rText.setText(String.format("예약 : %2d시 %2d분 %2d초",h,m,s));
                reservation.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //토글 해제
                tButton.setChecked(false);
                Clock.cancelReservation();
                reservation.dismiss();
            }
        });
    }

    private boolean checkClock(int h,int m,int s){
        if(h>=24||m>=60||s>=60){
            Toast.makeText(this,"정확한 시간을 입력해주세요",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}