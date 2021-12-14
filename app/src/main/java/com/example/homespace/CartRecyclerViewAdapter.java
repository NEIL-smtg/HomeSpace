package com.example.homespace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
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

public class CartRecyclerViewAdapter extends FirebaseRecyclerAdapter<AgentInfoAdapter, CartRecyclerViewAdapter.myViewHolder> {

    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnButtonClickListener{
        void onButtonClick(int position);
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
     */
    public CartRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<AgentInfoAdapter> options , Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull AgentInfoAdapter model)
    {
        String itemType = model.getItemType();
        String id = model.getId();
        String pushID = model.getPushID();
        ArrayList<String> sliderItems = new ArrayList<>();

        //set property title
        holder.title.setText(model.getTitle());

        //firebase reference path
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID + "/PropertyImage");

        //get images
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    sliderItems.add(ds.getValue().toString());
                }
                ImageSliderAdapter adapter = new ImageSliderAdapter(context,sliderItems);
                holder.viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //firebase reference
        reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID);

        //get property data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if (snapshot.child("builtup").exists())
                {
                    holder.builtup.setText(snapshot.child("builtup").getValue().toString() + " ft");
                }

                if (snapshot.child("landArea").exists())
                {
                    holder.landarea.setText(snapshot.child("landArea").getValue().toString() + " ft");
                }

                if (snapshot.child("furnishing").exists())
                {
                    holder.furnishing.setText(snapshot.child("furnishing").getValue().toString());
                }

                if (snapshot.child("rooms").exists())
                {
                    holder.roomNum.setText(snapshot.child("rooms").getValue().toString());
                }

                if (snapshot.child("toilets").exists())
                {
                    holder.toiletNum.setText(snapshot.child("toilets").getValue().toString());
                }

                if (snapshot.child("price").exists())
                {
                    holder.price.setText("RM " + snapshot.child("price").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        holder.radiobtn_unchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.radiobtn_unchecked.setVisibility(View.INVISIBLE);
                holder.radiobtn_checked.setVisibility(View.VISIBLE);
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.grey));

                CartItems.compareList.add(model);
                CartItems.comparelistchecker.setValue(true);
            }
        });

        holder.radiobtn_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.radiobtn_unchecked.setVisibility(View.VISIBLE);
                holder.radiobtn_checked.setVisibility(View.INVISIBLE);
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

                CartItems.compareList.remove(model);
                if (CartItems.compareList.size() == 0)
                {
                    CartItems.comparelistchecker.setValue(false);
                }
            }
        });

        Cart.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItems.compareList.remove(model);
                CartItems.agent.remove(model);

                if (CartItems.compareList.size() == 0 || CartItems.agent.size() ==0)
                {
                    CartItems.comparelistchecker.setValue(false);
                }

                if (CartItems.agent.size()==0)
                {
                    CartItems.agent.clear();
                }
                CartItems.updateRecyclerView.setValue(true);
                Toast.makeText(context, ""+CartItems.agent.size(), Toast.LENGTH_SHORT).show();
            }
        });


        Cart.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItems.compareList.clear();
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.radiobtn_checked.setVisibility(View.INVISIBLE);
                holder.radiobtn_unchecked.setVisibility(View.VISIBLE);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,builtup,landarea,roomNum,toiletNum,furnishing,select;
        ViewPager viewPager;
        Button radiobtn_unchecked;
        Button radiobtn_checked;
        CardView cardView;
        ImageView cancel,delete,compare,back;

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

            cancel = (ImageView) itemView.findViewById(R.id.cart_cancel_compare);
            delete = (ImageView) itemView.findViewById(R.id.cart_delete);
            compare = (ImageView) itemView.findViewById(R.id.cart_compare);
            back = (ImageView) itemView.findViewById(R.id.cart_back);


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
