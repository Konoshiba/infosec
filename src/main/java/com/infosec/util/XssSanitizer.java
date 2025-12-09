package com.infosec.util;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class XssSanitizer {

    /**
     * Sanitizes user input to prevent XSS attacks
     * Escapes HTML special characters
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Escape HTML entities to prevent XSS
        return StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * Sanitizes user input for JSON responses
     * Removes potentially dangerous characters
     */
    public String sanitizeForJson(String input) {
        if (input == null) {
            return null;
        }
        // Escape HTML and also handle JSON special characters
        String sanitized = StringEscapeUtils.escapeHtml4(input);
        // Additional sanitization for JSON
        sanitized = sanitized.replace("\n", "\\n")
                            .replace("\r", "\\r")
                            .replace("\t", "\\t");
        return sanitized;
    }
}

