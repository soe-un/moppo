package com.example.moppo;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DailyActivity extends AppCompatActivity {

    DbHelper helper;
    SQLiteDatabase db;

    private ArrayList<DailyPlan> mPlanList = new ArrayList<>();
    private CalendarAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    String selectedDate;
    int realIncome = 0;
    private String userID;  //static variable from Main
    private int ID_idx;     // static variable from Main


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        Intent intent = getIntent();

        //CalendarActivity 선택한 날짜 받아오기
        TextView date = (TextView) findViewById(R.id.date);
        selectedDate = intent.getExtras().getString("DATE");
        userID = intent.getStringExtra("userID");
        ID_idx = intent.getIntExtra("idx", 0);

        date.setText(selectedDate);

        //리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.PlanList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        helper = new DbHelper(this);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        mAdapter = new CalendarAdapter(DailyActivity.this, mPlanList);
        mRecyclerView.setAdapter(mAdapter);
        read();

        TextView totalIncome = (TextView) findViewById(R.id.total_income);
        totalIncome.setText("총 수입: " + realIncome);
    }

    public JSONArray goToServer(Cursor cursor) throws JSONException {
        cursor = helper.goToServerToInsert(db);
        JSONArray plansTables = new JSONArray();

        while (cursor.moveToNext()){
            int server_idx = cursor.getInt(cursor.getColumnIndex("server_idx"));
            String plan_name = cursor.getString(cursor.getColumnIndex("plan_name"));
            int plan_order = cursor.getInt(cursor.getColumnIndex("plan_order"));
            int income = cursor.getInt(cursor.getColumnIndex("income"));
            boolean is_complete = cursor.getInt(cursor.getColumnIndex("is_complete")) == 1? true : false;
            String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

            PlansTable tmp = new PlansTable(server_idx, plan_name, plan_order, income, is_complete, timestamp);
            try {
                String jsonPaln = tmp.toString();
                JSONObject jsonObject = new JSONObject(jsonPaln);
                plansTables.put(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(plansTables.length() == 0){
            try{
                JSONObject jsonObject = new JSONObject("{\"empty\":\"yes\"}");
                plansTables.put(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return plansTables;
    }

    @Override
    protected void onStop(){
        super.onStop();
        
        try {
            //insert record
            JSONArray insertPlans = goToServer(helper.goToServerToInsert(db));
            //update record
            JSONArray updatePlans = goToServer(helper.goToServerToUpdate(db));
            //delete record
            JSONArray deletePlans = goToServer(helper.goToServerToDelete(db));

        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                if (intOrder < 1) {
                    Toast.makeText(v.getContext(), "우선순위는 1부터", Toast.LENGTH_SHORT).show();
                    return;
                }

                //우선순위에 따라 돈 구현하기
                int possibleOrder = mPlanList.size() + 1;
                int intIncome = 0;

                /*
                intIncome = 100000 / possibleOrder * (possibleOrder - (intOrder - 1));
                //백의 자리부터 0
                intIncome = intIncome / 1000 * 1000;
                */

                switch (intOrder) {
                    case 1:
                        intIncome = 100000;
                        break;
                    case 2:
                        intIncome = 80000;
                        break;
                    case 3:
                        intIncome = 50000;
                        break;
                    case 4:
                        intIncome = 25000;
                        break;
                    default:
                        Toast.makeText(DailyActivity.this, "4가 최대입니다.", Toast.LENGTH_SHORT).show();
                        return;
                }

                //플랜 추가 .. &이거 다시 생각해보기
                DailyPlan planItem;
                if (intOrder > possibleOrder) {
                    planItem = new DailyPlan(plan, false, intOrder, -1, -1);
                } else {
                    planItem = new DailyPlan(plan, false, intOrder, intIncome, -1);
                }
                PlansTable pt = planItem.toPlansTable(-1, selectedDate);
                helper.putLocalDB(db, pt, 1);
                Cursor cursor = helper.readRecentRecord(db);
                if(cursor.moveToNext()){
                    planItem.setLocalIdx(cursor.getInt(cursor.getColumnIndex("idx")));
                }
                mPlanList.add(planItem);


                //추가될 때마다 기존 수입들도 바꾸기
                /*for (int i = 0; i < mPlanList.size(); i++) {
                    DailyPlan currentItem = mPlanList.get(i);
                    int currentOrder = Integer.parseInt(currentItem.getOrder());

                    if (currentOrder > mPlanList.size()) {
                        String currentIncome = "일정이 늘면 업데이트";
                        currentItem.setIncome(currentIncome);
                    } else {
                        //1000단위로 구현하기
                        int Order = 100000 / mPlanList.size() * (mPlanList.size() - (currentOrder - 1));
                        Order = Order / 1000 * 1000;
                        currentItem.setIncome(Integer.toString(Order));
                    }
                }*/

                //추가될 때마다 기존 수입들도 바꾸기
                for (int i = 0; i < mPlanList.size(); i++) {
                    DailyPlan currentItem = mPlanList.get(i);
                    int currentOrder = currentItem.getOrder();

                    if (currentOrder > mPlanList.size()) {
                        int currentIncome = -1;
                        currentItem.setIncome(currentIncome);
                    } else {
                        switch (currentOrder) {
                            case 1:
                                currentItem.setIncome(100000);
                                break;
                            case 2:
                                currentItem.setIncome(80000);
                                break;
                            case 3:
                                currentItem.setIncome(50000);
                                break;
                            case 4:
                                currentItem.setIncome(25000);
                                break;
                            //default:
                            //    Toast.makeText(DailyActivity.this, "4가 최대입니다.", Toast.LENGTH_SHORT).show();
                            //    return;
                        }
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
                if ((plan1.getOrder()) == plan2.getOrder())
                    return true;
            }
        }
        return false;
    }

    private boolean IsRight() { //우선순위가 일정 수보다 크지 않은지
        for (DailyPlan item : mPlanList)
            if ( item.getOrder() > mPlanList.size() )
                return false;
        return true;
    }

    /*public void store(View v) { //파일에 저장
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        realIncome = 0;
        String fileName = "planList" + selectedDate;
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
            totalIncome.setText("총 수입: " + realIncome);

            Toast.makeText(DailyActivity.this, "총수입이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();

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
    }*/

    //봉인.
    public void store(View v) { //DB에 저장

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

        //빈 item 없는지 check 해주세요
        for (DailyPlan item : mPlanList) { //plan 추가
            //PlansTable pt = new PlansTable(server_idx, item.getPlan(), item.getOrder(), item.getIncome(), item.getSelected(), selectedDate);
        }
        //PlansTable plansTable = new PlansTable(idx, planlist, flaglist);
        //System.out.println(plansTable);
        //helper.insertPlan(db, plansTable, selectedDate);

        totalIncome.setText("총 수입: " + realIncome);
        Toast.makeText(DailyActivity.this, "총수입이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();

    }

    /*private void read() { //파일 읽어오기
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
    }*/

    private void read() { //DB 읽어오기


        Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);

        while (cursor.moveToNext()){
            String plan = cursor.getString(cursor.getColumnIndex("plan_name"));
            int localidx = cursor.getInt(cursor.getColumnIndex("idx"));
            boolean isSelected = cursor.getInt(cursor.getColumnIndex("is_complete")) == 1? true : false;
            int order = cursor.getInt(cursor.getColumnIndex("plan_order"));
            int income = cursor.getInt(cursor.getColumnIndex("income"));

            DailyPlan dp = new DailyPlan(plan, isSelected, order, income, localidx);
            mPlanList.add(dp);
        }

        mAdapter.notifyDataSetChanged();
    }
}
