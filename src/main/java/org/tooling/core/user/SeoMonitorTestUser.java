package org.tooling.core.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class SeoMonitorTestUser {

    String username;

    String email;

    String password;

    boolean isAdmin;

    String shortUsername;

    String environment;

    Set<String> features;


    @Override
    public String toString() {
        return username + " - " + email;
    }

}
