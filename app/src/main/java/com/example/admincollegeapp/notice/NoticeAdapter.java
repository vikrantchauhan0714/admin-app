package com.example.admincollegeapp.notice;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {

    private Context context;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout,parent,false);
        return new NoticeViewAdapter(view);

        //firstly onviewholder complete
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, final int position) {
        final NoticeData currentItem = list.get(position);

        holder.deleNoticeTitle.setText(currentItem.getTitle());
        try {
            if (currentItem.getImage() != null)
                Picasso.get().load(currentItem.getImage()).into(holder.deleNoticeImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.deleteNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Alert dialog box code
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Abe jaldi yes kr or dele kr");
                builder.setCancelable(true);
                builder.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                                reference.child(currentItem.getKey()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(context, "Something went wrong.....", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                notifyItemRemoved(position);

                            }

                        });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }

                        });
                AlertDialog dialog=null;
                try {
                    dialog=builder.create();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(dialog!=null)
                dialog.show();
            }
        });
    }


                @Override
                public int getItemCount () {

                    return list.size();
                }

                public class NoticeViewAdapter extends RecyclerView.ViewHolder {

                    private TextView deleNoticeTitle;
                    private TextView deleTextview;
                    private MaterialButton deleteNoticeBtn;
                    private ImageView deleNoticeImg;

                    public NoticeViewAdapter(@NonNull View itemView) {
                        super(itemView);

                        deleTextview = itemView.findViewById(R.id.deleTextview);
                        deleNoticeTitle = itemView.findViewById(R.id.deleNoticeTitle);

                        deleteNoticeBtn = itemView.findViewById(R.id.deleteNoticeBtn);
                        deleNoticeImg = itemView.findViewById(R.id.deleNoticeImg);
                    }
                }
            }




