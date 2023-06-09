package com.example.op_time_ize.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.op_time_ize.AddNewTask;
import com.example.op_time_ize.Home;
import com.example.op_time_ize.Model.ToDoModel;
import com.example.op_time_ize.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

    public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

        private List<ToDoModel> todoList;
        private Home activity;
        private FirebaseFirestore firestore;

        public ToDoAdapter(Home home , List<ToDoModel> todoList){
            this.todoList = todoList;
            activity = home;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.task_layout, parent , false);
            firestore = FirebaseFirestore.getInstance();

            return new MyViewHolder(view);
        }

        public void deleteTask(int position){
            ToDoModel toDoModel = todoList.get(position);
            firestore.collection("task").document(toDoModel.TaskId).delete();
            todoList.remove(position);
            notifyItemRemoved(position);
        }
        public Context getContext(){
            return activity;
        }
        public void editTask(int position){
            ToDoModel toDoModel = todoList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString("task" , toDoModel.getTask());
            bundle.putString("due" , toDoModel.getDue());
            bundle.putString("id" , toDoModel.TaskId);
            bundle.putString("id_time", toDoModel.getDue_time());

            AddNewTask addNewTask = new AddNewTask();
            addNewTask.setArguments(bundle);
            addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            ToDoModel toDoModel = todoList.get(position);
            holder.mCheckBox.setText(toDoModel.getTask());

            holder.mDueDateTv.setText("Spraviť do " + toDoModel.getDue() + "      " + toDoModel.getDue_time());


            holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        firestore.collection("task").document(toDoModel.TaskId).update("status" , 1);
                    }else{
                        firestore.collection("task").document(toDoModel.TaskId).update("status" , 0);
                    }
                }
                });
        }
        private boolean toBoolean(int status){
            return status != 0;
        }

        @Override
        public int getItemCount() {
            return todoList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView mDueDateTv;
            TextView mDueTimeTv;
            CheckBox mCheckBox;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                mDueTimeTv = itemView.findViewById(R.id.dueTime);
                mDueDateTv = itemView.findViewById(R.id.due_date_tv);
                mCheckBox = itemView.findViewById(R.id.mcheckbox);

            }
        }
}
