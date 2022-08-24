//package com.mentoree.member.auth.config;
//
//import com.mentoree.member.domain.entity.Member;
//import com.mentoree.member.domain.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.*;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@RequiredArgsConstructor
////@Service
//public class CustomOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails()
//                .getUserInfoEndpoint()
//                .getUserNameAttributeName();
//
//        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
//        Member member = saveOrLogin(attributes);
//        attributes.getAttributes().put("id", member.getId());
//        return Mono.just(new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
//                attributes.getAttributes(),
//                attributes.getNameAttributeKey()));
//    }
//
//    private Member saveOrLogin(OAuthAttributes attributes) {
//        Optional<Member> member = memberRepository.findByEmail(attributes.getEmail());
//        return member.orElseGet(() -> memberRepository.save(attributes.toEntity()));
//    }
//}
