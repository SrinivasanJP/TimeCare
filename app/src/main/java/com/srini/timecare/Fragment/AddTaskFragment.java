package com.srini.timecare.Fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.srini.timecare.Helper.TaskHelperClass;
import com.srini.timecare.R;


public class AddTaskFragment extends Fragment {

    //view variables
    private EditText vTaskInput;
    private EditText vDescription;
    private TextView vDatePicker;
    private RelativeLayout vBtnAddTask;
    private ProgressBar vProgressBar;
    private TextView vAddTaskTextview;

    //backend variables
    private TaskHelperClass taskHelperClass;
    //firebase variables
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;

    Calendar calendar = Calendar.getInstance() ;
    final int year = calendar.get(Calendar.YEAR) ;
    final int month = calendar.get(Calendar.MONTH) ;
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    public AddTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        //hooks
        vTaskInput = view.findViewById(R.id.task_input);
        vDescription = view.findViewById(R.id.task_discription_input);
        vDatePicker = view.findViewById(R.id.select_date);
        vBtnAddTask = view.findViewById(R.id.btnAddTask);
        vProgressBar = view.findViewById(R.id.ProgressbarAddTask);
        vAddTaskTextview = view.findViewById(R.id.BtnAddTasktv);

        vDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        vDatePicker.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        vBtnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vAddTaskTextview.setVisibility(View.INVISIBLE);
                vProgressBar.setVisibility(view.VISIBLE);
                taskHelperClass = new TaskHelperClass();
                taskHelperClass.setTitle(vTaskInput.getText().toString().trim());
                taskHelperClass.setDescription(vDescription.getText().toString().trim());
                taskHelperClass.setDate(vDatePicker.getText().toString().trim());
                taskHelperClass.setDone(false);
                if(taskHelperClass.getTitle().isEmpty()){
                    vTaskInput.setError("Title required");
                    vAddTaskTextview.setVisibility(View.VISIBLE);
                    vProgressBar.setVisibility(view.INVISIBLE);
                }else if(taskHelperClass.getDate().isEmpty()){
                    vDatePicker.setError("Date is required");
                    vAddTaskTextview.setVisibility(View.VISIBLE);
                    vProgressBar.setVisibility(view.INVISIBLE);
                }else {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    documentReference = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(taskHelperClass.getTitle());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists() && !task.getResult().getBoolean("done")){
                                Toast.makeText(getActivity(), "Task already exists", Toast.LENGTH_SHORT).show();
                                vTaskInput.setError("change title to add new task");
                                vProgressBar.setVisibility(View.INVISIBLE);
                                vAddTaskTextview.setVisibility(View.VISIBLE);
                            }else{
                                documentReference.set(taskHelperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){vProgressBar.setVisibility(View.INVISIBLE);
                                            vAddTaskTextview.setVisibility(View.VISIBLE);
                                            vTaskInput.setText("");
                                            vDescription.setText("");
                                            vDatePicker.setText("");
                                            Toast.makeText(getActivity(), "New Task Added", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), "Somthing went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}