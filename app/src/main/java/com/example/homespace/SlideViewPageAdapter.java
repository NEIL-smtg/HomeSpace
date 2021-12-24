package com.example.homespace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SlideViewPageAdapter extends PagerAdapter {

    Context context;

    public SlideViewPageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.intro_slide_screen,container,false);

        ImageView logo = view.findViewById(R.id.logo);
        ImageView slide1_indicator = view.findViewById(R.id.slide1);
        ImageView slide2_indicator = view.findViewById(R.id.slide2);

        TextView title = view.findViewById(R.id.maintext);
        TextView desc = view.findViewById(R.id.description);

        ImageView back = view.findViewById(R.id.intro_back);
        ImageView forward = view.findViewById(R.id.intro_forward);

        Button getStarted = view.findViewById(R.id.getstarted);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntroActivity.viewPager.setCurrentItem(position-1);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntroActivity.viewPager.setCurrentItem(position+1);
            }
        });

        switch (position)
        {
            case 0:
                logo.setImageResource(R.drawable.logo);
                slide1_indicator.setImageResource(R.drawable.slide_indicator_selected);
                slide2_indicator.setImageResource(R.drawable.slide_indicator_unselected);

                title.setText("HOMESPACE");
                desc.setText("Find your home");
                back.setVisibility(View.GONE);
                break;

            case 1:
                logo.setImageResource(R.drawable.logo);
                slide1_indicator.setImageResource(R.drawable.slide_indicator_unselected);
                slide2_indicator.setImageResource(R.drawable.slide_indicator_selected);

                title.setText("HOMESPACE");
                desc.setText("One step close to your dream house");
                back.setVisibility(View.VISIBLE);
                forward.setVisibility(View.GONE);
                break;

            default:
                break;
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


