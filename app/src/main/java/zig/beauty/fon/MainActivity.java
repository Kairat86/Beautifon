package zig.beauty.fon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;

import static android.content.Intent.ACTION_SEND;


public class MainActivity extends Activity {

    private static final int ACTIVITY_REQUEST_CODE_IMAGE_CAPTURE = 1;
    public static final String RESULT_PHOTO = "result_photo.png";
    private ImageView imgMain;
    private static final int SELECT_PHOTO = 100;
    private Bitmap src;
    private float rotation = 90;
    private boolean isReady = true;
    private Menu menu;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgMain = findViewById(R.id.effect_main);
        src = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        View adView = findViewById(R.id.adView);
        ((AdView) adView).setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
        ((AdView) adView).loadAd(new AdRequest.Builder().build());
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.int_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    public void buttonClicked(final View v) {
        if (!isReady) {
            Toast.makeText(this, R.string.wait_please, Toast.LENGTH_LONG).show();
            return;
        }
        isReady = false;
        final ProgressBar prgrBar = findViewById(R.id.prgrBar);
        prgrBar.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                process(v, prgrBar);
                runOnUiThread(() -> {
                    isReady = true;
                    prgrBar.setVisibility(View.GONE);
                });
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isReady) {
            switch (item.getItemId()) {
                case R.id.action_capture:
                    capture();
                    break;
                case R.id.action_pic:
                    pick();
                    break;
                case R.id.action_rotate:
                    rotate();
                    break;
                case R.id.action_share:
                    share();
                    break;
            }
        } else {Toast.makeText(this, R.string.wait_please, Toast.LENGTH_LONG).show();}
        return true;
    }

    private void process(View v, ProgressBar prgrBar) {
        ImageFilters imgFilter = new ImageFilters();

        //        else if(v.getId() == R.id.effect_highlight)
        //            saveBitmap(imgFilter.applyHighlightEffect(src), "effect_highlight");
        if (v.getId() == R.id.effect_black)
            saveBitmap(imgFilter.applyBlackFilter(src, prgrBar), "effect_black");
        else if (v.getId() == R.id.effect_boost_1)
            saveBitmap(imgFilter.applyBoostEffect(src, 1, 40, prgrBar), "effect_boost_1");
        else if (v.getId() == R.id.effect_boost_2)
            saveBitmap(imgFilter.applyBoostEffect(src, 2, 30, prgrBar), "effect_boost_2");
        else if (v.getId() == R.id.effect_boost_3)
            saveBitmap(imgFilter.applyBoostEffect(src, 3, 67, prgrBar), "effect_boost_3");
        else if (v.getId() == R.id.effect_brightness)
            saveBitmap(imgFilter.applyBrightnessEffect(src, 80, prgrBar), "effect_brightness");
        else if (v.getId() == R.id.effect_color_red)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 255, 0, 0, prgrBar), "effect_color_red");
        else if (v.getId() == R.id.effect_color_green)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 255, 0, prgrBar), "effect_color_green");
        else if (v.getId() == R.id.effect_color_blue)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 0, 255, prgrBar), "effect_color_blue");
        else if (v.getId() == R.id.effect_color_depth_64)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 64, prgrBar), "effect_color_depth_64");
        else if (v.getId() == R.id.effect_color_depth_32)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 32, prgrBar), "effect_color_depth_32");
        else if (v.getId() == R.id.effect_contrast)
            saveBitmap(imgFilter.applyContrastEffect(src, 70, prgrBar), "effect_contrast");
        else if (v.getId() == R.id.effect_emboss)
            saveBitmap(imgFilter.applyEmbossEffect(src, prgrBar), "effect_emboss");
        else if (v.getId() == R.id.effect_engrave)
            saveBitmap(imgFilter.applyEngraveEffect(src, prgrBar), "effect_engrave");
        else if (v.getId() == R.id.effect_flea)
            saveBitmap(imgFilter.applyFleaEffect(src, prgrBar), "effect_flea");
        else if (v.getId() == R.id.effect_gaussian_blue)
            saveBitmap(imgFilter.applyGaussianBlurEffect(src, prgrBar), "effect_gaussian_blue");
        else if (v.getId() == R.id.effect_gamma)
            saveBitmap(imgFilter.applyGammaEffect(src, 1.8, 1.8, 1.8, prgrBar), "effect_gamma");
        else if (v.getId() == R.id.effect_grayscale)
            saveBitmap(imgFilter.applyGreyscaleEffect(src, prgrBar), "effect_grayscale");
        else if (v.getId() == R.id.effect_hue)
            saveBitmap(imgFilter.applyHueFilter(src, 2, prgrBar), "effect_hue");
        else if (v.getId() == R.id.effect_invert)
            saveBitmap(imgFilter.applyInvertEffect(src, prgrBar), "effect_invert");
        else if (v.getId() == R.id.effect_mean_remove)
            saveBitmap(imgFilter.applyMeanRemovalEffect(src, prgrBar), "effect_mean_remove");
            //        else if(v.getId() == R.id.effect_reflaction)
            //            saveBitmap(imgFilter.applyReflection(src),"effect_reflaction");
        else if (v.getId() == R.id.effect_round_corner)
            saveBitmap(imgFilter.applyRoundCornerEffect(src, 45), "effect_round_corner");
        else if (v.getId() == R.id.effect_saturation)
            saveBitmap(imgFilter.applySaturationFilter(src, 1, prgrBar), "effect_saturation");
        else if (v.getId() == R.id.effect_sepia)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 1.5, 0.6, 0.12, prgrBar), "effect_sepia");
        else if (v.getId() == R.id.effect_sepia_green)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 0.88, 2.45, 1.43, prgrBar), "effect_sepia_green");
        else if (v.getId() == R.id.effect_sepia_blue)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 1.2, 0.87, 2.1, prgrBar), "effect_sepia_blue");
        else if (v.getId() == R.id.effect_smooth)
            saveBitmap(imgFilter.applySmoothEffect(src, 100, prgrBar), "effect_smooth");
        else if (v.getId() == R.id.effect_sheding_cyan)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.CYAN, prgrBar), "effect_sheding_cyan");
        else if (v.getId() == R.id.effect_sheding_yellow)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.YELLOW, prgrBar), "effect_sheding_yellow");
        else if (v.getId() == R.id.effect_sheding_green)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.GREEN, prgrBar), "effect_sheding_green");
        else if (v.getId() == R.id.effect_tint)
            saveBitmap(imgFilter.applyTintEffect(src, 100, prgrBar), "effect_tint");
        else if (v.getId() == R.id.effect_watermark)
            saveBitmap(imgFilter.applyWaterMarkEffect(src, "", 200, 200, Color.GREEN, 80, 24, false, prgrBar), "effect_watermark");
    }

    private void saveBitmap(Bitmap bmp, String fileName) {
        try {
            imgMain.setImageBitmap(bmp);
            File f = new File(getFilesDir(), RESULT_PHOTO);
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    Uri selectedImage = data.getData();
                    Bitmap bmp = decodeUri(selectedImage);
                    if (bmp != null) {
                        src = bmp;
                        imgMain.setImageBitmap(src);
                    }
                    break;
                case ACTIVITY_REQUEST_CODE_IMAGE_CAPTURE:
                    File photo = new File(getFilesDir(), "PHOTO.png");
                    imgMain.setImageURI(Uri.fromFile(photo));
                    src = BitmapFactory.decodeFile(photo.getPath());
            }

            menu.findItem(R.id.action_share).setVisible(true);
        }
    }

    private Bitmap decodeUri(Uri selectedImage) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 400;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void rotate() {
        imgMain.setRotation(rotation += 90);
    }

    public void pick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public void capture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photo = new File(getFilesDir(), "PHOTO.png");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photo));
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_IMAGE_CAPTURE);
        }
    }

    public void share() {
        Intent intent = new Intent(ACTION_SEND);
        intent.setType("image/png");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = new File(getFilesDir(), RESULT_PHOTO);
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }
}
