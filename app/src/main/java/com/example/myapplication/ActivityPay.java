package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import db.Pay.PayClass;

public class ActivityPay extends AppCompatActivity implements View.OnClickListener{

    TextView sum,date_pay,name;

    Button add_oplaty;

    private DatabaseReference myDataBase;
    private String PayClass = "Pay_History";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__pay);
        getSupportActionBar().hide(); //УБИРАЕМ ВЕРХНЮЮ ШАПКУ

        sum = (TextView) findViewById(R.id.sum);
        date_pay = (TextView) findViewById(R.id.date_pay);
        name = (TextView) findViewById(R.id.namee);

        add_oplaty = (Button) findViewById(R.id.add_oplaty);
        add_oplaty.setOnClickListener(this);

        myDataBase = FirebaseDatabase.getInstance().getReference(PayClass);
    }

    public void onClick(View v) {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("pay1")) {
            if(sum.getText().toString().length()!=0 && date_pay.getText().toString().length()!=0) {

                String id = "apartment1";
                String pay = date_pay.getText().toString();
                String summ = sum.getText().toString();
                String namee = name.getText().toString();


                PayClass newPay = new PayClass(id,summ,pay,namee);
                myDataBase.push().setValue(newPay);

                intent.putExtra("11111", 11111);
                setResult(RESULT_OK, intent);
                Toast.makeText(this,"Оплату додано!",Toast.LENGTH_LONG).show();
                finish();

            }else {
                Toast.makeText(this,"Введіть суму та дату!",Toast.LENGTH_LONG).show();
            }
        }

        if (action.equals("pay2")) {
            if(sum.getText().toString().length()!=0 && date_pay.getText().toString().length()!=0) {


                String pay = date_pay.getText().toString();
                String id = "apartment2";
                String summ = sum.getText().toString();
                String namee = name.getText().toString();


                PayClass newPay = new PayClass(id,summ,pay,namee);
                myDataBase.push().setValue(newPay);

                intent.putExtra("22222", 22222);
                setResult(RESULT_OK, intent);
                Toast.makeText(this,"Оплату додано!",Toast.LENGTH_LONG).show();
                finish();

            }else {
                Toast.makeText(this,"Введіть суму та дату!",Toast.LENGTH_LONG).show();
            }
        }

        if (action.equals("pay3")) {
            if(sum.getText().toString().length()!=0 && date_pay.getText().toString().length()!=0) {


                String pay = date_pay.getText().toString();
                String summ = sum.getText().toString();
                String namee = name.getText().toString();
                String id = "apartment3";



                PayClass newPay = new PayClass(id,summ,pay,namee);
                myDataBase.push().setValue(newPay);

                intent.putExtra("33333", 33333);
                setResult(RESULT_OK, intent);
                Toast.makeText(this,"Оплату додано!",Toast.LENGTH_LONG).show();
                finish();

            }else {
                Toast.makeText(this,"Введіть суму та дату!",Toast.LENGTH_LONG).show();
            }
        }

        if (action.equals("pay4")) {
            if(sum.getText().toString().length()!=0 && date_pay.getText().toString().length()!=0) {


                String pay = date_pay.getText().toString();
                String summ = sum.getText().toString();
                String namee = name.getText().toString();
                String id = "apartment4";


                PayClass newPay = new PayClass(id,summ,pay,namee);
                myDataBase.push().setValue(newPay);

                intent.putExtra("44444", 44444);
                setResult(RESULT_OK, intent);
                Toast.makeText(this,"Оплату додано!",Toast.LENGTH_LONG).show();
                finish();

            }else {
                Toast.makeText(this,"Введіть суму та дату!",Toast.LENGTH_LONG).show();
            }
        }
    }
}