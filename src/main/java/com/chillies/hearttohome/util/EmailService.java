package com.chillies.hearttohome.util;

import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.entity.OrderService;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${frontend.url}")
    String frontendUrl;

    @Value("${sender.email}")
    String senderEmail;

    private void configureEmail(
            MimeMessageHelper helper,
            String recipientEmail
    ) throws UnsupportedEncodingException, MessagingException {

        helper.setFrom(new InternetAddress(
                senderEmail,
                "Heart to Home"
        ));

        helper.setTo(new InternetAddress(
                recipientEmail,
                "Customer"
        ));
    }

    public void sendSignupEmail(User savedUser) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            configureEmail(helper, savedUser.getEmail());

            helper.setSubject("Welcome to Heart to Home!");

            String formattedName = NameUtils.formatFirstName(savedUser.getFirstName());

            String servicesUrl = frontendUrl + "/services";

            String text = """
                    <p>Hi there, %s!,</p>
                    
                    <p>Welcome to <strong>Heart to Home</strong>!</p>
                    
                    <p>
                        Your account has been successfully created with the email address:
                        <br>
                        <strong>%s</strong>
                    </p>
                    
                    <p>
                        You can now browse our healthcare services and gift care to your loved ones.
                    </p>
                    
                    <p>
                        Visit our <a href="%s">Services page</a>.
                    </p>
                    
                    <p>
                        Thank you for choosing Heart to Home. We look forward to helping you care for those who matter most.
                    </p>
                    
                    <p>
                        Kind regards,<br>
                        <strong>Heart to Home Team</strong>
                    </p>
                    """.formatted(formattedName, savedUser.getEmail(), servicesUrl, servicesUrl);

            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            throw new EmailSendingException(
                    "We were unable to send the welcome email. Please try again later.",
                    ex
            );
        }
    }

    public void sendEmailForPasswordReset(String recipientName, String to, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            configureEmail(helper, to);

            helper.setSubject("Password Reset Request");

            String text = """
                      <p>Hi there, %s!</p>
                    
                      <p>You recently requested to reset your Heart to Home account password.</p>
                    
                      <p>
                          Click the link below to reset your password:
                          <br><br>
                          <a href="%s">%s</a>
                      </p>
                      <p><strong>Please note:</strong> This password reset link will expire 10 hours after 
                      this email is sent. After that, the link will no longer be valid, 
                      and you'll need to request a new password reset.</p>
                    
                      <p>If you did not request a password reset, you can safely ignore this email.</p>
                    
                      <p>
                          Kind regards,<br>
                          <strong>Heart to Home Team</strong>
                      </p>
                    """.formatted(recipientName, resetUrl, resetUrl);

            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            throw new EmailSendingException(
                    "Unable to send password reset email. Please try again later.",
                    ex
            );
        }
    }

    private String getServices(List<OrderService> services) {
        if (services == null || services.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<h3><u>Services</u></h3>");
        sb.append("<ul>");

        services.forEach(service ->
                sb.append("<li>").append(service.getTitle()).append("</li>")
        );

        sb.append("</ul>");

        return sb.toString();
    }

    public void sendEmailForOrderStatus(List<OrderService> services, String recipientName, String to, OrderStatus status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            configureEmail(helper, to);

            String subject;
            String orderedServices = getServices(services);
            String text;

            switch (status) {

                case PENDING:
                    subject = "Your Heart to Home Order is Pending";
                    text = "<p>Hi there, " + recipientName + "!</p>"
                            + """

        <p>Thank you for choosing <strong>Heart to Home</strong>.</p>

        <p>We have received your order and it is currently pending payment confirmation.</p>

        """
                            + orderedServices +
                            """
                    
                            <p>Once your payment is confirmed, we will begin processing your order.</p>
                    
                            <p>We will notify you when your order moves to the next stage.</p>
                    
                            <p>Thank you for your patience.</p>
                    
                            <p>
                                Kind regards,<br>
                                <strong>Heart to Home Team</strong>
                            </p>
                            """;
                    break;
                case IN_PROCESS:
                    subject = "Your Heart to Home Order is Being Processed";
                    text = "<p>Hi there, " + recipientName + "!</p>"
                            + """
                            
                            <p>Thank you for choosing <strong>Heart to Home</strong>.</p>
                            
                            <p>Your order has been received and is currently being processed.</p>
                            """
                            + orderedServices +
                            """
                                                            <p>We will notify you once it is ready for the clinic.</p>
                                    
                                                            <p>Thank you for your patience.</p>
                                    
                                                            <p>
                                                                Kind regards,<br>
                                                                <strong>Heart to Home Team</strong>
                                                            </p>
                                    """;
                    break;

                case READY_FOR_CLINIC:
                    subject = "Your Heart to Home Order is Ready for the Clinic";
                    text = "<p>Hi there, " + recipientName + "!</p>"
                            + """
                            
                            <p><strong>Good news!</strong></p>
                            
                            <p>Your order has been processed and is now ready for the clinic.</p>
                            """
                            + orderedServices +
                            """
                                                            <p>Our team will proceed with the next steps shortly.</p>
                                    
                                                            <p>Thank you for choosing <strong>Heart to Home</strong>.</p>
                                    
                                                            <p>
                                                                Kind regards,<br>
                                                                <strong>Heart to Home Team</strong>
                                                            </p>
                                    """;
                    break;

                case DELIVERED:
                    subject = "Your Heart to Home Order Has Been Delivered";
                    text = "<p>Hi there, " + recipientName + "!</p>"
                            + """
                            
                            <p>We're pleased to let you know that your order has been successfully delivered.</p>
                            """
                            + orderedServices +
                            """
                                        <p>Thank you for trusting <strong>Heart to Home</strong>. We hope our service met your expectations.</p>
                                    
                                        <p>
                                            Kind regards,<br>
                                            <strong>Heart to Home Team</strong>
                                        </p>
                                    """;
                    break;

                case CANCELED:
                    subject = "Your Heart to Home Order Has Been Cancelled";
                    text = "<p>Hi there, " + recipientName + "!</p>"
                            + """
                            
                            <p>We regret to inform you that your order has been cancelled.</p>
                            """
                            + orderedServices +
                            """
                                        <p>If you have any questions, please contact our support team.</p>
                                    
                                        <p>
                                            Kind regards,<br>
                                            <strong>Heart to Home Team</strong>
                                        </p>
                                    """;
                    break;

                default:
                    subject = "Heart to Home Order Update";
                    text = """
                            <p>Your order status has been updated.</p>
                            """;
            }

            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            throw new EmailSendingException(
                    "We were unable to send order status email. Please try again later.",
                    ex
            );
        }
    }

    public void sendEmailForOrderInitiation(List<OrderService> services, String recipientName, String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            configureEmail(helper, to);

            helper.setSubject("Your Heart to Home Order is Being Processed");
            String orderedServices = getServices(services);
            String text = """
                    <p>Hi there, """ + recipientName +
                                                                            """
                                                                            !</p>
                    
                    <p>Thank you for choosing <strong>Heart to Home</strong>.</p>
                    
                    <p>Your order has been received and is currently being processed.</p>
                    """
                    + orderedServices +
                    """
                                                        <p>We will notify you once it is ready for the clinic.</p>
                            
                                                        <p>Thank you for your patience.</p>
                            
                                                        <p>
                                                            Kind regards,<br>
                                                            <strong>Heart to Home Team</strong>
                                                        </p>
                            """;

            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            throw new EmailSendingException(
                    "We were unable to send the order initialization email. Please try again later.",
                    ex
            );
        }
    }
}
