package com.duc.chatting.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.interfaces.PDFListeners;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.databinding.ItemContainerFileListBinding;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder>{
    private List<PDFClass> pdfClasses;
    private PDFListeners pdfListeners;

    public FileListAdapter(List<PDFClass> pdfClasses, PDFListeners pdfListeners) {
        this.pdfClasses = pdfClasses;
        this.pdfListeners = pdfListeners;
    }

    @NonNull
    @Override
    public FileListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileListViewHolder(ItemContainerFileListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileListViewHolder holder, int position) {
        holder.setData(pdfClasses.get(position));
    }

    @Override
    public int getItemCount() {
        return pdfClasses.size();
    }

    public class FileListViewHolder extends RecyclerView.ViewHolder{
        ItemContainerFileListBinding binding;
        public FileListViewHolder(@NonNull ItemContainerFileListBinding itemView) {
            super(itemView.getRoot());
            this.binding=itemView;
        }
        public void setData(PDFClass pdfClass)
        {

                binding.textFileName.setText(pdfClass.getFileName());
                binding.getRoot().setOnClickListener(v -> {
                    pdfListeners.onUserClickedFileMessage(pdfClass);
                });

        }

    }
}
