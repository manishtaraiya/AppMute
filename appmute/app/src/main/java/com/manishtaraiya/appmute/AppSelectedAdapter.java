package com.manishtaraiya.appmute;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES;

public class AppSelectedAdapter extends RecyclerView.Adapter<AppSelectedAdapter.ViewHolder>{

    private List<ApplicationInfoModel> applicationInfoModels;

    //Context context;
    PackageManager pm;
    private MySharePreference sharePreference = new MySharePreference();

    public AppSelectedAdapter(List<ApplicationInfoModel> applicationInfoModel, PackageManager pm){
        this.applicationInfoModels = applicationInfoModel;
        this.pm = pm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);




        // Inflate the custom layout
        View appSelection = inflater.inflate(R.layout.item_app_selected, parent, false);

        // Return a new holder instance
        return new ViewHolder(appSelection);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final ApplicationInfoModel applicationInfoModel = applicationInfoModels.get(position);

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(applicationInfoModel.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
       if (applicationInfo != null) {
           holder.appName.setText(pm.getApplicationLabel(applicationInfo));

           RequestOptions options = new RequestOptions()
                   .fitCenter();
          // .placeholder(R.drawable.placeholder)
           //.error(R.drawable.placeholder);
           Drawable drawable = pm.getApplicationIcon(applicationInfo);

           Glide.with(context).load(drawable).apply(options).into(holder.appIcon);

           holder.appSelectedLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent launchIntent = pm.getLaunchIntentForPackage(applicationInfoModel.getPackageName());
                   context.startActivity(launchIntent);
               }
           });
       }

    }

    @Override
    public int getItemCount() {
        return applicationInfoModels.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        LinearLayout appSelectedLayout;
        ImageView appIcon;
        TextView appName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            appSelectedLayout = itemView.findViewById(R.id.appSelectedLayout);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);

        }
    }
}
