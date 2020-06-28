package com.example.tareasmc256;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class serviceTimer extends Service {

    private final static String TAG = "BroadcastService";
    private final static String CHANNEL_ID = "NOTIFICACIONTIMER ";

    public static serviceTimer instancia = null;

    Bundle datos;

    CountDownTimer cdt = null;



    @Override
    public void onCreate() {
        super.onCreate();
        instancia = this;
    }



    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        datos = intent.getExtras();

        Log.i(TAG, "Starting timer...");

        final long tiempo = datos.getLong("time", 0);
        final String title = datos.getString("title");
        final String details = datos.getString("details");

        createNotificationChannel();

        cdt = new CountDownTimer(tiempo, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 10000);

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, notificationIntent, 0);

                if(millisUntilFinished / 60000 == 0){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(details + " Timepo de finalizacion: " + millisUntilFinished / 1000 + " mins")
                            .setSmallIcon(R.drawable.icon_close)
                            .setContentIntent(pendingIntent)
                            .build();

                    startForeground(1, notification);
                }else {
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(details + " Timepo de finalizacion: " + millisUntilFinished / 60000 + " mins")
                            .setSmallIcon(R.drawable.icon_close)
                            .setContentIntent(pendingIntent)
                            .build();

                    startForeground(1, notification);
                }




            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                cdt.cancel();
                createNotification("has finalizado " + datos.getString("title"),
                        "Se ha finalizado el tiempo designado", (int) (Math.random()*255));
                stopForeground(true);
                stopSelf();

            }
        };

        cdt.start();



        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cdt != null) {
            cdt.cancel();    
        }

        instancia = null;
    }

    public void stopBecauseDeleteTask(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.imagen_users);
        builder.setContentTitle("Se ha detenido la tarea ");
        builder.setContentText("la tarea fallo exitosamente");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify((int) (Math.random() * 255), builder.build());
        onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);



        }
    }

    private void createNotification(String title, String description, int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.imagen_users);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(id, builder.build());
    }



}
