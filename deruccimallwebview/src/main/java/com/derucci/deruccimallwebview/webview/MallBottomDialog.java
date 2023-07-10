package com.derucci.deruccimallwebview.webview;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.derucci.deruccimallwebview.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/7/10
 */
public class MallBottomDialog extends BottomSheetDialogFragment {
    TextView tvCancel;

    IMallDialogItemClick iMallDialogItemClick;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.dialog_more_mall, null);
        dialog.setContentView(inflate);
        initView(inflate);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    private void initView(View inflate) {
        tvCancel = inflate.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener((view) -> {
            dismiss();
        });
        inflate.findViewById(R.id.mall_dialog_item1).setOnClickListener((view) -> {
            iMallDialogItemClick.run(1);
            dismiss();
        });

        inflate.findViewById(R.id.mall_dialog_item2).setOnClickListener((view) -> {
            iMallDialogItemClick.run(2);
            dismiss();
        });

        inflate.findViewById(R.id.mall_dialog_item3).setOnClickListener((view) -> {
            iMallDialogItemClick.run(3);
            dismiss();
        });

        inflate.findViewById(R.id.mall_dialog_item4).setOnClickListener((view) -> {
            iMallDialogItemClick.run(4);
            dismiss();
        });

        inflate.findViewById(R.id.mall_dialog_item5).setOnClickListener((view) -> {
            iMallDialogItemClick.run(5);
            dismiss();
        });
    }

    public void setMallDialogItemClick(IMallDialogItemClick iMallDialogItemClick){
        this.iMallDialogItemClick = iMallDialogItemClick;
    }

    public interface IMallDialogItemClick {
        public void run(int index);
    }
}
