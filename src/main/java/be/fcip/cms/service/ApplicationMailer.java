package be.fcip.cms.service;

import be.fcip.cms.persistence.model.WebsiteEntity;
import be.fcip.cms.persistence.service.IAppParamService;
import be.fcip.cms.persistence.service.IWebsiteService;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsTypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service("mailService")
public class ApplicationMailer implements IMailer {

    @Autowired private JavaMailSender mailSender;
    @Autowired private IAppParamService appParamService;
    @Autowired private IWebsiteService websiteService;

    /**
     * This method will send compose and send the message
     * */

    public void sendMail(String to, String subject, String body, WebsiteEntity websiteEntity) throws MessagingException, UnsupportedEncodingException {
        sendMail(to, subject, body, websiteEntity, null);
    }
    public void sendMail(String to, String subject, String body, WebsiteEntity websiteEntity, String signature) throws MessagingException, UnsupportedEncodingException {
        if(websiteEntity == null || to == null){ throw new IllegalArgumentException("To and Website can't be null"); }
        if (!StringUtils.isEmpty(signature)) {
            body += "<br>" + signature;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(new InternetAddress(websiteEntity.getMailFrom(), websiteEntity.getMailName()));

        if (ApplicationUtils.isDev){
            List<String> devEmails = appParamService.getDevEmails();
            if(devEmails.size() == 0) return;
            boolean multiple = devEmails.size() > 1;
            helper.setTo(devEmails.get(0));
            if(multiple){
                devEmails.remove(0);
                helper.setCc(devEmails.toArray(new String[0]));
            }
        } else {
            helper.setTo(to.trim());
        }
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }
}
