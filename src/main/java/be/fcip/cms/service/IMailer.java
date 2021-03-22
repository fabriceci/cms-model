package be.fcip.cms.service;

import be.fcip.cms.persistence.model.WebsiteEntity;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface IMailer {
    void sendMail(String to, String subject, String body, WebsiteEntity websiteEntity, String signature) throws MessagingException, UnsupportedEncodingException;
    void sendMail(String to, String subject, String body, WebsiteEntity websiteEntity) throws MessagingException, UnsupportedEncodingException;
}
