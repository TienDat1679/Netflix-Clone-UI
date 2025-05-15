package com.netflixcloneui.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeAgoUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Trả về chuỗi định dạng thời gian kiểu "1 giờ trước", "2 ngày trước", v.v...
     * @param createdAt thời gian dạng ISO (vd: 2024-05-05T14:30:00)
     * @return chuỗi thời gian đã trôi qua
     */
    public static String getTimeAgo(String createdAt) {
        try {
            LocalDateTime createdTime = LocalDateTime.parse(createdAt, FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(createdTime, now);
            long seconds = Math.abs(duration.getSeconds());

            if (seconds < 60) {
                return seconds + " giây trước";
            } else if (seconds < 3600) {
                return (seconds / 60) + " phút trước";
            } else if (seconds < 86400) {
                return (seconds / 3600) + " giờ trước";
            } else if (seconds < 604800) {
                return (seconds / 86400) + " ngày trước";
            } else if (seconds < 2592000) {
                return (seconds / 604800) + " tuần trước";
            } else if (seconds < 31536000) {
                return (seconds / 2592000) + " tháng trước";
            } else {
                return (seconds / 31536000) + " năm trước";
            }

        } catch (Exception e) {
            return "Không rõ thời gian";
        }
    }
}
