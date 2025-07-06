package com.example.shoes_project.ui;



import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** Gửi email OTP bằng Gmail SMTP */
public final class MailHelper {
    private static final String TAG = "MailHelper";
    private static final String SMTP_EMAIL = "ninjaanhem@gmail.com";
    private static final String SMTP_PASS  = "umjznbczkuyhjdjk";

    private static final ExecutorService POOL =
            Executors.newSingleThreadExecutor();

    private MailHelper() {} // utility

    public static void sendOtp(String toEmail, String otp, Context ctx) {
        Handler main = new Handler(Looper.getMainLooper());

        POOL.execute(() -> {
            try {
                Properties p = new Properties();
                p.put("mail.smtp.auth", "true");
                p.put("mail.smtp.starttls.enable", "true");
                p.put("mail.smtp.host", "smtp.gmail.com");
                p.put("mail.smtp.port", "587");

                Session s = Session.getInstance(p,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(SMTP_EMAIL, SMTP_PASS);
                            }
                        });
                 s.setDebug(true); // bật khi cần log

                Message m = new MimeMessage(s);
                m.setFrom(new InternetAddress(SMTP_EMAIL, "GO MAP App"));
                m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                m.setSubject("GO MAP – OTP Code");
                m.setText("Your OTP code is: " + otp + "\nValid for 5 minutes.");
                m.setSentDate(new java.util.Date());

                Transport.send(m);

                main.post(() ->
                        Toast.makeText(ctx, "OTP sent to " + toEmail,
                                Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "sendOtp: ", e);
                main.post(() ->
                        Toast.makeText(ctx, "Send mail failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }
        });
    }
}
