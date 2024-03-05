package me.nasukhov.study;

import java.util.List;

public record Question(String text, String answer, List<String> variants) {
}
