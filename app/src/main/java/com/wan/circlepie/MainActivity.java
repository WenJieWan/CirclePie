package com.wan.circlepie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*========= 数据相关 =========*/
    AnimationCirclePie college_num_pie;                    //饼状图控件
    List<CirclePieValue> circlePieValues;                 //饼状图数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        college_num_pie = (AnimationCirclePie) findViewById(R.id.college_num_pie);
        circlePieValues= new ArrayList<>();
        circlePieValues.add(new CirclePieValue("文本1",25f,getResources().getColor(R.color.risk_mid_bg)));
        circlePieValues.add(new CirclePieValue("文本2",25f,getResources().getColor(R.color.risk_high_bg)));
        circlePieValues.add(new CirclePieValue("文本3",25f,getResources().getColor(R.color.risk_low_bg)));
        college_num_pie.setPieData(circlePieValues);
    }
}
