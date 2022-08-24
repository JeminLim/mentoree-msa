package com.mentoree.common.jwt.util;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TokenMember {

    private Long id;
    private String email;
    private String role;

}
