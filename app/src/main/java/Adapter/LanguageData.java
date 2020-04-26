package Adapter;



public class LanguageData {

    String languageName, languageCode, languageCountryCode;

    public LanguageData(String languageName, String languageCode, String languageCountryCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.languageCountryCode = languageCountryCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCountryCode() {
        return languageCountryCode;
    }

    public void setLanguageCountryCode(String languageCountryCode) {
        this.languageCountryCode = languageCountryCode;
    }
}
