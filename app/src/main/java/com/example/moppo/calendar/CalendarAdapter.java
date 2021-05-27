package com.example.moppo.calendar;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moppo.DbHelper;
import com.example.moppo.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CustomViewHolder> {

    DbHelper helper;
    SQLiteDatabase db;

    private ArrayList<DailyPlan> dailyPlans;
    private Context mContext;

    public CalendarAdapter(Context context, ArrayList<DailyPlan> dailyPlans) { //constructor
        this.mContext = context;
        this.dailyPlans = dailyPlans;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder { //item 불러오기
        TextView plan;
        TextView order;
        TextView income;

        CheckBox cb;
        ImageButton edit_btn;
        ImageView remove_btn;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.plan = (TextView) itemView.findViewById(R.id.plan_it);
            this.order = (TextView) itemView.findViewById(R.id.order_it);
            this.income = (TextView) itemView.findViewById(R.id.income_it);

            this.cb = (CheckBox) itemView.findViewById(R.id.cb_it);
            this.edit_btn = (ImageButton) itemView.findViewById(R.id.edit_btn_it);
            this.remove_btn = (ImageView) itemView.findViewById(R.id.remove_btn_it);

            helper = new DbHelper(mContext.getApplicationContext());

            try { //get database
                db = helper.getWritableDatabase();
            } catch (SQLException ex) {
                db = helper.getReadableDatabase();
            }
        }
    }

    @NonNull
    @Override
    public CalendarAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 객체 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.CustomViewHolder holder, int position) {//데이터를 뷰홀더에 바인딩
        final DailyPlan dailyPlan = dailyPlans.get(position); //final로 선언해야 값이 바뀌지 않음

        holder.plan.setText(dailyPlan.getPlan());
        holder.order.setText("우선순위: " + dailyPlan.getOrder());
        holder.income.setText("수입: " + dailyPlan.getIncome());

        //수정하기
        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //edit_box.xml 불러서 다이얼로그 보여주기
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View view = LayoutInflater.from(mContext).inflate(R.layout.edit_box, null, false);
                builder.setView(view);

                final TextView dialogTitle = (TextView) view.findViewById(R.id.text_dialog);
                final EditText editTextPlan = (EditText) view.findViewById(R.id.et_dialog_plan);
                final EditText editTextOrder = (EditText) view.findViewById(R.id.et_dialog_order);
                final Button btn_edit_dialog = (Button) view.findViewById(R.id.btn_dialog_add);

                dialogTitle.setText("하루 일정 수정하기");
                btn_edit_dialog.setText("수정");


                //입력되어 있던 데이터
                editTextPlan.setText(dailyPlans.get(holder.getAdapterPosition()).getPlan());
                editTextOrder.setText(String.valueOf(dailyPlans.get(holder.getAdapterPosition()).getOrder()));

                final AlertDialog dialog = builder.create();

                //다이얼로그의 수정버튼 누르면
                btn_edit_dialog.setOnClickListener(new View.OnClickListener() {
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


                        //이 부분 오류 안 잡힘
                        if (order.equals("")) {
                            Toast.makeText(v.getContext(), "우선순위를 입력하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //우선순위에 따라 돈 구현하기
                        int possibleOrder = dailyPlans.size();
                        int intIncome = 0;

                        intIncome = 100000 / possibleOrder * (possibleOrder - (intOrder - 1));
                        //백의 자리부터 0
                        intIncome = intIncome / 1000 * 1000;

                        /*
                        switch (order) {
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
                                Toast.makeText(v.getContext(),"4가 최대입니다.", Toast.LENGTH_SHORT).show();
                                return;
                        }*/

                        //플랜 변경
                        DailyPlan planItem;
                        if (intOrder > possibleOrder) {
                            Toast.makeText(mContext, "우선순위를 1부터\n순서대로 입력하세요.", Toast.LENGTH_LONG).show();
                            return;
                            //planItem = new DailyPlan(plan, 0, intOrder, -1, dailyPlan.getLocalIdx());
                        } else
                            planItem = new DailyPlan(plan, 0, intOrder, intIncome, dailyPlan.getLocalIdx());

                        helper.updateLocalDB(db, planItem);
                        System.out.println(planItem.getLocalIdx());
                        dailyPlans.set(holder.getAdapterPosition(), planItem);

                        //업데이트
                        notifyItemChanged(holder.getAdapterPosition());

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //삭제하기
        holder.remove_btn.setTag(position); // 몇번짼지 태그 달기
        holder.remove_btn.setOnClickListener(new View.OnClickListener() { // 리무브 버튼 누르면 그 아이템 삭제시키기
            @Override
            public void onClick(View v) {
                int removePosition = (int) v.getTag();
                System.out.println(dailyPlans.get(removePosition).getLocalIdx());
                helper.deleteLocalDB(db, dailyPlans.get(removePosition).getLocalIdx());
                dailyPlans.remove(removePosition); // 그 아이템 삭제


                //삭제될 때마다 기존 수입들도 바꾸기
                for (int i = 0; i < dailyPlans.size(); i++) {
                    DailyPlan currentItem = dailyPlans.get(i);
                    int currentOrder = currentItem.getOrder();

                    if(currentOrder > dailyPlans.size()) {
                        Toast.makeText(mContext, "일정이 늘면 수입이 \n 정상적으로 업데이트 됩니다.", Toast.LENGTH_LONG).show();
                        currentItem.setIncome(0);
                    }
                    else {
                    //1000단위로 구현하기
                    int Order = 100000 / dailyPlans.size() * (dailyPlans.size() - (currentOrder - 1));
                    Order = Order / 1000 * 1000;
                    currentItem.setIncome(Order);
                    }
                }

                /*
                //삭제될 때마다 기존 수입들도 바꾸기
                for(int i=0; i<dailyPlans.size(); i++) {
                    DailyPlan currentItem = dailyPlans.get(i);
                    int currentOrder = currentItem.getOrder();

                    if(currentOrder > dailyPlans.size()) {
                        int currentIncome = -1;
                        currentItem.setIncome(currentIncome);
                    }
                    else {
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
                            //    Toast.makeText(v.getContext(), "4가 최대입니다.", Toast.LENGTH_SHORT).show();
                            //    break;
                        }
                    }
                }*/

                //업데이트
                notifyDataSetChanged();
            }
        });

        //먼저 체크박스 리스너 초기화
        holder.cb.setOnCheckedChangeListener(null);
        //getter로 체크 상태를 가져오고 setter로 이 값을 아이템 안의 체크박스에 set
        holder.cb.setChecked(dailyPlan.getSelected() == 1 ? true : false);
        //체크 상태를 알기 위해 리스너 부착
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //여기 dailyPlan이 final 키워드를 붙인 모델 클래스의 객체와 동일
                dailyPlan.setSelected(isChecked ? 1 : 0);
                helper.updateLocalDB(db, dailyPlan);
            }
        });
    }

    @Override
    public int getItemCount() {//전체 아이템 갯수 리턴
        return dailyPlans.size();
    }

    @Override
    public long getItemId(int position) {
        //return super.getItemId(position);
        return position;
    }
}

