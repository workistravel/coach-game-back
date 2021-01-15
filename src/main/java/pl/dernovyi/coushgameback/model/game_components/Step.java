package pl.dernovyi.coushgameback.model.game_components;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "step")
public class Step implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String name;

    private String title;

    private Long deckId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "step_id")
    List<Judgment> judgments;

    public Step() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public List<Judgment> getJudgments() {
        return judgments;
    }

    public void setJudgments(List<Judgment> judgments) {
        this.judgments = judgments;
    }
}
