package com.duc.chatting.otherComponents.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duc.chatting.R;

// File: HelpDetailFragment.java
public class HelpDetailFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    public static HelpDetailFragment newInstance(String title, String content) {
        HelpDetailFragment fragment = new HelpDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_detail, container, false);

        TextView titleText = view.findViewById(R.id.helpTitle);
        TextView contentText = view.findViewById(R.id.helpContent);

        if (getArguments() != null) {
            titleText.setText(getArguments().getString(ARG_TITLE));
            contentText.setText(getArguments().getString(ARG_CONTENT));
        }

        return view;
    }
}
