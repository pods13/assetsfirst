package com.topably.assets.core.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages.nullDestMatcher().authenticated()
            .simpDestMatchers("/app/**").authenticated()
            .simpSubscribeDestMatchers("/user/**", "/topic/**").authenticated()
            .anyMessage().denyAll();
        return messages.build();
    }

    /**
     * When using Cookie bases CSRF Protection, the raw CSRF Token is used and not the Xored protected
     * form. For Websockets, the default ChannelInterceptor expects the Xored form of the token, so
     * wie have to make the system use the interceptor which expects the raw token. See
     * {@link org.springframework.security.config.annotation.web.socket.WebSocketMessageBrokerSecurityConfiguration#CSRF_CHANNEL_INTERCEPTOR_BEAN_NAME}
     * on how its applied.
     *
     * @return CsrfChannelInterceptor
     */
    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
        return new CsrfChannelInterceptor();
    }
}
