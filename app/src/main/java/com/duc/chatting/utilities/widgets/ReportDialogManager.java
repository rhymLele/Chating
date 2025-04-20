package com.duc.chatting.utilities.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.duc.chatting.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportDialogManager {
    private final Context context;
    private final FragmentActivity activity;
    private final Map<String, List<String>> reportOptions;

    public interface OnReportSubmittedListener {
        void onReportSubmitted(String conversationId, String reason);
    }

    public ReportDialogManager(@NonNull FragmentActivity activity, @NonNull Map<String, List<String>> reportOptions) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.reportOptions = reportOptions;
    }

    public void show(String conversationId, OnReportSubmittedListener listener) {
        showMainReportSheet(conversationId, listener);
    }

    private void showMainReportSheet(String conversationId, OnReportSubmittedListener listener) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_list, null);
        TextView title = view.findViewById(R.id.title);
        ListView listView = view.findViewById(R.id.list_view);

        title.setText("Why are you reporting this conversation?");
        List<String> mainReasons = new ArrayList<>(reportOptions.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mainReasons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedReason = mainReasons.get(position);
            List<String> subReasons = reportOptions.get(selectedReason);
            sheetDialog.dismiss();

            if (subReasons != null && !subReasons.isEmpty()) {
                showSubReasonSheet(conversationId, selectedReason, subReasons, listener);
            } else {
                showSubmitSheet(conversationId, selectedReason, null, listener);
            }
        });

        sheetDialog.setContentView(view);
        sheetDialog.show();
    }

    private void showSubReasonSheet(String conversationId, String mainReason, List<String> subReasons, OnReportSubmittedListener listener) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_list, null);
        TextView title = view.findViewById(R.id.title);
        ListView listView = view.findViewById(R.id.list_view);

        title.setText("More details about: " + mainReason);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, subReasons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedSubReason = subReasons.get(position);
            sheetDialog.dismiss();
            showSubmitSheet(conversationId, mainReason, selectedSubReason, listener);
        });

        sheetDialog.setContentView(view);
        sheetDialog.show();
    }

    private void showSubmitSheet(String conversationId, String mainReason, String subReason, OnReportSubmittedListener listener) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_confirm, null);
        TextView title = view.findViewById(R.id.title);
        TextView reasonText = view.findViewById(R.id.reason_text);
        Button submitButton = view.findViewById(R.id.submit_button);

        String fullReason = mainReason + (subReason != null ? " - " + subReason : "");
        title.setText("You're about to submit a report");
        reasonText.setText(fullReason);

        submitButton.setOnClickListener(v -> {
            sheetDialog.dismiss();
            listener.onReportSubmitted(conversationId, fullReason);
        });

        sheetDialog.setContentView(view);
        sheetDialog.show();
    }
}
