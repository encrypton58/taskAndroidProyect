package com.example.tareasmc256;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class workerManager extends Worker {

    Context context;

    private final static String CHANNEL_ID = "NOTIFICACION";

    public workerManager(Context context, WorkerParameters workerParameters){
        super(context, workerParameters);
        this.context = context;
    }


    public static void saveNoti(long duration,  Data data, String tag){

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(workerManager.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data)
                .build();
        WorkManager manager = WorkManager.getInstance();
        manager.enqueue(workRequest);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("titleTask");
        String details = getInputData().getString("descriptionTask");
        int id = getInputData().getInt("idTask", 0);

        long time = Long.parseLong(getInputData().getString("designado")) * 60000;
        Intent in = new Intent(context, serviceTimer.class);
        in.putExtra("time", time);
        in.putExtra("title", title);
        in.putExtra("details",details);
        context.startService(in);

        createNotification(title,details,id);
        createNotificationChannel();

        return Result.success();
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
