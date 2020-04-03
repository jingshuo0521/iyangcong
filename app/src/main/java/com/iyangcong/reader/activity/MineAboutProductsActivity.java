package com.iyangcong.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DarkFlameMaster on 2017/5/10.
 */

public class MineAboutProductsActivity extends SwipeBackActivity implements
        ActionSheet.ActionSheetListener{

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.gridview)
    GridView gridView;

    @BindView(R.id.tv_tips)
    TextView tv_tips;
//    @BindView(R.id.guideImage)
//    ImageView guideImage;

   // private SimpleAdapter adapter;

   // private GridView gridview;

    GridViewSim myGridView;
    LayoutInflater inflater;
    private int clickedIndex;


    private List<Map<String, Object>> dataList;

    private String text[]={"公众号二维码","小程序二维码"};
    private int img_grid[] ={R.drawable.gongzhonghaodc,R.drawable.iyc_xiaochengxu};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_about_product);
        ButterKnife.bind(this);
        setMainHeadView();
        initMyView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    private void initMyView(){
        myGridView=new GridViewSim(this,text,img_grid);
        gridView.setAdapter(myGridView);

        inflater= LayoutInflater.from(this);
//        gridView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(MineAboutProductsActivity.this);
//                builder.setTitle("提示").setMessage(dataList.get(arg2).get("text").toString()).create().show();
//            }
//        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(MineAboutProductsActivity.this);
//               builder.setTitle("提示").setMessage("haah").create().show();
                clickedIndex=position;
                setTheme(R.style.ActionSheetStyleiOS7);
                ActionSheet.createBuilder(MineAboutProductsActivity.this, getSupportFragmentManager())
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles("保存到手机"/*,"分享到朋友圈"*/)
                        .setCancelableOnTouchOutside(true)
                        .setListener(MineAboutProductsActivity.this).show();
                return false;

            }
        });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("关于产品");
        btnBack.setImageResource(R.drawable.btn_back);
        tvVersion.setText(CommonUtil.getVersion(this));
        String copyrightDate="Copyright © 2011-";
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        copyrightDate=copyrightDate+year;
        tv_tips.setText(copyrightDate);
//        int screenWidth = getScreenWidth(this);
//        ViewGroup.LayoutParams lp = guideImage.getLayoutParams();
//        lp.width = screenWidth;
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        guideImage.setLayoutParams(lp);
//        guideImage.setMaxWidth(screenWidth);
//        guideImage.setMaxHeight(screenWidth * 5);
    }

    @OnClick(R.id.btnBack)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if(index==0){
            saveImage();
        }else if(index==1){

        }

    }


    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iyangcong";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static void saveBitmap(View view){
//
//        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
//        String fileName = System.currentTimeMillis() + ".jpg";
//        String filePath=storePath+"/"+fileName;
//        // 创建对应大小的bitmap
//        Bitmap  bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
//                Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//
//        //存储
//        FileOutputStream outStream = null;
//        File file=new File(filePath);
//        if(file.isDirectory()){//如果是目录不允许保存
//            return;
//        }
//        try {
//            outStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            outStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                bitmap.recycle();
//                if(outStream!=null){
//                    outStream.close();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }


    //保存图片
    private void saveImage() {
        Bitmap bitmap=null;
        if(clickedIndex==0){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gongzhonghaodc);
        }else if(clickedIndex==1){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iyc_xiaochengxu);
        }

        boolean isSaveSuccess = saveImageToGallery(MineAboutProductsActivity.this, bitmap);
        if (isSaveSuccess) {
            Toast.makeText(MineAboutProductsActivity.this, "保存图片成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MineAboutProductsActivity.this, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    class GridViewSim extends BaseAdapter{
        private Context context=null;
        private String data[]=null;
        private int imgId[]=null;


        private class Holder{

            ImageView item_img;
            TextView item_tex;

            public ImageView getItem_img() {
                return item_img;
            }

            public void setItem_img(ImageView item_img) {
                this.item_img = item_img;
            }

            public TextView getItem_tex() {
                return item_tex;
            }

            public void setItem_tex(TextView item_tex) {
                this.item_tex = item_tex;
            }




        }
        //构造方法
        public GridViewSim(Context context, String[] data, int[] imgId) {
            this.context = context;
            this.data = data;
            this.imgId = imgId;
        }


        @Override
        public int getCount() {


            return data.length;

        }

        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            Holder holder;
            if(view==null){
                view=inflater.inflate(R.layout.mine_about_gridview_item,null);
                holder=new Holder();
                holder.item_img=(ImageView)view.findViewById(R.id.img);
                holder.item_tex=(TextView)view.findViewById(R.id.text);
                view.setTag(holder);
            }else{
                holder=(Holder) view.getTag();
            }
            holder.item_tex.setText(data[position]);
            holder.item_img.setImageResource(imgId[position]);

            return view;
        }
    }
}
