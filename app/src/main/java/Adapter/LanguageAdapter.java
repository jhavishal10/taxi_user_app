package Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.taxitime.rideo.R;

import java.util.ArrayList;

import utils.ApplyFont;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.SingleItemRowHolder> {


    private ArrayList<LanguageData> itemsList;
    private Context mContext;
    private MyClickListener myClickListener;
    private int count;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;

    public LanguageAdapter(Context context, ArrayList<LanguageData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;

    }


    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.languageitem, viewGroup, false);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;

    }


    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {

        holder.languageName.setText(itemsList.get(i).getLanguageName());

    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView languageName;


        public SingleItemRowHolder(View view) {
            super(view);

            this.languageName = view.findViewById(R.id.button_english);

            ApplyFont.applyBold(mContext, view.findViewById(R.id.button_english));
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }


    public void setLoaded() {
        isLoading = false;
    }
}
