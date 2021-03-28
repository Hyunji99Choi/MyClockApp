package com.example.myclockapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyClock extends View {

    int cx,cy,cw; //뷰의 중앙, 시계의 폭
    int pw,ph; //시계 바늘의 폭과 높이

    private Bitmap clock;
    private Bitmap pins[] = new Bitmap[3];

    String AM_PM; //오후 여부
    int hour, min, sec; //시,분,초
    int rHour, rMin, rSec; //시,분,초침의 회전각

    Resources res; //리소스
    Context context;
    Timer cTime;

    boolean reservation = false;
    String rAM_PM; //오후 여부
    int rh, rm, rs; //예약 시간

    int count; //현재 디자인 번호
    final int MAX_DGN = 3;
    int[][] design = {
            {R.drawable.clcok1,R.drawable.clock2,R.drawable.clock3},
            {R.drawable.pin_sec1,R.drawable.pin_sec2,R.drawable.pin_sec3},
            {R.drawable.pin_minute1,R.drawable.pin_minute2,R.drawable.pin_minute3},
            {R.drawable.pin_hour1,R.drawable.pin_hour2,R.drawable.pin_hour3}
    };
    Typeface[] tArray = {Typeface.create(Typeface.DEFAULT,Typeface.NORMAL),
            Typeface.create(Typeface.SERIF,Typeface.NORMAL),
            Typeface.create(Typeface.MONOSPACE,Typeface.NORMAL)};
    int[] backColor = {Color.WHITE,Color.YELLOW,Color.LTGRAY};

    public MyClock(Context context) {
        super(context);

        count = 0; //현재 디자인 번호
        this.context = context;

        //시계 이미지 세팅
        res = context.getResources();
        settingDesign(0);

        cTime = new Timer();
        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                invalidate();
            }
        };

        cTime.schedule(TT,1000,1000);

        //현재 시간 세팅
        currentTime();

    }
    public void cancelReservation(){
        reservation = false; //false
    }
    public void settingReservation(int h,int m,int s){
        if(h>=12) {
            rAM_PM = "PM";
            rh=h-12;
        }else {
            rAM_PM = "AM";
            rh=h;
        }
        reservation = true;   rm=m;   rs=s;
        Log.w("예약",rAM_PM+" "+rh+" "+rm+" "+rs);
    }
    public void changeTime(int h, int m, int s){

        if(hour>12) {
            AM_PM = "PM";
            hour=h-12;
        }else {
            AM_PM = "AM";
            hour=h;
        }
        min=m;  sec=s;
    }
    public void changeDesign(){
        count=(count+1)%MAX_DGN;
        settingDesign(count);
        invalidate();
    }
    public void settingDesign(int i){
        clock = BitmapFactory.decodeResource(res, design[0][i]);
        pins[0] = BitmapFactory.decodeResource(res, design[1][i]);//초침
        pins[1] = BitmapFactory.decodeResource(res, design[2][i]);//분침
        pins[2] = BitmapFactory.decodeResource(res, design[3][i]);//시침
    }

    public void onDraw(Canvas canvas){

        //viw 중심좌표 cx,cy에 저장
        cx = canvas.getWidth()/2;
        cy = canvas.getHeight()/2;
        //시계 바늘 폭과 높이
        cw=clock.getWidth()/2;
        //pw = pins[0].getWidth()/2; //침 폭
        //ph= pins[0].getHeight()/2; //침 높이

        CalcTime(); //시간, 각도 구하기
        CheckRsv(); //예약 시간 확인

        //canvas.drawColor(Color.WHITE); //배경색
        canvas.drawColor(backColor[count]); //배경색

        canvas.drawBitmap(clock,cx-cw,cy-cw,null); //시계 배경을 캔버스에 그림

        //시침을 그리기 위해 캔버스를 회전시킴
        canvas.rotate(rHour,cx,cy);
        pw = pins[2].getWidth()/2; //침 폭
        ph= pins[2].getHeight()/2; //침 높이
        canvas.drawBitmap(pins[2],cx-pw,cy-ph,null);

        //분침을 그리기 위해 캔버스를 회전
        canvas.rotate(rMin-rHour,cx,cy);
        pw = pins[0].getWidth()/2; //침 폭
        ph= pins[0].getHeight()/2; //침 높이
        canvas.drawBitmap(pins[1],cx-pw,cy-ph,null);

        //초침
        canvas.rotate(rSec-rMin,cx,cy);
        canvas.drawBitmap(pins[0],cx-pw,cy-ph,null);

        canvas.rotate(-rSec,cx,cy); //캔버스를 원래대로 회전

        //시,분,초 디지털 표시
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setTypeface(tArray[count]); //글꼴
        canvas.drawText(String.format("%2s %2d : %2d : %2d",AM_PM,hour,min,sec),cx-140,cy+cw+70,paint);


    }

    public void CheckRsv(){
        if(reservation==true&&AM_PM.equals(rAM_PM)&&hour==rh&&min==rm&&sec==rs){
            Toast.makeText(context,"예약한 시간입니다.",Toast.LENGTH_LONG).show();
        }
    }

    public void CalcTime(){
        //1초 증가
        upTime();

        //회전 각 구하기
        rSec=sec*6;
        rMin=min*6+rSec/60;
        rHour=hour*30+rMin/60;

    }

    private void upTime(){
        min+=(sec+1)/60; //초가 60초를 넘으면 분 증가
        hour+=min/60; //분이 60분이 넘으면 시 증가

        sec=(sec+1)%60;
        min%=60;

        if(hour>=12){
            if(AM_PM == "PM") //오후
                AM_PM = "AM"; //오전으로 변환
            else
                AM_PM = "PM"; //오후로 변환
        }
        hour%=12;

    }

    private void currentTime(){ //현재 시간 구하기
        GregorianCalendar calendar = new GregorianCalendar();
        hour = calendar.get(Calendar.HOUR);
        min=calendar.get(Calendar.MINUTE);
        sec=calendar.get(Calendar.SECOND);

        //오전 오후 구분
        if(calendar.get(Calendar.AM_PM)==1)
            AM_PM = "PM";
        else
            AM_PM = "AM";
    }

}
