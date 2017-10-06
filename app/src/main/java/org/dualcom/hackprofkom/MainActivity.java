package org.dualcom.hackprofkom;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.dualcom.hackprofkom.MyClass.MyPHP;
import org.dualcom.hackprofkom.MyClass.Storage;
import org.dualcom.hackprofkom.MyClass.Windows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ALL")
public class MainActivity extends Activity {

    Context context = this;
    private String response;
    private WebView mWebView;
    private int counter = 1;
    private String[] result;
    private double percent;

    //Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo == null) return false;
        boolean res = (!activeNetworkInfo.isConnected())?false:true;
        res = (!activeNetworkInfo.isAvailable())?false:true;
        return res;
    }
    /*************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView GroupList = (TextView) findViewById(R.id.GroupList);
        final Button GetGroup = (Button) findViewById(R.id.GetGroup);
        final TextView ClearStatus = (TextView) findViewById(R.id.ClearStatus);
        final Button CleanGroup = (Button) findViewById(R.id.CleanGroup);
        final TextView SetCounter = (TextView) findViewById(R.id.SetCounter);
        final Button SetGroup = (Button) findViewById(R.id.SetGroup);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        final TextView time_fow = (TextView) findViewById(R.id.time_foward);
        final CheckBox teach = (CheckBox) findViewById(R.id.teach);

        GetGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                    try {
                        String _teach = (teach.isChecked()) ? "&t=true" : "";
                        String rezdel = (teach.isChecked()) ? ":," : ";,";

                        String list_group = new MyPHP().execute("decoder_group"+_teach).get();
                        Windows.alert(context,"",list_group);
                        String[] count = list_group.split(rezdel);
                        GroupList.setText("Получено: "+(count.length-1));
                        Storage.saveData(context, "GROUPS_LIST", list_group);

                        time_fow.setText(String.format("%d:%d", (((count.length-1)*3) * 1000) / 60000,
                                ((((count.length-1)*3) * 1000) % 60000) / 1000));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
            }
        });

        CleanGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                    try {
                        String _teach = (teach.isChecked()) ? "&t=true" : "";

                        String clearRes = new MyPHP().execute("clear_groups"+_teach).get();
                        if(clearRes.equals("1"))
                            ClearStatus.setText("Расписание очищено");
                        else
                            ClearStatus.setText("Ошибка очистки");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
            }
        });

        SetGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String rezdel = (teach.isChecked()) ? ":," : ";,";
                result = Storage.loadData(context, "GROUPS_LIST").split(rezdel);

                //Windows.alert(context,"",result[1]);

                progress.setMax(result.length);

                GetGroup.setEnabled(false);
                CleanGroup.setEnabled(false);
                SetGroup.setEnabled(false);

                final Timer myTimer = new Timer();
                final Handler uiHandler = new Handler();

                //Веб форма
                mWebView = (WebView) findViewById(R.id.LogSetGroup);
                mWebView.setAlpha(1); //Вкл вебки
                mWebView.setWebViewClient(new CustomWebViewClient());
                mWebView.getSettings().setJavaScriptEnabled(true);

                //Создаем таймер обратного отсчета на 20 секунд с шагом отсчета
                //в 1 секунду (задаем значения в миллисекундах):
                new CountDownTimer(((result.length-1)*3) *1000, 1000) {

                    //Здесь обновляем текст счетчика обратного отсчета с каждой секундой
                    public void onTick(long millisUntilFinished) {
                        time_fow.setText(String.format("%d:%d", millisUntilFinished / 60000,
                                (millisUntilFinished % 60000) / 1000));
                    }
                    //Задаем действия после завершения отсчета (высвечиваем надпись "Бабах!"):
                    public void onFinish() {
                        time_fow.setText("Кража завершена! :)");
                    }
                }.start();

                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                String _teach = (teach.isChecked()) ? "&t=true" : "";

                                // указываем страницу загрузки
                                if (isNetworkAvailable())
                                    mWebView.loadUrl("http://rapoo.mysit.ru/hack?module=decoder&g="+result[counter]+_teach);
                                //**********/
                                percent = ((counter/(float)(result.length-1))*100);
                                percent = new BigDecimal(percent).setScale(2, RoundingMode.UP).doubleValue();
                                SetCounter.setText("#"+counter+" ("+percent+"%)");
                                progress.setProgress(counter);
                                counter++;
                                if(counter == result.length) {
                                    Windows.alert(context, "Работа завершена", "Обработано " + (counter-1) + " объектов");
                                        GetGroup.setEnabled(true);
                                        CleanGroup.setEnabled(true);
                                        SetGroup.setEnabled(true);
                                    myTimer.cancel();
                                }
                            }
                        });
                    }
                }, 0L, 1L * 3000);
            }
        });

    }

    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

}
