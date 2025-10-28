package co.com.ebsa.ebsa_nexus.infrastructure.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generate_and_validate_and_extract_claims() {
        String token = jwtUtil.generateToken("user@example.com", "ADMIN", 123L);
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("user@example.com");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ADMIN");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(123L);
        assertThat(jwtUtil.isTokenExpired(token)).isFalse();
    }

    @Test
    void invalid_token_is_rejected() {
        assertThat(jwtUtil.validateToken("invalid.token"))
                .isFalse();
        assertThat(jwtUtil.extractEmail(" ")).isNull();
        assertThat(jwtUtil.extractRole(null)).isNull();
        assertThat(jwtUtil.extractUserId(null)).isNull();
    }
}
