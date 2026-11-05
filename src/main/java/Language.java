/**
 * Supported languages for vocabulary entries.
 * <p>
 * Values are persisted by {@link #name()}, so constants must never be renamed
 * once data exists. Change {@link #displayName()} instead to alter what users see.
 */
public enum Language {
    ENGLISH("English", "en"),
    FRENCH("French", "fr"),
    GERMAN("German", "de"),
    ITALIAN("Italian", "it"),
    PORTUGUESE("Portuguese", "pt"),
    SPANISH("Spanish", "es");

    private final String displayName;
    private final String code;

    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    /** Human-readable name, e.g. "Spanish". */
    public String displayName() {
        return displayName;
    }

    /** ISO 639-1 language code, e.g. "es". */
    public String code() {
        return code;
    }

    /**
     * Parses user input into a Language.
     * <p>
     * Case-insensitive. Accepts the name ({@code spanish}) or the ISO code ({@code es}).
     *
     * @param input raw user input
     * @return the matching language
     * @throws IllegalArgumentException if the input matches no supported language;
     *         the message names the bad input and lists the supported languages
     */
    public static Language parse(String input) {
        String normalized = input == null ? "" : input.trim();
        for (Language l : values()) {
            if (l.name().equalsIgnoreCase(normalized) || l.code.equalsIgnoreCase(normalized)) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown language: " + input
                + ". Use a name (e.g. spanish) or ISO code (e.g. es). Supported: " + supportedList());
    }

    /** Comma-separated list of display names, for help and error messages. */
    public static String supportedList() {
        StringBuilder sb = new StringBuilder();
        for (Language l : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(l.displayName);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
