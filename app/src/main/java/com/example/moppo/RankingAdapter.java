package com.example.moppo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.CustomViewHolder>{

    private ArrayList<UserInfo> users;
    private Context mContext;

    public RankingAdapter(Context context, ArrayList<UserInfo> users) { //constructor
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

            System.out.println("CustomViewHolder");
        }

        public TextView getNickname(){
            return nickname;
        }
    }

    @NonNull
    @Override
    public RankingAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//뷰홀더 객체 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        System.out.println("onCreateViewHolder");
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.CustomViewHolder holder, int position) {//데이터를 뷰홀더에 바인딩
        System.out.println("position: "+position+", users: "+users.get(position).getNick());
        holder.getNickname().setText(users.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
