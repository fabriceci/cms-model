package be.fcip.cms.service;

import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.service.IAppParamService;
import be.fcip.cms.persistence.service.IBlockService;
import be.fcip.cms.util.ApplicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service("mailService")
public class ApplicationMailer implements IMailer {

    @Autowired
    private IBlockService blockService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private IAppParamService appParamService;

    /**
     * This method will send compose and send the message
     * */
    public void sendMail(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {

        BlockEntity signature_email = blockService.findByNameWithCache("EMAIL_SIGNATURE");
        if (signature_email != null) {
            body += "<br>" + signature_email.getContent();
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(new InternetAddress(appParamService.getParam(IAppParamService.PARAM_EMAIL_FROM_ADDRESS), appParamService.getParam(IAppParamService.PARAM_EMAIL_FROM_NAME)));

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
