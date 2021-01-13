package pl.dernovyi.coushgameback.model.game_components;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "card")
public class Card implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private boolean used;
    private boolean horizon;
    private String pictureUrl;


    public Card() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isHorizon() {
        return horizon;
    }

    public void setHorizon(boolean horizon) {
        this.horizon = horizon;
    }
}
