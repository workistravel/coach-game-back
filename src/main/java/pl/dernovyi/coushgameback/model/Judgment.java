package pl.dernovyi.coushgameback.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "judgment")
public class Judgment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String judgment;

    public Judgment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJudgment() {
        return judgment;
    }

    public void setJudgment(String judgment) {
        this.judgment = judgment;
    }
}
