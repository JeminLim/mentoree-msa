package com.mentoree.member.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "member_oauth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberOAuth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * authId - 각 제공자 마다 사용자의 계정에 소속된 고유 ID 번호를 발급한다.
     */
    @Column
    private String authId;

    @Column
    private String email;

    @Column
    private String provider;

    @Column
    private String accessToken;

    @Column
    private String refreshToken;

    @Column
    private Long expiration;

    @Builder
    public MemberOAuth(String email, String authId, String provider, String accessToken, Long expiration, String refreshToken) {
        this.email = email;
        this.authId = authId;
        this.provider = provider;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void updateToken(String accessToken, String refreshToken, Long expiration) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

}
