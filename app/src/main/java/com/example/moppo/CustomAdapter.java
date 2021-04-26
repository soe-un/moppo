package com.example.moppo;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<DailyPlan> dailyPlans;
    private Context mContext;

    public CustomAdapter(Context context, ArrayList<DailyPlan> dailyPlans) { //constructor
        this.mContext = context;
        this.dailyPlans = dailyPlans;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{ //item들 불러오기
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
        }
    }

    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰홀더 객체 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {//데이터를 뷰홀더에 바인딩
        final DailyPlan dailyPlan = dailyPlans.get(position); //final로 선언해야 값이 바뀌지 않음

        holder.plan.setText(dailyPlan.getPlan());
        holder.order.setText("우선순위: " + dailyPlan.getOrder());
        holder.income.setText("수입: " + dailyPlan.getIncome());

        //수정하기
        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int editPosition = (int) v.getTag();

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
                editTextOrder.setText(dailyPlans.get(holder.getAdapterPosition()).getOrder());

                final AlertDialog dialog = builder.create();

                //다이얼로그의 수정버튼 누르면
                btn_edit_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //입력 내용 가져오기
                        String plan = editTextPlan.getText().toString();
                        String order = editTextOrder.getText().toString();

                        //order에 따라 돈 구현하기
                        int intOrder = Integer.parseInt(order);
                        int possibleOrder = dailyPlans.size() + 1;
                        int intIncome = 0;
                        intIncome = 100000/possibleOrder*(possibleOrder-(intOrder-1));

                        //데이터 변경
                        DailyPlan planItem = new DailyPlan(plan, false, order, Integer.toString(intIncome));
                        dailyPlans.set(holder.getAdapterPosition(),planItem); //수정

                        //추가될 때마다 기존 수입들도 바꾸기
                        for(int i=0; i<dailyPlans.size(); i++) {
                            DailyPlan currentItem = dailyPlans.get(i);
                            int currentOrder = Integer.parseInt(currentItem.getOrder());
                            currentItem.setIncome(Integer.toString(100000/dailyPlans.size()*(dailyPlans.size()-(currentOrder-1))));
                        }

                        //어댑터에게 알리기
                        notifyItemChanged(holder.getAdapterPosition()); //업데이트
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
                dailyPlans.remove(removePosition); // 그 아이템 삭제
                notifyItemRemoved(removePosition);
                notifyItemRangeChanged(removePosition,dailyPlans.size()); // 업데이트
            }
        });

        //먼저 체크박스 리스너 초기화
        holder.cb.setOnCheckedChangeListener(null);
        //getter로 체크 상태를 가져오고 setter로 이 값을 아이템 안의 체크박스에 set
        holder.cb.setChecked(dailyPlan.getSelected());
        //체크 상태를 알기 위해 리스너 부착
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //여기 dailyPlan이 final 키워드를 붙인 모델 클래스의 객체와 동일
                dailyPlan.setSelected(isChecked);
            }
        });

        //체크박스 체크면 수입 표시 아니면 수입이 안 보이게
     /*   holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cb.isChecked()){
                    holder.income.setVisibility(View.VISIBLE);
                }else{
                    holder.income.setVisibility(View.INVISIBLE);
                }
            }
        });*/
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
