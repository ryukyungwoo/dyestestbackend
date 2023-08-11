package com.dyes.backend.userTest;

import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.UserServiceImpl;
import com.dyes.backend.domain.user.service.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.provider.GoogleOauthClientIdProvider;
import com.dyes.backend.utility.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserMockingTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RedisService mockRedisService;
    @Mock
    private GoogleOauthClientIdProvider mockGoogleOauthClientIdProvider;
    @Mock
    private RestTemplate mockRestTemplate;
    @InjectMocks
    private UserServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_CLIENT_ID()).thenReturn("your-client-id");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_REDIRECT_URL()).thenReturn("your-redirect-url");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_SECRETS()).thenReturn("your-client-secret");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL()).thenReturn("your-token-request-url");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL()).thenReturn("your-user-info-request-url");

        mockService = new UserServiceImpl(
                mockGoogleOauthClientIdProvider,
                mockUserRepository,
                mockRedisService,
                mockRestTemplate
        );
    }

    @Test
    @DisplayName("userMockingTest: userSignUp")
    public void 엑세스토큰으로_유저정보를_요청합니다() {
        final String accessToken = "구글에서 받은 엑세스 토큰";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);

        when(mockRestTemplate.exchange(anyString(), HttpMethod.GET, HttpEntity.class, GoogleOauthUserInfoResponse.class))
                .thenReturn(new GoogleOauthUserInfoResponse("id", "email", true, "name", "givenName", "femilyName", "picture", "locale"));

        ResponseEntity<GoogleOauthUserInfoResponse> response = mockService.requestUserInfoWithAccessToken(accessToken);

        assertEquals(expectedResponse, response);
    }
}
