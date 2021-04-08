package com.example.myapplication.fragments.fragments3;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import db.Pay.PayClass;
import db.Repair.RepairAdapter;
import db.Repair.RepairClass;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Repair3 extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    RepairAdapter repairAdapter;



    public Fragment_Repair3() {

    }



    public static Fragment_Repair3 newInstance(String param1, String param2) {
        Fragment_Repair3 fragment = new Fragment_Repair3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("SHARED_PREF",MODE_PRIVATE);


        String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");
        View view = inflater.inflate(R.layout.fragment_repair3, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recview_remont3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<RepairClass> options1 =
                new FirebaseRecyclerOptions.Builder<RepairClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference(auth).child("Repairs_History").orderByChild("id").equalTo("apartment3"), RepairClass.class).build();

        repairAdapter = new RepairAdapter(options1);
        recyclerView.setAdapter(repairAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        repairAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        repairAdapter.stopListening();
    }
}