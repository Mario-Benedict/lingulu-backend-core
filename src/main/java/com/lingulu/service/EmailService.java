package com.lingulu.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {
        String subject = "Verify Your Email - Lingulu";
        String htmlContent = buildOtpEmailTemplate(otp);

        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Reset Your Password - Lingulu";
        String htmlContent = buildPasswordResetEmailTemplate(resetLink);

        sendHtmlEmail(to, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            helper.setFrom("noreply@lingulu.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildOtpEmailTemplate(String otp) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verification</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <!-- Main Container -->
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; max-width: 600px;">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background-color: #4f46e5; padding: 40px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: 1px;">
                                            LINGULU
                                        </h1>
                                        <div style="margin-top: 8px; height: 2px; width: 60px; background-color: #ffffff; opacity: 0.5; display: inline-block;"></div>
                                        <p style="margin: 12px 0 0; color: #e0e7ff; font-size: 14px; font-weight: 400; letter-spacing: 0.5px;">
                                            ENGLISH LEARNING PLATFORM
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 60px 40px;">
                                        <h2 style="margin: 0 0 24px; color: #1f2937; font-size: 24px; font-weight: 600; letter-spacing: -0.5px;">
                                            Email Verification Required
                                        </h2>
                                        
                                        <p style="margin: 0 0 32px; color: #4b5563; font-size: 16px; line-height: 1.6;">
                                            Thank you for creating a Lingulu account. To ensure the security of your account, please verify your email address using the code below:
                                        </p>
                                        
                                        <!-- OTP Box -->
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="center" style="padding: 32px 0;">
                                                    <table cellpadding="0" cellspacing="0" style="border: 2px solid #e5e7eb; border-radius: 8px;">
                                                        <tr>
                                                            <td style="background-color: #f9fafb; padding: 24px 48px; text-align: center;">
                                                                <span style="font-size: 32px; font-weight: 700; letter-spacing: 12px; color: #4f46e5; font-family: 'Courier New', Consolas, monospace;">
                                                                    %s
                                                                </span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 32px 0 0;">
                                            <tr>
                                                <td style="background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px 20px;">
                                                    <p style="margin: 0; color: #92400e; font-size: 14px; line-height: 1.6;">
                                                        <strong style="display: block; margin-bottom: 4px;">Important:</strong>
                                                        This verification code will expire in <strong>10 minutes</strong>.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <div style="margin-top: 48px; padding-top: 32px; border-top: 2px solid #f3f4f6;">
                                            <p style="margin: 0 0 12px; color: #1f2937; font-size: 14px; font-weight: 600;">
                                                Security Guidelines
                                            </p>
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="padding: 8px 0;">
                                                        <span style="display: inline-block; width: 6px; height: 6px; background-color: #4f46e5; border-radius: 50%%; margin-right: 12px; vertical-align: middle;"></span>
                                                        <span style="color: #6b7280; font-size: 14px; line-height: 1.6;">Never share this verification code with anyone</span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 8px 0;">
                                                        <span style="display: inline-block; width: 6px; height: 6px; background-color: #4f46e5; border-radius: 50%%; margin-right: 12px; vertical-align: middle;"></span>
                                                        <span style="color: #6b7280; font-size: 14px; line-height: 1.6;">Lingulu will never request your verification code via email or phone</span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 8px 0;">
                                                        <span style="display: inline-block; width: 6px; height: 6px; background-color: #4f46e5; border-radius: 50%%; margin-right: 12px; vertical-align: middle;"></span>
                                                        <span style="color: #6b7280; font-size: 14px; line-height: 1.6;">If you did not request this code, please disregard this message</span>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f9fafb; padding: 32px 40px; border-top: 1px solid #e5e7eb;">
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td style="text-align: center; padding-bottom: 16px;">
                                                    <p style="margin: 0; color: #6b7280; font-size: 14px;">
                                                        Questions? Contact our support team
                                                    </p>
                                                    <p style="margin: 8px 0 0;">
                                                        <a href="mailto:support@lingulu.com" style="color: #4f46e5; text-decoration: none; font-weight: 600; font-size: 14px;">support@lingulu.com</a>
                                                    </p>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="text-align: center; padding-top: 16px; border-top: 1px solid #e5e7eb;">
                                                    <p style="margin: 0; color: #9ca3af; font-size: 12px;">
                                                        © 2026 Lingulu. All rights reserved.
                                                    </p>
                                                    <p style="margin: 12px 0 0;">
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Privacy Policy</a>
                                                        <span style="color: #d1d5db;">•</span>
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Terms of Service</a>
                                                        <span style="color: #d1d5db;">•</span>
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Contact Us</a>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(otp);
    }

    private String buildPasswordResetEmailTemplate(String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Your Password</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f5f5f5; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <!-- Main Container -->
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; max-width: 600px;">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background-color: #4f46e5; padding: 40px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: 1px;">
                                            LINGULU
                                        </h1>
                                        <div style="margin-top: 8px; height: 2px; width: 60px; background-color: #ffffff; opacity: 0.5; display: inline-block;"></div>
                                        <p style="margin: 12px 0 0; color: #e0e7ff; font-size: 14px; font-weight: 400; letter-spacing: 0.5px;">
                                            ENGLISH LEARNING PLATFORM
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 60px 40px;">
                                        <h2 style="margin: 0 0 24px; color: #1f2937; font-size: 24px; font-weight: 600; letter-spacing: -0.5px;">
                                            Password Reset Request
                                        </h2>
                                        
                                        <p style="margin: 0 0 32px; color: #4b5563; font-size: 16px; line-height: 1.6;">
                                            We have received a request to reset the password for your Lingulu account. To proceed with the password reset, please click the button below:
                                        </p>
                                        
                                        <!-- Reset Button -->
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="center" style="padding: 32px 0;">
                                                    <table cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="background-color: #4f46e5; border-radius: 6px; text-align: center;">
                                                                <a href="%s" style="display: inline-block; color: #ffffff; text-decoration: none; padding: 16px 48px; font-size: 16px; font-weight: 600; letter-spacing: 0.5px;">
                                                                    RESET PASSWORD
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 32px 0 0;">
                                            <tr>
                                                <td style="background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px 20px;">
                                                    <p style="margin: 0; color: #92400e; font-size: 14px; line-height: 1.6;">
                                                        <strong style="display: block; margin-bottom: 4px;">Important:</strong>
                                                        This password reset link will expire in <strong>1 hour</strong>.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 32px 0 0;">
                                            <tr>
                                                <td style="background-color: #fee2e2; border-left: 4px solid #dc2626; padding: 16px 20px;">
                                                    <p style="margin: 0; color: #991b1b; font-size: 14px; line-height: 1.6;">
                                                        <strong style="display: block; margin-bottom: 4px;">Did not request this?</strong>
                                                        If you did not initiate this password reset request, please disregard this email. Your account password will remain unchanged, and no further action is required.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <div style="margin-top: 48px; padding-top: 32px; border-top: 2px solid #f3f4f6;">
                                            <p style="margin: 0 0 12px; color: #1f2937; font-size: 14px; font-weight: 600;">
                                                Alternative Access
                                            </p>
                                            <p style="margin: 0 0 12px; color: #6b7280; font-size: 14px; line-height: 1.6;">
                                                If the button above does not work, please copy and paste the following URL into your web browser:
                                            </p>
                                            <p style="margin: 0; padding: 12px; background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 4px; word-break: break-all;">
                                                <a href="%s" style="color: #4f46e5; text-decoration: none; font-size: 13px; font-family: 'Courier New', Consolas, monospace;">%s</a>
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f9fafb; padding: 32px 40px; border-top: 1px solid #e5e7eb;">
                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td style="text-align: center; padding-bottom: 16px;">
                                                    <p style="margin: 0; color: #6b7280; font-size: 14px;">
                                                        Questions? Contact our support team
                                                    </p>
                                                    <p style="margin: 8px 0 0;">
                                                        <a href="mailto:support@lingulu.com" style="color: #4f46e5; text-decoration: none; font-weight: 600; font-size: 14px;">support@lingulu.com</a>
                                                    </p>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="text-align: center; padding-top: 16px; border-top: 1px solid #e5e7eb;">
                                                    <p style="margin: 0; color: #9ca3af; font-size: 12px;">
                                                        © 2026 Lingulu. All rights reserved.
                                                    </p>
                                                    <p style="margin: 12px 0 0;">
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Privacy Policy</a>
                                                        <span style="color: #d1d5db;">•</span>
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Terms of Service</a>
                                                        <span style="color: #d1d5db;">•</span>
                                                        <a href="#" style="color: #9ca3af; text-decoration: none; font-size: 12px; margin: 0 8px;">Contact Us</a>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(resetLink, resetLink, resetLink);
    }
}

