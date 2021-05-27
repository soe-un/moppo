package com.example.moppo.calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.example.moppo.DbHelper;
import com.example.moppo.TablePlans;
import com.example.moppo.R;
import com.example.moppo.TableServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

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

        try { //get database
            db = helper.getWritableDatabase();
        } catch (SQLException ex) {
            db = helper.getReadableDatabase();
        }

        mAdapter = new CalendarAdapter(DailyActivity.this, mPlanList);
        mRecyclerView.setAdapter(mAdapter);
        read();

        TextView totalIncome = (TextView) findViewById(R.id.total_income);
        totalIncome.setText("총 수입: " + realIncome);
    }

    public JSONArray goToServer(Cursor cursor) {

        JSONArray plansTables = new JSONArray();

        while (cursor.moveToNext()) {
            int server_idx = cursor.getInt(cursor.getColumnIndex("server_idx"));
            String plan_name = cursor.getString(cursor.getColumnIndex("plan_name"));
            int plan_order = cursor.getInt(cursor.getColumnIndex("plan_order"));
            int income = cursor.getInt(cursor.getColumnIndex("income"));
            int is_complete = cursor.getInt(cursor.getColumnIndex("is_complete"));
            String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));


            TablePlans tmp = new TablePlans(server_idx, plan_name, plan_order, income, is_complete, timestamp);
            try {
                String jsonPaln = tmp.toString();
                JSONObject jsonObject = new JSONObject(jsonPaln);
                plansTables.put(jsonObject);
                //helper.reflectionServer(db, cursor.getInt(cursor.getColumnIndex("idx")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (plansTables.length() == 0) {
            try {
                JSONObject jsonObject = new JSONObject("{\"empty\":\"yes\"}");
                plansTables.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return plansTables;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life", "pause Daily");
        JSONArray json = new JSONArray();
        //insert record
        JSONArray insertPlans = goToServer(helper.goToServerToInsert(db));
        //update record
        JSONArray updatePlans = goToServer(helper.goToServerToUpdate(db));
        //delete record
        JSONArray deletePlans = goToServer(helper.goToServerToDelete(db));
        //USER
        JSONArray user = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject("{\"userNo\":\"" + ID_idx + "\"}");
            user.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        json.put(insertPlans);
        json.put(updatePlans);
        json.put(deletePlans);
        json.put(user);

        System.out.println(json.toString());

        Response.Listener<JSONArray> responseListener;
        responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());
            }
        };
        TableServer tableServer = new TableServer(responseListener, json);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(tableServer);


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

                //빈 항목 체크
                if (plan.isEmpty()) {
                    Toast.makeText(DailyActivity.this, "일정을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (order.isEmpty()) {
                    Toast.makeText(DailyActivity.this, "우선순위를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                int intOrder = Integer.parseInt(order);

                DailyPlan planItem;

                planItem = new DailyPlan(plan, 0, intOrder, 0, -1);
                mPlanList.add(planItem);

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
        for (DailyPlan item : mPlanList) {
            if (item.getOrder() > mPlanList.size())
                return false;
        }
        return true;
    }

    private boolean IsPositive() {
        for (DailyPlan item : mPlanList) {
            if (item.getOrder() < 1)
                return false;
        }
        return true;
    }

    public void store(View v) { // 저장 버튼 클릭

        realIncome = 0;

        TextView totalIncome = (TextView) findViewById(R.id.total_income);

        //*오류 체크들 토스트 안뜸 !!
        if (IsRepeat()) { //우선순위 중복이면 저장 x
            Toast.makeText(DailyActivity.this, "우선순위 중복으로 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!IsRight()) { //우선순위가 일정 수보다 크면 저장 x
            Toast.makeText(v.getContext(), "우선순위가 일정의 수를 넘어\n저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            Log.d("daily", "isright");
            return;
        }

        if (!IsPositive()) { //우선순위가 1보다 작으면 저장 x
            Toast.makeText(v.getContext(), "우선순위는 1부터 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.sort(mPlanList); // 우선순위 오름차순 정렬

        for (DailyPlan item : mPlanList) {
            //수입
            int currentOrder = item.getOrder();
            int income = 100000 / mPlanList.size() * (mPlanList.size() - (currentOrder - 1));
            income = income / 1000 * 1000;
            item.setIncome(income);

            //총수입
            if (item.getSelected() == 1) {
                realIncome += item.getIncome();
            }

            //없는 요소일 경우 local db에 put
            if(item.getLocalIdx() == -1) {
                TablePlans pt = new TablePlans(-1, item.getPlan(), item.getOrder(), item.getIncome(), item.getSelected(), selectedDate);
                helper.putLocalDB(db, pt, 1);
            }else{ //있는 요소일 경우 update
                helper.updateLocalDB(db, item); //local db에 업데이트
            }
        }

        mAdapter.notifyDataSetChanged();
        totalIncome.setText("총 수입: " + realIncome);
        Toast.makeText(DailyActivity.this, "총수입이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();

    }

    private void read() { //DB 읽어오기

        Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);

        while (cursor.moveToNext()) {
            String plan = cursor.getString(cursor.getColumnIndex("plan_name"));
            int localidx = cursor.getInt(cursor.getColumnIndex("idx"));
            int isSelected = cursor.getInt(cursor.getColumnIndex("is_complete"));
            int order = cursor.getInt(cursor.getColumnIndex("plan_order"));
            int income = cursor.getInt(cursor.getColumnIndex("income"));
            if (isSelected == 1)
                realIncome += income;

            DailyPlan dp = new DailyPlan(plan, isSelected, order, income, localidx);
            mPlanList.add(dp);
        }

        mAdapter.notifyDataSetChanged();
    }
}
