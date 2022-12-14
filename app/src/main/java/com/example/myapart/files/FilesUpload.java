package com.example.myapart.files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

public class FilesUpload extends AppCompatActivity implements View.OnClickListener{

    ImageView imagebrowse,filelogo,cancelfile;
    EditText filetitle;
    Uri filepath;
    Button imageupload;
    ImageButton back;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_upload);

        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("SHARED_PREF",MODE_PRIVATE);

        String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

        storageReference= FirebaseStorage.getInstance().getReference(auth);
        databaseReference= FirebaseDatabase.getInstance().getReference(auth).child("My_Documents");

        filetitle=findViewById(R.id.filetitle);
        back=findViewById(R.id.back);

        imagebrowse=findViewById(R.id.imagebrowse);

        imageupload=findViewById(R.id.imageupload);
        imageupload.setOnClickListener(this);

        filelogo=findViewById(R.id.filelogo);
        cancelfile=findViewById(R.id.cancelfile);


        filelogo.setVisibility(View.INVISIBLE);
        cancelfile.setVisibility(View.INVISIBLE);

        cancelfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filelogo.setVisibility(View.INVISIBLE);
                cancelfile.setVisibility(View.INVISIBLE);
                imagebrowse.setVisibility(View.VISIBLE);

                filetitle.setVisibility(View.INVISIBLE);
                imageupload.setVisibility(View.INVISIBLE);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imagebrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"?????????????? PDF ????????"),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }

                        }).check();
            }
        });

        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processupload(filepath);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101 && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            filelogo.setVisibility(View.VISIBLE);
            cancelfile.setVisibility(View.VISIBLE);
            imagebrowse.setVisibility(View.INVISIBLE);
            imageupload.setVisibility(View.VISIBLE);

            filetitle.setVisibility(View.VISIBLE);
        }

    }

    public void processupload(Uri filepath)
    {

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("filesUP1")) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("???????? ????????????????????????????!");
            pd.show();

            final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String id = "apartment1";

                                    String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

                                    FileInfoModel obj = new FileInfoModel(id, filetitle.getText().toString(), uri.toString(),auth);
                                    databaseReference.child("apartment1").child(databaseReference.push().getKey()).setValue(obj);

                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "???????? ????????????????????????!", Toast.LENGTH_LONG).show();




                                    finish();
                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("???????????????????????? :" + " " + (int) percent + "%");
                        }
                    });
        }



        if (action.equals("filesUP2")) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("???????? ????????????????????????????!");
            pd.show();

            final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

                                    String id = "apartment2";

                                    FileInfoModel obj = new FileInfoModel(id, filetitle.getText().toString(), uri.toString(),auth);
                                    databaseReference.child("apartment2").child(databaseReference.push().getKey()).setValue(obj);

                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "???????? ????????????????????????!", Toast.LENGTH_LONG).show();


                                    finish();
                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("???????????????????????? :" + " " + (int) percent + "%");
                        }
                    });
        }


        if (action.equals("filesUP3")) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("???????? ????????????????????????????!");
            pd.show();

            final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String id = "apartment3";

                                    String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

                                    FileInfoModel obj = new FileInfoModel(id, filetitle.getText().toString(), uri.toString(),auth);
                                    databaseReference.child("apartment3").child(databaseReference.push().getKey()).setValue(obj);

                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "???????? ????????????????????????!", Toast.LENGTH_LONG).show();

                                    finish();
                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("???????????????????????? :" + " " + (int) percent + "%");
                        }
                    });
        }



        if (action.equals("filesUP4")) {

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("???????? ????????????????????????????!");
            pd.show();

            final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
            reference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String id = "apartment4";

                                    String auth = sharedPreferences.getString("auth","").replaceAll("[^A-Za-z0-9]","");

                                    FileInfoModel obj = new FileInfoModel(id, filetitle.getText().toString(), uri.toString(),auth);
                                    databaseReference.child("apartment4").child(databaseReference.push().getKey()).setValue(obj);

                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "???????? ????????????????????????!", Toast.LENGTH_LONG).show();

                                    finish();
                                }
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("???????????????????????? :" + " " + (int) percent + "%");
                        }
                    });
        }

    }

    @Override
    public void onClick(View v) {

    }

    //?????? ?????????????? ???? ?????????? ???????????????? ???????????????????? --->

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

    // <---
}