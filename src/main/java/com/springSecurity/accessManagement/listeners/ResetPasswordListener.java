package com.springSecurity.accessManagement.listeners;


import com.springSecurity.accessManagement.events.OnResetPasswordEvent;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.services.interfaces.UserAccountService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.util.UUID;


@Component
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {
    private static final String TEMPLATE_NAME = "html/password-reset";
    private static final String MAIL_SUBJECT = "Password Reset";

    @Autowired
    private  Environment environment;

    @Autowired
    private  UserAccountService userAccountService;

    @Autowired
    private  JavaMailSender mailSender;

    @Autowired
    private  TemplateEngine htmlTemplateEngine;

    @Override
    @Async
    public void onApplicationEvent(OnResetPasswordEvent event) {
        this.sendResetPasswordEmail(event);
    }

    private void sendResetPasswordEmail(OnResetPasswordEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userAccountService.save(user, token);

        String resetUrl = environment.getProperty("app.url.password-reset") + "?token=" + token;
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email;
        try {
            email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            email.setTo(user.getEmail());
            email.setSubject(MAIL_SUBJECT);
            email.setFrom(new InternetAddress(mailFrom, mailFromName));

            final Context ctx = new Context(LocaleContextHolder.getLocale());
            ctx.setVariable("email", user.getEmail());
            ctx.setVariable("name", user.getFirstName() + " " + user.getLastName());
            ctx.setVariable("url", resetUrl);

            final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

            email.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}