package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineShoppingAdapter;
import com.iyangcong.reader.bean.MineShoppingBookIntroduction;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.IntentUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonShopcartURL;

public class MineShoppingActivity extends SwipeBackActivity {
    private double allPrice;
    private int selectedCount;
    BigDecimal bigDecimal;

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_shopping_listview)
    ListView mineShoppingListview;
    @BindView(R.id.mine_shopping_cb)
    CheckBox mineShoppingCb;
    @BindView(R.id.mine_shopping_btn)
    FlatButton mineShoppingBtn;
    @BindView(R.id.mine_shopping_pay_tv)
    TextView mineShoppingPayTv;
    @BindView(R.id.mine_shopping_allchoose_tv)
    TextView mineShoppingAllchooseTv;
    @BindView(R.id.ll_all_select)
    LinearLayout llAllSelect;
    @BindView(R.id.layout_no_content)
    LinearLayout llNoContent;



    @OnClick({R.id.btnBack, R.id.mine_shopping_btn, R.id.ll_all_select,R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.mine_shopping_btn:
                if (getIsCanPay()) {
                    String bookIdStr = getBookIdList();
                    IntentUtils.goToPayActivity(this,bookIdStr,getBookPriceStrs(),allPrice,selectedCount);
//                    bigDecimal = new BigDecimal(allPrice);
//                    Intent intent = new Intent(MineShoppingActivity.this, MinePayActivity.class);
//                    intent.putExtra("bookIds", bookIdStr);
//                    intent.putExtra("totalPrice", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                    intent.putExtra("count", selectedCount);
//                    startActivity(intent);
                    finish();
                } else {
                    ToastCompat.makeText(MineShoppingActivity.this, "请选择要结算的书籍", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_all_select:
                allchoose();
                break;
            case R.id.btnFunction:
                //批量清空购物车
                if (getIsCanPay()) {
                    final NormalDialog normalDialog = new NormalDialog(MineShoppingActivity.this);
                    DialogUtils.setAlertDialogNormalStyle(normalDialog, getResources().getString(R.string.tips), "是否从购物车移除所选图书 ");
                    normalDialog.setOnBtnClickL(new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            normalDialog.dismiss();
                            showLoadingDialog();
                            String bookIdStr = getBookIdList();
                            batchRemoveBookFromShoppingCart(bookIdStr);

                        }
                    }, new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            normalDialog.dismiss();
                            //ToastCompat.makeText(MineShoppingActivity.this, "取消", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    ToastCompat.makeText(MineShoppingActivity.this, "您没有选择任何书籍", Toast.LENGTH_SHORT).show();
                }

                break;
                default:
                    break;
        }
    }

    private void allchoose() {
        mineShoppingCb.setChecked(!mineShoppingCb.isChecked());
        for (MineShoppingBookIntroduction book : booklist) {
            book.setChoose(mineShoppingCb.isChecked());
        }
        mineShoppingAdapter.notifyDataSetChanged();
        if (mineShoppingCb.isChecked()) {
            allPrice = 0;
            for (MineShoppingBookIntroduction book : booklist) {
                if (book.isChoose())
                    allPrice += book.getPresentPrice();
            }
            bigDecimal = new BigDecimal(allPrice);
            mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            allPrice = 0;
            bigDecimal = new BigDecimal(allPrice);
            mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
    }

    private List<MineShoppingBookIntroduction> booklist;
    private MineShoppingAdapter mineShoppingAdapter;

    public List<MineShoppingBookIntroduction> getBooklist() {
        return booklist;
    }

    public void setBooklist(List<MineShoppingBookIntroduction> booklist) {
        this.booklist.clear();
        this.booklist.addAll(booklist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_shopping);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatasFromNetwork();
    }

    protected void initView() {


        mineShoppingAdapter = new MineShoppingAdapter(this, booklist);
        mineShoppingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                booklist.get(position).setChoose(!booklist.get(position).isChoose());
                mineShoppingAdapter.notifyDataSetChanged();
                allPrice = 0;
                for (MineShoppingBookIntroduction book : booklist)
                    if (book.isChoose())
                        allPrice += book.getPresentPrice();
                bigDecimal = new BigDecimal(allPrice);
                mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (isAllChoose()) {
                    mineShoppingCb.setChecked(true);
                }
                if (!isAllChoose()) {
                    mineShoppingCb.setChecked(false);
                }
            }
        });
        mineShoppingListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final NormalDialog normalDialog = new NormalDialog(MineShoppingActivity.this);
                DialogUtils.setAlertDialogNormalStyle(normalDialog, getResources().getString(R.string.tips), "是否从购物车移除 " + booklist.get(i).getBookName() + " 这本书");
                normalDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        normalDialog.dismiss();
                        showLoadingDialog();
                        removeBookFromShoppingCart(i);
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        normalDialog.dismiss();
                        ToastCompat.makeText(MineShoppingActivity.this, "取消", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
        mineShoppingListview.setAdapter(mineShoppingAdapter);
        mineShoppingCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mineShoppingAdapter.notifyDataSetChanged();
                if (mineShoppingCb.isChecked()) {
                    allPrice = 0;
                    for (MineShoppingBookIntroduction book : booklist) {
                        if (book.isChoose())
                            allPrice += book.getPresentPrice();
                    }
                    bigDecimal = new BigDecimal(allPrice);
                    mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
            }
        });

    }

    /**
     * 批量清空图书
     * @param bookIdStr
     */
    private void batchRemoveBookFromShoppingCart(final String bookIdStr) {
        OkGo.get(Urls.BookMarketBatchRemoveFromShoppingCartURL)
                .params("bookIdString", bookIdStr)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<String>>(this) {

                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(MineShoppingActivity.this, iycResponse.getMsg(), Toast.LENGTH_SHORT).show();

                        Iterator<MineShoppingBookIntroduction> iterator=booklist.iterator();
                        while(iterator.hasNext())
                        {
                            MineShoppingBookIntroduction book=iterator.next();
                            if(book.isChoose())
                            {
                                iterator.remove();
                            }
                        }
                            //booklist.remove(position);
                            mineShoppingAdapter.notifyDataSetChanged();
                            allPrice = 0;
                            for (MineShoppingBookIntroduction book : booklist)
                                if (book.isChoose())
                                    allPrice += book.getPresentPrice();
                            bigDecimal = new BigDecimal(allPrice);
                            mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        if(booklist.size()==0){
                            llNoContent.setVisibility(View.VISIBLE);
                        }else{
                            llNoContent.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }
                });
    }

    private void removeBookFromShoppingCart(final int position) {
        OkGo.get(Urls.BookMarketRemoveFromShoppingCartURL)
                .params("bookid", booklist.get(position).getBookId())
                .params("userid", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<String>>(this) {

                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        if (iycResponse.getData().equals("1")) {
                            booklist.remove(position);
                            mineShoppingAdapter.notifyDataSetChanged();
                            allPrice = 0;
                            for (MineShoppingBookIntroduction book : booklist)
                                if (book.isChoose())
                                    allPrice += book.getPresentPrice();
                            bigDecimal = new BigDecimal(allPrice);
                            mineShoppingPayTv.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        }
                        if(booklist.size()==0){
                            llNoContent.setVisibility(View.VISIBLE);
                        }else{
                            llNoContent.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }
                });
    }


    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("购物车");
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setVisibility(View.VISIBLE);
        btnFunction.setImageResource(R.drawable.clearcart);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        booklist = new ArrayList<MineShoppingBookIntroduction>();
    }

    private void getDatasFromNetwork() {
        OkGo.get(PersonShopcartURL)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<List<MineShoppingBookIntroduction>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<MineShoppingBookIntroduction>> listIycResponse, Call call, Response response) {
                        setBooklist(listIycResponse.getData());
                        for (MineShoppingBookIntroduction book : booklist) {
                            if (book.getBookPrice().getFree_status() == 0) {
                                book.setPresentPrice(0.0);
                            } else if (book.getBookPrice().getFree_status() == -1) {
                                if (book.getBookPrice().getSpecial_status() == 0) {
                                    book.setPresentPrice(book.getBookPrice().getSpecial_price());
                                } else if (book.getBookPrice().getSpecial_status() == -1) {
                                    book.setPresentPrice(book.getBookPrice().getPrice());
                                }
                            }
                        }
                        if(booklist.size()==0){
                            llNoContent.setVisibility(View.VISIBLE);
                        }else{
                            llNoContent.setVisibility(View.GONE);
                        }
                        mineShoppingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });

    }

    public String getBookIdList() {
        StringBuffer bookIdStr = new StringBuffer();
        selectedCount = 0;
        for (MineShoppingBookIntroduction book : booklist) {
            if (book.isChoose()) {
                bookIdStr.append(book.getBookId() + ",");
                selectedCount++;
            }
        }
        return bookIdStr.substring(0, bookIdStr.length() - 1);
    }
    public String getBookPriceStrs(){
        StringBuilder bookPriceStrs = new StringBuilder();
        for (MineShoppingBookIntroduction book:booklist){
            if(book.isChoose()){
                bookPriceStrs.append(book.getBookPrice()+",");
            }
        }
        return bookPriceStrs.substring(0,bookPriceStrs.length()-1);
    }

    public boolean getIsCanPay() {
        for (MineShoppingBookIntroduction book : booklist) {
            if (book.isChoose()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllChoose() {
        for (MineShoppingBookIntroduction book : booklist) {
            if (!book.isChoose()) {
                return false;
            }
        }
        return true;
    }
}
