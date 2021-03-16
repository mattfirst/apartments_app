package db.Pay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class PayAdapter extends FirebaseRecyclerAdapter<PayClass,PayAdapter.viewHolder> {


    public PayAdapter(@NonNull FirebaseRecyclerOptions<PayClass>options){super(options);
    }


    protected void onBindViewHolder(@NonNull final viewHolder holder, final int position, @NonNull final PayClass payClass)
    {
        holder.money_date.setText(payClass.getDate());
        holder.money_money.setText(payClass.getPay()+" Грн.");
        holder.money_name.setText(payClass.getName());



        holder.delete_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Оберіть дію");



                builder.setPositiveButton("Видалити", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Pay_History")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNeutralButton("Назад", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        holder.edit_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus= DialogPlus.newDialog(holder.itemView.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_edit_pay))
                        .setExpanded(true,700)
                        .create();



                View myview=dialogPlus.getHolderView();





                final EditText name=myview.findViewById(R.id.name_edit_pay);
                final EditText date=myview.findViewById(R.id.date_edit_pay);
                final EditText pay=myview.findViewById(R.id.sum_edit_pay);


                Button update_pay=myview.findViewById(R.id.update_pay);


                name.setText(payClass.getName());
                pay.setText(payClass.getPay());
                date.setText(payClass.getDate());



                dialogPlus.show();

                update_pay.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map=new HashMap<>();

                        map.put("name",name.getText().toString());
                        map.put("pay",pay.getText().toString());
                        map.put("date",date.getText().toString());



                        FirebaseDatabase.getInstance().getReference().child("Pay_History")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });


                    }


                });




            }
        });




    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_archive_money,parent,false);
        return new viewHolder(view);


    }


    class viewHolder extends RecyclerView.ViewHolder{

        TextView money_money,money_date,money_name;
        ImageView edit_pay,delete_pay;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            money_money=(TextView)itemView.findViewById(R.id.money_money);
            money_date=(TextView)itemView.findViewById(R.id.money_date);
            money_name=(TextView)itemView.findViewById(R.id.money_name);

            edit_pay=(ImageButton)itemView.findViewById(R.id.edit_clients);
            delete_pay=(ImageView) itemView.findViewById(R.id.delete_pay);


        }
    }
}