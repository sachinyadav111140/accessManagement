package com.springSecurity.accessManagement.listeners;


import com.springSecurity.accessManagement.events.OnRegistrationCompleteEvent;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.services.interfaces.UserAccountService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private static final String TEMPLATE_NAME = "html/registration";
    private static final String SPRING_LOGO_IMAGE = "templates/html/images/spring.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Registration Confirmation";

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
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userAccountService.save(user, token);

        String confirmationUrl = environment.getProperty("app.url.confirm-account") + "?token=" + token;
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
            ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
            ctx.setVariable("url", confirmationUrl);

            final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

            email.setText(htmlContent, true);

            ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);

            email.addInline("springLogo", clr, PNG_MIME);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}