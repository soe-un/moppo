package com.example.moppo.ranking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moppo.R;
import com.example.moppo.SupportActivity;
import com.example.moppo.InfoUser;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.CustomViewHolder>{

    private ArrayList<InfoUser> users;
    private Context mContext;

    public RankingAdapter(Context context, ArrayList<InfoUser> users) { //constructor
        this.mContext = context;
        this.users = users;
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
                intent.putExtra("idx", useridx);
                intent.putExtra("userID", userID);
                intent.putExtra("nickname", nickname);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
