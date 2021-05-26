package com.example.moppo.ranking;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.moppo.R;
import com.example.moppo.InfoUser;
import com.example.moppo.TableUsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentRanking extends Fragment {

    private RankingAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<InfoUser> mUserList = new ArrayList<>();

    String userID;
    int idx;
    String userNick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        idx = bundle.getInt("idx");
        userNick = bundle.getString("nickname");

        return inflater.inflate(R.layout.fragment_ranking,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //전체 유저 목록 가져오기
        mUserList.clear();

        Response.Listener<String> responseListener;
        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for(int i = 0 ; i<jsonArray.length() ; i++){
                        JSONObject tmpjsonobj = (JSONObject) jsonArray.get(i);
                        if(idx == tmpjsonobj.getInt("idx")){ //자신은 제외
                            continue;
                        }
                        String nickname = tmpjsonobj.getString("nickname");
                        int idx = tmpjsonobj.getInt("idx");
                        int totalMoney = tmpjsonobj.getInt("totalMoney");
                        String userID = tmpjsonobj.getString("userID");

                        InfoUser ui = new InfoUser(nickname, idx, totalMoney, userID);
                        mUserList.add(ui);

                    }
                    mAdapter = new RankingAdapter(getActivity(), mUserList);
                    mRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        TableUsers tableUsers = new TableUsers(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(tableUsers);


    }

}
