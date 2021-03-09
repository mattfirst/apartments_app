package db;

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

public class RemontAdapter extends FirebaseRecyclerAdapter<RemontClass,RemontAdapter.viewHolder> {


    public RemontAdapter(@NonNull FirebaseRecyclerOptions<RemontClass>options){super(options);
    }


    protected void onBindViewHolder(@NonNull final viewHolder holder, final int position, @NonNull final RemontClass remontClass)
    {
        holder.repair_date.setText(remontClass.getDate());
        holder.repair_name.setText(remontClass.getName());
        holder.repair_money.setText(remontClass.getSum());


    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_archive_repair,parent,false);
        return new viewHolder(view);


    }


    class viewHolder extends RecyclerView.ViewHolder{

        TextView repair_date,repair_name,repair_money;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            repair_date=(TextView)itemView.findViewById(R.id.repair_date);
            repair_name=(TextView)itemView.findViewById(R.id.repair_name);
            repair_money=(TextView)itemView.findViewById(R.id.repair_money);


        }
    }
}