package pl.dernovyi.coushgameback.model;



import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "deck")
public class Deck implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String backOfCardUrl;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "deck_id")
    private List<Card> cards;

    public Deck() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackOfCardUrl() {
        return backOfCardUrl;
    }

    public void setBackOfCardUrl(String backOfCardUrl) {
        this.backOfCardUrl = backOfCardUrl;
    }


    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
