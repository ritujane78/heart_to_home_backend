package com.chillies.hearttohome.util;

import com.chillies.hearttohome.models.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailForPasswordReset(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetUrl);

        mailSender.send(message);
    }

    public void sendEmailForOrderStatus(String to, OrderStatus status) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);

        String subject;
        String text;

        switch (status) {

            case IN_PROCESS:
                subject = "Your Heart to Home Order is Being Processed";
                text = """
                    Dear Customer,

                    Thank you for choosing Heart to Home.

                    Your order has been received and is currently being processed. We will notify you once it is ready for the clinic.

                    Thank you for your patience.

                    Kind regards,
                    Heart to Home Team
                    """;
                break;

            case READY_FOR_CLINIC:
                subject = "Your Heart to Home Order is Ready for the Clinic";
                text = """
                    Dear Customer,

                    Good news!

                    Your order has been prepared and is now ready for the clinic. Our team will proceed with the next steps shortly.

                    Thank you for choosing Heart to Home.

                    Kind regards,
                    Heart to Home Team
                    """;
                break;

            case DELIVERED:
                subject = "Your Heart to Home Order Has Been Delivered";
                text = """
                    Dear Customer,

                    We're pleased to let you know that your order has been successfully delivered.

                    Thank you for trusting Heart to Home. We hope our service met your expectations.

                    Kind regards,
                    Heart to Home Team
                    """;
                break;

            case CANCELED:
                subject = "Your Heart to Home Order Has Been Cancelled";
                text = """
                    Dear Customer,

                    We regret to inform you that your order has been cancelled.

                    If you have any questions, please contact our support team.

                    Kind regards,
                    Heart to Home Team
                    """;
                break;

            default:
                subject = "Heart to Home Order Update";
                text = "Your order status has been updated.";
        }

        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

}
