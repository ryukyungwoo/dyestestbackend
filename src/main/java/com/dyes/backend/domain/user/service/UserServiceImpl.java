package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.user.service.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.provider.GoogleOauthClientIdProvider;
import com.dyes.backend.utility.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final private GoogleOauthClientIdProvider googleOauthClientIdProvider;
    final private UserRepository userRepository;
    final private RedisService redisService;
    final private RestTemplate restTemplate;

    @Override
    public void googleUserChecker(String code) {

        final String userUUID = requestAccessToken(code);
    }

    public String requestAccessToken(String code) {

        log.info("requestAccessToken start");

        final String googleClientId = googleOauthClientIdProvider.getGOOGLE_AUTH_CLIENT_ID();
        log.info("googleClientId: " + googleClientId);

        final String googleRedirectUrl = googleOauthClientIdProvider.getGOOGLE_AUTH_REDIRECT_URL();
        log.info("googleRedirectUrl: " + googleRedirectUrl);

        final String googleClientSecret = googleOauthClientIdProvider.getGOOGLE_AUTH_SECRETS();
        log.info("googleClientSecret: " + googleClientSecret);

        final String googleTokenRequestUrl = googleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL();
        log.info("googleTokenRequestUrl: " + googleTokenRequestUrl);


        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectUrl);
        params.put("grant_type", "authorization_code");

        ResponseEntity<GoogleOauthAccessTokenResponse> accessTokenRequest = restTemplate.postForEntity(googleTokenRequestUrl, params, GoogleOauthAccessTokenResponse.class);
        log.info("accessTokenRequest: " + accessTokenRequest);
        if(accessTokenRequest.getStatusCode() == HttpStatus.OK){
            ResponseEntity<GoogleOauthUserInfoResponse> userInfoResponse =
                    requestUserInfoWithAccessToken(accessTokenRequest.getBody().getAccessToken());
            log.info("userInfoResponse: " + userInfoResponse);
            User yetCheckedUser = new User(
                    userInfoResponse.getBody().getId(),
                    accessTokenRequest.getBody().getAccessToken(),
                    accessTokenRequest.getBody().getRefreshToken()
                    );
            log.info("yetCheckedUser: " + yetCheckedUser);
            User user = userCheckIsOurUser(yetCheckedUser);
            log.info("user" + user);
            final String googleUUID = "google" + UUID.randomUUID();
            redisService.setUUIDAndUser(googleUUID, user.getId());
            log.info("requestAccessToken end");

            return googleUUID;
        }
        log.info("requestAccessToken end");

        return null;
    }
    public ResponseEntity<GoogleOauthUserInfoResponse> requestUserInfoWithAccessToken(String AccessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization","Bearer "+ AccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        log.info("request: " + request);
        ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                googleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
        log.info("response: " + response);
        log.info("requestUserInfoWithAccessTokenForSignIn end");
        return response;
    }
    public User userCheckIsOurUser (User user) {
        log.info("userCheckIsOurUser start");
        Optional<User> maybeUser = userRepository.findById(user.getId());
        if (maybeUser.isPresent()) {
            log.info("userCheckIsOurUser end");
            return maybeUser.get();
        }
        userRepository.save(user);
        log.info("userCheckIsOurUser end");
        return user;
    }
}
