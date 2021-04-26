package com.example.moppo;

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

import java.util.ArrayList;

public class DailyActivity extends AppCompatActivity {

    private ArrayList<DailyPlan> mPlanList = new ArrayList<>();
    private CustomAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

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

        //액티비티 메인
        TextView date = (TextView) findViewById(R.id.date); //캘린더 누른 날짜 받아오기 구현

        ImageView btn_add = (ImageView) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //edit_box.xml 불러서 다이얼로그 보여주기
                AlertDialog.Builder builder = new AlertDialog.Builder(DailyActivity.this);
                View view = LayoutInflater.from(DailyActivity.this).inflate(R.layout.edit_box, null, false);
                builder.setView(view);

                final TextView dialogTitle = (TextView) view.findViewById(R.id.text_dialog);
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

                        //order가 size+1까지만 가능하게 ! 가능하면 중복도 없게!
                        /*for(int i=0; i < intOrder; )*/


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
        });
    }
}