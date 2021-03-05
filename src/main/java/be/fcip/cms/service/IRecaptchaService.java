package be.fcip.cms.service;

public interface IRecaptchaService {
    boolean isResponseValid(String remoteIp, String response);
}
