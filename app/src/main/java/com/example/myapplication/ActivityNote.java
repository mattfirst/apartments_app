package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.note.CreateNote;
import com.example.myapplication.note.EditNote;
import com.example.myapplication.note.NoteModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import db.Client.ClientAdapter;
import db.Client.ClientClass;

public class ActivityNote extends AppCompatActivity {

    ImageView imageAddNoteMain;
    CardView notecard;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    ImageButton back;
    EditText inputSearch;




    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;


    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<NoteModel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        imageAddNoteMain=findViewById(R.id.imageAddNoteMain);
        inputSearch=findViewById(R.id.inputSearch);

        notecard=findViewById(R.id.notecard);
        back=findViewById(R.id.back);

        firebaseAuth=FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("SHARED_PREF",MODE_PRIVATE);

        String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getSupportActionBar().hide();
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ActivityNote.this, CreateNote.class));

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Query query=firebaseFirestore.collection(auth).document(firebaseUser.getUid()).collection("myNotes")
                .orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<NoteModel> allusernotes= new FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query,NoteModel.class).build();

        noteAdapter= new FirestoreRecyclerAdapter<NoteModel, NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull NoteModel firebasemodel) {


                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colourcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.notecontent.setText(firebasemodel.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();



                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Редагувати").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                Intent intent=new Intent(v.getContext(), EditNote.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
                                intent.putExtra("noteId",docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Видалити").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                DocumentReference documentReference=firebaseFirestore.collection(auth).document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),"Видалено!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Помилка видалення!",Toast.LENGTH_SHORT).show();
                                    }
                                });


                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note,parent,false);
                return new NoteViewHolder(view);
            }
        };


        mrecyclerview=findViewById(R.id.notesRecyclerView);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

     inputSearch.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
             Query query=firebaseFirestore.collection(auth).document(firebaseUser.getUid()).collection("myNotes")
                     .orderBy("title").startAt(s.toString()).endAt(s.toString()+"\uf8ff");

             FirestoreRecyclerOptions<NoteModel> allusernotes= new FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query,NoteModel.class).build();

             noteAdapter= new FirestoreRecyclerAdapter<NoteModel, NoteViewHolder>(allusernotes) {
                 @RequiresApi(api = Build.VERSION_CODES.M)
                 @Override
                 protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull NoteModel firebasemodel) {


                     ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                     int colourcode=getRandomColor();
                     noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode,null));

                     noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                     noteViewHolder.notecontent.setText(firebasemodel.getContent());

                     String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();



                     popupbutton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                             popupMenu.setGravity(Gravity.END);
                             popupMenu.getMenu().add("Редагувати").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                 @Override
                                 public boolean onMenuItemClick(MenuItem item) {

                                     Intent intent=new Intent(v.getContext(), EditNote.class);
                                     intent.putExtra("title",firebasemodel.getTitle());
                                     intent.putExtra("content",firebasemodel.getContent());
                                     intent.putExtra("noteId",docId);
                                     v.getContext().startActivity(intent);
                                     return false;
                                 }
                             });

                             popupMenu.getMenu().add("Видалити").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                 @Override
                                 public boolean onMenuItemClick(MenuItem item) {

                                     DocumentReference documentReference=firebaseFirestore.collection(auth).document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                     documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                             Toast.makeText(v.getContext(),"Видалено!",Toast.LENGTH_SHORT).show();
                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(v.getContext(),"Помилка видалення!",Toast.LENGTH_SHORT).show();
                                         }
                                     });


                                     return false;
                                 }
                             });

                             popupMenu.show();
                         }
                     });


                 }

                 @NonNull
                 @Override
                 public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                     View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note,parent,false);
                     return new NoteViewHolder(view);
                 }
             };


             mrecyclerview=findViewById(R.id.notesRecyclerView);
             mrecyclerview.setHasFixedSize(true);
             staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
             mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
             mrecyclerview.setAdapter(noteAdapter);
             noteAdapter.startListening();
         }

         @Override
         public void afterTextChanged(Editable s) {

             if(s.toString().equals("")) {

                 Query query = firebaseFirestore.collection(auth).document(firebaseUser.getUid())
                         .collection("myNotes").orderBy("title");

                 FirestoreRecyclerOptions<NoteModel> allusernotes = new FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query, NoteModel.class).build();

                 noteAdapter = new FirestoreRecyclerAdapter<NoteModel, NoteViewHolder>(allusernotes) {
                     @RequiresApi(api = Build.VERSION_CODES.M)
                     @Override
                     protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull NoteModel firebasemodel) {


                         ImageView popupbutton = noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                         int colourcode = getRandomColor();
                         noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode, null));

                         noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                         noteViewHolder.notecontent.setText(firebasemodel.getContent());

                         String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();


                         popupbutton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                                 popupMenu.setGravity(Gravity.END);
                                 popupMenu.getMenu().add("Редагувати").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                     @Override
                                     public boolean onMenuItemClick(MenuItem item) {

                                         Intent intent = new Intent(v.getContext(), EditNote.class);
                                         intent.putExtra("title", firebasemodel.getTitle());
                                         intent.putExtra("content", firebasemodel.getContent());
                                         intent.putExtra("noteId", docId);
                                         v.getContext().startActivity(intent);
                                         return false;
                                     }
                                 });

                                 popupMenu.getMenu().add("Видалити").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                     @Override
                                     public boolean onMenuItemClick(MenuItem item) {

                                         DocumentReference documentReference = firebaseFirestore.collection(auth).document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                         documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 Toast.makeText(v.getContext(), "Видалено!", Toast.LENGTH_SHORT).show();
                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                 Toast.makeText(v.getContext(), "Помилка видалення!", Toast.LENGTH_SHORT).show();
                                             }
                                         });


                                         return false;
                                     }
                                 });

                                 popupMenu.show();
                             }
                         });


                     }

                     @NonNull
                     @Override
                     public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false);
                         return new NoteViewHolder(view);
                     }
                 };


                 mrecyclerview = findViewById(R.id.notesRecyclerView);
                 mrecyclerview.setHasFixedSize(true);
                 staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                 mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
                 mrecyclerview.setAdapter(noteAdapter);
                 noteAdapter.startListening();
             }
         }
     });




    }



    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;
        EditText inputSearch;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
            inputSearch=itemView.findViewById(R.id.inputSearch);




        }


    }



    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }


    private int getRandomColor()
    {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);

        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }

}