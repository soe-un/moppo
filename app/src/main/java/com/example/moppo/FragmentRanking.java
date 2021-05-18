package com.example.moppo;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentRanking extends Fragment {

    DbHelper helper;
    SQLiteDatabase db;

    private RankingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<UserInfo> mUserList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_ranking,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new DbHelper(getActivity());

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //전체 유저 목록 가져오기
        mUserList.clear();
        Cursor cursor = helper.getRankList(db);
        while (cursor.moveToNext()) {
            String tmpnick = cursor.getString(cursor.getColumnIndex("nickname"));
            UserInfo tmpuserinfo = new UserInfo(tmpnick);
            mUserList.add(tmpuserinfo);
        }
        cursor.close();

        mAdapter = new RankingAdapter(getActivity(), mUserList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
