package com.mentoree;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import static org.assertj.core.api.Assertions.*;

public class PathMatcherTest {

    private final String[] excludePath = {
            "/mentoring-service/api/programs/list/**",
            "/mentoring-service/api/programs/{programId:\\d+}",
            "/member-auth-service/auth/**"};

    @Test
    public void antPatternMatchTest() {

        assertThat(checkPath("/member-auth-service/auth/login?code=4/0AdQt8qjG8jqBKVNf8I3yPozT3gSorEH9uznq_XsdIhehFOJR3dCHb9TcIn8XkbcuLXw-Hw&provider=google")).isFalse();

    }

    private boolean checkPath(String path) {
        boolean shouldLoginCheck = true;
        AntPathMatcher matcher = new AntPathMatcher();
        for (String whiteList : excludePath) {
            if(matcher.match(whiteList, path)) {
                shouldLoginCheck = false;
                break;
            }
        }
        return shouldLoginCheck;
    }


}
