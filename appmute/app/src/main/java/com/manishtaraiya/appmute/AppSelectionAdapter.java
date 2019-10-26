package com.manishtaraiya.appmute;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionAdapter extends RecyclerView.Adapter<AppSelectionAdapter.ViewHolder>{

    private List<ApplicationSelectionModel> applicationInfoModels;




    public AppSelectionAdapter(List<ApplicationSelectionModel> applicationInfoModel){
        this.applicationInfoModels = applicationInfoModel;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);




        // Inflate the custom layout
        View appSelection = inflater.inflate(R.layout.item_app_selection, parent, false);

        // Return a new holder instance
        return new ViewHolder(appSelection);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final ApplicationSelectionModel applicationInfoModel = applicationInfoModels.get(position);
        /*ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(applicationInfoModel.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/


            holder.appName.setText(applicationInfoModel.getAppName());
            holder.appSelection.setChecked(applicationInfoModel.isSelected());
            holder.appSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = ((CheckBox) v).isChecked();
                    Utils.addRemoveSelectedApp(context, applicationInfoModel.getPackageName(),applicationInfoModel.getAppName(), isChecked);
                }
            });
        /*holder.appSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addRemoveSelectedApp(context,applicationInfoModel,isChecked);
            }
        });*/
            RequestOptions options = new RequestOptions()
                    .fitCenter();
            //.placeholder(R.drawable.placeholder)
            //.error(R.drawable.placeholder);
            Glide.with(context).load(applicationInfoModel.getImage()).apply(options).into(holder.appIcon);

        /*holder.appSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.appSelection.setChecked(!holder.appSelection.isChecked());
                addRemoveSelectedApp(context,applicationInfoModel,holder.appSelection.isChecked());
            }
        });*/




    }

    @Override
    public int getItemCount() {
        return applicationInfoModels.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        LinearLayout appSelectionLayout;
        ImageView appIcon;
        TextView appName;
        CheckBox appSelection;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            appSelectionLayout = itemView.findViewById(R.id.appSelectionLayout);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appSelection = itemView.findViewById(R.id.appSelected);

        }
    }
}
