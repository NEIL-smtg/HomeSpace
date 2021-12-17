package com.example.homespace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class agentProfileRecyclerView extends FirebaseRecyclerAdapter<AuctionHelperClass, agentProfileRecyclerView.myViewHolder> {

    private OnItemClickListener mListener;
    private String id;
    private Context context;
    private boolean isMyself;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param isMyself
     */
    public agentProfileRecyclerView(@NonNull FirebaseRecyclerOptions<AuctionHelperClass> options, Context context, String id, boolean isMyself) {
        super(options);

        this.context=context;
        this.id=id;
        this.isMyself = isMyself;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull AuctionHelperClass model)
    {
        holder.title.setText(model.getTitle());
        holder.builtup.setText(model.getBuiltup() + " ft");
        holder.furnishing.setText(model.getFurnishing());
        holder.price.setText("RM "+ model.getPrice());
        holder.landarea.setText(model.getLandArea() + " ft");
        holder.roomNum.setText(""+model.getRooms());
        holder.toiletNum.setText(""+model.getToilets());


        String itemType = model.getItemType();
        ArrayList<String> sliderItems = new ArrayList<>();

        //firebase reference path
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + model.pushID + "/PropertyImage");

        //get images
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    sliderItems.add(ds.getValue().toString());
                }
                ImageSliderAdapter adapter = new ImageSliderAdapter(context, sliderItems);
                holder.viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if (isMyself)
        {
            holder.editPropertybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //start intent to edit property
                    Intent intent = new Intent(context,editProperty.class);
                    intent.putExtra("model",model);
                    intent.putExtra("id",id);
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title, price, builtup, landarea, roomNum, toiletNum, furnishing;
        ViewPager viewPager;
        Button radiobtn_unchecked;
        Button radiobtn_checked;
        CardView cardView;
        ImageView editPropertybtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.cart_propertytitle);
            price = (TextView) itemView.findViewById(R.id.pricesign_txt);
            builtup = (TextView) itemView.findViewById(R.id.builtupsign_txt);
            landarea = (TextView) itemView.findViewById(R.id.landareasign_txt);
            roomNum = (TextView) itemView.findViewById(R.id.roomsign_txt);
            toiletNum = (TextView) itemView.findViewById(R.id.toiletsign_txt);
            furnishing = (TextView) itemView.findViewById(R.id.furnishingsign_txt);
            viewPager = (ViewPager) itemView.findViewById(R.id.cart_item_viewPager);
            radiobtn_unchecked = (Button) itemView.findViewById(R.id.radio_btn_unchecked);
            radiobtn_checked = (Button) itemView.findViewById(R.id.radio_btn_checked);
            cardView = (CardView) itemView.findViewById(R.id.cart_cardview);
            editPropertybtn = (ImageView) itemView.findViewById(R.id.property_edit);


            if (isMyself)
            {
                editPropertybtn.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION)
                        {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
