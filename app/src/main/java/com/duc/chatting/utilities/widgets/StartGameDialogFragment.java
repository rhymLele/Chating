package com.duc.chatting.utilities.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.duc.chatting.R;

public class StartGameDialogFragment extends DialogFragment {
    public interface StartGameDialogListener {
        void onDiscard();
        void onConfirm();
    }
    private StartGameDialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDiscard(); // Call the method in Activity
                    }
                })
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onConfirm(); // Call the method in Activity
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
                // Set custom colors for the buttons// Set positive button color
                negativeButton.setTextColor(Color.RED);   // Set negative button color
            }
        });
        return dialog;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Ensure the activity implements the interface
        try {
            mListener = (StartGameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StartGameDialogListener");
        }
    }
}

