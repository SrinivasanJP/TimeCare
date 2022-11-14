package com.srini.timecare.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.compose.material.SnackbarData;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DocumentCollections;
import com.srini.timecare.Datastore.SharedPreferenceStore;
import com.srini.timecare.Helper.TaskHelperClass;
import com.srini.timecare.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {


    //view variables
    private TextView vUser;
    private RecyclerView vRecyclerView;
    private LinearLayoutManager vLinearLayoutManager;
    private TextView vNoOfTaskDue;
    private TextView vDueToday;
    private TextView vTaskDone;

    //firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<TaskHelperClass,taskViewHolder> taskAdapter;
    private DocumentReference documentReference;
    private CollectionReference db;

    //backend variables
    SharedPreferences sharedPreferences;
    String userName;
    TaskHelperClass swipedTask;
    FirestoreRecyclerOptions<TaskHelperClass> allTasks;
    View view;
    private int dueToday=0;
    private int totalNoTask = 0;
    private int taskDone = 0;
    SimpleDateFormat patten = new SimpleDateFormat("yyyy/MM/dd");

    //Queries
    Query query;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(taskAdapter!=null){
            taskAdapter.stopListening();
        }
    }
    void refresh(){
        dueToday=0;
        totalNoTask = 0;
        taskDone = 0;
        String currentDate = patten.format(new Date());
        db = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("allTasks");
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot ds : task.getResult()) {
                        String taskDate = ds.getString("date");
                        if (currentDate.equals(taskDate) && !ds.getBoolean("done")) {
                            dueToday++;
                        }
                        Log.d("i",String.valueOf(ds.getBoolean("done")));
                        if(ds.getBoolean("done")){
                            taskDone++;

                        }
                        if(!ds.getBoolean("done")){
                            totalNoTask++;
                        }
                    }
                    vDueToday.setText(String.valueOf(dueToday));
                    vNoOfTaskDue.setText(String.valueOf(totalNoTask));
                    vTaskDone.setText(String.valueOf(taskDone));

                } else {
                    dueToday = 0;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //hooks
        vUser = view.findViewById(R.id.username);
        vNoOfTaskDue = view.findViewById(R.id.no_of_task_due);
        vDueToday = view.findViewById(R.id.dueToday);
        vTaskDone = view.findViewById(R.id.task_done);




        sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferenceStore.SHARED_PREFERENCE_NAME,this.getActivity().MODE_PRIVATE);
        vUser.setText("Hello "+sharedPreferences.getString(SharedPreferenceStore.KEY_FIRST_NAME,"Hello"));


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        query = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("allTasks").orderBy("date",Query.Direction.ASCENDING).whereEqualTo("done",false);

        allTasks = new FirestoreRecyclerOptions.Builder<TaskHelperClass>().setQuery(query,TaskHelperClass.class).build();

        taskAdapter = new FirestoreRecyclerAdapter<TaskHelperClass, taskViewHolder>(allTasks) {
            @NonNull
            @Override
            public taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_recyclerview,parent,false);
                return new taskViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull taskViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull TaskHelperClass model) {
                holder.vTitle.setText(model.getTitle());
                holder.vDescription.setText(model.getDescription());
                String[] sDate = model.getDate().split("/");
                holder.vDate.setText(sDate[2] + "/" + sDate[1] + "/" + sDate[0]);





                String docTitle = taskAdapter.getSnapshots().get(position).getTitle();
                TaskHelperClass deletedTask = allTasks.getSnapshots().get(position);
                holder.vPopUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.END);
                        popupMenu.getMenu().add("Done").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                documentReference=FirebaseFirestore.getInstance().collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(docTitle);
                                documentReference.update("done",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity(), "Congragulations", Toast.LENGTH_SHORT).show();
                                            taskAdapter.notifyItemRemoved(position);
                                        }
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //edit
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                          @Override
                          public boolean onMenuItemClick(MenuItem item) {
                              documentReference = FirebaseFirestore.getInstance().collection("tasks").document(firebaseUser.getUid()).collection("allTasks").document(docTitle);
                                documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            taskAdapter.notifyItemRemoved(position);
                                            refresh();
                                            Toast.makeText(getActivity(),"Successfully Deleted",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), "Falid to Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                BottomNavigationView bnv = getActivity().findViewById(R.id.bottom_nav);
                                Snackbar snackbar = Snackbar.make(view,docTitle,Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                documentReference.set(deletedTask);
                                                taskAdapter.notifyDataSetChanged();
                                                refresh();
                                            }
                                        });
                                snackbar.setAnchorView(bnv);
                                snackbar.show();
                              return false;
                          }
                      });
                      popupMenu.show();
                    }
                });
            }
        };

        vRecyclerView = view.findViewById(R.id.recyclerView_containor);
        vLinearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        vRecyclerView.setLayoutManager(vLinearLayoutManager);
        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setAdapter(taskAdapter);
        refresh();
        return view;
    }


    public class taskViewHolder extends RecyclerView.ViewHolder{

        private TextView vTitle;
        private TextView vDescription;
        private TextView vDate;
        private RelativeLayout vitemHolder;
        private ImageView vPopUpButton;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            vTitle = itemView.findViewById(R.id.task_title_show);
            vDescription = itemView.findViewById(R.id.task_description_show);
            vDate = itemView.findViewById(R.id.task_time_show);
            vitemHolder = itemView.findViewById(R.id.taskHolders);
            vPopUpButton = itemView.findViewById(R.id.card_menu);


        }
    }
}