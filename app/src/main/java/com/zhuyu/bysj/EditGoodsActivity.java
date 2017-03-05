package com.zhuyu.bysj;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.yalantis.ucrop.UCrop;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;
import com.zhuyu.bysj.dialog.ChoosePictureDialog;
import com.zhuyu.bysj.dialog.StateDialog;
import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.CropUtils;
import com.zhuyu.bysj.utils.Names;
import com.zhuyu.bysj.utils.RequestSaveUtil;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zhuyu.bysj.AlterActivity.ALTERACTIVITY_CODE;


public class EditGoodsActivity extends AppCompatActivity {


    @BindView(R.id.goodsNameText)
    TextView goodsNameText;
    @BindView(R.id.stateText)
    TextView stateText;
    @BindView(R.id.stateLayout)
    LinearLayout stateLayout;
    @BindView(R.id.dateText)
    TextView dateText;
    @BindView(R.id.dateLayout)
    LinearLayout dateLayout;
    @BindView(R.id.addressText)
    TextView addressText;
    @BindView(R.id.addressLayout)
    LinearLayout addressLayout;
    @BindView(R.id.priceText)
    TextView priceText;
    @BindView(R.id.priceLayout)
    LinearLayout priceLayout;
    @BindView(R.id.goodsNameLayout)
    LinearLayout goodsNameLayout;
    @BindView(R.id.pictureLayout)
    LinearLayout pictureLayout;

    public static final int TAKEPHOTO_CODE = 2;
    public static final int FROMALBUM_CODE = 3;
    public static final int TAKEPHOTO_PERMISSION = 1;
    @BindView(R.id.goodsImage)
    ImageView goodsImage;

    private StateDialog stateDialog;
    private String stateCode;
    private Goods goods;
    private Type type;
    private int goodsid;
    private ChoosePictureDialog dialog;
    File picture;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goods);
        ButterKnife.bind(this);
        initView();
        initdates();

    }

    private void initView() {
        ActionBarUtil.create(this, "编辑商品信息", ActionBarUtil.DEFAULT_HOME);
    }

    private void initdates() {

        goods = (Goods) getIntent().getSerializableExtra("goods");
        type = (Type) getIntent().getSerializableExtra("type");
        goodsid = goods.getGoodsid();
        Log.d("editActivity", "goods: " + goods.toString());


        picture = new File(getExternalCacheDir(), goodsid + "goods.jpg");
        long time = picture.lastModified();//获得文件最后的修改时间
        if (!TextUtils.isEmpty(goods.getBuy_picture())) {
            Glide.with(this)
                    .load(MainActivity.baseUrl + goods.getBuy_picture())
                    .signature(new StringSignature(time + ""))   // 重点在这行
                    .into(goodsImage);
        }else {
            Glide.with(this)
                    .load(picture)
                    .signature(new StringSignature(time + ""))   // 重点在这行
                    .into(goodsImage);

        }


        dateText.setText(goods.getBuy_date());
        stateText.setText(goods.getBuy_date());
        addressText.setText(goods.getBuy_address());
        priceText.setText(goods.getBuy_price());
        goodsNameText.setText(type.getTrademark_chinese() + type.getTypename());
        showState();//根据goods中的state状态  修改statebtn和statecode内容


    }

    private void changePicture() {
        //检查权限并请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKEPHOTO_PERMISSION);
        } else {
            if (dialog == null) {
                dialog = new ChoosePictureDialog(this);
                dialog.setOnDialogClickListener(new ChoosePictureDialog.OnDialogClickListener() {
                    @Override
                    public void onTakephotoClick() {
                        takephoto();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFromalbumClick() {
                        fromalbum();
                        dialog.dismiss();
                    }
                });
            }
            dialog.show();
        }
    }

    /**
     * 拍照
     */
    private void takephoto() {
        try {
            if (picture.exists()) {
                picture.delete();
            }
            picture.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri pictureUri;

        //解决7.0系统相机崩溃问题
        if (Build.VERSION.SDK_INT < 24) {
            pictureUri = Uri.fromFile(picture);
        } else {
            pictureUri = FileProvider.getUriForFile(this, "com.zhuyu.bysj", picture);
        }
        Log.d("usericonUri", pictureUri + "");
        // 启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);//为相机应用指定图片的输出地址
        startActivityForResult(intent, TAKEPHOTO_CODE);
    }

    /**
     * 从相册选择
     */
    private void fromalbum() {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, FROMALBUM_CODE);

    }

    private void showState() {
        switch (goods.getState()) {
            case "normal":
                stateText.setText("正常");
                stateCode = "normal";
                break;
            case "lost":
                stateText.setText("已挂失");
                stateCode = "lost";
                break;
            case "unbind":
                stateText.setText("已解绑");
                stateCode = "unbind";
                break;
            default:
                break;
        }
    }

    private void setstate() {
        if (stateDialog == null) {
            stateDialog = new StateDialog(this);
            stateDialog.setOnStateClickListener(new StateDialog.OnStateClickListener() {
                @Override
                public void onNormalClick() {
                    stateText.setText("正常");
                    stateCode = "normal";
                    RequestSaveUtil.requestSaveGoodsInfo(EditGoodsActivity.this, Names.STATE, Names.STATE, stateCode, goodsid, callback);
                    stateDialog.dismiss();
                }

                @Override
                public void onLostClick() {
                    stateText.setText("已挂失");
                    stateCode = "lost";
                    RequestSaveUtil.requestSaveGoodsInfo(EditGoodsActivity.this, Names.STATE, Names.STATE, stateCode, goodsid, callback);
                    stateDialog.dismiss();
                }

                @Override
                public void onUnbindClick() {
                    stateText.setText("已注销");
                    stateCode = "unbind";
                    RequestSaveUtil.requestSaveGoodsInfo(EditGoodsActivity.this, Names.STATE, Names.STATE, stateCode, goodsid, callback);
                    stateDialog.dismiss();
                }
            });
        }

        stateDialog.show();

    }

    private void setDate() {
        //初始化日历信息
        Calendar calendar = Calendar.getInstance();
        //获取当前年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("setDate", year + "-" + month + "-" + day);
        /**context：当前上下文；
         callback：OnDateSetListener日期改变监听器；
         year：初始化的年；
         monthOfYear：初始化的月（从0开始计数，所以实际应用时需要加1）；
         dayOfMonth：初始化的日；
         */
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                dateText.setText(date);
                RequestSaveUtil.requestSaveGoodsInfo(EditGoodsActivity.this, Names.BUY_DATE, Names.BUY_DATE, date, goodsid, callback);
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.stateLayout, R.id.dateLayout, R.id.addressLayout, R.id.priceLayout, R.id.goodsNameLayout, R.id.pictureLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stateLayout:
                setstate();
                break;
            case R.id.dateLayout:
                setDate();
                break;
            case R.id.addressLayout:
                AlterActivity.actionstart(this, Names.BUY_ADDRESS, addressText.getText().toString(), goodsid);
                break;
            case R.id.priceLayout:
                AlterActivity.actionstart(this, Names.BUY_PRICE, priceText.getText().toString(), goodsid);
                break;
            case R.id.goodsNameLayout:
                break;
            case R.id.pictureLayout:
                changePicture();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ALTERACTIVITY_CODE:
                if (resultCode == RESULT_OK) {
                    String name = data.getExtras().getString("name");
                    String value = data.getExtras().getString("value");
                    switch (name) {
                        case Names.BUY_ADDRESS:
                            addressText.setText(value);
                            break;
                        case Names.BUY_PRICE:
                            priceText.setText(value);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case TAKEPHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    //需要将usericonUri转换为fileuri
                    //所以
                    Uri uri = Uri.fromFile(picture);

                    //进行裁剪,裁剪比例1：1
                    CropUtils.startUCrop(this, uri, uri, 1, 1);
                }
                break;
            case FROMALBUM_CODE:
                if (resultCode == RESULT_OK) {
                    //获得图片真是路径
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
                    c.close();
                    //根据相册中图片路径获取uri，作为ucrop的sourceUri
                    Uri iconFromAlbumUri = Uri.fromFile(new File(imagePath));
                    //目标Uri
                    Uri uri = Uri.fromFile(picture);
                    //进行裁剪,裁剪比例1：1
                    CropUtils.startUCrop(this, iconFromAlbumUri, uri, 1, 1);
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {

                    uploadGoodsPicture(picture, goodsid);//上传头像
                }
                break;
        }
    }

    //上传服务器
    private void uploadGoodsPicture(final File file, int goodsid) {
        if (client == null) {
            client = new OkHttpClient();
        }
        Log.d("goodsid", goodsid + "");
        String url = MainActivity.baseUrl + "uploadGoodsPicture";
        RequestBody filebBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Names.GOODSID, goodsid + "")
                .addFormDataPart("image", goodsid + ".jpg", filebBody)
                .build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("IOException", e.toString());
                        Toast.makeText(EditGoodsActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d("result", result);
                if (result.equals("ok")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditGoodsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                            long time = file.lastModified();//获得文件最后的修改时间
                            //记载图片
                            Glide.with(EditGoodsActivity.this)
                                    .load(file)
                                    .signature(new StringSignature(time + ""))   // 重点在这行
                                    .into(goodsImage);
                        }
                    });

                } else if (result.equals("error")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditGoodsActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    Callback callback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EditGoodsActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();

            if (result.equals("ok")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditGoodsActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (result.equals("error")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditGoodsActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKEPHOTO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    changePicture();
                else
                    Toast.makeText(this, "你拒绝了权限！", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
