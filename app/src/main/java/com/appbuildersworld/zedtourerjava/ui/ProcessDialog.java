package com.appbuildersworld.zedtourerjava.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appbuildersworld.zedtourerjava.R;
import com.google.android.material.button.MaterialButton;


public class ProcessDialog {

    private AlertDialog dialog;
    AlertDialog.Builder mBuilder;
    View mView;
    LayoutInflater li;
    Context context;
    private ProgressBar progressBar;
    private TextView dText;
    private ActionClickListener actionClickListener;
    private NetResponseClickListener netResponseClickListener;
    private CloseDialogClickListener closeDialogClickListener;
    private BackPressedListener backPressedListener;
    private ImageButton ibClose;
    private ImageButton ibResponse;
    private MaterialButton mbAction;


    public ProcessDialog(Context context) {
        this.context = context;
        mBuilder = new AlertDialog.Builder(context);

        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = li.inflate(R.layout.dialog_processing_data, null);

        ibClose = mView.findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialogClickListener.onCloseDialogClick();
            }
        });
        ibResponse = mView.findViewById(R.id.ibResponse);
        ibResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netResponseClickListener.onNetResponseClick();
            }
        });

        progressBar = mView.findViewById(R.id.progressbar);
        dText = mView.findViewById(R.id.textView_status_initialization);

        mbAction = mView.findViewById(R.id.mbAction);
        mbAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionClickListener.onActionClick();
            }
        });
        mBuilder.setView(mView);

        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                backPressedListener.onBackPressedClick();
            }
        });
    }

    public void showProcessingDialog(String message) {
        dText.setText(message);
        mbAction.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        dialog.show();
    }

    public void showResponseDialog(String message, String action) {
        dText.setText(message);
        progressBar.setVisibility(View.INVISIBLE);
        mbAction.setVisibility(View.VISIBLE);
        mbAction.setText(action);
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
    }

    public void setTouchCancel(boolean touchCancel) {
        dialog.setCanceledOnTouchOutside(touchCancel);
    }


    public void disableResponseViewBtn() {
        ibResponse.setVisibility(View.GONE);
    }

    public void enableResponseViewBtn() {
        ibResponse.setVisibility(View.VISIBLE);
    }

    public interface ActionClickListener {
        void onActionClick();
    }

    public void setOnActionListener(ActionClickListener listener) {
        this.actionClickListener = listener;
    }

    public interface NetResponseClickListener {
        void onNetResponseClick();
    }

    public void setOnNetResponseListener(NetResponseClickListener listener) {
        this.netResponseClickListener = listener;
    }

    public interface CloseDialogClickListener {
        void onCloseDialogClick();
    }

    public void setOnCloseDialogListener(CloseDialogClickListener listener) {
        this.closeDialogClickListener = listener;
    }

    public interface BackPressedListener {
        void onBackPressedClick();
    }

    public void setOnBackPressedListener(BackPressedListener listener) {
        this.backPressedListener = listener;
    }


}
