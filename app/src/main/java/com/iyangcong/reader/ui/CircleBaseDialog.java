package com.iyangcong.reader.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.LabelAdapter;
import com.iyangcong.reader.bean.CircleLabel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class CircleBaseDialog extends BaseDialog<CircleBaseDialog> {
    //    @BindView(R.id.tv_art)
//    TextView tvArt;
//    @BindView(R.id.tv_small_fresh)
//    TextView tvSmallFresh;
//    @BindView(R.id.tv_study_dad)
//    TextView tvStudyDad;
//    @BindView(R.id.tv_cute)
//    TextView tvCute;
//    @BindView(R.id.tv_academic)
//    TextView tvAcademic;
//    @BindView(R.id.tv_astronomical)
//    TextView tvAstronomical;
//    @BindView(R.id.tv_philosophy)
//    TextView tvPhilosophy;
//    @BindView(R.id.tv_biography)
//    TextView tvBiography;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.dialogRecycleView)
    RecyclerView dialogRecycleView;
    private List<CircleLabel> labelList;
    private LabelAdapter adapter;
    private OnDialogDismissListener onDialogDismissListener;
    private OnDialogItemSelectedListener onDialogItemSelectedListener;
    @OnClick(R.id.tv_confirm)
    public void onClick() {
        this.dismiss();
        if(onDialogDismissListener != null)
            onDialogDismissListener.onDialogDismiss(labelList);
    }

    public CircleBaseDialog(Context context,List<CircleLabel> labelList) {
        super(context);
        this.labelList = new ArrayList<>();
        adapter = new LabelAdapter(context,labelList,false);

    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        View inflate = View.inflate(mContext, R.layout.dialog_circle_base, null);
        ButterKnife.bind(this, inflate);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        adapter.setLabelSeclectedListener(new LabelAdapter.OnLabelSeclectedListener() {
            @Override
            public void onLabelSelceted(List<CircleLabel> labels) {
                labelList.clear();
                for(CircleLabel label:labels)
                    labelList.add(label);
            }
        });

        adapter.setItemChoosedListener(new LabelAdapter.OnItemChoosedListener() {
            @Override
            public void onItemChoosed(int position) {
                if(position > -1 && onDialogItemSelectedListener != null)
                    onDialogItemSelectedListener.onDialogItemSelected(position);
            }
        });
        dialogRecycleView.setAdapter(adapter);
        StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        dialogRecycleView.setLayoutManager(sgm);
    }

    public void noftifyDataChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public List<CircleLabel> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<CircleLabel> labelList) {
        this.labelList = labelList;
    }

    public OnDialogDismissListener getOnDialogDismissListener() {
        return onDialogDismissListener;
    }

    public void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
        this.onDialogDismissListener = onDialogDismissListener;
    }

    public interface OnDialogDismissListener{
        public void onDialogDismiss(List<CircleLabel> list);
    }

    public OnDialogItemSelectedListener getOnDialogItemSelectedListener() {
        return onDialogItemSelectedListener;
    }

    public void setOnDialogItemSelectedListener(OnDialogItemSelectedListener onDialogItemSelectedListener) {
        this.onDialogItemSelectedListener = onDialogItemSelectedListener;
    }

    public interface OnDialogItemSelectedListener{
        public void onDialogItemSelected(int position);
    }
}
