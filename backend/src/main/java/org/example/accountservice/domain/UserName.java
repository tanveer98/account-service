package org.example.accountservice.domain;

public record UserName(String value) {

    @Override
    public String toString() {
        return "[UserName] PII - Obfuscated";
    }
}
