package com.iyangcong.reader.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookListCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomBaseDialog extends BaseDialog<CustomBaseDialog> {


    @BindView(R.id.iv_subject_ring)
    ImageView ivSubjectRing;
    @BindView(R.id.gv_subject)
    TagGroup gvSubject;
    @BindView(R.id.ll_subject)
    LinearLayout llSubject;
    @BindView(R.id.iv_version_ring)
    ImageView ivVersionRing;
    @BindView(R.id.gv_version)
    TagGroup gvVersion;
    @BindView(R.id.iv_grade_ring)
    ImageView ivGradeRing;
    @BindView(R.id.gv_grade)
    TagGroup gvGrade;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private int sign;
    private List<BookListCategory> subjectList;
    private List<BookListCategory> versionList;
    private List<BookListCategory> gradetList;
    private StringBuffer selectedButtons;
    private SortCallback sortCallback;

    private List<Integer> positionSubList;
    private List<Integer> positionVerList;
    private List<Integer> positionGraList;
    private String selectSub;
    private String selectVer;
    private String selectGra;
    int mLeftBtnTextColor = Color.parseColor("#8a000000");
    protected float mLeftBtnTextSize = 15f;
    /**
     * background color(背景颜色)
     */
    protected int mBgColor = Color.parseColor("#ffffff");
    /**
     * btn press color(按钮点击颜色)
     */
    protected int mBtnPressColor = Color.parseColor("#E3E3E3");// #85D3EF,#ffcccccc,#E3E3E3
    /**
     * corner radius,dp(圆角程度,单位dp)
     */
    protected float mCornerRadius = 3;

    int i;

    public CustomBaseDialog(Context context, int sign, List<BookListCategory> subjectList, List<BookListCategory> versionList, List<BookListCategory> gradetList, StringBuffer selectedButtons) {
        super(context);
        this.sign = sign;
        this.subjectList = subjectList;
        this.versionList = versionList;
        this.gradetList = gradetList;
        this.selectedButtons = selectedButtons;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
//        showAnim(new Swing());
        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(mContext, R.layout.dialog_custom_base, null);
        ButterKnife.bind(this, inflate);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        selectSub = "";
        selectVer = "";
        selectGra = "";
        if (sign == 1) {
            llSubject.setVisibility(View.VISIBLE);
        }
        showTags(gvSubject, subjectList);
        showTags(gvVersion, versionList);
        showTags(gvGrade, gradetList);

        gvSubject.setOnTagCheckListener(new TagGroup.OnTagCheckListener() {
            @Override
            public void onTagCheck(TagGroup.TagView tag) {
                tag.setOnCheck(gvSubject, tag);
                positionSubList = gvSubject.getCheckedTagPosition();
            }
        });
        gvVersion.setOnTagCheckListener(new TagGroup.OnTagCheckListener() {
            @Override
            public void onTagCheck(TagGroup.TagView tag) {
                tag.setOnCheck(gvVersion, tag);
                positionVerList = gvVersion.getCheckedTagPosition();
            }
        });
        gvGrade.setOnTagCheckListener(new TagGroup.OnTagCheckListener() {
            @Override
            public void onTagCheck(TagGroup.TagView tag) {
                tag.setOnCheck(gvGrade, tag);
                positionGraList = gvGrade.getCheckedTagPosition();
            }
        });
        float radius = dp2px(mCornerRadius);
        tvConfirm.setGravity(Gravity.CENTER);
        tvConfirm.setTextColor(mLeftBtnTextColor);
        tvConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, mLeftBtnTextSize);
        tvConfirm.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        tvConfirm.setBackgroundDrawable(com.flyco.dialog.utils.CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 0));

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer selectedIndexs = new StringBuffer();
                if (positionSubList == null) {
                    selectedIndexs.append("-1,");
                    selectSub = "";
                } else {
                    for (i = 0; i < positionSubList.size(); i++) {
                        selectSub = subjectList.get(positionSubList.get(i)).getType();
                        selectedIndexs.append(positionSubList.get(i) + ",");
                    }
                }
                if (positionVerList == null) {
                    selectVer = "";
                    selectedIndexs.append("-1,");
                } else {
                    for (i = 0; i < positionVerList.size(); i++) {
                        selectedIndexs.append(positionVerList.get(i) + ",");
                        selectVer = versionList.get(positionVerList.get(i)).getTypeId();
                    }
                }
                if (positionGraList == null) {
                    selectGra = "";
                    selectedIndexs.append("-1");
                } else {
                    for (i = 0; i < positionGraList.size(); i++) {
                        selectedIndexs.append(positionGraList.get(i));
                        selectGra = gradetList.get(positionGraList.get(i)).getTypeId();
                    }
                }
                sortCallback.getsort(selectSub, selectVer, selectGra, sign);
                sortCallback.getSortIndex(selectedIndexs.toString());
                dismiss();
            }
        });

        String[] selectedIndexs = selectedButtons.toString().split(",");
        if (selectedIndexs.length > 1) {
            int selectedSub = Integer.parseInt(selectedIndexs[0]);
            if (selectedSub != -1) {
                gvSubject.getTagAt(selectedSub).setOnCheck(gvSubject, gvSubject.getTagAt(selectedSub));
                positionSubList = gvSubject.getCheckedTagPosition();
            }
            int selectedVer = Integer.parseInt(selectedIndexs[1]);
            if (selectedVer != -1) {
                gvVersion.getTagAt(selectedVer).setOnCheck(gvVersion, gvVersion.getTagAt(selectedVer));
                positionVerList = gvVersion.getCheckedTagPosition();
            }
            if(selectedIndexs.length > 2) {
                int selectedGra = Integer.parseInt(selectedIndexs[2]);
                if (selectedGra != -1) {
                    gvGrade.getTagAt(selectedGra).setOnCheck(gvGrade, gvGrade.getTagAt(selectedGra));
                    positionGraList = gvGrade.getCheckedTagPosition();
                }
            }
        }
    }


    public interface SortCallback {
        void getsort(String selectSub, String selectVer, String selectGra, int sign);

        void getSortIndex(String selectedIndexs);
    }

    public void setsortCallback(SortCallback sortCallback) {
        this.sortCallback = sortCallback;
    }

    private synchronized void showTags(TagGroup tagGroup, List<BookListCategory> list) {
        int tagSize = list.size();
        String[] tags = new String[tagSize];
        for (int j = 0; j < list.size(); j++) {
            tags[j] = list.get(j).getType();
        }
        tagGroup.setTags(tags);
    }
}
