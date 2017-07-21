package com.example.lenovo.camera;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterPath extends Dialog{

    Button bCancel,bConfirm;
    EditText etPath;
    public EnterPath(@NonNull Context context, final String oldPath) {
        super(context);
        setContentView(R.layout.layout_enterpath);

        bCancel=(Button)findViewById(R.id.bCancel);
        bConfirm=(Button)findViewById(R.id.bConfirm);
        etPath=(EditText)findViewById(R.id.etPath);

        etPath.setText(oldPath);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mListener.returnPath(oldPath);
            }
        });
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                String path=etPath.getText().toString().trim();

                mListener.returnPath(path);
            }
        });

    }

    interface OnDialogClickListener{
        void returnPath(String path);
    }

    private OnDialogClickListener mListener;
    public void setOnDialogClickListener(OnDialogClickListener listener){
        mListener=listener;
    }

}
