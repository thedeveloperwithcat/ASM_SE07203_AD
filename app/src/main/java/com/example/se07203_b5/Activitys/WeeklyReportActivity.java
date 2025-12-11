package com.example.se07203_b5.Activitys; // Chung package với các Activity khác

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ListenableWorker; // Thêm import này
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.se07203_b5.Database.DatabaseHelper;
import com.example.se07203_b5.R;

import java.util.Calendar;

public class WeeklyReportActivity extends Worker {

    public WeeklyReportActivity(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        Context context = getApplicationContext();
        DatabaseHelper db = new DatabaseHelper(context);

        long userId = context.getSharedPreferences("AppData", Context.MODE_PRIVATE).getLong("user_id", -1);

        if (userId == -1) return ListenableWorker.Result.success();

        // Logic tính toán tuần
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        long endOfLastWeek = cal.getTimeInMillis() - 1;
        long startOfLastWeek = endOfLastWeek - (7 * 24 * 60 * 60 * 1000) + 1;

        long spentLastWeek = db.getTotalExpenseInRange(userId, startOfLastWeek, endOfLastWeek);
        long remaining = db.getTotalRemainingBudget(userId);

        showNotification("Báo cáo tuần", "Tuần trước tiêu: " + spentLastWeek + " đ. Số dư: " + remaining + " đ");

        return ListenableWorker.Result.success();
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        Context context = getApplicationContext();

        // Kiểm tra quyền cho Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String channelId = "weekly_report_channel";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel cho Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Weekly Reports", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Đảm bảo icon này tồn tại
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(100, builder.build());
    }
}