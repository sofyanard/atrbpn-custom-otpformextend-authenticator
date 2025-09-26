package com.atrbpn.keycloak.authenticator.otpformextend;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atrbpn.keycloak.authenticator.otpformextend.tnc.TncRequest;
import com.atrbpn.keycloak.authenticator.otpformextend.tnc.TncResponse;
import com.atrbpn.keycloak.authenticator.otpformextend.tnc.TncRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomPreAuthentication implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(CustomPreAuthentication.class);

    private static String smtpHost;
    private static String smtpFrom;
    private static String environment;
    private static String otpMechanism;
    
    static {
        try {
            Context initCxt =  new InitialContext();

            smtpHost = (String) initCxt.lookup("java:/smtpHost");
            smtpFrom = (String) initCxt.lookup("java:/smtpFrom");
            environment = (String) initCxt.lookup("java:/environment");
            otpMechanism = (String) initCxt.lookup("java:/otpMechanism");

        } catch (Exception ex) {
            log.error("unable to get jndi connection for SMTP or Environment");
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        
        UserModel userModel = authenticationFlowContext.getUser();
        String username = authenticationFlowContext.getAuthenticationSession().getAuthNote("username");
        
        // get tnc from external api here
        if (TncRestClient.tncApiBaseUrl != null && !TncRestClient.tncApiBaseUrl.trim().isEmpty()) {
            log.info("starting tnc request for username : {} ", username);
            TncRequest tncRequest = new TncRequest(userModel.getAttributes().get("orcluserid").get(0), "internal");
            TncResponse tncResponse;
            try {
                tncResponse = TncRestClient.verifyUser(tncRequest);
                log.info("tnc response: {}", new ObjectMapper().writeValueAsString(tncResponse));

                if (tncResponse != null)
                {
                    authenticationFlowContext.form().setAttribute("tncMessage", tncResponse.getMessage());
                    if (tncResponse.getData() != null) {
                        authenticationFlowContext.form().setAttribute("tncStatus", tncResponse.getData().getStatusTnc());
                        authenticationFlowContext.form().setAttribute("tncContent", tncResponse.getData().getKonten());
                        authenticationFlowContext.form().setAttribute("tncVersionUpdated", tncResponse.getData().getVersiTncTerbaru());
                        authenticationFlowContext.form().setAttribute("tncUrl", tncResponse.getData().getUrl());

                        // set statusTnc to auth note for next process
                        String tncStatus = String.valueOf(tncResponse.getData().getStatusTnc());
                        authenticationFlowContext.getAuthenticationSession().setAuthNote("tncStatus", tncStatus);
                    } else {
                        log.warn("tnc response data is null");
                    }
                } else {
                    log.warn("tnc response is null");
                }
            } catch (IOException ex) {
                log.error("error request tnc from external api");
                log.error(ex.getMessage(), ex);
            }
        }

        authenticationFlowContext.success();
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
        authenticationFlowContext.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }

}
