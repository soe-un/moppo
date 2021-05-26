package com.example.moppo.ranking;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.moppo.DbHelper;
import com.example.moppo.R;
import com.example.moppo.SupportActivity;
import com.example.moppo.InfoUser;
import com.example.moppo.TablePlans;
import com.example.moppo.TableUsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.CustomViewHolder>{

    private ArrayList<InfoUser> users;
    private Context mContext;

    int userNo;
    DbHelper helper;
    SQLiteDatabase db;

    public RankingAdapter(Context context, ArrayList<InfoUser> users, int idx) { //constructor
        this.mContext = context;
        this.users = users;
        this.userNo = idx;

        helper = new DbHelper(context);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{ //item 불러오기
        ImageView profile;
        TextView nickname;
        Button support;

        public CustomViewHolder(@NonNull View itemView) {

            super(itemView);
            this.profile = (ImageView) itemView.findViewById(R.id.profile_it);
            this.nickname = (TextView) itemView.findViewById(R.id.nickname_it);
            this.support = (Button) itemView.findViewById(R.id.support_it);

        }

        public TextView getNickname(){
            return nickname;
        }
        public Button getSupport() {
            return support;
        }
    }

    @NonNull
    @Override
    public RankingAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//뷰홀더 객체 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.CustomViewHolder holder, int position) {//데이터를 뷰홀더에 바인딩
        holder.getNickname().setText(users.get(position).getNick());

        holder.support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SupportActivity.class);

                int useridx = users.get(position).getIdx();
                String userID = users.get(position).getUserID();
                String nickname = users.get(position).getNick();
                int inMoney = users.get(position).getInMoney();
                intent.putExtra("userNo", userNo); //사용자 idx
                intent.putExtra("idx", useridx); //상대방 idx
                intent.putExtra("userID", userID); //상대방 ID
                intent.putExtra("nickname", nickname); //상대방 nickname
                intent.putExtra("inMoney", inMoney); //상대방이 후원을 통해 받은 도ㅗㄴ

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void getPlansfromServer(int index) { //원하는 idx의 DB 읽어오기

        Response.Listener<String> responseListener;
        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    helper.cleanLocalDB(db);

                    for(int i = 0 ; i<jsonArray.length() ; i++){
                        JSONObject tmpjsonobj = (JSONObject) jsonArray.get(i);

                        int server_idx = tmpjsonobj.getInt("server_idx");
                        String plan_name = tmpjsonobj.getString("plan_name");
                        int plan_order = tmpjsonobj.getInt("plan_order");
                        int income = tmpjsonobj.getInt("income");
                        int is_complete = tmpjsonobj.getInt("is_complete");
                        String timestamp = tmpjsonobj.getString("timestamp");

                        TablePlans tablePlans = new TablePlans(server_idx, plan_name, plan_order, income, is_complete, timestamp);
                        helper.putLocalDB(db, tablePlans, 0);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        TableUsers tableUsers = new TableUsers(responseListener, String.valueOf(index));
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(tableUsers);

    }
}
