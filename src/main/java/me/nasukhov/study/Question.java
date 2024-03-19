package me.nasukhov.study;

import java.util.List;

public record Question(int id, String text, String answer, List<String> variants) {
}
