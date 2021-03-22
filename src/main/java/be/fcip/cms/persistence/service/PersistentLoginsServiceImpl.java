package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PersistentLoginsEntity;
import be.fcip.cms.persistence.repository.IPersistentLoginsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class PersistentLoginsServiceImpl implements PersistentTokenRepository {

    @Autowired private IPersistentLoginsRepository persistentLoginsRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLoginsEntity logins = new PersistentLoginsEntity();
        logins.setUsername(token.getUsername());
        logins.setSeries(token.getSeries());
        logins.setToken(token.getTokenValue());
        logins.setLastUsed(token.getDate());
        persistentLoginsRepository.save(logins);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentLoginsEntity result = persistentLoginsRepository.findOneBySeries(series);
        if (result != null) {
            result.setToken(tokenValue);
            result.setLastUsed(lastUsed);
            persistentLoginsRepository.save(result);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentLoginsEntity p = persistentLoginsRepository.findOneBySeries(seriesId);
        return (p != null) ? new PersistentRememberMeToken(p.getUsername(), p.getSeries(), p.getToken(), p.getLastUsed()) : null;
    }

    @Override
    public void removeUserTokens(String username) {
        persistentLoginsRepository.deleteByUsername(username);
    }
}