package com.example.moppo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

public class DailyActivity extends AppCompatActivity {

    private ArrayList<DailyPlan> mPlanList = new ArrayList<>();
    private CustomAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    String selectedDate;
    int realIncome = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        //리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.PlanList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mAdapter = new CustomAdapter(mPlanList);
        mAdapter = new CustomAdapter(this,mPlanList);
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();

        //CalendarActivity 선택한 날짜 받아오기
        TextView date = (TextView) findViewById(R.id.date);
        selectedDate = intent.getExtras().getString("DATE");
        date.setText(selectedDate);

        read();
        mAdapter.notifyDataSetChanged();

        TextView totalIncome = (TextView) findViewById(R.id.total_income);
        totalIncome.setText("총 수입: " + realIncome);


    }

    public void add(View v){
        //edit_box.xml 불러서 다이얼로그 보여주기
        AlertDialog.Builder builder = new AlertDialog.Builder(DailyActivity.this);
        View view = LayoutInflater.from(DailyActivity.this).inflate(R.layout.edit_box, null, false);
        builder.setView(view);

        final EditText editTextPlan = (EditText) view.findViewById(R.id.et_dialog_plan);
        final EditText editTextOrder = (EditText) view.findViewById(R.id.et_dialog_order);
        final Button btn_add_dialog = (Button) view.findViewById(R.id.btn_dialog_add);

        final AlertDialog dialog = builder.create();

        //다이얼로그의 추가 버튼을 누르면
        btn_add_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력 내용 가져오기
                String plan = editTextPlan.getText().toString();
                String order = editTextOrder.getText().toString();

                //order에 따라 돈 구현하기
                int intOrder = Integer.parseInt(order);
                int possibleOrder = mPlanList.size() + 1;
                int intIncome = 0;
                intIncome = 100000/possibleOrder*(possibleOrder-(intOrder-1));

                //ArrayList에 넣기
                DailyPlan planItem = new DailyPlan(plan, false, order, Integer.toString(intIncome));
                mPlanList.add(planItem); //뒤에 삽입

                //추가될 때마다 기존 수입들도 바꾸기
                for(int i=0; i<mPlanList.size(); i++) {
                    DailyPlan currentItem = mPlanList.get(i);
                    int currentOrder = Integer.parseInt(currentItem.getOrder());
                    currentItem.setIncome(Integer.toString(100000/mPlanList.size()*(mPlanList.size()-(currentOrder-1))));
                }

                //어댑터에게 알리기
                mAdapter.notifyDataSetChanged(); //업데이트

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void store(View v){
        FileOutputStream fos =  null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        String fileName = "planList" + selectedDate;
        realIncome = 0;
        TextView totalIncome = (TextView) findViewById(R.id.total_income);

        try{
            fos = openFileOutput(fileName, getApplicationContext().MODE_PRIVATE);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);
            dos.writeInt(mPlanList.size());
            for( DailyPlan item : mPlanList){
                dos.writeUTF(item.getPlan());
                dos.writeUTF(item.getOrder());
                dos.writeUTF(item.getIncome());
                dos.writeBoolean(item.getSelected());
                if(item.getSelected()) realIncome += Integer.parseInt(item.getIncome());
            }
            dos.writeInt(realIncome);

            read();
            totalIncome.setText("총 수입: " + realIncome);

            dos.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if (dos != null) dos.close();
                if (bos != null) bos.close();
                if (fos != null) fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void read(){
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        String fileName = "planList" + selectedDate;

        try{
            fis = openFileInput(fileName);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            int size = dis.readInt();
            for(int i = 0; i< size; i++ ){
                String plan = dis.readUTF();
                String order = dis.readUTF();
                String income = dis.readUTF();
                Boolean isSelected = dis.readBoolean();

                DailyPlan dailyPlan = new DailyPlan(plan, isSelected, order, income);
                mPlanList.add(dailyPlan);
            }
            realIncome = dis.readInt();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
         try{
             if (dis != null) dis.close();
             if (bis != null) bis.close();
             if (fis != null) fis.close();
         }catch(Exception e){
             e.printStackTrace();
         }
        }
    }


}