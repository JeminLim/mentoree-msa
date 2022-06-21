package com.mentoree.jwt.util;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TokenMember {

    private String email;
    private String role;

}
