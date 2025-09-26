package com.atrbpn.keycloak.authenticator.otpformextend;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomPostAuthentication implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(CustomPostAuthentication.class);

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        log.info("set post-auth message!");
        authenticationFlowContext.form().setAttribute("postAuthMessage", "post-authentication message!");

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
