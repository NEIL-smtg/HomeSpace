package com.example.homespace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartAdapterVH> {

    Context context;
    private cartAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{ void onItemClick(int position);}

    public void setOnItemClickListener(cartAdapter.OnItemClickListener listener) { mListener = listener; }

    public cartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public cartAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);

        return new cartAdapterVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapterVH holder, int position) {
        String itemType = CartItems.agent.get(position).getItemType();
        String id = CartItems.agent.get(position).getId();
        String pushID = CartItems.agent.get(position).getPushID();
        ArrayList<String> sliderItems = new ArrayList<>();

        //set property title
        holder.title.setText(CartItems.agent.get(position).getTitle());

        //firebase reference path
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID + "/PropertyImage");

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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //firebase reference
        reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID);

        //get property data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("builtup").exists()) {
                    holder.builtup.setText(snapshot.child("builtup").getValue().toString() + " ft");
                }

                if (snapshot.child("landArea").exists()) {
                    holder.landarea.setText(snapshot.child("landArea").getValue().toString() + " ft");
                }

                if (snapshot.child("furnishing").exists()) {
                    holder.furnishing.setText(snapshot.child("furnishing").getValue().toString());
                }

                if (snapshot.child("rooms").exists()) {
                    holder.roomNum.setText(snapshot.child("rooms").getValue().toString());
                }

                if (snapshot.child("toilets").exists()) {
                    holder.toiletNum.setText(snapshot.child("toilets").getValue().toString());
                }

                if (snapshot.child("price").exists()) {
                    holder.price.setText("RM " + snapshot.child("price").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }

    @Override
    public int getItemCount() {
        return CartItems.agent.size();
    }


    class cartAdapterVH extends RecyclerView.ViewHolder {

        TextView title, price, builtup, landarea, roomNum, toiletNum, furnishing, select;
        ViewPager viewPager;
        Button radiobtn_unchecked;
        Button radiobtn_checked;
        CardView cardView;
        ImageView cancel, delete, compare, back;

        private cartAdapter adapter;


        public cartAdapterVH(@NonNull View itemView) {
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
            delete = (ImageView) itemView.findViewById(R.id.cartdelete);
            compare = (ImageView) itemView.findViewById(R.id.cart_compare);
            back = (ImageView) itemView.findViewById(R.id.cart_back);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartItems.agent.remove(getAdapterPosition());
                    adapter.notifyItemRemoved(getAdapterPosition());
                    CartItems.itemAdded.setValue(false);
                }
            });

            //select
            radiobtn_unchecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radiobtn_unchecked.setVisibility(View.INVISIBLE);
                    radiobtn_checked.setVisibility(View.VISIBLE);
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.grey));

                    CartItems.compareList.add(CartItems.agent.get(getAdapterPosition()));
                    CartItems.comparelistchecker.setValue(true);

                    if (CartItems.compareList.size() ==1)
                    {
                        Toast.makeText(context, "Please select at least 2 properties to compare", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            //deselect
            radiobtn_checked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radiobtn_unchecked.setVisibility(View.VISIBLE);
                    radiobtn_checked.setVisibility(View.INVISIBLE);
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

                    CartItems.compareList.remove(CartItems.agent.get(getAdapterPosition()));
                    if (CartItems.compareList.size() ==0)
                    {
                        CartItems.comparelistchecker.setValue(false);
                    }

                }
            });

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

        public cartAdapterVH linkAdapter(cartAdapter adapter)
        {
            this.adapter = adapter;
            return this;
        }
    }
}
