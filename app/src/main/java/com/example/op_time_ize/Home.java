package com.example.op_time_ize;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.op_time_ize.Adapter.ToDoAdapter;
import com.example.op_time_ize.Model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity implements OnDIalogCLoseListener {


    private RecyclerView tasksRecyclerView;
    private FloatingActionButton fab;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        settingsButton();
        kalendarButton();



        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        firestore = FirebaseFirestore.getInstance();

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(Home.this , mList);

        tasksRecyclerView = findViewById(R.id.taskRecycleView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(adapter);


        fab = findViewById(R.id.fab);


        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
        showData();

    }

    private void showData(){
        query = firestore.collection("task").whereEqualTo("userId", user.getUid()).orderBy("time", Query.Direction.DESCENDING);
                listenerRegistration =query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<DocumentChange> documentChanges = value.getDocumentChanges();

                            for (DocumentChange documentChange : documentChanges){
                                if (documentChange.getType() == DocumentChange.Type.ADDED){
                                    String id = documentChange.getDocument().getId();
                                    ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                                    mList.add(toDoModel);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        listenerRegistration.remove();
                    }
                });

    }


    protected void settingsButton(){
        ImageButton settingsbtn = (ImageButton) findViewById(R.id.settingsbtn);
                settingsbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Home.this, Settings.class));
                    }
                });
    }

    protected void kalendarButton(){
        ImageButton kalendarbtn = (ImageButton) findViewById(R.id.kalendarbtn);
            kalendarbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Home.this, Kalendar.class));
                }
            });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}
