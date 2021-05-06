package com.example.moppo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DailyActivity extends AppCompatActivity {

    private ArrayList<DailyPlan> mPlanList = new ArrayList<>();
    private CalendarAdapter mAdapter;
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

        mAdapter = new CalendarAdapter(this, mPlanList);
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

    public void add(View v) {
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

                int intOrder = Integer.parseInt(order);
                //우선순위 1부터 가능
                if(intOrder<1){
                    Toast.makeText(v.getContext(), "우선순위는 1부터", Toast.LENGTH_SHORT).show();
                    return;
                }

                //우선순위에 따라 돈 구현하기
                int possibleOrder = mPlanList.size() + 1;
                int intIncome = 0;
                intIncome = 100000 / possibleOrder * (possibleOrder - (intOrder - 1));
                //백의 자리부터 0
                intIncome = intIncome / 1000 * 1000;

                //플랜 추가
                DailyPlan planItem;
                if(intOrder > possibleOrder ) {
                    planItem = new DailyPlan(plan, false, order, "일정이 늘면 업데이트");
                } else {
                    planItem = new DailyPlan(plan, false, order, Integer.toString(intIncome));
                }
                mPlanList.add(planItem);

                //추가될 때마다 기존 수입들도 바꾸기
                for (int i = 0; i < mPlanList.size(); i++) {
                    DailyPlan currentItem = mPlanList.get(i);
                    int currentOrder = Integer.parseInt(currentItem.getOrder());

                    if(currentOrder > mPlanList.size()) {
                        String currentIncome = "일정이 늘면 업데이트";
                        currentItem.setIncome(currentIncome);
                    }
                    else {
                        //1000단위로 구현하기
                        int Order = 100000 / mPlanList.size() * (mPlanList.size() - (currentOrder - 1));
                        Order = Order / 1000 * 1000;
                        currentItem.setIncome(Integer.toString(Order));
                    }
                }

                //어댑터에게 알리기
                mAdapter.notifyDataSetChanged(); //업데이트
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean IsRepeat() { //우선순위 중복인지
        for (int i = 0; i < mPlanList.size() - 1; i++) {
            DailyPlan plan1 = mPlanList.get(i);
            for (int k = i + 1; k < mPlanList.size(); k++) {
                DailyPlan plan2 = mPlanList.get(k);
                if (Integer.parseInt((plan1.getOrder())) == Integer.parseInt(plan2.getOrder()))
                    return true;
            }
        }
        return false;
    }

    private boolean IsRight() { //우선순위가 일정 수보다 크지 않은지
        for (DailyPlan item : mPlanList)
            if (Integer.parseInt(item.getOrder()) > mPlanList.size())
                return false;
        return true;
    }

    public void store(View v) { //파일에 저장
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        String fileName = "planList" + selectedDate;
        realIncome = 0;
        TextView totalIncome = (TextView) findViewById(R.id.total_income);

        if (IsRepeat()) { //우선순위 중복이면 저장 x
            Toast.makeText(DailyActivity.this, "우선순위 중복으로 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!IsRight()) { //우선순위가 일정 수보다 크면 저장 x
            Toast.makeText(DailyActivity.this, "우선순위가 일정의 수를 넘어\n저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            fos = openFileOutput(fileName, getApplicationContext().MODE_PRIVATE);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);
            dos.writeInt(mPlanList.size());
            for (DailyPlan item : mPlanList) {
                dos.writeUTF(item.getPlan());
                dos.writeUTF(item.getOrder());
                dos.writeUTF(item.getIncome());
                dos.writeBoolean(item.getSelected());
                if (item.getSelected()) realIncome += Integer.parseInt(item.getIncome());
            }
            dos.writeInt(realIncome);

            read();
            totalIncome.setText("총 수입: " + realIncome);

            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) dos.close();
                if (bos != null) bos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void read() { //파일 읽어오기
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        String fileName = "planList" + selectedDate;

        try {
            fis = openFileInput(fileName);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                String plan = dis.readUTF();
                String order = dis.readUTF();
                String income = dis.readUTF();
                Boolean isSelected = dis.readBoolean();

                DailyPlan dailyPlan = new DailyPlan(plan, isSelected, order, income);
                mPlanList.add(dailyPlan);
            }
            realIncome = dis.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) dis.close();
                if (bis != null) bis.close();
                if (fis != null) fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}