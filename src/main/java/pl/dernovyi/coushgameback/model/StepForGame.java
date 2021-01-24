package pl.dernovyi.coushgameback.model;

public class StepForGame {
    private String urlPicture;
    private String judgment;

    public StepForGame() {
    }

    public StepForGame(String urlPicture, String judgment) {
        this.urlPicture = urlPicture;
        this.judgment = judgment;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getJudgment() {
        return judgment;
    }

    public void setJudgment(String judgment) {
        this.judgment = judgment;
    }
}
