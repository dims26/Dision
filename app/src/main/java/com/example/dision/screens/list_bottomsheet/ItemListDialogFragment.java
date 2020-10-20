package com.example.dision.screens.list_bottomsheet;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dision.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     itemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ItemListDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_LIST = "item_list";
    private static final String ARG_ITEM_CALLBACK = "item_callback";

    public static ItemListDialogFragment newInstance(ArrayList<String> items, ItemListCallback callback) {
        final ItemListDialogFragment fragment = new ItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_LIST, items);
        args.putSerializable(ARG_ITEM_CALLBACK, callback);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //noinspection ConstantConditions,unchecked,unchecked
        recyclerView.setAdapter(
                new itemAdapter((List<String>) getArguments().getSerializable(ARG_ITEM_LIST),
                (ItemListCallback) getArguments().getSerializable(ARG_ITEM_CALLBACK)));
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        final Dialog dialog = getDialog();
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(com.google.android.material.R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
        }
    }

    private class itemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<String> mItemList;
        final ItemListCallback mCallback;

        itemAdapter(List<String> itemList, ItemListCallback callback) {
            Log.e("ITEMLISTDIALOGFRAGMENT", itemList.toString());
            mItemList = itemList;
            mCallback = callback;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.text.setText(mItemList.get(position));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemSelected(mItemList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

    }

}